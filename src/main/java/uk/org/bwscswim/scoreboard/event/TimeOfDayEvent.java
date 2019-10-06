package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author adavis
 */
public class TimeOfDayEvent extends PageEvent
{
    private static DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");

    private String timeOfDay;

    public TimeOfDayEvent(Text text, int count)
    {
        super(text, count);

        timeOfDay = dateFormat.format(new Date());
    }

    public String getTimeOfDay()
    {
        return timeOfDay;
    }
}
