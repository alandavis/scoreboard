package uk.org.bwscswim.scoreboard.event;

/**
 * @author adavis
 */
public class TestcardEvent implements ScoreboardEvent
{
    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
}
