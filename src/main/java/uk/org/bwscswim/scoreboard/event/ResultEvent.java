package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.model.EventEntry;
import uk.org.bwscswim.scoreboard.meet.model.RaceTime;
import uk.org.bwscswim.scoreboard.meet.model.Swimmer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adavis
 */
public class ResultEvent extends PageEvent
{
    private class Improvement
    {
        String improvement = "";
//        boolean countyTime;
//        boolean regionalTime;
    }

    private List<Improvement> improvements = new ArrayList<>();

    public ResultEvent(Text text, int count, List<Event> events)
    {
        super(text, count);
        Event event = lookupEvent(events);
        int laneCount = getLaneCount();
        for (int i = 0; i < laneCount; i++)
        {
            String name = text.getName(i);
            String time = text.getTime(i);

            EventEntry entry = lookupEntry(name, event);
            Improvement improvement = calculateImprovement(entry, time);
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

    private EventEntry lookupEntry(String name, Event event)
    {
        EventEntry match = null;
        for (EventEntry entry : event.getEntries())
        {
            Swimmer swimmer = entry.getSwimmer();
            String swimmerName = swimmer.getAbbreviatedName(14);
            if (swimmerName.equals(name))
            {
                if (match != null) // we have a duplicate, so don't display either.
                {
                    match = null;
                    break;
                }
                match = entry;
            }
        }

        return match;
    }

    private Improvement calculateImprovement(EventEntry entry, String time)
    {
        Improvement improvement = new Improvement();
        if (entry != null)
        {
            RaceTime entryTime = entry.getEntryTime();
            RaceTime newTime = new RaceTime(time);
            String timeDifference = newTime.minus(entryTime);
            if (timeDifference.startsWith("-"))
            {
                improvement.improvement = timeDifference;
            }
        }
        return improvement;
    }

    public String getImprovement(int laneIndex)
    {
        return improvements.get(laneIndex).improvement;
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
                String improvment = getImprovement(i - 3);
                if (!improvment.isEmpty())
                {
                    sb.append("| ").append(improvment);
                }
            }
        }
        return sb.toString();
    }
}

