package uk.org.bwscswim.scoreboard.event;

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

    public TimeOfDayEvent(int count)
    {
        super(null, count);

        timeOfDay = dateFormat.format(new Date());
    }

    public String getTimeOfDay()
    {
        return timeOfDay;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).
                append(" ").append(count).append(" ").append(timeOfDay);
        return sb.toString();
    }
}
