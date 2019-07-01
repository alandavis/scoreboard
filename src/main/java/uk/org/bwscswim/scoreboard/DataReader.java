/*
 * #%L
 * BWSC AbstractScoreboard
 * %%
 * Copyright (C) 2018-2019 Bracknell and Wokingham Swimming Club (BWSC)
 * %%
 * This file is part of BWSC AbstractScoreboard.
 *
 * BWSC AbstractScoreboard is free software: you can redistribute it and/or modify
 * it under the terms of the LGNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BWSC AbstractScoreboard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGNU Lesser General Public License for more details.
 *
 * You should have received a copy of the LGNU Lesser General Public License
 * along with BWSC AbstractScoreboard.  If not, see <https://www.gnu.org/licenses/>.
 * #L%
 */
package uk.org.bwscswim.scoreboard;

import com.fazecast.jSerialComm.SerialPort;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class DataReader
{
    private static final Logger logger = Logger.getLogger(DataReader.class);

    private static final int SOL = 0x16; // Start of transmission
    private static final int SOH = 0x01; // Separator 1
    private static final int STX = 0x02; // Separator 2
    private static final int EOT = 0x04; // Separator 2
    private static final int ETB = 0x17; // End of transmission

    private static final String HEADER = "20000000";
    private static final String CONTROL_SUFFIX = "004010";
    private static final int CONTROL_SUFFIX_LENGTH = CONTROL_SUFFIX.length();

    private final BaseBoard scoreboard;
    private final Config config;

    private int prevByte;
    private InputStream inputStream;
    private boolean trace = true;
    private Text text = new Text();
    public Writer writer;

    public DataReader(Config config, BaseBoard scoreboard)
    {
        this.config = config;
        this.scoreboard = scoreboard;
        setTrace(config.getBoolean("trace", true));
        if (config.getBoolean("traceFile", true))
        {
            String filename = System.currentTimeMillis()+".log";

            try
            {
                writer = new BufferedWriter(new FileWriter(filename));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    public void setTrace(boolean trace)
    {
        this.trace = trace;
    }

    public void readDataInBackground()
    {
        if (logger.isInfoEnabled())
        {
            logger.info("Available serial ports:");
            SerialPort[] commPorts = SerialPort.getCommPorts();
            for (int i = 0; i < commPorts.length; i++)
            {
                logger.info((i + 1) + ". " + commPorts[i].getPortDescription());
            }
        }
        Thread t = new Thread(() ->
        {
            String testFilename = config.getString("testFilename", null);
            if (testFilename != null && !testFilename.isEmpty())
            {
                do
                {
                    try
                    {
                        inputStream = new DummyInputStream(testFilename);
                        readInputStream();
                    }
                    catch (InterruptedException ignore)
                    {
                    }
                    catch (FileNotFoundException e)
                    {
                        System.err.println("The test file " + testFilename + " could not be found.");
                    } finally
                    {
                        try
                        {
                            inputStream.close();
                        }
                        catch (IOException ignore)
                        {
                        }
                    }
                } while (config.getBoolean("testLoop", false) && trace);
                System.exit(0);
            }
            else
            {
                // Keep trying in case the port is temporary not there.
                for (; ; )
                {
                    try
                    {
                        SerialPort port = config.getPort();
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
                            System.err.println("The port " + port.getSystemPortName() + " failed to open for an unknown reason.");
                        }
                        Thread.sleep(2000);
                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    void readInputStream() throws InterruptedException
    {
        for (; ; )
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
                sb.append((char) b);
                continue loop;
            }

            for (int separator : separators)
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
                    log("\n");
                }
                long now = System.currentTimeMillis();
                if (time != -1)
                {
                    long delay = ((now - time + 5) / 10) * 10; // round to 10 ms
                    log(((delay == 0) ? "   " : Long.toString(delay)) + ' ');
                }
                time = now;
            }
            // Some ports just return 0 endlessly in disconnected.
            if (b == 0)
            {
                if (traceZeroCount < 30)
                {
                    log(format(b));
                    if (++traceZeroCount == 30)
                    {
                        log("...\n");
                    }
                }
                Thread.sleep(100); // don't take all the CPU if disconnected.
            }
            else
            {
                traceZeroCount = 0;
                if (prevByte != ETB || b != ETB)
                {
                    log(format(b));
                    if (b == ETB)
                    {
                        log("\n");
                    }
                }
                prevByte = b;
            }
        }

        return b;
    }

    private void log(String str)
    {
        System.err.print(str);
        if (writer != null)
        {
            try
            {
                writer.write(str);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private String format(int b)
    {
        return ((b >= 0x20 && b <= 0x7F)) ? Character.toString((char) b) : String.format("[%02x]", b);
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
            scoreboard.clear();
            text.clear();
            if (scoreboard instanceof AbstractScoreboard)
            {
                setState("", "", ' ');
            }
        }
        else if (fields.length == 4)
        {
            String control = fields[1];
            String data = fields[2];
            if (control.startsWith(CONTROL_SUFFIX))
            {
                int position = parseInt(control, CONTROL_SUFFIX.length(), 4);
                int lineNumber = position / 100;
                int offset = position % 100;
                text.setText(lineNumber, offset, data);

                boolean swimmerLine = position != 230 && lineNumber >= 2 && lineNumber != 11;
                if (swimmerLine)
                {
                    boolean result = data.charAt(0) == 'P';
                    if (result)
                    {
                        scoreboard.setState(State.RESULT);
                    }
                }

                if (scoreboard instanceof AbstractScoreboard)
                {
                    AbstractScoreboard scoreboard = (AbstractScoreboard) this.scoreboard;
                    text.setText(lineNumber, offset, data);
                    String title = text.getText(config.getRange("titleRange", null), "");
                    String subTitle = text.getText(config.getRange("subTitleRange", null), "");
                    String clock = text.getText(config.getRange("clockRange", null), "");
                    String timer = text.getText(config.getRange("timerRange", null), "");
                    clock = clock.startsWith(" ") ? "" : clock;
                    timer = timer.startsWith(" ") ? timer : "";

                    String lanesRange = config.getCharRange("lanesRange", null);
                    int from = Text.getCharRangeFrom(lanesRange);
                    int to = Text.getCharRangeTo(lanesRange);
                    String laneRange = config.getCharRange("laneRange", null);
                    int i = Text.getCharRangeFrom(laneRange);
                    char firstCharOfLanes = text.getChar(from, i, ' ');

                    if (firstCharOfLanes != ' ')
                    {
                        clock = "";
                    }
                    clock = clock.isEmpty() ? timer : clock;

                    setState(title, clock, firstCharOfLanes);

                    scoreboard.setTitle(title);
                    scoreboard.setSubTitle(subTitle);
                    if (!clock.isEmpty())
                    {
                        scoreboard.setClock(clock);
                    }

                    int lane = 0;
                    if (lineNumber >= from && lineNumber < to)
                    {
                        if (firstCharOfLanes != ' ') // Just a timer, so ignore
                        {
                            String placeRange = config.getCharRange("placeRange", null);
                            boolean result = firstCharOfLanes == 'P';
                            int indent = result ? 1 : 0;
                            int laneDefault = lineNumber - from + 1;
                            lane = result
                                    ? text.getInt(lineNumber, placeRange, indent, laneDefault)
                                    : text.getInt(lineNumber, laneRange, indent, laneDefault);
                            int place = result
                                    ? text.getInt(lineNumber, laneRange, indent, 0)
                                    : text.getInt(lineNumber, placeRange, indent, 0);
                            String name = text.getText(lineNumber, config.getCharRange("nameRange", null), indent, "").trim();
                            String club = text.getText(lineNumber, config.getCharRange("clubRange", null), indent, "").trim();
                            String time = text.getText(lineNumber, config.getCharRange("timeRange", null), indent, "").trim();
                            scoreboard.setLaneValues(lineNumber - from, lane, place, name, club, time);
                        }
                    }
                }
                else
                {
                    ((RawDisplay) scoreboard).setText(lineNumber, offset, data);
                }
            }
        }
        scoreboard.setVisible(true);
    }

    private void setState(String title, String clock, char firstCharOfLanes)
    {
        title = title.trim();
        clock = clock.trim();
        State state = scoreboard.state;

        boolean noTitleOrLanes = firstCharOfLanes == ' ' && title.isEmpty();
        if (noTitleOrLanes && !clock.isEmpty())
        {
            state = State.TIME_OF_DAY;
        }
        else if ((state == State.TIME_OF_DAY || state == State.RESULT) && noTitleOrLanes)
        {
            state = State.LINEUP;
        }
        else if (state == State.LINEUP && !clock.isEmpty())
        {
            state = State.READY;
        }
        else if (state == State.READY && !"0.0".equals(clock))
        {
            state = State.RUNNING;
        }
        else if ((firstCharOfLanes == 'P') ||
                 (state == State.RUNNING && noTitleOrLanes))
        {
            state = State.RESULT;
        }

        scoreboard.setState(state);
    }

    public void close()
    {
        if (writer != null)
        {
            try
            {
                writer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
