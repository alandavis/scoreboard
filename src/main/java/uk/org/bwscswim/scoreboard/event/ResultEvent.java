package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;

/**
 * @author adavis
 */
public class ResultEvent extends PageEvent
{
    public ResultEvent(Text text, int count)
    {
        super(text, count);
    }
}

