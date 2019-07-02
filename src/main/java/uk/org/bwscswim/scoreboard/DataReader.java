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

import static uk.org.bwscswim.scoreboard.State.CLEAR;
import static uk.org.bwscswim.scoreboard.State.LINEUP;
import static uk.org.bwscswim.scoreboard.State.READY;
import static uk.org.bwscswim.scoreboard.State.RESULT;
import static uk.org.bwscswim.scoreboard.State.RUNNING;
import static uk.org.bwscswim.scoreboard.State.TIME_OF_DAY;

public class DataReader
{
    private static final Logger logger = Logger.getLogger(DataReader.class);

    private static final int SOL = 0x16; // Start of transmission
    private static final int SOH = 0x01; // Separator 1
    private static final int STX = 0x02; // Separator 2
    private static final int EOT = 0x04; // Separator 2
    private static final int ETB = 0x17; // End of transmission

    private static final String CONTROL_CLEAR = "008010000002048000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    private static final String CONTROL_LINEUP = "003000001";
    private static final String CONTROL_RESULTS = "003000002";
    private static final String CONTROL_TIME_OF_DAY = "003000004";
    private static final String CONTROL_LINE_SUFFIX = "004010";
    private        final String CONTROL_CLOCK;
    private static final int CONTROL_LINE_SUFFIX_LENGTH = CONTROL_LINE_SUFFIX.length();

    private final BaseBoard scoreboard;
    private final Config config;
    private final String titleRange;
    private final String subTitleRange;
    private final String clockRange;

    private final int firstLaneLineNumber;
    private final int lastLaneLineNumber;

    private final String laneRange;
    private final String nameRange;
    private final String clubRange;
    private final String timeRange;
    private final Writer writer;

    private boolean trace = true;
    private int prevByte;
    private InputStream inputStream;
    private Text text = new Text();
    private TimerThread timerThread;

    public DataReader(Config config, BaseBoard scoreboard)
    {
        this.config = config;
        this.scoreboard = scoreboard;

        titleRange = config.getRange("titleRange", null);
        subTitleRange = config.getRange("subTitleRange", null);
        clockRange = config.getRange("clockRange", null);
        CONTROL_CLOCK = CONTROL_LINE_SUFFIX+Text.getFromRange(clockRange);

        String lanesRange = config.getCharRange("lanesRange", null);
        firstLaneLineNumber = Text.getCharRangeFrom(lanesRange);
        lastLaneLineNumber = Text.getCharRangeTo(lanesRange);

        laneRange = config.getCharRange("laneRange", null);
        nameRange = config.getCharRange("nameRange", null);
        clubRange = config.getCharRange("clubRange", null);
        timeRange = config.getCharRange("timeRange", null);

        setTrace(config.getBoolean("trace", true));

        Writer writer = null;
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
        this.writer = writer;
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
                    }
                    finally
                    {
                        try
                        {
                            if (inputStream != null)
                            {
                                inputStream.close();
                            }
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
        String control = fields.length >= 2 ? fields[1] : "";
        String data = fields.length >= 3 ? fields[2] : "";
        handleTransmission(control, data);
    }

    private void handleTransmission(String control, String data) throws IOException
    {
        if (control.startsWith(CONTROL_LINE_SUFFIX))
        {
            int position = parseInt(control, CONTROL_LINE_SUFFIX_LENGTH, 4);
            int lineNumber = position / 100;
            int offset = position % 100;
            text.setText(lineNumber, offset, data);

            if (scoreboard instanceof RawDisplay)
            {
                ((RawDisplay) scoreboard).setText(lineNumber, offset, data);
            }
            else
            {
                AbstractScoreboard scoreboard = (AbstractScoreboard)this.scoreboard;

                if (control.equals(CONTROL_CLOCK))
                {
                    String clock = text.getText(clockRange, "");
                    if (scoreboard instanceof AbstractScoreboard)
                    {
                        State state = getState();
                        if (state == LINEUP) // clock is probably "0.0"
                        {
                            setState(READY);
                        }
                        else if (state == READY)
                        {
                            setState(RUNNING);
                            timerThread = new TimerThread(this, clock);
                        }
                        else if (state == RUNNING)
                        {
                            timerThread.setClock(clock);
                        }
                        else if (state == TIME_OF_DAY)
                        {
                            scoreboard.setClock(clock);
                        }
                    }
                }
                else
                {
                    String title = text.getText(titleRange, "");
                    String subTitle = text.getText(subTitleRange, "");

                    scoreboard.setTitle(title);
                    scoreboard.setSubTitle(subTitle);

                    if (lineNumber >= firstLaneLineNumber && lineNumber < lastLaneLineNumber)
                    {
                        String placeRange = config.getCharRange("placeRange", null);
                        boolean result = getState() == RESULT;
                        int indent = result ? 1 : 0;
                        int laneDefault = lineNumber - firstLaneLineNumber + 1;
                        int lane = result
                                ? text.getInt(lineNumber, placeRange, indent, laneDefault)
                                : text.getInt(lineNumber, laneRange, indent, laneDefault);
                        int place = result
                                ? text.getInt(lineNumber, laneRange, indent, 0)
                                : text.getInt(lineNumber, placeRange, indent, 0);
                        String name = text.getText(lineNumber, nameRange, indent, "").trim();
                        String club = text.getText(lineNumber, clubRange, indent, "").trim();
                        String time = text.getText(lineNumber, timeRange, indent, "").trim();
                        scoreboard.setLaneValues(lineNumber - firstLaneLineNumber, lane, place, name, club, time);
                    }
                }
            }
            scoreboard.setVisible(true);
        }
        else if (CONTROL_CLEAR.equals(control))
        {
            scoreboard.clear();
            text.clear();
            if (scoreboard instanceof AbstractScoreboard)
            {
                setState(CLEAR);
            }
            scoreboard.setVisible(true);
        }
        else if (CONTROL_LINEUP.equals(control))
        {
            setState(LINEUP);
        }
        else if (CONTROL_RESULTS.equals(control))
        {
            setState(RESULT);
        }
        else if (CONTROL_TIME_OF_DAY.equals(control))
        {
            setState(TIME_OF_DAY);
        }
    }

    public void setClock(String clock)
    {
//        System.err.println("    set clock "+clock);
        ((AbstractScoreboard)scoreboard).setClock(clock);
    }

    private synchronized void setState(State state)
    {
        scoreboard.setState(state);
    }

    public synchronized State getState()
    {
        // TODO The scoreboard state will eventually not be the same as the current data state
        return scoreboard.state;
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
