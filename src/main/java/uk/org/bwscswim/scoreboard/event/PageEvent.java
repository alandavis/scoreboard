package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;

/**
 * Base event for display of a screen full of information.
 *
 * @author adavis
 */
public class PageEvent
{
    private final Text text;
    private final int count;

    public PageEvent(Text text, int count)
    {
        this.text = text;
        this.count = count;
    }

    /**
     * Returns the number of time the event has been issued for this state. Starts at 0 and increments. Resets when
     * the state changes. There may be more than one if the state is held for a minimum amount of time and has a 'tick'
     * sub event.
     */
    public int getCount()
    {
        return count;
    }

    public String getTitle()
    {
        return text.getTitle();
    }

    public String getSubtitle()
    {
        return text.getSubtitle();
    }

    public String getClock()
    {
        return text.getNonZeroClock();
    }

    public int getLaneCount()
    {
        return text.getLaneCount();
    }

    int getLaneIndex(int lineNumber)
    {
        return text.getLaneIndex(lineNumber);
    }

    public int getLane(int laneIndex)
    {
        return text.getLane(laneIndex);
    }

    public String getName(int laneIndex)
    {
        return text.getName(laneIndex);
    }

    public String getClub(int laneIndex)
    {
        return text.getClub(laneIndex);
    }

    public String getTime(int laneIndex)
    {
        return text.getTime(laneIndex);
    }

    public int getPlace(int laneIndex)
    {
        return text.getPlace(laneIndex);
    }

    protected String toStringLine1Suffix()
    {
        return "";
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).
                append(" ").append(count).
                append(toStringLine1Suffix()).
                append("\n").
                append(text);
        return sb.toString();
    }
}