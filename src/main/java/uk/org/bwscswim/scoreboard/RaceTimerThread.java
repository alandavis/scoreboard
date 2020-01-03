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

import static uk.org.bwscswim.scoreboard.State.RACE_FINISHING;

/**
 * A Thread that make the race clock look like it is running in hundredths of a second rather than tenths for most of
 * the time with splits and results in hundredths. Once started it calls {@link DataReader#setClock(String)} to set
 * the time. When a new time is read the thread resets itself. It also looks keeps the race clock running after splits
 * and after the first competitor finishes. The clock terminates when requested to do so or when it appears the race is
 * over.
 *
 * @author adavis
 */
class RaceTimerThread extends Thread
{
    // Increment on the race timer by 0.075 seconds looks good when displayed to 2 decimal places
    public static final int WAIT = 75;

    // The race winner has finished if there has not been a clock for more than 2.1 seconds (add an extra 0.4 in case it is slow)
    private static final int FINISHED_IF_NO_CLOCK = 2500;

    private final DataReader dataReader;
    private final Sleeper sleeper;
    private final long wakeIn;
    private final long finishedIfNoClock;

    private boolean terminate;
    private long lastClock;
    private long timeZero;

    RaceTimerThread(DataReader dataReader, String clock, Sleeper sleeper)
    {
        this.dataReader = dataReader;
        this.sleeper = sleeper;
        wakeIn = sleeper.convert(WAIT);
        finishedIfNoClock = sleeper.convert(FINISHED_IF_NO_CLOCK);

        resetClock(clock);
        setDaemon(true);
        setName("RaceTimerThread");
        start();
    }

    @Override
    public synchronized void run()
    {
        boolean winnerFinished = false;
        long now = System.currentTimeMillis();
        try
        {
            for(;;)
            {
                wait(wakeIn);
                now = System.currentTimeMillis();

                long lastClockAge = now - lastClock;
                int timeNow = (int)(sleeper.convertBack(now - timeZero) /10)*10;
//                System.err.println("------ wake  lastClock="+lastClock+" timeZero="+timeZero+" lastClockAge="+lastClockAge+" finishedIfNoClock="+finishedIfNoClock+" timeNow="+timeNow);
                if (!winnerFinished && lastClockAge > finishedIfNoClock)
                {
//                    System.err.println("------ FINISHED");
                    dataReader.setState(RACE_FINISHING);
                    winnerFinished = true;
                }

                if (terminate || !dataReader.isRaceInProgress())
                {
                    dataReader.setClock("");
                    break;
                }
                setClock(timeNow);
            }
        }
        catch (InterruptedException ignore)
        {
            // Just exit
        }
    }

    void resetClock(String clock)
    {
        clock = clock.trim();
        int i = clock.indexOf(':');
        int j = clock.indexOf('.');
        int mins = i == -1 ? 0 : Integer.parseInt(clock.substring(0, i));
        int secs = Integer.parseInt(clock.substring(i == -1 ? 0 : i+1, j));
        int hunds = Integer.parseInt(clock.substring(j+1)+(clock.length()-2 == j ? "0" : ""));
        long time = sleeper.convert((mins*60+secs)*1000 + hunds*10);
        lastClock = System.currentTimeMillis();
        synchronized(this)
        {
            lastClock = System.currentTimeMillis();
            timeZero = lastClock - time;
//            System.err.println("------ reset lastClock="+lastClock+" timeZero="+timeZero+" clock="+clock+" time="+time+" convert="+((int)(sleeper.convertBack(lastClock - timeZero) /10)*10));
        }
    }

    private void setClock(int timeNow)
    {
        int hunds = (timeNow % 1000)/10;
        int secs = timeNow/1000;
        int mins = secs / 60;
        secs = secs % 60;
        String clock = "    "+
            (mins == 0 ? "" : Integer.toString(mins)+':')+
            (mins > 0 && secs <= 9 ? "0" : "")+secs+'.'+
            (hunds <= 9 ? "0" : "")+hunds;
        clock = clock.substring(clock.length()-8);
        dataReader.setClock(clock);
    }

    synchronized void terminate()
    {
//        System.err.println("------ TERMINATE "+System.currentTimeMillis());
        terminate = true;
    }
}
