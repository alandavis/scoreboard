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
    private final DataReader dataReader;
    private final StateTrace stateTrace;
    private boolean terminate;
    private long lastClock;
    private long timeZero;

    RaceTimerThread(DataReader dataReader, String clock, StateTrace stateTrace)
    {
        this.dataReader = dataReader;
        this.stateTrace = stateTrace;

        resetClock(clock);
        setDaemon(true);
        setName("RaceTimerThread");
        start();
    }

    @Override
    public void run()
    {
//        stateTrace.trace("raceTimerThread starts");
        boolean winnerFinished = false;
        try
        {
            long now = System.currentTimeMillis();
            for(;;)
            {
                long wakeIn = 75 - (now % 75);
//                stateTrace.trace("  wakeIn="+wakeIn);
                Thread.sleep(wakeIn);
                now = System.currentTimeMillis();
                int timeNow;
                synchronized (this)
                {
                    // The race winner has finished if there has not been a clock for more than 2.1 seconds (add a buffer of 0.4)
                    long lastClockAge = now - lastClock;
                    if (!winnerFinished && lastClockAge > 2500)
                    {
//                        stateTrace.trace("raceTimerThread setRaceFinishing");
                        dataReader.setRaceFinishing();
                        winnerFinished = true;
                    }

                    if (terminate || !dataReader.isRaceInProgress())
                    {
//                        stateTrace.trace("terminate:"+terminate+" isRaceInProgress="+dataReader.isRaceInProgress());
                        dataReader.setClock("");
                        break;
                    }
                    timeNow = (int)((now - timeZero)/10)*10;
                }
                setClock(timeNow);
            }
        }
        catch (InterruptedException ignore)
        {
            // Just exit
        }
//        stateTrace.trace("raceTimerThread exits");
    }

    void resetClock(String clock)
    {
        clock = clock.trim();
        int i = clock.indexOf(':');
        int j = clock.indexOf('.');
        int mins = i == -1 ? 0 : Integer.parseInt(clock.substring(0, i));
        int secs = Integer.parseInt(clock.substring(i == -1 ? 0 : i+1, j));
        int hunds = Integer.parseInt(clock.substring(j+1)+(clock.length()-2 == j ? "0" : ""));
        int time = (mins*60+secs)*1000 + hunds*10;
        lastClock = System.currentTimeMillis();
        synchronized(this)
        {
            timeZero = lastClock - time;
        }
//        stateTrace.trace("resetClock "+clock);
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
//        stateTrace.trace("setClock "+clock.trim());
        dataReader.setClock(clock);
        dataReader.makeScoreboardVisible();
    }

    synchronized void terminate()
    {
        terminate = true;
    }
}
