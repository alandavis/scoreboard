package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adavis
 */
public class ClubEvent implements ScoreboardEvent
{
    private Text text;

    public ClubEvent(Text text, String title,
                     String name1, String score1, String place1,
                     String name2, String score2, String place2,
                     String name3, String score3, String place3,
                     String name4, String score4, String place4,
                     String name5, String score5, String place5,
                     String name6, String score6, String place6)
    {
        this.text = text;
        text.clear();
        setTitle(title);
        setLaneNameScorePlace(0, name1, score1, place1);
        setLaneNameScorePlace(1, name2, score2, place2);
        setLaneNameScorePlace(2, name3, score3, place3);
        setLaneNameScorePlace(3, name4, score4, place4);
        setLaneNameScorePlace(4, name5, score5, place5);
        setLaneNameScorePlace(5, name6, score6, place6);
        this.text = new Text(text);
    }

    public List<RawTextEvent> getRawTextEvents()
    {
        List<RawTextEvent> events = new ArrayList<>();

        events.add(new RawTextEvent());
        events.add(new RawTextEvent(0, 0, text.getText(0, "")));

        int lineNumber = text.getLineNumber(0);
        for (int laneIndex = 0; laneIndex < 6; laneIndex++, lineNumber++)
        {
            events.add(new RawTextEvent(lineNumber, 0, text.getText(lineNumber, "")));
        }

        return events;
    }

    public String getTitle()
    {
        return text.getTitle();
    }

    private void setTitle(String title)
    {
        text.setTitle(title);
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

    public String getScore(int laneIndex)
    {
        return text.getScore(laneIndex);
    }

    public int getPlace(int laneIndex)
    {
        return text.getPlace(laneIndex);
    }

    private void setLaneNameScorePlace(int laneIndex, String name, String score, String place)
    {
        text.setLane(laneIndex);
        text.setName(laneIndex, name);
        text.setScore(laneIndex, score);
        text.setPlace(laneIndex, place);
    }

    protected String toStringLine1Suffix()
    {
        return "";
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).
                append(toStringLine1Suffix()).
                append("\n").
                append(text);
        return sb.toString();
    }
}
