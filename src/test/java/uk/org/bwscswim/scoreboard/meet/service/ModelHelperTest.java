package uk.org.bwscswim.scoreboard.meet.service;

import org.junit.Test;
import uk.org.bwscswim.scoreboard.meet.model.Abbreviations;
import uk.org.bwscswim.scoreboard.meet.model.Club;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.model.EventEntry;
import uk.org.bwscswim.scoreboard.meet.model.RaceTime;
import uk.org.bwscswim.scoreboard.meet.model.Swimmer;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ModelHelperTest
{
    @Test
    public void fullTest() throws IOException
    {
        ModelHelper helper = new ModelHelper(":Clubs.txt", ":CountyTimes.txt",
                ":RegionalTimes.txt", ":AcceptedTest.txt",
                ":PBTest.txt", null
        );
        helper.setYear(2019);

        List<Event> events = helper.getEvents();
        assertEquals(24, events.size());

        Event event = events.get(0);
        assertEquals(1, event.getNumber());
        assertEquals("Girls 200 Butterfly", event.getName());

        event = events.get(4);
        assertEquals(5, event.getNumber());
        assertEquals("Girls 100 Backstroke", event.getName());

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
        assertEquals(      "-18.95",  event.getImprovement("Amber Wildey", "1:31.05").toString());
        assertEquals(      "-19.95",  event.getImprovement("Amber Wildey", "1:30.05").toString()); // 1:50.00 vs 1:30.05
        assertEquals(    "-20.00CT*", event.getImprovement("Amber Wildey", "1:30.00").toString()); // the county time
        assertEquals(    "-20.05CT*", event.getImprovement("Amber Wildey", "1:29.95").toString()); // better than the county time
        assertEquals(            "",  event.getImprovement("Amber Wildey", "1.29.95").toString()); // invalid time
        assertEquals(    "-33.00rt*", event.getImprovement("Amber Wildey", "1:17.00").toString()); // better than the regional base time
        assertEquals(    "-36.00RT*", event.getImprovement("Amber Wildey", "1:14.00").toString()); // better than the regional auto time

        assertEquals("Girls 100 Backstroke", event.getName());
        assertEquals(RaceTime.create("1:30.00"), event.getCountyTime(2011)); // 9
        assertEquals(RaceTime.create("1:30.00"), event.getCountyTime(2010));
        assertEquals(RaceTime.create("1:30.00"), event.getCountyTime(2009)); // 11 youngest CT 'NEXT YEAR'
        assertEquals(RaceTime.create("1:25.00"), event.getCountyTime(2008)); // 12
        assertEquals(RaceTime.create("1:19.50"), event.getCountyTime(2007)); // 13
        assertEquals(RaceTime.create("1:17.00"), event.getCountyTime(2006));
        assertEquals(RaceTime.create("1:15.00"), event.getCountyTime(2005));
        assertEquals(RaceTime.create("1:13.50"), event.getCountyTime(2004));
        assertEquals(RaceTime.create("1:12.00"), event.getCountyTime(2003)); // 17 oldest CT
        assertEquals(RaceTime.create("1:12.00"), event.getCountyTime(2002));

        // No county times for 100 IM
        event = events.get(11);
        assertEquals(12, event.getNumber());
        assertEquals("Boys 100 IM", event.getName());
        assertNull(event.getCountyTime(2009));
    }

    @Test
    public void swimmerTest() throws IOException
    {
        Abbreviations clubAbbreviations = new Abbreviations(":Clubs.txt", null);
        Club club = new Club("Bracknell", clubAbbreviations);

        assertEquals("Mia Richardson", new Swimmer("Mia Richardson",  2001, club).getName());
        assertEquals("M Richardson",   new Swimmer("Mia Richardson",  2001, club).getStandardName(13));

        assertEquals("Mi Richardson",  new Swimmer("Mi Richardson",   2001, club).getName());
        assertEquals("Mi Richardson",  new Swimmer("Mi Richardson",   2001, club).getStandardName(13));

        assertEquals("M Richardson",   new Swimmer("Miad Richardson", 2001, club).getStandardName(13));

        assertEquals("M Richardson",   new Swimmer("Mia Richardson",  2001, club).getStandardName(12));
        assertEquals("M Richardso",    new Swimmer("Mia Richardson",  2001, club).getStandardName(11));
        assertEquals("M R",            new Swimmer("Mia Richardson",  2001, club).getStandardName(3));

        assertEquals("Elen Rance",     new Swimmer("Elen RANCE",      2001, club).getStandardName(13));
        assertEquals("Elen Rance",     new Swimmer("Elen RANCE",      2001, club).getStandardName(10));
        assertEquals("E Rance",        new Swimmer("E RANCE",         2001, club).getStandardName(9));
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
    public void eventStdNameTest() throws IOException
    {
        for (String[] pair: new String[][]{
                {"Girls 50 Free",                "Girls 50 Freestyle"},
                {"Girls Open 100m Free",         "Girls 100 Freestyle"},
                {"Girls Open 100m Freestyle",    "Girls 100 Freestyle"},
                {"Girls 100m Free",              "Girls 100 Freestyle"},
                {"Boys 100m Open Fly",           "Boys 100 Butterfly"},
                {"Boys 100m Breast",             "Boys 100 Breaststroke"},
                {"Boys 50m Back",                "Boys 50 Backstroke"},
                {"Boys 400m IM",                 "Boys 400 IM"}})
        {
            assertEquals(pair[1], Event.getStdName(pair[0]));
        }
    }

    @Test
    public void clubAbbreviationsTest() throws IOException
    {
        Abbreviations clubAbbreviations = new Abbreviations(":Clubs.txt", null);

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
        Event event = new Event(3,"Girls Open 200m IM");

        assertEquals(3, event.getNumber());
        assertEquals("Girls 200 IM", event.getName());
    }

    @Test
    public void improvementTest() throws IOException
    {
        // Tests written with 2019 as the current year.
        //   11 years is the lowest county year: 2008
        //   12 years is the lowest regional year: 2007
        Event event = new Event(3,"Girls Open 200m IM");

        Swimmer alice1 = new Swimmer("Alice", 2005, null);
        Swimmer alice2 = new Swimmer("Alice", 2005, null);
        Swimmer jane   = new Swimmer("Jane",  2005, null);
        Swimmer clare  = new Swimmer("Clare", 2005, null);
        Swimmer emma   = new Swimmer("Emma", 2005, null);
        Swimmer tess   = new Swimmer("Tess",  2005, null);
        event.add(new EventEntry(alice1, RaceTime.create("1:33.00")));
        event.add(new EventEntry(alice2, RaceTime.create("1:34.00")));
        event.add(new EventEntry(jane,   RaceTime.create("1:33.00")));
        event.add(new EventEntry(clare,  RaceTime.create("1:30.00"))); // 1 sec under county
        event.add(new EventEntry(emma,   RaceTime.create("1:32.00")));
        event.add(new EventEntry(tess,   RaceTime.create("")));
        assertEquals(        "",  event.getImprovement("Alice", "33.45").toString());   // duplicate name
        assertEquals(        "",  event.getImprovement("Jane",  "").toString());        // did not finish
        assertEquals(   "-1.00",  event.getImprovement("Jane",  "1:32.00").toString()); // faster
        assertEquals(        "",  event.getImprovement("Clare", "1:32.00").toString()); // slower
        assertEquals(   "-1.00",  event.getImprovement("Emma",  "1:31.00").toString()); // faster
        assertEquals(        "",  event.getImprovement("Tess",  "1:32.00").toString()); // new time

        TreeMap<Integer, RaceTime> countyTimes = new TreeMap<>();
        countyTimes.put(2005, RaceTime.create("1:31.0"));
        countyTimes.put(2006, RaceTime.create("1:31.5"));
        event.setCountyTimes(countyTimes);

        assertEquals(        "",  event.getImprovement("Alice", "33.45").toString());   // duplicate name
        assertEquals(        "",  event.getImprovement("Jane",  "").toString());        // did not finish
        assertEquals(   "-1.00",  event.getImprovement("Jane",  "1:32.00").toString()); // faster
        assertEquals(        "",  event.getImprovement("Clare", "1:32.00").toString()); // slower
        assertEquals(        "",  event.getImprovement("Clare", "1:31.05").toString()); // slower than county and previous county time
        assertEquals(      "CT",  event.getImprovement("Clare", "1:30.05").toString()); // slower but county
        assertEquals(      "CT",  event.getImprovement("Clare", "1:30.00").toString()); // equal but county
        assertEquals( "-0.05CT",  event.getImprovement("Clare", "1:29.95").toString()); // faster county
        assertEquals( "-1.00CT*", event.getImprovement("Emma",  "1:31.00").toString()); // new county
        assertEquals(        "",  event.getImprovement("Tess",  "1:32.00").toString()); // new time, but not county
        assertEquals(      "CT*", event.getImprovement("Tess",  "1:30.00").toString()); // new time and county

        Swimmer clare2 = new Swimmer("Clare2",2005, null);
        Swimmer emma2  = new Swimmer("Emma2",2005, null);
        Swimmer clare3 = new Swimmer("Clare3",2005, null);
        Swimmer emma3  = new Swimmer("Emma3",2005, null);
        event.add(new EventEntry(clare2, RaceTime.create("1:28.00"))); // 1 sec user regional base and 3 secs under county
        event.add(new EventEntry(emma2,  RaceTime.create("1:30.00")));
        event.add(new EventEntry(clare3, RaceTime.create("1:26.00"))); // 1 sec user regional auto and 3 secs under base
        event.add(new EventEntry(emma3,  RaceTime.create("1:28.00")));

        TreeMap<Integer, RaceTime> regionalBaseTimes = new TreeMap<>();
        regionalBaseTimes.put(2005, RaceTime.create("1:29.0")); // 2 secs faster than county
        regionalBaseTimes.put(2006, RaceTime.create("1:29.5"));
        event.setRegionalBaseTimes(regionalBaseTimes);

        TreeMap<Integer, RaceTime> regionalAutoTimes = new TreeMap<>();
        regionalAutoTimes.put(2005, RaceTime.create("1:27.0")); // 2 sec faster than base
        regionalAutoTimes.put(2006, RaceTime.create("1:27.5"));
        event.setRegionalAutoTimes(regionalAutoTimes);

        // repeat above to ensure we still get CTs
        assertEquals(        "",  event.getImprovement("Alice", "33.45").toString());   // duplicate name
        assertEquals(        "",  event.getImprovement("Jane",  "").toString());        // did not finish
        assertEquals(   "-1.00",  event.getImprovement("Jane",  "1:32.00").toString()); // faster

        assertEquals(        "",  event.getImprovement("Clare", "1:32.00").toString()); // slower
        assertEquals(        "",  event.getImprovement("Clare", "1:31.05").toString()); // slower than county and previous county time
        assertEquals(      "CT",  event.getImprovement("Clare", "1:30.05").toString()); // slower but county
        assertEquals(      "CT",  event.getImprovement("Clare", "1:30.00").toString()); // equal but county
        assertEquals( "-0.05CT",  event.getImprovement("Clare", "1:29.95").toString()); // faster county
        assertEquals( "-1.00CT*", event.getImprovement("Emma",  "1:31.00").toString()); // new county
        assertEquals(        "",  event.getImprovement("Tess",  "1:32.00").toString()); // new time, but not county
        assertEquals(      "CT*", event.getImprovement("Tess",  "1:30.00").toString()); // new time and county

        // 1:31 CT, 1:29 rt, 1:28 RT
        assertEquals(        "",  event.getImprovement("Clare2", "1:32.00").toString()); // slower than even county
        assertEquals(      "CT",  event.getImprovement("Clare2","1:29.05").toString()); // slower than regional base and previous regional base time
        assertEquals(      "rt",  event.getImprovement("Clare2","1:28.05").toString()); // slower but regional base
        assertEquals(      "rt",  event.getImprovement("Clare2","1:28.00").toString()); // equal but regional base
        assertEquals( "-0.05rt",  event.getImprovement("Clare2","1:27.95").toString()); // faster regional base
        assertEquals( "-1.00rt*", event.getImprovement("Emma2", "1:29.00").toString()); // new regional base
        assertEquals(      "CT*", event.getImprovement("Tess",  "1:30.00").toString()); // new time, but not regional base
        assertEquals(      "rt*", event.getImprovement("Tess",  "1:28.00").toString()); // new time and regional base

        assertEquals(      "RT",  event.getImprovement("Clare3","1:26.05").toString()); // slower but regional base
        assertEquals(      "RT",  event.getImprovement("Clare3","1:26.00").toString()); // equal but regional base
        assertEquals( "-0.05RT",  event.getImprovement("Clare3","1:25.95").toString()); // faster regional base
        assertEquals( "-1.00RT*", event.getImprovement("Emma3", "1:27.00").toString()); // new regional base
        assertEquals(      "rt*", event.getImprovement("Tess",  "1:28.00").toString()); // new time, but not regional base
        assertEquals(      "RT*", event.getImprovement("Tess",  "1:26.00").toString()); // new time and regional base
        assertEquals(      "RT*", event.getImprovement("TESS",  "1:26.00").toString()); // new time and regional base
    }

    @Test
    public void clubTest() throws IOException
    {
        Abbreviations clubAbbreviations = new Abbreviations(":Clubs.txt", null);
        Club club = new Club("Bracknell", clubAbbreviations);

        assertEquals("BRKS", club.getAbbreviation());
        assertEquals("Bracknell", club.getShortName());
        assertEquals("Bracknell & Wokingham SC", club.getLongName());
    }
}