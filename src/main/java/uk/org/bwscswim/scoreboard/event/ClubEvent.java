package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adavis
 */
public class ClubEvent implements ScoreboardEvent
{
    private static class Lane
    {
        final String name;
        final String score;
        final int place;

        Lane(String name, String score, String place)
        {
            this.name = name;
            this.score = score;
            this.place = score.isEmpty() || place.isEmpty() ? 0 : Integer.parseInt(place);
        }
    }

    private final String title;
    private final List<Lane> lanes = new ArrayList<>();

    public ClubEvent(String title,
                     String name1, String score1, String place1,
                     String name2, String score2, String place2,
                     String name3, String score3, String place3,
                     String name4, String score4, String place4,
                     String name5, String score5, String place5,
                     String name6, String score6, String place6)
    {
        this.title = title;
        lanes.add(new Lane(name1, score1, place1));
        lanes.add(new Lane(name2, score2, place2));
        lanes.add(new Lane(name3, score3, place3));
        lanes.add(new Lane(name4, score4, place4));
        lanes.add(new Lane(name5, score5, place5));
        lanes.add(new Lane(name6, score6, place6));
    }

    public String getTitle()
    {
        return title;
    }

    public String getName(int laneIndex)
    {
        return lanes.get(laneIndex).name;
    }

    public String getScore(int laneIndex)
    {
        return lanes.get(laneIndex).score;
    }

    public int getPlace(int laneIndex)
    {
        return lanes.get(laneIndex).place;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("\n  ").append(title);
        lanes.forEach(lane->sb.append("\n    ").append(lane.name).append(' ').append(lane.score).append(' ').append(lane.place));
        return sb.toString();
    }
}
