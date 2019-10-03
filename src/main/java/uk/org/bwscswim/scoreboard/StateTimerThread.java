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
class StateTimerThread extends Thread
{
    private final long tickTime;
    private final long end;
    private int count;
    private boolean terminate;

    StateTimerThread(uk.org.bwscswim.scoreboard.State state, long tickTime, long runForTime)
    {
        this.tickTime = tickTime;
        end = runForTime == -1 ? Long.MAX_VALUE : System.currentTimeMillis() + runForTime;
        setDaemon(true);
        setName("StateTimerThread "+state);
        start();
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
                    if (now >= end || terminate)
                    {
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
                if (terminate)
                {
                    break;
                }
            }
            if (!terminate)
            {
                end();
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
