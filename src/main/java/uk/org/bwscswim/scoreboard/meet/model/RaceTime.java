package uk.org.bwscswim.scoreboard.meet.model;


/**
 * @author adavis
 */
public class RaceTime
{
    private final String time;

    /**
     * @param time in either 999:88.77 or 9998877 format.
     */
    public RaceTime(String time)
    {
        // Strip : and . chars
        int i = time.indexOf(':');
        if (i != -1)
        {
            time = time.substring(0,i)+time.substring(i+1);
        }

        i = time.indexOf('.');
        if (i != -1)
        {
            time = time.substring(0, i) + time.substring(i + 1) + (i == time.length()-2 ? "0" : "");
        }
        this.time = time;
    }

    public String toDigits()
    {
        return time;
    }

    public String toString()
    {
        int size = time.length();
        return (size <= 4 ? "" : time.substring(0, size-4)+':')+
               (size == 3 ? time.substring(0, 1) : time.substring(size-4, size-2))+'.'+
               time.substring(size-2);
    }

    public String minus(RaceTime other)
    {
        long time = toTime();
        long otherTime = other.toTime();
        long minus = time-otherTime;
        String minusString = fromTime(minus);
        return minusString;
    }

    private long toTime()
    {
        int size = time.length();
        return (size <= 4 ? 0 : Integer.parseInt(time.substring(0, size-4))*60000)+
                Integer.parseInt(size == 3 ? time.substring(0, 1) : time.substring(size-4, size-2))*1000+
                Integer.parseInt(time.substring(size-2))*10;
    }

    private String fromTime(long time)
    {
        StringBuilder sb = new StringBuilder();
        if (time < 0)
        {
            sb.append('-');
            time = time*-1;
        }

        long mins = time / 60000;
        long secs = (time % 60000) / 1000;
        long hunds = (time % 1000) / 10;

        if (mins > 0)
        {
            sb.append(mins).append(':');
            if (secs <= 9)
            {
                sb.append('0');
            }
        }

        sb.append(secs).append('.');

        if (hunds <= 9)
        {
            sb.append('0');
        }
        sb.append(hunds);

        return sb.toString();
    }
}
