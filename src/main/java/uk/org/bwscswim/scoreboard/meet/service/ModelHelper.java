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

    private int year = LocalDate.now().getYear();

    private int lineNumber;
    private String prevStdEventName;

    public ModelHelper(String clubsFilename, String countyTimesFilename, String regionalTimesFilename,
                       String acceptedSwimFilename, String pbFilename, Config config) throws IOException
    {
        clubAbbreviations = new Abbreviations(clubsFilename, config);

        loadAcceptedSwimmers(acceptedSwimFilename, config);
        loadCountyTimes(countyTimesFilename, acceptedSwimFilename, config);
        loadRegionalTimes(regionalTimesFilename, acceptedSwimFilename, config);
        loadPBTimes(pbFilename, config);
    }

    void setYear(int year)
    {
        this.year = year;
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
            reader.lines().forEach(line -> loadCountyTime(line, events, missingEvents));
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

    private void loadCountyTime(String line, List<Event> events, StringJoiner missingEvents)
    {
        String[] split = line.split(",");
        String eventName = Event.getStdName(split[0]);
        Event event = lookupEvent(events, eventName);
        if (event != null)
        {
            TreeMap<Integer, RaceTime> times = new TreeMap<>();
            int baseYear = getYearOfBirth(11);
            event.setCountyTimes(times);
            for (int i=1; i<split.length; i++)
            {
                String timeStr = split[i].trim();
                if (!timeStr.isEmpty())
                {
                    RaceTime time = RaceTime.create(timeStr);
                    int age = baseYear-i+1;
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

    private void loadRegionalTimes(String filename, String acceptedSwimFilename, Config config) throws IOException
    {
        List<Event> events = getEvents();
        StringJoiner missingEvents = new StringJoiner("\n    ",
                "There are no events in "+acceptedSwimFilename+
                        " that match the following regional events from "+filename+":\n    ", "\n");

        prevStdEventName = null;
        try (BufferedReader reader = FileLoader.getBufferedReader(filename, config))
        {
            reader.lines().forEach(line -> loadRegionalTimes(line, events, missingEvents));
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

    private void loadRegionalTimes(String line, List<Event> events, StringJoiner missingEvents)
    {
        String[] split = line.split(",");
        String eventName = Event.getStdName(split[0]);
        Event event = lookupEvent(events, eventName);
        if (event != null)
        {
            TreeMap<Integer, RaceTime> baseTimes = new TreeMap<>();
            TreeMap<Integer, RaceTime> autoTimes = new TreeMap<>();
            int baseYear = getYearOfBirth(12);
            event.setRegionalBaseTimes(baseTimes);
            event.setRegionalAutoTimes(autoTimes);
            for (int i=1, j=1; j<split.length; i++, j+=2)
            {
                int age = baseYear-i+1;
                String timeStr = split[j].trim();
                if (!timeStr.isEmpty())
                {
                    RaceTime time = RaceTime.create(timeStr);
                    baseTimes.put(age, time);
                }
                timeStr = split[j+1].trim();
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

    private void loadPBTimes(String pbFilename, Config config)
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
