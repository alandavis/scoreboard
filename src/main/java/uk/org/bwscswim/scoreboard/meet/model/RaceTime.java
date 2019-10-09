package uk.org.bwscswim.scoreboard.meet.model;


import java.util.Objects;

/**
 * @author adavis
 */
public class RaceTime implements Comparable<RaceTime>
{
    private final String time;

    /**
     * @param time in either 999:88.77 or 9998877 format.
     */
    private RaceTime(String time)
    {
        // Add trailing zeros if not all digits.
        int i = time.indexOf('.');
        int j = time.indexOf(':');
        int l = time.length();
        if (i != -1 || j != -1 || l <= 2)
        {
            if (i == -1 || l <= 2)
            {
                time += ".00";
            }
            else
            {
                int k = l -i;
                if (k == 1)
                {
                    time += "00";
                }
                else if (k == 2)
                {
                    time += "0";
                }
                else
                {
                    time = time.substring(0, i+3);
                }
            }
            i = time.indexOf('.');
        }

        // Strip : and . chars
        if (i != -1)
        {
            time = time.substring(0, i) + time.substring(i + 1);
        }

        if (j != -1)
        {
            while (time.charAt(0) == '0')
            {
                time = time.substring(1);
                j--;
            }
            if (time.charAt(0) == ':')
            {
                time = time.substring(1);
            }
            else
            {
                time = time.substring(0,j)+time.substring(j+1);
            }
        }

        this.time = time;
    }

    public static RaceTime create(String time)
    {
        RaceTime raceTime = null;
        String orig = time;
        if (time != null)
        {
            time = time.trim();
            if (!time.isEmpty())
            {
                int i = -1;
                int j = -1;
                boolean ok = true;
                for (int x = time.length() - 1; x >= 0; x--)
                {
                    char c = time.charAt(x);
                    if (c == ':')
                    {
                        if (j == -1)
                        {
                            j = x;
                        }
                        else
                        {
                            System.err.println("RaceTime "+orig+" has multiple ':' characters");
                            ok = false;
                            break;
                        }
                    }
                    else if (c == '.')
                    {
                        if (i == -1)
                        {
                            i = x;
                        }
                        else
                        {
                            System.err.println("RaceTime "+orig+" has multiple '.' characters");
                            ok = false;
                            break;
                        }
                    }
                    else if ((c < '0' || c > '9'))
                    {
                        System.err.println("RaceTime "+orig+" includes '"+c+"' character");
                        ok = false;
                    }
                }

                if (ok && (i == -1 || j == -1 || i - j == 3))
                {
                    raceTime = new RaceTime((time));
                }
                else if (ok) // another reason
                {
                    System.err.println("RaceTime "+orig+" is invalid.");
                }
            }
        }
        return raceTime;
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

    @Override
    public int compareTo(RaceTime other)
    {
        long time = toTime();
        long otherTime = other.toTime();
        long minus = time-otherTime;
        return minus < 0 ? -1 : minus > 0 ? 1 : 0;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof RaceTime))
        {
            return false;
        }
        return compareTo((RaceTime)o) == 0;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(time);
    }
}
