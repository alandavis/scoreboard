package uk.org.bwscswim.scoreboard.meet.service;

import org.junit.Test;
import uk.org.bwscswim.scoreboard.meet.model.Abbreviations;
import uk.org.bwscswim.scoreboard.meet.model.Club;
import uk.org.bwscswim.scoreboard.meet.model.RaceTime;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.model.EventEntry;
import uk.org.bwscswim.scoreboard.meet.model.Swimmer;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.*;

public class ModelHelperTest
{
    @Test
    public void fullTest() throws IOException
    {
        ModelHelper helper = new ModelHelper("Accepted.txt",
                "Events.txt", "Clubs.txt", "CountyTimes.txt");

        List<Event> events = helper.getEvents();
        assertEquals(24, events.size());

        Event event = events.get(0);
        assertEquals(1, event.getNumber());
        assertEquals("Girls Open 200m Butterfly", event.getName());

        event = events.get(4);
        assertEquals(5, event.getNumber());
        assertEquals("Girls Open 100m Backstroke", event.getName());
        assertEquals("Ev5/4 Girls 100 Back", event.getHeading(4));

        List<EventEntry> entries = events.get(4).getEntries();
        assertEquals(66, entries.size());
        assertEquals("Grace Walker",  entries.get(0).getSwimmer().getName());

        Swimmer swimmer = entries.get(65).getSwimmer();
        RaceTime entryTime = entries.get(65).getEntryTime();
        assertEquals("Amber Wildey", swimmer.getName());
        assertEquals(2009,           swimmer.getYearOfBirth().intValue());
        assertEquals("Bracknell",    swimmer.getClub().getShortName());
        assertEquals("BRKS",         swimmer.getClub().getAbbreviation());
        assertEquals("1:50.00",      entryTime.toString());

        assertEquals("1:31.05", RaceTime.create("1:31.05").toString());
        assertEquals(       "18.95", event.getImprovement("Amber Wildey", "1:31.05"));
        assertEquals(       "19.95", event.getImprovement("Amber Wildey", "1:30.05")); // 1:50.00 vs 1:30.05
        assertEquals(        "* CT", event.getImprovement("Amber Wildey", "1:30.00")); // the county time
        assertEquals(        "* CT", event.getImprovement("Amber Wildey", "1:29.95")); // better than the county time
        assertEquals(            "", event.getImprovement("Amber Wildey", "1.29.95")); // invalid time

        assertEquals("Girls 100 Back", event.getShortName());
        assertEquals(RaceTime.create("1:30.00"), event.getCountyTime(2010)); // 9
        assertEquals(RaceTime.create("1:30.00"), event.getCountyTime(2009));
        assertEquals(RaceTime.create("1:30.00"), event.getCountyTime(2008)); // 11 youngest CT
        assertEquals(RaceTime.create("1:25.00"), event.getCountyTime(2007)); // 12
        assertEquals(RaceTime.create("1:19.50"), event.getCountyTime(2006)); // 13
        assertEquals(RaceTime.create("1:17.00"), event.getCountyTime(2005));
        assertEquals(RaceTime.create("1:15.00"), event.getCountyTime(2004));
        assertEquals(RaceTime.create("1:13.50"), event.getCountyTime(2003));
        assertEquals(RaceTime.create("1:12.00"), event.getCountyTime(2002)); // 17 oldest CT
        assertEquals(RaceTime.create("1:12.00"), event.getCountyTime(2001));

        // No county times for 100 IM
        event = events.get(11);
        assertEquals(12, event.getNumber());
        assertEquals("Boys Open 100m IM", event.getName());
        assertNull(event.getCountyTime(2009));
    }

    @Test
    public void swimmerTest() throws IOException
    {
        Abbreviations clubAbbreviations = new Abbreviations("Clubs.txt");
        Club club = new Club("Bracknell", clubAbbreviations);

        assertEquals("Mia Richardson", new Swimmer("Mia Richardson",  2001, club).getName());
        assertEquals("M Richardson",   new Swimmer("Mia Richardson",  2001, club).getAbbreviatedName(13));

        assertEquals("Mi Richardson",  new Swimmer("Mi Richardson",   2001, club).getName());
        assertEquals("Mi Richardson",  new Swimmer("Mi Richardson",   2001, club).getAbbreviatedName(13));

        assertEquals("M Richardson",   new Swimmer("Miad Richardson", 2001, club).getAbbreviatedName(13));

        assertEquals("M Richardson",   new Swimmer("Mia Richardson",  2001, club).getAbbreviatedName(12));
        assertEquals("M Richardso",    new Swimmer("Mia Richardson",  2001, club).getAbbreviatedName(11));
        assertEquals("M R",            new Swimmer("Mia Richardson",  2001, club).getAbbreviatedName(3));
    }

    @Test
    public void entryTimeTest()
    {
        assertEquals("1:23.45", RaceTime.create("1:23.45").toString());
        assertEquals("1:23.45", RaceTime.create("12345").toString());
        assertEquals("12345", RaceTime.create("1:23.45").toDigits());
        assertEquals("12345", RaceTime.create("12345").toDigits());

        assertEquals("23.45", RaceTime.create("23.45").toString());
        assertEquals("23.45", RaceTime.create("2345").toString());
        assertEquals("2345", RaceTime.create("23.45").toDigits());
        assertEquals("2345", RaceTime.create("2345").toDigits());

        assertEquals("11:23.45", RaceTime.create("11:23.45").toString());
        assertEquals("23.45", RaceTime.create("23.45").toString());
        assertEquals("3.45", RaceTime.create("3.45").toString());

        assertEquals("3.40", RaceTime.create("3.4").toString());
        assertEquals("11:23.40", RaceTime.create("11:23.4").toString());

        assertEquals("23.45", RaceTime.create("  23.45  ").toString());
        assertEquals("23.40", RaceTime.create("23.4").toString());
        assertEquals("23.00", RaceTime.create("23.").toString());
        assertEquals("23.45", RaceTime.create("23.456").toString());
        assertEquals("23.45", RaceTime.create("23.4567").toString());
        assertEquals("23.00", RaceTime.create("23").toString());
        assertEquals("2.00", RaceTime.create("2").toString());
        assertNull(RaceTime.create(""));
        assertNull(RaceTime.create("1:3.45"));
        assertNull(RaceTime.create(null));
        assertNull(RaceTime.create("01:3.45"));
        assertEquals("03.45", RaceTime.create("0:03.45").toString());
        assertEquals("1:03.45", RaceTime.create("01:03.45").toString());
        assertEquals("1:03.45", RaceTime.create("001:03.45").toString());

        assertEquals("-0.01", RaceTime.create("1:23.44").minus(RaceTime.create("1:23.45")).toString());
        assertEquals("-0.10", RaceTime.create("1:23.35").minus(RaceTime.create("1:23.45")).toString());
        assertEquals("0.00", RaceTime.create("1:23.45").minus(RaceTime.create("1:23.45")).toString());
        assertEquals("0.01", RaceTime.create("1:23.46").minus(RaceTime.create("1:23.45")).toString());
        assertEquals("0.10", RaceTime.create("1:23.55").minus(RaceTime.create("1:23.45")).toString());

        assertEquals("-1.00", RaceTime.create("1:22.45").minus(RaceTime.create("1:23.45")).toString());
        assertEquals("-1.01", RaceTime.create("1:22.44").minus(RaceTime.create("1:23.45")).toString());
        assertEquals("-1:01.01", RaceTime.create("22.44").minus(RaceTime.create("1:23.45")).toString());
        assertEquals("-12:01.01", RaceTime.create("1:22.44").minus(RaceTime.create("13:23.45")).toString());
        assertEquals("-0.95", RaceTime.create("1:22.50").minus(RaceTime.create("1:23.45")).toString());
        assertEquals("-10.95", RaceTime.create("1:22.50").minus(RaceTime.create("1:33.45")).toString());
        assertEquals("-2:10.95", RaceTime.create("1:22.50").minus(RaceTime.create("3:33.45")).toString());

        assertTrue(RaceTime.create("45.00").compareTo(RaceTime.create("44.00")) > 0);
        assertTrue(RaceTime.create("45.00").compareTo(RaceTime.create("45.00")) == 0);
        assertTrue(RaceTime.create("45.00").compareTo(RaceTime.create("46.00")) < 0);
    }

    @Test
    public void eventAbbreviationsTest() throws IOException
    {
        Abbreviations eventAbbreviations = new Abbreviations("Events.txt");

        assertEquals("does not exist", eventAbbreviations.lookupAbbreviation("does not exist"));
        assertEquals("Girls 200 IM", eventAbbreviations.lookupAbbreviation("Girls Open 200m IM"));
        assertEquals("Girls 200 Breast", eventAbbreviations.lookupAbbreviation("Girls Open 200m Breaststroke"));
        assertEquals("Boys 200 Breast", eventAbbreviations.lookupAbbreviation("Boys Open 200m Breaststroke"));
    }

    @Test
    public void clubAbbreviationsTest() throws IOException
    {
        Abbreviations clubAbbreviations = new Abbreviations("Clubs.txt");

        assertEquals("does not exist", clubAbbreviations.lookupAbbreviation("does not exist"));

        assertEquals("BRKS", clubAbbreviations.lookupAbbreviation("Bracknell"));
        assertEquals("BRKS", clubAbbreviations.lookupAbbreviation("Bracknell & Wokingham SC"));
        assertEquals("Bracknell",               clubAbbreviations.lookupShortName("BRKS"));
        assertEquals("Bracknell & Wokingham SC", clubAbbreviations.lookupLongName("BRKS"));

        assertEquals("does not exist", clubAbbreviations.lookupShortName("does not exist"));
        assertEquals("does not exist", clubAbbreviations.lookupLongName("does not exist"));

        assertEquals("Didcot & Bar",          clubAbbreviations.lookupShortName("DABS"));
        assertEquals("Didcot & Barramundi SC", clubAbbreviations.lookupLongName("DABS"));

        assertEquals("City of Southampton SC", clubAbbreviations.lookupLongName("COSS"));
    }

    @Test
    public void eventTest() throws IOException
    {
        Abbreviations eventAbbreviations = new Abbreviations("Events.txt");
        Event event = new Event(3,"Girls Open 200m IM", eventAbbreviations);

        assertEquals(3, event.getNumber());
        assertEquals("Girls Open 200m IM", event.getName());
        assertEquals("Girls 200 IM", event.getShortName());
        assertEquals("Ev3/23 Girls 200 IM", event.getHeading(23));
    }

    @Test
    public void improvementTest() throws IOException
    {
        Abbreviations eventAbbreviations = new Abbreviations("Events.txt");
        Event event = new Event(3,"Girls Open 200m IM", eventAbbreviations);

        Swimmer alice1 = new Swimmer("Alice", 2005, null);
        Swimmer alice2 = new Swimmer("Alice", 2005, null);
        Swimmer jane   = new Swimmer("Jane",  2005, null);
        Swimmer clare  = new Swimmer("Clare", 2005, null);
        Swimmer emma   = new Swimmer("Emma", 2005, null);
        Swimmer tess   = new Swimmer("Tess",  2005, null);
        event.add(new EventEntry(alice1, RaceTime.create("1:33.00")));
        event.add(new EventEntry(alice2, RaceTime.create("1:34.00")));
        event.add(new EventEntry(jane,   RaceTime.create("1:33.00")));
        event.add(new EventEntry(clare,  RaceTime.create("1:30.00")));
        event.add(new EventEntry(emma,   RaceTime.create("1:32.00")));
        event.add(new EventEntry(tess,   RaceTime.create("")));
        assertEquals(       "",  event.getImprovement("Alice", "33.45"));   // duplicate name
        assertEquals(       "",  event.getImprovement("Jane",  ""));        // did not finish
        assertEquals(   "1.00",  event.getImprovement("Jane",  "1:32.00")); // faster
        assertEquals(       "",  event.getImprovement("Clare", "1:32.00")); // slower
        assertEquals(   "1.00",  event.getImprovement("Emma",  "1:31.00")); // faster
        assertEquals(       "",  event.getImprovement("Tess",  "1:32.00")); // new time

        TreeMap<Integer, RaceTime> countryTimes = new TreeMap<>();
        countryTimes.put(2005, RaceTime.create("1:31.0"));
        countryTimes.put(2006, RaceTime.create("1:31.5"));
        event.setCountyTimes(countryTimes);
        assertEquals(       "",  event.getImprovement("Alice", "33.45"));   // duplicate name
        assertEquals(       "",  event.getImprovement("Jane",  ""));        // did not finish
        assertEquals(   "1.00",  event.getImprovement("Jane",  "1:32.00")); // faster
        assertEquals(       "",  event.getImprovement("Clare", "1:32.00")); // slower
        assertEquals(       "",  event.getImprovement("Clare", "1:31.05")); // slower than county and previous county time
        assertEquals(     "CT",  event.getImprovement("Clare", "1:30.05")); // slower but county
        assertEquals(     "CT",  event.getImprovement("Clare", "1:30.00")); // equal but county
        assertEquals("0.05 CT",  event.getImprovement("Clare", "1:29.95")); // faster county
        assertEquals(   "* CT",  event.getImprovement("Emma",  "1:31.00")); // new county
        assertEquals("",         event.getImprovement("Tess",  "1:32.00")); // new time, but not county
        assertEquals(   "* CT",  event.getImprovement("Tess",  "1:30.00")); // new time and county
    }

    @Test
    public void clubTest() throws IOException
    {
        Abbreviations clubAbbreviations = new Abbreviations("Clubs.txt");
        Club club = new Club("Bracknell", clubAbbreviations);

        assertEquals("BRKS", club.getAbbreviation());
        assertEquals("Bracknell", club.getShortName());
        assertEquals("Bracknell & Wokingham SC", club.getLongName());
    }
}