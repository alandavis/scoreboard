package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;

/**
 * @author adavis
 */
public class TimeOfDayEvent extends PageEvent
{
    public TimeOfDayEvent(Text text, int count)
    {
        super(text, count);
    }
}
