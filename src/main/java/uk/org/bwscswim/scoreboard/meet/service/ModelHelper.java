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
package uk.org.bwscswim.scoreboard.meet.service;

import uk.org.bwscswim.scoreboard.Config;
import uk.org.bwscswim.scoreboard.FileLoader;
import uk.org.bwscswim.scoreboard.meet.model.Abbreviations;
import uk.org.bwscswim.scoreboard.meet.model.Club;
import uk.org.bwscswim.scoreboard.meet.model.RaceTime;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.model.EventEntry;
import uk.org.bwscswim.scoreboard.meet.model.Swimmer;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Reads the accepted swims file and populates data structures that may the be accessed via {@link #getEvents()}.
 *
 * @author adavis
 */
public class ModelHelper
{
    private final Abbreviations clubAbbreviations;

    private final Map<String, Club> clubs = new HashMap<>();
    private final Map<Integer, Event> events = new HashMap<>();
    private final Map<String, Swimmer> swimmers = new HashMap<>();
    private final int year;
    private final List<String> clubEvents = new ArrayList<>();

    private int lineNumber;
    private String prevStdEventName;
    private int firstAge;

    public ModelHelper(Config config) throws IOException
    {
        this(config.getClubsFilename(),
             config.getCountyTimesFilename(),
             config.getRegionalTimesFilename(),
             config.getAcceptedSwimFilename(),
             config.getPbFilename(),
             config.getClubEventFilename(),
             config, -1);
    }

    public ModelHelper(String clubsFilename, String countyTimesFilename, String regionalTimesFilename,
                       String acceptedSwimFilename, String pbFilename, String clubEventFilename,
                       Config config, int testYear) throws IOException
    {
        year = testYear >= 2019 ? testYear : LocalDate.now().getYear();

        clubAbbreviations = new Abbreviations(clubsFilename, config);

        loadAcceptedSwimmers(acceptedSwimFilename, config);
        loadCountyTimes(countyTimesFilename, acceptedSwimFilename, config);
        loadRegionalTimes(regionalTimesFilename, acceptedSwimFilename, config);
        loadPBTimes(pbFilename, config);

        loadClubEvents(clubEventFilename, config);
    }

    public List<Event> getEvents()
    {
        List<Event> events = new ArrayList<>(this.events.values());
        Collections.sort(events);
        return events;
    }

    private void loadAcceptedSwimmers(String filename, Config config) throws IOException
    {
        try (BufferedReader reader = FileLoader.getBufferedReader(filename, config))
        {
            reader.lines().forEach(line -> loadAcceptedSwimmer(line, filename));
        }
    }

    private void loadAcceptedSwimmer(String line, String filename)
    {
        if (lineNumber++ > 0)
        {
            String eventName = null;
            try
            {
                String[] col = line.split("\t");
                String swimmerName = col[1];
                String yearOfBirth = col[2];
                String clubName = col[3];
                String eventNumber = col[5];
                       eventName = col[6];
                String entryTime = col[4];

                assertNotNull(swimmerName, "swimmerName");
                assertNotNull(eventNumber, "eventNumber");
                assertNotNull(entryTime, "entryTime");

                Club club = lookupOrCreateClub(clubName);
                Event event = lookupOrCreateEvent(eventNumber, eventName);
                boolean teamEvent = event.isTeamEvent();
                Swimmer swimmer = lookupOrCreateSwimmer(swimmerName, yearOfBirth, club, teamEvent);
                RaceTime time = RaceTime.create(entryTime);
                EventEntry eventEntry = new EventEntry(swimmer, time);
                event.add(eventEntry);
            }
            catch (IllegalArgumentException e)
            {
                System.err.println(filename+' '+e.getMessage()+eventName);
            }
        }
    }

    private void assertNotNull(Object field, String fieldName)
    {
        if (field == null)
        {
            throw new IllegalArgumentException(fieldName+" the "+fieldName+" must not be null");
        }
    }

    private Club lookupOrCreateClub(String clubName)
    {
        Club club = null;
        if (club == null)
        {
            club = clubs.get(clubName);
            if (club == null)
            {
                club = new Club(clubName, clubAbbreviations);
                clubs.put(clubName, club);
            }
        }
        return club;
    }

    private Event lookupOrCreateEvent(String eventNumberString, String eventName)
    {
        Integer eventNumber = toInteger(eventNumberString, lineNumber, "eventNumber");
        Event event = events.get(eventNumber);
        if (event == null)
        {
            event = new Event(eventNumber, eventName);
            events.put(eventNumber, event);
        }
        return event;
    }

    private Swimmer lookupOrCreateSwimmer(String swimmerName, String yearOfBirth, Club club, boolean teamEvent)
    {
        Swimmer swimmer = swimmers.get(swimmerName);
        if (swimmer == null)
        {
            Integer intYearOfBirth = null;
            try
            {
                intYearOfBirth = toYear(yearOfBirth, lineNumber, "yearOfBirth");
            }
            catch (NumberFormatException e)
            {
                if (!teamEvent) // yearOfBirth may not be set if the team event has mixed ages.
                {
                    throw e;
                }
            }
            swimmer = new Swimmer(swimmerName, intYearOfBirth, club);
            swimmers.put(swimmerName, swimmer);
        }
        return swimmer;
    }

    private static Integer toInteger(String i, int line, String fieldName)
    {
        try
        {
            return Integer.parseInt(i);
        }
        catch (NumberFormatException e)
        {
            throw new NumberFormatException("line " + line + " invalid " + fieldName + ": " + i);
        }
    }

    private static Integer toYear(String year, int line, String fieldName)
    {
        try
        {
            return Integer.parseInt(year.trim());
        }
        catch (NumberFormatException e)
        {
            throw new NumberFormatException("line " + line + " invalid " + fieldName + ": "+year);
        }
    }

    private void loadCountyTimes(String filename, String acceptedSwimFilename, Config config) throws IOException
    {
        List<Event> events = getEvents();
        StringJoiner missingEvents = new StringJoiner("\n    ",
                "There are no events in "+acceptedSwimFilename+
                        " that match the following county events from "+filename+":\n    ", "\n");

        prevStdEventName = null;
        try (BufferedReader reader = FileLoader.getBufferedReader(filename, config))
        {
            AtomicInteger lineNumber = new AtomicInteger(1);
            reader.lines().forEach(line -> loadCountyTime(lineNumber.getAndIncrement(), line, events, missingEvents));
        }

        if (missingEvents.length() > 0)
        {
            System.err.println(missingEvents);
        }
        StringJoiner noCountyTimeEvents = new StringJoiner("\n    ",
                "The following events from "+acceptedSwimFilename+
                        " don't have county times in "+filename+":\n    ", "\n");
        for (Event event: events)
        {
            if (!event.hasCountyTimes())
            {
                noCountyTimeEvents.add(event.getName());
            }
        }
        if (noCountyTimeEvents.length() > 0)
        {
            System.err.println(noCountyTimeEvents);
        }
    }

    private void loadCountyTime(int lineNumber, String line, List<Event> events, StringJoiner missingEvents)
    {
        if (lineNumber == 1)
        {
            firstAge = getFirstAge(line);
        }
        else
        {
            String[] split = line.split(",");
            String eventName = Event.getStdName(split[0]);
            Event event = lookupEvent(events, eventName);
            if (event != null)
            {
                TreeMap<Integer, RaceTime> times = new TreeMap<>();
                int baseYear = getYearOfBirth(firstAge) + 1;
                event.setCountyTimes(times);
                for (int i = 1; i < split.length; i++)
                {
                    String timeStr = split[i].trim();
                    if (!timeStr.isEmpty())
                    {
                        RaceTime time = RaceTime.create(timeStr);
                        int age = baseYear - i + 1;
                        times.put(age, time);
                    }
                }
            }
            else if (!eventName.equals(prevStdEventName))
            {
                missingEvents.add(eventName);
            }
            prevStdEventName = eventName;
        }
    }

    private int getFirstAge(String line)
    {
        String[] split = line.split(",");
        String firstAgeString = split[1].trim();
        int i = firstAgeString.lastIndexOf('/');
        firstAgeString = i == -1 ? firstAgeString : firstAgeString.substring(i+1);
        return Integer.parseInt(firstAgeString);
    }

    private void loadRegionalTimes(String filename, String acceptedSwimFilename, Config config) throws IOException
    {
        List<Event> events = getEvents();
        StringJoiner missingEvents = new StringJoiner("\n    ",
                "There are no events in "+acceptedSwimFilename+
                        " that match the following regional events from "+filename+":\n    ", "\n");

        prevStdEventName = null;
        try (BufferedReader reader = FileLoader.getBufferedReader(filename, config))
        {
            AtomicInteger lineNumber = new AtomicInteger(1);
            reader.lines().forEach(line -> loadRegionalTimes(lineNumber.getAndIncrement(), line, events, missingEvents));
        }

        if (missingEvents.length() > 0)
        {
            System.err.println(missingEvents);
        }
        StringJoiner noCountyTimeEvents = new StringJoiner("\n    ",
                "The following events from "+acceptedSwimFilename+
                        " don't have regional times in "+filename+":\n    ", "\n");
        for (Event event: events)
        {
            if (!event.hasRegionalAutoTimes())
            {
                noCountyTimeEvents.add(event.getName());
            }
        }
        if (noCountyTimeEvents.length() > 0)
        {
            System.err.println(noCountyTimeEvents);
        }
    }

    private void loadRegionalTimes(int lineNumber, String line, List<Event> events, StringJoiner missingEvents)
    {
        if (lineNumber == 1)
        {
            firstAge = getFirstAge(line);
        }
        else
        {
            String[] split = line.split(",");
            String eventName = Event.getStdName(split[0]);
            Event event = lookupEvent(events, eventName);
            if (event != null)
            {
                TreeMap<Integer, RaceTime> baseTimes = new TreeMap<>();
                TreeMap<Integer, RaceTime> autoTimes = new TreeMap<>();
                int baseYear = getYearOfBirth(firstAge) + 1;
                event.setRegionalBaseTimes(baseTimes);
                event.setRegionalAutoTimes(autoTimes);
                for (int i = 1, j = 1; j < split.length; i++, j += 2)
                {
                    int age = baseYear - i + 1;
                    String timeStr = split[j].trim();
                    if (!timeStr.isEmpty())
                    {
                        RaceTime time = RaceTime.create(timeStr);
                        baseTimes.put(age, time);
                    }
                    timeStr = split[j + 1].trim();
                    if (!timeStr.isEmpty())
                    {
                        RaceTime time = RaceTime.create(timeStr);
                        autoTimes.put(age, time);
                    }
                }
            }
            else if (!eventName.equals(prevStdEventName))
            {
                missingEvents.add(eventName);
            }
            prevStdEventName = eventName;
        }
    }

    private void loadPBTimes(String filename, Config config)
    {
    }

    private void loadClubEvents(String filename, Config config) throws IOException
    {
        try (BufferedReader reader = FileLoader.getBufferedReader(filename, config))
        {
            reader.lines().forEach(line -> clubEvents.add(line));
        }
    }

    public List<String> getClubEvents()
    {
        return clubEvents;
    }

    private void loadClubEvent(String line, String filename)
    {
    }

    private int getYearOfBirth(int age)
    {
        return year - age;
    }

    private Event lookupEvent(List<Event> events, String name)
    {
        for (Event event : events)
        {
            String eventName = event.getName();
            if (name.equals(eventName))
            {
                return event;
            }
        }
        return null;
    }
}
