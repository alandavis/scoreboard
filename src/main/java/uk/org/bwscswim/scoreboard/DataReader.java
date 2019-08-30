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

import com.fazecast.jSerialComm.SerialPort;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import static uk.org.bwscswim.scoreboard.ScoreboardState.CLEAR;
import static uk.org.bwscswim.scoreboard.ScoreboardState.LINEUP;
import static uk.org.bwscswim.scoreboard.ScoreboardState.LINEUP_COMPLETE;
import static uk.org.bwscswim.scoreboard.ScoreboardState.RACE;
import static uk.org.bwscswim.scoreboard.ScoreboardState.RACE_COMPLETE;
import static uk.org.bwscswim.scoreboard.ScoreboardState.RACE_FINISHING;
import static uk.org.bwscswim.scoreboard.ScoreboardState.RESULTS;
import static uk.org.bwscswim.scoreboard.ScoreboardState.RESULTS_COMPLETE;
import static uk.org.bwscswim.scoreboard.ScoreboardState.TIME_OF_DAY;
import static uk.org.bwscswim.scoreboard.TimingEquipmentTrace.format;

/**
 * Reads data from port or test file and generates events for scoreboards to display their data. Some events are
 * automatically held or discarded so that minimum display times occur for results or line ups.
 *
 * @author adavis
 */
class DataReader
{
            static final int SOL = 0x16; // Start of transmission
    private static final int SOH = 0x01; // Separator 1
    private static final int STX = 0x02; // Separator 2
    private static final int EOT = 0x04; // Separator 2
            static final int ETB = 0x17; // End of transmission

    private static int[] FILED_SEPARATORS = new int[] {SOH, STX, EOT};

    private static final String CONTROL_CLEAR = "008010000002048000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
    private static final String CONTROL_LINEUP = "003000001";
    private static final String CONTROL_RESULTS = "003000002";
    private static final String CONTROL_TIME_OF_DAY = "003000004";
    private static final String CONTROL_LINE_SUFFIX = "004010";
    private        final String CONTROL_CLOCK;
    private static final int CONTROL_LINE_SUFFIX_LENGTH = CONTROL_LINE_SUFFIX.length();

    private final AbstractScoreboard scoreboard1;
    private final AbstractScoreboard scoreboard2;
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
    private final TimingEquipmentTrace timingEquipmentTrace;
    private final StateTrace stateTrace;

    private final long displayFinishFor;
    private final long displayResultsFor;
    private final long displayLineupFor;

    private boolean trace = true;
    private InputStream inputStream;
    private Text text = new Text();
    private RaceTimerThread raceTimerThread;

    private int lanesWithTimesAtTheEndOfTheRace;
    private ScoreboardState state;
    private int splitCount;

    private List<StateData> queuedStateData = new ArrayList<>();
    private StateTimerThread stateTimerThread;

    DataReader(Config config, AbstractScoreboard scoreboard1, AbstractScoreboard scoreboard2)
    {
        this.config = config;
        this.scoreboard1 = scoreboard1;
        this.scoreboard2 = scoreboard2;

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
        displayResultsFor = config.getInt("displayResultsFor", 10000);
        displayLineupFor = config.getInt("displayLineupFor", 6000);

        setTrace(config.getBoolean("trace", true));
        stateTrace = new StateTrace();
        timingEquipmentTrace = new TimingEquipmentTrace(config, stateTrace);
    }

    void setInputStream(InputStream inputStream)
    {
        this.inputStream = inputStream;
    }

    void setTrace(boolean trace)
    {
        this.trace = trace;
    }

    void readDataInBackground()
    {
        System.out.println("\nAvailable serial ports:");
        SerialPort[] commPorts = SerialPort.getCommPorts();
        for (int i = 0; i < commPorts.length; i++)
        {
            System.out.println((i + 1) + ". " + commPorts[i].getPortDescription());
        }
        Thread t = new Thread(() ->
        {
            scoreboard1.beforeFirstRead();
            scoreboard2.beforeFirstRead();
            String testFilename = config.getString("testFilename", null);
            if (testFilename != null && !testFilename.isEmpty())
            {
                do
                {
                    try
                    {
                        setInputStream(new DummyInputStream(testFilename));
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
                                setInputStream(port.getInputStream());
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
                waitForSOL();
                String[] fields = readFields();
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

    private void waitForSOL() throws IOException, InterruptedException
    {
        while (readByte() != SOL)
            ;
    }

    private String[] readFields() throws IOException, InterruptedException
    {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();

        loop:
        for (int b = readByte(); b != ETB; b = readByte())
        {
            if ((b >= 0x20 && b <= 0x7F))
            {
                sb.append((char) b);
                continue;
            }

            for (int separator : FILED_SEPARATORS)
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

        return list.toArray(new String[0]);
    }

    private int readByte() throws IOException, InterruptedException
    {
        int b = inputStream.read();
        if (b == -1)
        {
            timingEquipmentTrace.reset();
            throw new EOFException("Unexpected end of data from timing equipment");
        }

        if (trace)
        {
            timingEquipmentTrace.trace(b);
        }

        return b;
    }

    private int getPosition(String str) throws StreamCorruptedException
    {
        int from = CONTROL_LINE_SUFFIX_LENGTH;
        int len = 4;
        int to = from + len;
        String n = (to > str.length()
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
            int position = getPosition(control);
            int lineNumber = position / 100;
            int offset = position % 100;
            text.setText(lineNumber, offset, data);

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
                        raceTimerThread = new RaceTimerThread(this, clock, stateTrace);
                    }
                }
                else if (state == RACE)
                {
                    if (raceTimerThread != null)
                    {
                        raceTimerThread.resetClock(clock);
                    }
                }
                else if (state == TIME_OF_DAY)
                {
                    if (showData())
                    {
                        scoreboard1.setClock(clock);
                        scoreboard2.setClock(clock);
                        makeScoreboardVisible();
                    }
                }
            }
            else if (state == RESULTS &&
                     lineNumber >= firstLaneLineNumber && lineNumber < lastLaneLineNumber &&
                     lanesWithTimesAtTheEndOfTheRace == countLanesWithTimes(text))
            {
                setState(RESULTS_COMPLETE);
            }
            else if ((state == RACE || state == RACE_FINISHING) &&
                     lineNumber >= firstLaneLineNumber && lineNumber < lastLaneLineNumber)
            {
                // Set or clear split/finish time for swimmer if currently visible
                drawScoreboard(state, splitCount++, text);

                if (state == RACE_FINISHING && countLanesWithNames(text) == countLanesWithTimes(text))
                {
                    setState(RACE_COMPLETE);
                }
            }
        }
        else if (CONTROL_CLEAR.equals(control))
        {
            lanesWithTimesAtTheEndOfTheRace = countLanesWithTimes(text);
            text.clear();
            setState(CLEAR);
        }
        else if (CONTROL_LINEUP.equals(control))
        {
            setState(LINEUP);
        }
        else if (CONTROL_RESULTS.equals(control))
        {
            setState(RESULTS);
        }
        else if (CONTROL_TIME_OF_DAY.equals(control))
        {
            clearRaceTimer();
            clearStateQueue();
            setState(TIME_OF_DAY);
        }
    }

    void setRaceFinishing()
    {
        setState(RACE_FINISHING);
//        if (countLanesWithNames(text) == countLanesWithTimes(text)) // TODO remove? This code resulted in the time for the last swimmer not being sent sometimes.
//        {
//            setState(RACE_COMPLETE);
//        }
    }

    private boolean showData()
    {
        // We hold off showing data if we need to display the race finish, results or lineup a bit longer.
        return stateTimerThread == null;
    }

    private int countLanesWithNames(Text text)
    {
        int count = 0;
        for (int laneIndex=0; laneIndex<laneCount; laneIndex++)
        {
            int lineNumber = firstLaneLineNumber + laneIndex;

            boolean result = state == RESULTS || state == RESULTS_COMPLETE;
            int indent = result ? 1 : 0;

            String name = text.getText(lineNumber, nameRange, indent, "").trim();
            if (!name.isEmpty())
            {
                count++;
            }
        }
        return count;
    }

    private int countLanesWithTimes(Text text)
    {
        int count = 0;
        for (int laneIndex=0; laneIndex<laneCount; laneIndex++)
        {
            int lineNumber = firstLaneLineNumber + laneIndex;
            boolean result = state == RESULTS || state == RESULTS_COMPLETE;
            int indent = result ? 1 : 0;

            String time = this.text.getText(lineNumber, timeRange, indent, "").trim();
            if (!time.isEmpty())
            {
                count++;
            }
        }
        return count;
    }

    boolean isRaceInProgress()
    {
        return state == RACE || state == RACE_FINISHING;
    }

    private void drawScoreboard(ScoreboardState state, int count, Text text)
    {
        if (showData()) // TODO turn into a LineupEvent, RaceEvent or ResultEvent
        {
            String event =
                    ( state == LINEUP_COMPLETE ? "Lineup"
                    : state == RACE || state == RACE_FINISHING? "Race"
                    : state == RESULTS_COMPLETE ? "Result"
                    : null);
            if (event != null)
            {
                event += "Event("+count+")";

                stateTrace.trace(event);
                scoreboard1.setState(state);
                scoreboard2.setState(state);

                drawTitles(text);
                drawClock(text);
                for (int laneIndex = 0; laneIndex < laneCount; laneIndex++)
                {
                    drawLane(text, laneIndex);
                }
                makeScoreboardVisible();
            }
            else
            {
                stateTrace.trace("visible - ignore "+state);
            }
        }
    }

    private void drawTitles(Text text)
    {
        String title = text.getText(titleRange, "");
        String subTitle = text.getText(subTitleRange, "");

        scoreboard1.setTitle(title);
        scoreboard1.setSubTitle(subTitle);
        scoreboard2.setTitle(title);
        scoreboard2.setSubTitle(subTitle);
    }

    private void drawClock(Text text)
    {
        String clock = text.getText(clockRange, "");
        if ("0.0".equals(clock.trim()))
        {
            clock = "";
        }
        scoreboard1.setClock(clock);
        scoreboard2.setClock(clock);
    }

    private void drawLane(Text text, int laneIndex)
    {
        int lineNumber = firstLaneLineNumber + laneIndex;
        String placeRange = config.getCharRange("placeRange", null);
        boolean result = state == RESULTS || state == RESULTS_COMPLETE;
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
        scoreboard1.setLaneValues(laneIndex, lane, place, name, club, time);
        scoreboard2.setLaneValues(laneIndex, lane, place, name, club, time);
    }

    void makeScoreboardVisible()
    {
        scoreboard1.setVisible(true);
        scoreboard2.setVisible(true);
    }

    private void clearScoreboard()
    {
        scoreboard1.clear();
        scoreboard2.clear();
    }

    void setClock(String clock)
    {
        if (showData())
        {
            stateTrace.trace("RaceTimerEvent("+clock.trim()+")");
            scoreboard1.setClock(clock);
            scoreboard2.setClock(clock);
        }
    }

    private synchronized void setState(ScoreboardState state)
    {
        ScoreboardState prevState = this.state;
        if (stateTimerThread != null || !queuedStateData.isEmpty())
        {
            ScoreboardState nextAllowedState = prevState.nextRaceState();
            if (state == nextAllowedState)
            {
                queuedStateData.add(new StateData(state, text));
                StringJoiner q = new StringJoiner(", ", "Queued: ", "");
                queuedStateData.forEach(sd->q.add(sd.getState().toString()));
                stateTrace.trace(q.toString());
            }
            else
            {
                // There has been a break in the expected sequence of queued events, so start again with the live state.
                stateTrace.trace("Unexpected sequence of events. Event queue cleared. Was "+state+" but expected "+nextAllowedState);
                clearStateQueue();
            }
        }
        else
        {
            startStateTimerIfNeeded(state, text);
        }
        this.state = state;
    }

    private synchronized void dequeueNextState()
    {
        stateTimerThread = null;
        while (stateTimerThread == null && !queuedStateData.isEmpty())
        {
            StateData stateData = queuedStateData.remove(0);

            Text text = stateData.getText();
            ScoreboardState state = stateData.getState();
            stateTrace.trace(state+" - removed from queue");
            startStateTimerIfNeeded(state, text);
            if (queuedStateData.isEmpty())
            {
                splitCount = 0;
                drawScoreboard(this.state, splitCount++, text);
            }
        }
    }

    private synchronized void startStateTimerIfNeeded(ScoreboardState state, Text text)
    {
        if (showData())
        {
            if (state == RACE_COMPLETE)
            {
                stateTimerThread = new StateTimerThread(state, displayFinishFor, displayFinishFor)
                {
                    @Override
                    public void tick(int count)
                    {
                        drawScoreboard(state, count, text);
                    }

                    @Override
                    public void end()
                    {
                        stateTrace.trace("RACE_COMPLETE - SWITCH TO RESULTS");
                        dequeueNextState();
                    }
                };

                if (isRaceInProgress())
                {
                    raceTimerThread.terminate();
                    raceTimerThread = null;
                }
            }
            else if (state == RESULTS_COMPLETE)
            {
                drawScoreboard(state, 0, text);
                stateTimerThread = new StateTimerThread(state, 1000, displayResultsFor)
                {
                    @Override
                    public void tick(int count)
                    {
                        drawScoreboard(state, count, text);
                    }

                    @Override
                    public void end()
                    {
                        stateTrace.trace("RESULTS_COMPLETE - SWITCH TO LINEUP IF AVAILABLE");
                        dequeueNextState();
                    }
                };
            }
            else if (state == LINEUP_COMPLETE)
            {
                drawScoreboard(state, 0, text);
                stateTimerThread = new StateTimerThread(state, 1000, displayLineupFor)
                {
                    @Override
                    public void tick(int count)
                    {
                        drawScoreboard(state, count, text);
                    }

                    @Override
                    public void end()
                    {
                        stateTrace.trace("LINEUP_COMPLETE - SWITCH TO THE RACE IF AVAILABLE");
                        dequeueNextState();
                    }
                };
            }
        }
    }

    private void clearStateQueue()
    {
        queuedStateData.clear();
        if (stateTimerThread != null)
        {
            stateTimerThread.terminate();
            stateTimerThread = null;
        }
    }

    private void clearRaceTimer()
    {
        if (raceTimerThread != null)
        {
            raceTimerThread.terminate();
            raceTimerThread = null;
        }
    }

    void close()
    {
        timingEquipmentTrace.close();
    }
}
