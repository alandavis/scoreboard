/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2019 Bracknell and Wokingham Swimming Club (BWSC)
 * %%
 * This file is part of BWSC Scoreboard.
 *
 * BWSC Scoreboard is free software: you can redistribute it and/or modify
 * it under the terms of the LGNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BWSC Scoreboard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGNU Lesser General Public License for more details.
 *
 * You should have received a copy of the LGNU Lesser General Public License
 * along with BWSC Scoreboard.  If not, see <https://www.gnu.org/licenses/>.
 * #L%
 */
package uk.org.bwscswim.scoreboard;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.bwscswim.scoreboard.model.Scoreboard;

import javax.annotation.PostConstruct;

@Component
public class DataReader
{
    private static final Logger logger = LoggerFactory.getLogger(DataReader.class);

    private static final int SOL = 0x16; // Start of transmission
    private static final int SOH = 0x01; // Separator 1
    private static final int STX = 0x02; // Separator 2
    private static final int EOT = 0x04; // Separator 2
    private static final int ETB = 0x17; // End of transmission

    private static final String HEADER = "20000000";
    private static final String CONTROL_SUFFIX = "004010";
    private static final int CONTROL_SUFFIX_LENGTH = CONTROL_SUFFIX.length();

    @Autowired
    private Scoreboard scoreboard;

    @Autowired
    private SerialPort port;

    @Autowired
    private String dummyFilename;

    @Autowired
    private Boolean trace;

    private InputStream inputStream;

    void setScoreboard(Scoreboard scoreboard)
    {
        this.scoreboard = scoreboard;
    }

    void setTrace(boolean trace)
    {
        this.trace = trace;
    }

    void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    @PostConstruct
    public void readDataInBackground()
    {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                if (dummyFilename != null && !dummyFilename.isEmpty())
                {
                    try
                    {
                        inputStream = new DummyInputStream(dummyFilename);
                        readInputStream();
                    }
                    catch (InterruptedException ignore)
                    {
                    }
                    catch (FileNotFoundException e)
                    {
                        System.err.println("The test file " + dummyFilename + " could not be found.");
                    }
                    finally
                    {
                        try
                        {
                            inputStream.close();
                        }
                        catch (IOException ignore)
                        {
                        }
                        System.exit(0);
                    }
                }
                else
                {
                    // Keep trying in case the port is temporary not there.
                    for (; ; )
                    {
                        try
                        {
                            if (port.openPort())
                            {
                                try
                                {
                                    inputStream = port.getInputStream();
                                    readInputStream();
                                } finally
                                {
                                    port.closePort();
                                }
                            }
                            else
                            {
                                System.err.println("The port " + port.getPortDescription() + " failed to open for an unknown reason.");
                            }
                            Thread.sleep(2000);
                        }
                        catch (InterruptedException e)
                        {
                            break;
                        }
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    void readInputStream() throws InterruptedException
    {
        for (;;)
        {
            try
            {
                waitFor(SOL);
                String[] fields = readStrings(ETB, SOH, STX, EOT);
                handleTransmission(fields);
            }
            catch (EOFException e)
            {
                logger.error(e.getMessage());
                break;
            }
            catch (IOException e)
            {
                logger.error(e.getMessage());
            }
        }
    }

    private String[] readStrings(int endByte, int... separators) throws IOException, InterruptedException
    {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        loop:
        for (int b = readByte(); b != endByte; b = readByte())
        {
            if ((b >= 0x20 && b <= 0x7F))
            {
                sb.append((char)b);
                continue loop;
            }

            for (int separator: separators)
            {
                if (b == separator)
                {
                    list.add(sb.toString());
                    sb = new StringBuilder();
                    continue loop;
                }
            }

            throw new StreamCorruptedException("Unexpected byte " + format(b) + "found");
        }
        list.add(sb.toString());

        return list.toArray(new String[list.size()]);
    }

    private void waitFor(int startByte) throws IOException, InterruptedException
    {
        while (readByte() != startByte)
            ;
    }

    private boolean waitingForFirstByteOfTransmission = true;
    private int traceZeroCount = 0;
    private long time = -1;

    private int readByte() throws IOException, InterruptedException
    {
        int b = inputStream.read();
        if (b == -1)
        {
            waitingForFirstByteOfTransmission = true;
            time = -1;
            throw new EOFException("Unexpected end of data from timing equipment");
        }

        if (trace)
        {
            if (b == SOL)
            {
                if (waitingForFirstByteOfTransmission)
                {
                    waitingForFirstByteOfTransmission = false;
                    System.err.println("");
                }
                long now = System.currentTimeMillis();
                if (time != -1)
                {
                    long delay = ((now - time + 5) / 10) * 10; // round to 10 ms
                    System.err.print(((delay == 0) ? "   " : Long.toString(delay))+' ');
                }
                time = now;
            }
            // Some ports just return 0 endlessly in disconnected.
            if (b == 0)
            {
                if (traceZeroCount < 30)
                {
                    System.err.print(format(b));
                    if (++traceZeroCount == 30)
                    {
                        System.err.println("...");
                    }
                }
                Thread.sleep(100); // don't take all the CPU if disconnected.
            }
            else
            {
                traceZeroCount = 0;
                System.err.print(format(b));
                if (b == ETB)
                {
                    System.err.println("");
                }
            }
        }

        return b;
    }

    private String format(int b)
    {
        return ((b >= 0x20 && b <= 0x7F)) ? Character.toString((char)b) : String.format("[%02x]", b);
    }

    private String format(String str)
    {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray())
        {
            sb.append(format(c));
        }
        return sb.toString();
    }

    private int parseInt(String str, int from, int len) throws StreamCorruptedException
    {
        int to = from + len;
        String n = (len <= 0 || to > str.length()
                ? str.substring(from)
                : str.substring(from, to)).trim();
        if (n.isEmpty())
        {
            n = "-1";
        }
        try
        {
            return Integer.parseInt(n);
        }
        catch (NumberFormatException ignore)
        {
            throw new StreamCorruptedException("Expected integer at index " + from + ", " + to + " of " + format(str));
        }
    }

    private void handleTransmission(String[] fields) throws IOException
    {
        if (fields.length == 3)
        {
            scoreboard.reset();
        }
        else if (fields.length == 4)
        {
            String control = fields[1];
            String data = fields[2];
            if (control.startsWith(CONTROL_SUFFIX))
            {
                int lineNumber = parseInt(control, CONTROL_SUFFIX.length(), 2);
                if (lineNumber < 2 || lineNumber == 11)
                {
                    data = data.trim();
                    if (lineNumber == 0)
                    {
                        scoreboard.setTitle(data.trim());
                    }
                    else if (lineNumber == 1)
                    {
                        scoreboard.setSubTitle(data.trim());
                    }
                    else
                    {
                        scoreboard.setClock(data.trim());
                    }
                }
                else
                {
                    if (data.length() != 37)
                    {
                        throw new StreamCorruptedException("Expected data to be 36 characters long. "+format(data));
                    }

                    boolean result = false;
                    int p = 34;
                    int l = 0;
                    if (data.charAt(0) == 'P')
                    {
                        result = true;
                        p = 0;
                        l = 34;
                        data = data.substring(1);
                    }
                    int lane = parseInt(data, l, 2);
                    int place = parseInt(data, p, 2);
                    String name = data.substring(3, 16).trim();
                    String club = data.substring(20, 24).trim();
                    String time = data.substring(25, 33).trim();

                    scoreboard.setResult(result);
                    scoreboard.setLaneValues(lineNumber-2, lane, place, name, club, time);
                }
            }
        }
    }
}
