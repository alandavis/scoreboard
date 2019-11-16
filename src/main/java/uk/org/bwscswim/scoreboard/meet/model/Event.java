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

    private List<EventEntry> entries = new ArrayList<>();
    private TreeMap<Integer, RaceTime> countyTimes;

    public Event(int number, String name)
    {
        this.number = number;
        this.name = getStdName(name);
    }

    public static String getStdName(String name)
    {
        for (String[] pair: new String[][]{
                {" Open",             ""},
                {"Individual Medley", "IM"},
                {"Ind Medley",        "IM"},
                {" Free$",            " Freestyle"},
                {" Back$",            " Backstroke"},
                {" Fly",              " Butterfly"},
                {" Breast$",          " Breaststroke"},
                {"m Freestyle",       " Freestyle"},
                {"m Backstroke",      " Backstroke"},
                {"m Butterfly",       " Butterfly"},
                {"m Breaststroke",    " Breaststroke"},
                {"m IM",              " IM"}})
        {
            name = name.replaceFirst(pair[0], pair[1]);
        }
        return name;
    }

    public int getNumber()
    {
        return number;
    }

    public String getName()
    {
        return name;
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

    public Improvement getImprovement(String name, String time)
    {
        EventEntry eventEntry = lookupEventEntry(name);
        return eventEntry == null ? new Improvement() : new Improvement(eventEntry, time, this);
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


    public boolean isTeamEvent()
    {
        return name.toLowerCase().contains("team");
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

    public RaceTime getPb(Swimmer swimmer)
    {
        return null; // TODO
    }

    public RaceTime getRegionalBaseTime(Integer yearOfBirth)
    {
        return null; // TODO
    }

    public RaceTime getRegionalAutoTime(Integer yearOfBirth)
    {
        return null; // TODO
    }
}
