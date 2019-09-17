package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;

/**
 * @author adavis
 */
public class LineupEvent extends PageEvent
{
    public LineupEvent(Text text, int count)
    {
        super(text, count);
    }
}
