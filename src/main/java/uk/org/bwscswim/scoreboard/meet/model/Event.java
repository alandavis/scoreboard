package uk.org.bwscswim.scoreboard.meet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author adavis
 */
public class Event implements Comparable<Event>
{
    private final int number;
    private final String name;
    private final Abbreviations abbreviations;

    private List<EventEntry> entries = new ArrayList<>();
    private TreeMap<Integer, RaceTime> countyTimes;

    public Event(int number, String name, Abbreviations abbreviations)
    {
        this.number = number;
        this.name = name;
        this.abbreviations = abbreviations;
    }

    public int getNumber()
    {
        return number;
    }

    public String getName()
    {
        return name;
    }

    public String getHeading(int heatNumber)
    {
        return String.format("Ev%d/%d %s", number, heatNumber, abbreviations.lookupAbbreviation(name));
    }

    public String getShortName()
    {
        return abbreviations.lookupAbbreviation(name);
    }


    public List<EventEntry> getEntries()
    {
        return entries;
    }

    public TreeMap<Integer, RaceTime> getCountyTimes()
    {
        return countyTimes;
    }

    public void setCountyTimes(TreeMap<Integer, RaceTime> countyTimes)
    {
        this.countyTimes = countyTimes;
    }

    public void add(EventEntry eventEntry)
    {
        entries.add(eventEntry);
    }

    public String getImprovement(String name, String time)
    {
        String improvement = "";
        EventEntry eventEntry = lookupEventEntry(name);
        if (eventEntry != null)
        {
            improvement = eventEntry.calculateImprovement(time, this);
        }
        return improvement;
    }

    private EventEntry lookupEventEntry(String name)
    {
        EventEntry match = null;
        for (EventEntry entry : getEntries())
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

    public RaceTime getCountyTime(Integer yearOfBirth)
    {
        RaceTime raceTime = null;
        if (yearOfBirth != null && countyTimes != null)
        {
            raceTime = countyTimes.get(yearOfBirth);
            if (raceTime == null)
            {
                Integer lowestYearOfBirth = countyTimes.firstKey();
                raceTime = yearOfBirth < lowestYearOfBirth ? countyTimes.get(lowestYearOfBirth) : countyTimes.get(countyTimes.lastKey());
            }
        }
        return raceTime;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Event))
        {
            return false;
        }
        Event event = (Event) o;
        return number == event.number;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(number);
    }

    @Override
    public int compareTo(Event event)
    {
        return number - event.number;
    }
}
