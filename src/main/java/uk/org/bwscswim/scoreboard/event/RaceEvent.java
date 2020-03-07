package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;

/**
 *
 *
 * @author adavis
 */
public class RaceEvent extends PageEvent
{
    public RaceEvent(Text text, int count, boolean abrTitle)
    {
        super(text, count, abrTitle);
    }
}
