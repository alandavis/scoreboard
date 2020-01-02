package uk.org.bwscswim.scoreboard.event;

/**
 * @author adavis
 */
public class RaceTimerEvent implements ScoreboardEvent
{
    private static int counter;

    private final String clock;
    private final int count;

    public RaceTimerEvent(String clock)
    {
        this.clock =clock;
        count = counter++;
        if (clock.trim().isEmpty())
        {
            counter = 0;
        }
    }

    public String getClock()
    {
        return clock;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).
                append(" ").append(count).append(" ").append(clock);
        return sb.toString();
    }
}
