/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2020 Bracknell and Wokingham Swimming Club (BWSC)
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
import uk.org.bwscswim.scoreboard.event.EventPublisher;
import uk.org.bwscswim.scoreboard.event.LineupEvent;
import uk.org.bwscswim.scoreboard.event.Observer;
import uk.org.bwscswim.scoreboard.event.PageEvent;
import uk.org.bwscswim.scoreboard.event.RaceEvent;
import uk.org.bwscswim.scoreboard.event.RaceSplitTimeEvent;
import uk.org.bwscswim.scoreboard.event.RaceTimerEvent;
import uk.org.bwscswim.scoreboard.event.RawTextEvent;
import uk.org.bwscswim.scoreboard.event.ResultEvent;
import uk.org.bwscswim.scoreboard.event.ScoreboardEvent;
import uk.org.bwscswim.scoreboard.event.TestcardEvent;
import uk.org.bwscswim.scoreboard.event.TimeOfDayEvent;
import uk.org.bwscswim.scoreboard.meet.model.Event;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;
import static uk.org.bwscswim.scoreboard.RawTrace.format;
import static uk.org.bwscswim.scoreboard.State.LINEUP;
import static uk.org.bwscswim.scoreboard.State.LINEUP_COMPLETE;
import static uk.org.bwscswim.scoreboard.State.RACE;
import static uk.org.bwscswim.scoreboard.State.RACE_COMPLETE;
import static uk.org.bwscswim.scoreboard.State.RACE_FINISHING;
import static uk.org.bwscswim.scoreboard.State.RESULTS;
import static uk.org.bwscswim.scoreboard.State.RESULTS_COMPLETE;
import static uk.org.bwscswim.scoreboard.State.TIME_OF_DAY;

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

    private final Config config;

    private final RawTrace rawTrace;
    private final StateTrace stateTrace;

    private final long displayFinishFor;
    private final long displayResultsFor;
    private final long displayLineupFor;

    private boolean trace = true;
    private InputStream inputStream;
    private Text text;
    private RaceTimerThread raceTimerThread;

    private int lanesWithTimesAtTheEndOfTheRace;

    private List<Text> queuedStateData = new ArrayList<>();
    private StateTimer stateTimer;
    private List<Event> events;
    private EventPublisher eventPublisher;
    private Sleeper sleeper;

    private final List<SerialPort> commPorts;
    private int nextPortIndex;
    private int readTimeout = 0;
    private final boolean tryNextPortOnZero;
    private final boolean waitBetweenConnects;

    DataReader(Config config)
    {
        this.config = config;

        text = new Text(config);
        CONTROL_CLOCK = CONTROL_LINE_SUFFIX+text.getClockFromRange();

        displayFinishFor = config.getInt("displayFinishFor", 3000);
        displayResultsFor = config.getInt("displayResultsFor", 12000);
        displayLineupFor = config.getInt("displayLineupFor", 6000);

        setTrace(config.getBoolean("trace", true));

        tryNextPortOnZero = config.getBoolean("tryNextPortOnZero", true);
        waitBetweenConnects = config.getBoolean("waitBetweenConnects", false);

        sleeper= new Sleeper();
        stateTrace = new StateTrace();
        stateTrace.setSleeper(sleeper);
        eventPublisher = new EventPublisher();
        eventPublisher.setStateTrace(stateTrace);
        rawTrace = new RawTrace(config, stateTrace);
        commPorts = obtainSerialPorts();
    }

    public EventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    /**
     * @return a list of all ports to be tried. If the port has been specified as a config value, only that one is
     * returned. Also lists the ports known to the system.
     */
    private List<SerialPort> obtainSerialPorts()
    {
        List<SerialPort> commPorts = Arrays.asList(SerialPort.getCommPorts());
        System.out.println("Available serial ports:");
        for (SerialPort commPort : commPorts)
        {
            String systemPortName = commPort.getSystemPortName();
            System.out.println("    " + systemPortName);
        }

        String port = config.getString("port", null);
        if (port != null)
        {
            SerialPort commPort = SerialPort.getCommPort(port);
            commPorts = new ArrayList<>();
            commPorts.add(commPort);
        }
        else if (tryNextPortOnZero)
        {
            readTimeout = 100; // 1/10 of a second
        }
        return commPorts;
    }

    public void addObserver(Observer observer)
    {
        eventPublisher.addObserver(observer);
    }

    public void setEvents(List<Event> events)
    {
        this.events = events;
    }

    void setTrace(boolean trace)
    {
        this.trace = trace;
    }

    void readDataInBackground()
    {
        Thread t = new Thread(() ->
        {
            showTestCard();
            showTimeOfDay();
            String testFilename = config.getString("testFilename", null);
            if (testFilename != null && !testFilename.isEmpty())
            {
                do
                {
                    try
                    {
                        float speedFactor = config.getFloat("speedFactor", 1);
                        DummyInputStream inputStream = new DummyInputStream(testFilename, config);
                        readInputStream(inputStream, speedFactor);
                    }
                    catch (InterruptedException ignore)
                    {
                    }
                    catch (FileNotFoundException e)
                    {
                        System.err.println("The test file " + testFilename + " could not be found.");
                        System.exit(-1);
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
                        SerialPort port = getPort();
                        if (port.openPort())
                        {
                            System.out.println("Using " + toString(port));
                            try
                            {
                                InputStream inputStream = port.getInputStream();
                                readInputStream(inputStream, 1);
                            }
                            finally
                            {
                                port.closePort();
                            }
                        }
                        if (waitBetweenConnects)
                        {
                            sleep(250);
                        }
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

    SerialPort getPort()
    {
        SerialPort commPort = commPorts.get(nextPortIndex);
        if (++nextPortIndex >= commPorts.size())
        {
            nextPortIndex = 0;
        }

        commPort.setBaudRate(config.getInt("baudRate", 19200));
        commPort.setNumDataBits(config.getInt("numDataBits", 8));
        commPort.setNumStopBits(config.getInt("numStopBits", 1));
        commPort.setParity(config.getInt("parity", SerialPort.NO_PARITY)); // 0
        commPort.setFlowControl(config.getInt("flowControl", SerialPort.FLOW_CONTROL_DISABLED)); // 0
        commPort.setComPortTimeouts(
                config.getInt("timeoutMode", SerialPort.TIMEOUT_READ_BLOCKING), // 2
                config.getInt("readTimeout", readTimeout),
                config.getInt("writeTimeout", 0));
        return commPort;
    }

    private String toString(SerialPort commPort)
    {
        return "port="+commPort.getSystemPortName()+
                " baudRate="+commPort.getBaudRate()+
                " numDataBits="+commPort.getNumDataBits()+
                " numStopBits="+commPort.getNumStopBits()+
                " parity="+commPort.getParity()+
                " flowControl="+commPort.getFlowControlSettings()+
                " readTimeout="+commPort.getReadTimeout()+
                " writeTimeout="+commPort.getWriteTimeout();
    }

    private void showTestCard()
    {
        long showTestCardFor = config.getInt("showTestCardFor", 0);
        if (showTestCardFor > 0)
        {
            TestcardEvent event = new TestcardEvent();
            publishEvent(event);
            try
            {
                sleep(showTestCardFor);
            }
            catch (InterruptedException ignore)
            {
            }
        }
    }

    void readInputStream(InputStream inputStream, float speedFactor) throws InterruptedException
    {
        this.inputStream = inputStream;
        sleeper.setSpeedFactor(speedFactor);
        if (inputStream instanceof DummyInputStream)
        {
            ((DummyInputStream)inputStream).setSleeper(sleeper);
        }
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
                synchronized (this)
                {
                    this.inputStream = null;
                }
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
            rawTrace.reset();
            throw new EOFException("End of data from timing equipment");
        }
        if (b == 0 && tryNextPortOnZero) // Port appears not to be connected
        {
            rawTrace.reset();
            throw new EOFException("Port appears not to be connected");
        }

        if (trace)
        {
            rawTrace.trace(b);
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
        State state = text.getState();
        if (control.startsWith(CONTROL_LINE_SUFFIX))
        {
            int position = getPosition(control);
            int lineNumber = position / 100;
            int offset = position % 100;
            text.setText(lineNumber, offset, data);
            publishEvent(new RawTextEvent(lineNumber, offset, data));

            if (control.equals(CONTROL_CLOCK))
            {
                String clock = text.getClock();
                if (state == LINEUP) // clock is probably 0.0
                {
                    setState(LINEUP_COMPLETE);
                }
                else if (state == LINEUP_COMPLETE) // clock is probably 0.1
                {
                    if (!"0.0".equals(clock.trim())) // Think we are getting another 0.0 sometimes with test runs on data system
                    {
                        setState(RACE);
                        raceTimerThread = new RaceTimerThread(this, clock, sleeper);
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
                    // Ignore
                }
            }
            else if (state == RESULTS && text.isLaneLineNumber(lineNumber) &&
                     lanesWithTimesAtTheEndOfTheRace == text.countLanesWithTimes())
            {
                setState(RESULTS_COMPLETE);
            }
            else if ((state == RACE || state == RACE_FINISHING) && text.isLaneLineNumber(lineNumber))
            {
                // Set or clear split/finish time for swimmer if currently visible
                updateScoreboard(state, text.getSplitCountAndIncrement(), text, lineNumber);

                if (state == RACE_FINISHING && text.countLanesWithNames() == text.countLanesWithTimes())
                {
                    // TODO don't call setState(RACE_COMPLETE); here. Allow the raceTimer to do it
                    setState(RACE_COMPLETE);
                    clearRaceTimer();
                }
            }
        }
        else if (CONTROL_CLEAR.equals(control))
        {
            lanesWithTimesAtTheEndOfTheRace = text.countLanesWithTimes();
            text.clear();
            publishEvent(new RawTextEvent());
        }
        else if (CONTROL_LINEUP.equals(control))
        {
            setState(LINEUP);
        }
        else if (CONTROL_RESULTS.equals(control))
        {
            clearRaceTimer();
            setState(RESULTS);
        }
        else if (CONTROL_TIME_OF_DAY.equals(control))
        {
            // ignore as we do our own
        }
    }

    private boolean showData()
    {
        // We hold off showing data if we need to display the race finish, results or lineup a bit longer.
        return stateTimer == null || StateTimer.isStateTimerThread();
    }

    boolean isRaceInProgress()
    {
        State state = text.getState();
        return state == RACE || state == RACE_FINISHING;
    }

    private void updateScoreboard(State state, int count, Text text, int lineNumberWithSplitTime)
    {
        if (showData())
        {
            PageEvent event =
                      state == TIME_OF_DAY ? new TimeOfDayEvent(count)
                    : state == LINEUP_COMPLETE ? new LineupEvent(text, count)
                    : state == RACE || state == RACE_FINISHING || state == RACE_COMPLETE
                              ? lineNumberWithSplitTime == -1
                                ? new RaceEvent(text, count)
                                : new RaceSplitTimeEvent(text, count, lineNumberWithSplitTime)
                    : new   ResultEvent(text, count, events);
            publishEvent(event);
        }
    }

    void setClock(String clock)
    {
        if (showData())
        {
            RaceTimerEvent event = new RaceTimerEvent(clock);
            publishEvent(event);
        }
    }

    protected void publishEvent(ScoreboardEvent event)
    {
        eventPublisher.publishEvent(event);
    }

    synchronized void setState(State state)
    {
        if (state.isQueueable())
        {
            handleOrQueueState(state);
        }
        text.setState(state);
    }

    private void handleOrQueueState(State state)
    {
        boolean emptyQueue = queuedStateData.isEmpty();
        if (stateTimer != null || !emptyQueue)
        {
            State nextState = emptyQueue ? null : queuedStateData.get(queuedStateData.size() - 1).getState().nextQueueableState();
            if (emptyQueue || state == nextState)
            {
                queuedStateData.add(new Text(text, state));
                stateTrace.trace(state+" added to queue: ", queuedStateData);
            }
            else
            {
                // There has been a break in the expected sequence of queued events, so start again with the live state.
                stateTrace.trace("Unexpected sequence of events. Was "+state+" but expected "+nextState+". Clearing queue: ", queuedStateData);
                clearRaceTimer();
                clearStateQueue();
            }
        }
        else
        {
            stateTrace.trace(state+" live state");
            Text text = new Text(this.text, state);
            startStateTimerIfNeeded(state, text);
        }
    }

    private synchronized void dequeueState()
    {
        stateTimer = null;
        while (stateTimer == null && !queuedStateData.isEmpty())
        {
            Text queuedText = queuedStateData.remove(0);

            State state = queuedText.getState();
            stateTrace.trace(state+" removed from queue: ", queuedStateData);
            startStateTimerIfNeeded(state, queuedText);
            if (queuedStateData.isEmpty() && (state == RACE || state == RACE_FINISHING))
            {
                state = this.text.getState();
                updateScoreboard(state, 0, this.text, -1);
            }
        }
        State state = this.text.getState();
        if (state == RESULTS_COMPLETE && stateTimer == null && inputStream != null)
        {
            showTimeOfDay();
        }
    }

    // Used in testing only
    synchronized void waitForFinish() throws InterruptedException
    {
        while (stateTimer != null || !queuedStateData.isEmpty())
        {
//            System.out.println("stateTimer is "+(stateTimer == null ? "" : "NOT ")+"null   "+
//                    "queue is "+(queuedStateData.isEmpty() ? "" : "NOT ")+"empty  "+
//                    "inputStream is "+(inputStream == null ? "closed" : "open"));

            wait(1000);
        }

        // Give the race timer a chance to include the last clock reset and end of race
         long wakeIn = sleeper.convert(3000);
        wait(wakeIn);
        clearRaceTimer();
    }

    private void showTimeOfDay()
    {
        Text text = new Text(this.text, TIME_OF_DAY);
        text.clearLanes();
        startStateTimerIfNeeded(TIME_OF_DAY, text);
    }

    private synchronized void startStateTimerIfNeeded(State state, Text text)
    {
        if (showData())
        {
            if (state == RACE_COMPLETE)
            {
                if (queuedStateData.isEmpty()) // don't hold the race display if we are backed up.
                {
                    stateTimer = new StateTimer(1000, displayFinishFor, sleeper)
                    {
                        @Override
                        public void tick(int count)
                        {
                            updateScoreboard(state, count, text, -1);
                        }

                        @Override
                        public void end()
                        {
                            stateTrace.trace("RACE_COMPLETE - SWITCH TO RESULTS");
                            dequeueState();
                        }
                    };
                }
                else
                {
                    stateTrace.trace("RACE_COMPLETE ignored as we have a queue of events and the race will " +
                            "visually just switch to RESULTS_COMPLETE from the LINEUP_COMPLETE");
                }
            }
            else if (state == RESULTS_COMPLETE)
            {
                stateTimer = new StateTimer(1000, displayResultsFor, sleeper)
                {
                    @Override
                    public void tick(int count)
                    {
                        updateScoreboard(state, count, text, -1);
                    }

                    @Override
                    public void end()
                    {
                        stateTrace.trace("RESULTS_COMPLETE - SWITCH TO LINEUP IF AVAILABLE");
                        dequeueState();
                    }
                };
            }
            else if (state == LINEUP_COMPLETE)
            {
                stateTimer = new StateTimer(1000, displayLineupFor, sleeper)
                {
                    @Override
                    public void tick(int count)
                    {
                        updateScoreboard(state, count, text, -1);
                    }

                    @Override
                    public void end()
                    {
                        stateTrace.trace("LINEUP_COMPLETE - SWITCH TO THE RACE IF AVAILABLE");
                        dequeueState();
                    }
                };
            }
            else if (state == TIME_OF_DAY)
            {
                stateTimer = new StateTimer(1000, -1, sleeper)
                {
                    @Override
                    public void tick(int count)
                    {
                        boolean emptyQueue;
                        synchronized(this)
                        {
                            emptyQueue = queuedStateData.isEmpty();
                        }
                        if (emptyQueue)
                        {
                            updateScoreboard(state, count, text, -1);
                        }
                        else
                        {
                            terminate();
                            stateTrace.trace("TIME_OF_DAY - ENDS");
                            dequeueState();
                        }
                    }
                };
            }
        }
    }

    private void clearStateQueue()
    {
        queuedStateData.clear();
        if (stateTimer != null)
        {
            stateTimer.terminate();
            try
            {
                stateTimer.join();
            }
            catch (InterruptedException ignore)
            {
            }
            stateTimer = null;
        }
    }

    private void clearRaceTimer()
    {
        if (raceTimerThread != null)
        {
            raceTimerThread.terminate();
            try
            {
                raceTimerThread.join();
            }
            catch (InterruptedException ignore)
            {
            }
            raceTimerThread = null;
        }
    }

    void close()
    {
        rawTrace.close();
    }
}
