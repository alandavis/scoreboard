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
}
