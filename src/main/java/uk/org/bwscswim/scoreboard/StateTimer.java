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
 * A Thread that calls {@link #end()} after {code}runForTime{code} milliseconds and then exits. It also calls
 * {@link #tick(int)} after starting and then every {code}tickTime{code} milliseconds.
 * Used to remove states that have been queued up waiting for the scoreboard minimum display times.
 *
 * @author adavis
 */
class StateTimer extends Thread
{
    public static final String THREAD_NAME_PREFIX = "StateTimer"; // "AWT-EventQueue";
    private final long tickTime;
    private final long end;
    private int count;
    private boolean terminate;

    StateTimer(long tickTime, long runForTime, Sleeper sleeper)
    {
        this.tickTime = sleeper.convert(tickTime);
        end = runForTime == -1 ? Long.MAX_VALUE : System.currentTimeMillis() + sleeper.convert(runForTime);
        setDaemon(true);
        setName(THREAD_NAME_PREFIX);
        start();
    }

    public static boolean isStateTimerThread()
    {
        return Thread.currentThread().getName().startsWith(THREAD_NAME_PREFIX);
    }

    @Override
    public void run()
    {
        try
        {
            for (;;)
            {
                long now = System.currentTimeMillis();
                synchronized (this)
                {
                    if (terminate)
                    {
                        break;
                    }
                    if (now >= end)
                    {
                        end();
                        break;
                    }
                }

                tick(count++);
                if (terminate)
                {
                    break;
                }

                long wakeIn = now + tickTime > end ? end - now : tickTime;
                Thread.sleep(wakeIn);
            }
        }
        catch (InterruptedException ignore)
        {
            // Just exit
        }
    }

    public void tick(int count)
    {
    }

    public void end()
    {
    }

    synchronized void terminate()
    {
        terminate = true;
    }
}
