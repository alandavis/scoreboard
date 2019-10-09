package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.model.EventEntry;
import uk.org.bwscswim.scoreboard.meet.model.Swimmer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adavis
 */
public class ResultEvent extends PageEvent
{
    private List<String> improvements = new ArrayList<>();

    public ResultEvent(Text text, int count, List<Event> events)
    {
        super(text, count);
        Event event = lookupEvent(events);
        int laneCount = getLaneCount();
        for (int i = 0; i < laneCount; i++)
        {
            String name = text.getName(i);
            String time = text.getTime(i);

            String improvement = event == null ? "" : event.getImprovement(name, time);
            improvements.add(improvement);
        }
    }

    private Event lookupEvent(List<Event> events)
    {
        int eventNumber = getEventNumber();
        if (eventNumber != -1)
        {
            for (Event event : events)
            {
                if (event.getNumber() == eventNumber)
                {
                    return event;
                }
            }
        }
        return null;
    }

    public String getImprovement(int laneIndex)
    {
        String improvement = improvements.get(laneIndex);
        return isCountyTime(laneIndex) ? improvement.substring(0, improvement.length()-2) : improvement;
    }

    public boolean isCountyTime(int laneIndex)
    {
        String improvement = improvements.get(laneIndex);
        return improvement.endsWith("CT") || improvement.endsWith("ct");
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        String superString = super.toString();
        String[] lines = superString.split("\n");
        int laneCount = getLaneCount();
        for (int i = 0; i < lines.length; i++)
        {
            if (i > 0)
            {
                sb.append("\n");

            }
            sb.append(lines[i]);
            if (i-3 >= 0 && i-3 < laneCount)
            {
                String improvement = getImprovement(i - 3);
                if (!improvement.isEmpty())
                {
                    sb.append("| ").append(improvement);
                }
            }
        }
        return sb.toString();
    }
}

