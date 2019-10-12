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
}
