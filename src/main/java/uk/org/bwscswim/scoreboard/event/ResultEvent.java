package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.model.Improvement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adavis
 */
public class ResultEvent extends PageEvent
{
    private List<Improvement> improvements = new ArrayList<>();

    public ResultEvent(Text text, int count, List<Event> events, boolean abrTitle)
    {
        super(text, count, abrTitle);
        Event event = lookupEvent(events);
        int laneCount = getLaneCount();
        for (int i = 0; i < laneCount; i++)
        {
            String name = text.getName(i);
            String time = text.getTime(i);

            Improvement improvement = event == null ? new Improvement() : event.getImprovement(name, time);
            improvements.add(improvement);
        }
    }

    private Event lookupEvent(List<Event> events)
    {
        int eventNumber = getEventNumber();
        if (eventNumber != -1 && events != null)
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

    public Improvement getImprovement(int laneIndex)
    {
        return improvements.get(laneIndex);
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
                Improvement improvement = getImprovement(i - 3);
                if (!improvement.isBlank())
                {
                    sb.append("| ").append(improvement);
                }
            }
        }
        return sb.toString();
    }
}

