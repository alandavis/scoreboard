package uk.org.bwscswim.scoreboard.meet.service;

import uk.org.bwscswim.scoreboard.meet.model.Abbreviations;
import uk.org.bwscswim.scoreboard.meet.model.Club;
import uk.org.bwscswim.scoreboard.meet.model.RaceTime;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.model.EventEntry;
import uk.org.bwscswim.scoreboard.meet.model.Swimmer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Reads the accepted swims file and populates data structures that may the be accessed via {@link #getEvents()}.
 *
 * @author adavis
 */
public class ModelHelper
{
    private final Abbreviations eventAbbreviations;
    private final Abbreviations clubAbbreviations;

    private final Map<String, Club> clubs = new HashMap<>();
    private final Map<Integer, Event> events = new HashMap<>();
    private final Map<String, Swimmer> swimmers = new HashMap<>();

    private int lineNumber;
    private String previousAbbreviation;

    public ModelHelper(String acceptedSwimFilename, String eventsFilename, String clubsFilename,
                       String countyTimesFilename) throws IOException
    {
        eventAbbreviations = new Abbreviations(eventsFilename);
        clubAbbreviations = new Abbreviations(clubsFilename);

        loadAcceptedSwimmers(acceptedSwimFilename);
        loadCountyTimes(countyTimesFilename);
    }

    public List<Event> getEvents()
    {
        List<Event> events = new ArrayList<>(this.events.values());
        Collections.sort(events);
        return events;
    }

    private void loadAcceptedSwimmers(String filename) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            reader.lines().forEach(line -> loadAcceptedSwimmer(line));
        }
    }

    private void loadAcceptedSwimmer(String line)
    {
        if (lineNumber++ > 0)
        {
            try
            {
                String[] col = line.split("\t");
                String swimmerName = col[1];
                String yearOfBirth = col[2];
                String clubName = col[3];
                String eventNumber = col[5];
                String eventName = col[6].replaceAll("Open ", ""); // just strip "Open " if it exists
                String entryTime = col[4];

                assertNotNull(swimmerName, "swimmerName");
                assertNotNull(eventNumber, "eventNumber");
                assertNotNull(entryTime, "entryTime");

                Club club = lookupOrCreateClub(clubName);
                Event event = lookupOrCreateEvent(eventNumber, eventName);
                Swimmer swimmer = lookupOrCreateSwimmer(swimmerName, yearOfBirth, club);
                RaceTime time = RaceTime.create(entryTime);
                EventEntry eventEntry = new EventEntry(swimmer, time);
                event.add(eventEntry);
            }
            catch (IllegalArgumentException e)
            {
                System.err.println(e.getMessage());
            }
        }
    }

    private void assertNotNull(Object field, String fieldName)
    {
        if (field == null)
        {
            throw new IllegalArgumentException("The "+fieldName+" must not be null");
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
            event = new Event(eventNumber, eventName, eventAbbreviations);
            events.put(eventNumber, event);
        }
        return event;
    }

    private Swimmer lookupOrCreateSwimmer(String swimmerName, String yearOfBirth, Club club)
    {
        Swimmer swimmer = swimmers.get(swimmerName);
        if (swimmer == null)
        {
            Integer longYearOfBirth = toYear(yearOfBirth, lineNumber, "yearOfBirth");
            swimmer = new Swimmer(swimmerName, longYearOfBirth, club);
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
            throw new NumberFormatException("Line "+line+" invalid "+fieldName+": "+i);
        }
    }

    private static Integer toYear(String year, int line, String fieldName)
    {
        try
        {
            return Integer.parseInt(year);
        }
        catch (NumberFormatException e)
        {
            throw new NumberFormatException("Line "+line+" invalid "+fieldName+": "+year);
        }
    }

    private void loadCountyTimes(String filename) throws IOException
    {
        List<Event> events = getEvents();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            reader.lines().forEach(line -> loadCountyTime(line, events));
        }

        previousAbbreviation = null;
        for (Event event: events)
        {
            if (event.getCountyTimes() == null)
            {
                System.err.println("No county times found for "+event.getName()+" ("+event.getShortName()+")");
            }
        }
    }

    private void loadCountyTime(String line, List<Event> events)
    {
        String[] split = line.split(",");
        String eventName = split[0];
        String abbreviation = eventAbbreviations.lookupAbbreviation(eventName);
        Event event = lookupEvent(events, abbreviation);
        if (event != null)
        {
            TreeMap<Integer, RaceTime> countyTimes = new TreeMap<>();
            int baseYear = getYearOfBirthIf11();
            event.setCountyTimes(countyTimes);
            for (int i=1; i<split.length; i++)
            {
                String timeStr = split[i].trim();
                if (!timeStr.isEmpty())
                {
                    RaceTime time = RaceTime.create(timeStr);
                    int age = baseYear-i+1;
                    countyTimes.put(age, time);
                }
            }
        }
        else if (!abbreviation.equals(previousAbbreviation))
        {
            System.err.println("County times file event ("+abbreviation+") not found in accepted swims");
        }
        previousAbbreviation = abbreviation;
    }

    private int getYearOfBirthIf11()
    {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        return year - 11;
    }

    private Event lookupEvent(List<Event> events, String abbreviation)
    {
        for (Event event : events)
        {
            String name = event.getShortName();
            if (abbreviation.equals(name))
            {
                return event;
            }
        }
        return null;
    }
}
