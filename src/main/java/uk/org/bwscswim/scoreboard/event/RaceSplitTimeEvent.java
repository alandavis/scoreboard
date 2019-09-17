package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;

/**
 * @author adavis
 */
public class RaceSplitTimeEvent extends RaceEvent
{
    private final int lineNumberWithSplitTime;

    public RaceSplitTimeEvent(Text text, int count, int lineNumberWithSplitTime)
    {
        super(text, count);
        this.lineNumberWithSplitTime = lineNumberWithSplitTime;
    }

    /**
     * When the race is running there may be a number of RaceEvents and these will include setting and clearing of
     * split or final times. Although this Event can return the complete set of data for the scoreboard, this method
     * allows the scoreboard to optimise this by returning the index of the single lane that has changed. {code}-1{code}
     * is returned if the event was not for a split or final time.
     */
    public int getIndexOfLaneWithSplitTime()
    {
        return getLaneIndex(lineNumberWithSplitTime);
    }

    @Override
    protected String toStringLine1Suffix()
    {
        return lineNumberWithSplitTime == -1 ? "" : " laneIndex "+getIndexOfLaneWithSplitTime();
    }
}
