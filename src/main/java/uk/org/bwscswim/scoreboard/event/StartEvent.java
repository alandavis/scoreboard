package uk.org.bwscswim.scoreboard.event;

/**
 * @author adavis
 */
public class StartEvent implements ScoreboardEvent
{
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
}
