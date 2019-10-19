package uk.org.bwscswim.scoreboard.event;

/**
 * @author adavis
 */
public class RaceTimerEvent implements ScoreboardEvent
{
    private final String clock;

    public RaceTimerEvent(String clock)
    {
        this.clock =clock;
    }

    public String getClock()
    {
        return clock;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).
                append(" ").append(clock);
        return sb.toString();
    }
}
