package uk.org.bwscswim.scoreboard;

/**
 * @author adavis
 */
public class StateTimerThread extends Thread
{
    private final DataReader dataReader;
    private final ScoreboardState state;
    private final long tickTime;
    private final long end;
    private int count;
    private boolean terminate;

    public StateTimerThread(DataReader dataReader, ScoreboardState state, long start, long runForTime)
    {
        this(dataReader, state, start, runForTime, runForTime);
    }

    public StateTimerThread(DataReader dataReader, ScoreboardState state, long start, long tickTime, long runForTime)
    {
        this.dataReader = dataReader;
        this.state = state;
        this.tickTime = tickTime;
        end = start + runForTime;
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

                long wakeIn = now + tickTime > end ? end - now : tickTime;
//              System.out.println(state + " wakeIn=" + wakeIn);
                Thread.sleep(wakeIn);
            }
            end();
            dataReader.dequeueNextState();
        }
        catch (InterruptedException ignoreAndJustExist)
        {
        }
    }

    public void tick(int count)
    {
    }

    public void end()
    {
    }

    public synchronized void terminate()
    {
        terminate = true;
    }
}
