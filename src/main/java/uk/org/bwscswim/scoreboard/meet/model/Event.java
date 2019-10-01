package uk.org.bwscswim.scoreboard.meet.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author adavis
 */
public class Event implements Comparable<Event>
{
    private final int number;
    private final String name;
    private final Abbreviations abbreviations;

    private List<EventEntry> entries = new ArrayList<>();

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

    public List<EventEntry> getEntries()
    {
        return entries;
    }

    public void add(EventEntry eventEntry)
    {
        entries.add(eventEntry);
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
