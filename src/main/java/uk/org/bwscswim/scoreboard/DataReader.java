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

import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static uk.org.bwscswim.scoreboard.ScoreboardState.CLEAR;
import static uk.org.bwscswim.scoreboard.ScoreboardState.LINEUP;
import static uk.org.bwscswim.scoreboard.ScoreboardState.LINEUP_COMPLETE;
import static uk.org.bwscswim.scoreboard.ScoreboardState.RACE;
import static uk.org.bwscswim.scoreboard.ScoreboardState.RACE_COMPLETE;
import static uk.org.bwscswim.scoreboard.ScoreboardState.RACE_FINISHING;
import static uk.org.bwscswim.scoreboard.ScoreboardState.RESULT;
import static uk.org.bwscswim.scoreboard.ScoreboardState.RESULT_COMPLETE;
import static uk.org.bwscswim.scoreboard.ScoreboardState.TIME_OF_DAY;

public class DataReader
{
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
    private final int laneCount;

    private final String laneRange;
    private final String nameRange;
    private final String clubRange;
    private final String timeRange;
    private final Writer writer;

    private final long displayFinishFor;
    private final long displayResultsFor;
    private final long displayLineupFor;

    private boolean trace = true;
    private int prevByte;
    private InputStream inputStream;
    private Text text = new Text();
    private RaceTimerThread raceTimerThread;
    private long lastLaneResultAt;

    private int lanesWithTimes;
    public ScoreboardState state;

    private List<StateData> queuedStateData = new ArrayList<>();
    private List<ScoreboardState> statesThatMayBeQueued = Collections.EMPTY_LIST;
    private StateTimerThread stateTimerThread;

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
        laneCount = lastLaneLineNumber-firstLaneLineNumber;

        laneRange = config.getCharRange("laneRange", null);
        nameRange = config.getCharRange("nameRange", null);
        clubRange = config.getCharRange("clubRange", null);
        timeRange = config.getCharRange("timeRange", null);

        displayFinishFor = config.getInt("displayFinishFor", 3000);
        displayResultsFor = config.getInt("displayResultsFor", 5000);
        displayLineupFor = config.getInt("displayLineupFor", 5000);

        setTrace(config.getBoolean("trace", true));

        Writer writer = null;
        if (config.getBoolean("traceFile", false))
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
        System.out.println("\nAvailable serial ports:");
        SerialPort[] commPorts = SerialPort.getCommPorts();
        for (int i = 0; i < commPorts.length; i++)
        {
            System.out.println((i + 1) + ". " + commPorts[i].getPortDescription());
        }
        Thread t = new Thread(() ->
        {
            scoreboard.beforeFirstRead();
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
                } while (config.getBoolean("testLoop", true) && trace);
                System.exit(0);
            }
            else
            {
                // Keep trying in case the port is temporarily not there.
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
                System.err.println(e.getMessage());
                break;
            }
            catch (IOException e)
            {
                System.err.println(e.getMessage());
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

    private synchronized void handleTransmission(String control, String data) throws IOException
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
                    if (state == LINEUP) // clock is probably 0.0
                    {
                        setState(LINEUP_COMPLETE);
                    }
                    else if (state == LINEUP_COMPLETE) // clock is probably 0.1
                    {
                        if (!"0.0".equals(clock.trim())) // Think we are getting another 0.0 sometimes with test runs on data system
                        {
                            setState(RACE);
                        }
                    }
                    else if (state == RACE)
                    {
                        if (showData() && raceTimerThread != null)
                        {
                            raceTimerThread.setClock(clock);
                        }
                    }
                    else if (state == TIME_OF_DAY)
                    {
                        if (showData())
                        {
                            scoreboard.setClock(clock);
                            makeScoreboardVisible();
                        }
                    }
                    else
                    {
                        // Similar to if (state == LINEUP_COMPLETE), but is for the situation we need to restart the scoreboard
                        if (showData())
                        {
                            clearScoreboard();
                            setState(RACE);
                        }                    }
                }
                else if (state == RESULT && lineNumber >= firstLaneLineNumber && lineNumber < lastLaneLineNumber &&
                         lanesWithTimes == countLanesWithTimes())
                {
                    setState(RESULT_COMPLETE);
                }
                else if ((state == RACE || state == RACE_FINISHING) && lineNumber >= firstLaneLineNumber && lineNumber < lastLaneLineNumber)
                {
                    if (showData())
                    {
                        lastLaneResultAt = System.currentTimeMillis();
                        drawLane(lineNumber - firstLaneLineNumber);
                        makeScoreboardVisible();
                    }
                    if (state == RACE_FINISHING && countLanesWithNames() == countLanesWithTimes())
                    {
                        setState(RACE_COMPLETE);
                    }
                }
            }
        }
        else if (CONTROL_CLEAR.equals(control))
        {
            lanesWithTimes = countLanesWithTimes();
            text.clear();
            setState(CLEAR);
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

    private boolean showData()
    {
        // We hold off showing data if we need to display the race finish, results or lineup a bit longer.
        return stateTimerThread == null;
    }

    public void setRaceFinishing()
    {
        if (countLanesWithNames() == countLanesWithTimes())
        {
            setState(RACE_COMPLETE);
        }
        else
        {
            setState(RACE_FINISHING, lastLaneResultAt);
        }
    }

    private int countLanesWithNames()
    {
        int count = 0;
        for (int laneIndex=0; laneIndex<laneCount; laneIndex++)
        {
            int lineNumber = firstLaneLineNumber + laneIndex;

            boolean result = state == RESULT || state == RESULT_COMPLETE;
            int indent = result ? 1 : 0;

            String name = text.getText(lineNumber, nameRange, indent, "").trim();
            if (!name.isEmpty())
            {
                count++;
            }
        }
        return count;
    }

    private int countLanesWithTimes()
    {
        int count = 0;
        for (int laneIndex=0; laneIndex<laneCount; laneIndex++)
        {
            int lineNumber = firstLaneLineNumber + laneIndex;
            boolean result = state == RESULT || state == RESULT_COMPLETE;
            int indent = result ? 1 : 0;

            String time = text.getText(lineNumber, timeRange, indent, "").trim();
            if (!time.isEmpty())
            {
                count++;
            }
        }
        return count;
    }

    private void drawScoreboard()
    {
        drawTitles();
        drawClock();
        for (int laneIndex=0; laneIndex<laneCount; laneIndex++)
        {
            drawLane(laneIndex);
        }
        makeScoreboardVisible();
    }

    private void drawTitles()
    {
        String title = text.getText(titleRange, "");
        String subTitle = text.getText(subTitleRange, "");

        AbstractScoreboard scoreboard = (AbstractScoreboard)this.scoreboard;
        scoreboard.setTitle(title);
        scoreboard.setSubTitle(subTitle);
    }

    private void drawClock()
    {
        String clock = text.getText(clockRange, "");
        if ("0.0".equals(clock.trim()))
        {
            clock = "";
        }
        ((AbstractScoreboard)this.scoreboard).setClock(clock);
    }

    private void drawLane(int laneIndex)
    {
        if (showData())
        {
            int lineNumber = firstLaneLineNumber + laneIndex;
            String placeRange = config.getCharRange("placeRange", null);
            boolean result = state == RESULT || state == RESULT_COMPLETE;
            int indent = result ? 1 : 0;
            int lane = result
                    ? text.getInt(lineNumber, placeRange, indent, 0)
                    : text.getInt(lineNumber, laneRange, indent, 0);
            int place = result
                    ? text.getInt(lineNumber, laneRange, indent, 0)
                    : text.getInt(lineNumber, placeRange, indent, 0);
            String name = text.getText(lineNumber, nameRange, indent, "").trim();
            String club = text.getText(lineNumber, clubRange, indent, "").trim();
            String time = text.getText(lineNumber, timeRange, indent, "").trim();
            if (name.isEmpty())
            {
                lane = 0;
                place = 0;
            }
            ((AbstractScoreboard) scoreboard).setLaneValues(laneIndex, lane, place, name, club, time);
        }
    }

    public void makeScoreboardVisible()
    {
        scoreboard.setVisible(true);
    }

    public void clearScoreboard()
    {
        if (scoreboard instanceof AbstractScoreboard)
        {
            ((AbstractScoreboard)scoreboard).clear();
        }
    }

    public void setClock(String clock)
    {
        ((AbstractScoreboard)scoreboard).setClock(clock);
    }

    public String getClock()
    {
        String clock = text.getText(clockRange, "");
        return clock;
    }

    private synchronized void setState(ScoreboardState state)
    {
        setState(state, System.currentTimeMillis());
    }

    private synchronized void setState(ScoreboardState state, long stateStart)
    {
        if (!statesThatMayBeQueued.isEmpty())
        {
            ScoreboardState nextAllowedState = statesThatMayBeQueued.remove(0);
            if (state != nextAllowedState)
            {
                // There has been a break in the expected sequence of queued events, so start again with the live state.
                clearStateQueue();
            }
            else
            {
//                System.out.println("    Add "+state+" to the queue");
//                System.out.println("    this.state="+this.state+"  state="+state+" lanesWithTimes="+lanesWithTimes+"\n"+text);
                queuedStateData.add(new StateData(this.state, state, text, lanesWithTimes));
            }
        }

        setScoreboardState(state, stateStart);
    }

    private void clearStateQueue()
    {
        statesThatMayBeQueued = Collections.EMPTY_LIST;
        queuedStateData.clear();
        if (stateTimerThread != null)
        {
            stateTimerThread.terminate();
            stateTimerThread = null;
        }
    }

    public synchronized void dequeueNextState()
    {
        if (!queuedStateData.isEmpty())
        {
            Text origText = text;
            int origLanesWithTimes = lanesWithTimes;
            ScoreboardState origState = state;
            try
            {
                do
                {
                    stateTimerThread = null;
                    StateData stateData = queuedStateData.remove(0);

                    state = stateData.getPrevState();
                    text = stateData.getText();
                    lanesWithTimes = stateData.getLanesWithTimes();
                    ScoreboardState state = stateData.getState();

//                    System.out.println("    this.state="+this.state+"  state="+state+" lanesWithTimes="+lanesWithTimes+"\n"+text);

                    setScoreboardState(state, System.currentTimeMillis());
                }
                while (stateTimerThread == null && !queuedStateData.isEmpty());
            }
            finally
            {
                if (!queuedStateData.isEmpty())
                {
                    text = origText;
                    lanesWithTimes = origLanesWithTimes;
                    state = origState;
                }
            }
        }
        else
        {
            clearStateQueue();
        }
    }

    private synchronized void setScoreboardState(ScoreboardState state, long stateStart)
    {
        if (showData())
        {
            if (state == RACE_COMPLETE)
            {
                drawScoreboard();
                if (statesThatMayBeQueued.isEmpty())
                {
                    statesThatMayBeQueued = new ArrayList<>(Arrays.asList(CLEAR, RESULT, RESULT_COMPLETE, CLEAR, LINEUP, LINEUP_COMPLETE, RACE));
                }
                stateTimerThread = new StateTimerThread(this, state, stateStart, displayFinishFor)
                {
                    @Override
                    public void end()
                    {
                        System.out.println("RACE_COMPLETE - SWITCH TO RESULTS IF AVAILABLE");
                    }
                };
            }
            else if (state == RESULT_COMPLETE)
            {
                drawScoreboard();
                if (statesThatMayBeQueued.isEmpty())
                {
                    statesThatMayBeQueued = new ArrayList<>(Arrays.asList(CLEAR, LINEUP, LINEUP_COMPLETE, RACE));
                }
                stateTimerThread = new StateTimerThread(this, state, stateStart, 1000, displayResultsFor)
                {
                    @Override
                    public void tick(int count)
                    {
                        int mod = count % 3;
                        String display = mod == 0 ? "TIMEs" : mod == 1 ? "PBs" : "CTs";
//                        System.out.println("DISPLAY RESULT " + display);
                    }

                    @Override
                    public void end()
                    {
                        System.out.println("RESULT_COMPLETE - SWITCH TO LINEUP IF AVAILABLE");
                    }
                };
            }
            else if (state == LINEUP_COMPLETE)
            {
                drawScoreboard();
                if (statesThatMayBeQueued.isEmpty())
                {
                    statesThatMayBeQueued = new ArrayList<>(Arrays.asList(RACE));
                }
                stateTimerThread = new StateTimerThread(this, state, stateStart, displayLineupFor)
                {
                    @Override
                    public void end()
                    {
                        System.out.println("LINEUP_COMPLETE - SWITCH TO THE RACE IF AVAILABLE");
                    }
                };
            }
            else if (state == RACE && this.state == LINEUP_COMPLETE)
            {
                String clock = text.getText(clockRange, "");
                raceTimerThread = new RaceTimerThread(this, clock);
            }
            else if (state == RACE_COMPLETE && (this.state == RACE || this.state == RACE_FINISHING))
            {
                raceTimerThread.terminate();
                raceTimerThread = null;
            }
            else if (state == CLEAR)
            {
                clearScoreboard();
                makeScoreboardVisible();
            }
        }
        this.state = state;
        if (showData())
        {
            scoreboard.setState(state);
        }
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
