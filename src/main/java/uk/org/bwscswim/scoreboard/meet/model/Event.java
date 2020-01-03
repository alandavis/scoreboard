/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2020 Bracknell and Wokingham Swimming Club (BWSC)
 * %%
 * This file is part of BWSC Scoreboard.
 *
 * BWSC Scoreboard is free software: you can redistribute it and/or modify
 * it under the terms of the LGNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BWSC Scoreboard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGNU Lesser General Public License for more details.
 *
 * You should have received a copy of the LGNU Lesser General Public License
 * along with BWSC Scoreboard.  If not, see <https://www.gnu.org/licenses/>.
 * #L%
 */
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
    private TreeMap<Integer, RaceTime> regionalBaseTimes;
    private TreeMap<Integer, RaceTime> regionalAutoTimes;

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

    public void setCountyTimes(TreeMap<Integer, RaceTime> countyTimes)
    {
        this.countyTimes = countyTimes;
    }

    public void setRegionalBaseTimes(TreeMap<Integer, RaceTime> regionalBaseTimes)
    {
        this.regionalBaseTimes = regionalBaseTimes;
    }

    public boolean hasCountyTimes()
    {
        return countyTimes != null;
    }

    public boolean hasRegionalAutoTimes()
    {
        return regionalAutoTimes != null;
    }

    public void setRegionalAutoTimes(TreeMap<Integer, RaceTime> regionalAutoTimes)
    {
        this.regionalAutoTimes = regionalAutoTimes;
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
        name = Swimmer.getStandardName(name, 14);
        EventEntry match = null;
        for (EventEntry entry : getEntries())
        {
            Swimmer swimmer = entry.getSwimmer();
            String swimmerName = swimmer.getStandardName(14);
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
        return getTime(countyTimes, yearOfBirth);
    }

    public RaceTime getRegionalBaseTime(Integer yearOfBirth)
    {
        return getTime(regionalBaseTimes, yearOfBirth);
    }

    public RaceTime getRegionalAutoTime(Integer yearOfBirth)
    {
        return getTime(regionalAutoTimes, yearOfBirth);
    }

    public RaceTime getPb(Swimmer swimmer)
    {
        return null; // TODO
    }

    private RaceTime getTime(TreeMap<Integer, RaceTime> times, Integer yearOfBirth)
    {
        RaceTime raceTime = null;
        if (yearOfBirth != null && times != null)
        {
            raceTime = times.get(yearOfBirth);
            if (raceTime == null)
            {
                Integer lowestYearOfBirth = times.firstKey();
                raceTime = yearOfBirth < lowestYearOfBirth ? times.get(lowestYearOfBirth) : times.get(times.lastKey());
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
}
