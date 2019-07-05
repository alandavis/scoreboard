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
                if (now >= end)
                {
                    break;
                }

                tick(count++);

                long wakeIn = now + tickTime > end ? end - now : tickTime;
//              System.out.println(state + " wakeIn=" + wakeIn);
                Thread.sleep(wakeIn);
            }
            end();
        }
        catch (InterruptedException ignoreAndJustExist)
        {
        }
    }

    public void tick(int count)
    {
        System.out.println(state+" tick("+count+")");
    }

    public void end()
    {
        System.out.println(state+" end()");
    }
}
