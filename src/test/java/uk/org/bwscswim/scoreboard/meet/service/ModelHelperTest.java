package uk.org.bwscswim.scoreboard.meet.service;

import org.junit.Test;
import uk.org.bwscswim.scoreboard.meet.model.Abbreviations;
import uk.org.bwscswim.scoreboard.meet.model.Club;
import uk.org.bwscswim.scoreboard.meet.model.EntryTime;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.model.EventEntry;
import uk.org.bwscswim.scoreboard.meet.model.Swimmer;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class ModelHelperTest
{
    @Test
    public void fullTest() throws IOException
    {
        ModelHelper helper = new ModelHelper("Accepted Swims data.txt",
                "EventAbbreviations.txt", "ClubAbbreviations.txt");

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
        EntryTime entryTime = entries.get(65).getEntryTime();
        assertEquals("Amber Wildey", swimmer.getName());
        assertEquals(2009,           swimmer.getYearOfBirth().intValue());
        assertEquals("Bracknell",    swimmer.getClub().getShortName());
        assertEquals("BRKS",         swimmer.getClub().getAbbreviation());
        assertEquals("1:50.00",      entryTime.toString());
    }

    @Test
    public void swimmerTest() throws IOException
    {
        Abbreviations clubAbbreviations = new Abbreviations("ClubAbbreviations.txt");
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
        assertEquals("1:23.45", new EntryTime("1:23.45").toString());
        assertEquals("1:23.45", new EntryTime("12345").toString());
        assertEquals("12345", new EntryTime("1:23.45").toDigits());
        assertEquals("12345", new EntryTime("12345").toDigits());

        assertEquals("23.45", new EntryTime("23.45").toString());
        assertEquals("23.45", new EntryTime("2345").toString());
        assertEquals("2345", new EntryTime("23.45").toDigits());
        assertEquals("2345", new EntryTime("2345").toDigits());
    }

    @Test
    public void eventAbbreviationsTest() throws IOException
    {
        Abbreviations eventAbbreviations = new Abbreviations("EventAbbreviations.txt");

        assertEquals("does not exist", eventAbbreviations.lookupAbbreviation("does not exist"));
        assertEquals("Girls 200 IM", eventAbbreviations.lookupAbbreviation("Girls Open 200m IM"));
        assertEquals("Girls 200 Breast", eventAbbreviations.lookupAbbreviation("Girls Open 200m Breaststroke"));
        assertEquals("Boys 200 Breast", eventAbbreviations.lookupAbbreviation("Boys Open 200m Breaststroke"));
    }

    @Test
    public void clubAbbreviationsTest() throws IOException
    {
        Abbreviations clubAbbreviations = new Abbreviations("ClubAbbreviations.txt");

        assertEquals("does not exist", clubAbbreviations.lookupAbbreviation("does not exist"));

        assertEquals("BRKS", clubAbbreviations.lookupAbbreviation("Bracknell"));
        assertEquals("BRKS", clubAbbreviations.lookupAbbreviation("Bracknell & Wokingham SC"));
        assertEquals("Bracknell", clubAbbreviations.lookupShortName("BRKS"));
        assertEquals("Bracknell & Wokingham SC", clubAbbreviations.lookupLongName("BRKS"));

        assertEquals("Didcot & Barramundi SC", clubAbbreviations.lookupShortName("DABS"));
        assertEquals("Didcot & Barramundi SC", clubAbbreviations.lookupLongName("DABS"));

        assertEquals("City of Southampton SC", clubAbbreviations.lookupLongName("COSS"));
    }

    @Test
    public void eventTest() throws IOException
    {
        Abbreviations eventAbbreviations = new Abbreviations("EventAbbreviations.txt");
        Event event = new Event(3,"Girls Open 200m IM", eventAbbreviations);
        assertEquals(3, event.getNumber());
        assertEquals("Girls Open 200m IM", event.getName());
        assertEquals("Ev3/23 Girls 200 IM", event.getHeading(23));

        event.add(new EventEntry(null, null));
        event.add(new EventEntry(null, null));
        assertEquals(2, event.getEntries().size());
    }

    @Test
    public void clubTest() throws IOException
    {
        Abbreviations clubAbbreviations = new Abbreviations("ClubAbbreviations.txt");
        Club club = new Club("Bracknell", clubAbbreviations);

        assertEquals("BRKS", club.getAbbreviation());
        assertEquals("Bracknell", club.getShortName());
        assertEquals("Bracknell & Wokingham SC", club.getLongName());
    }

/*

Index comes from Entry file output.txt which includes the ASA number

Event Index Event Name
23     0    Boys Open 100m Freestyle
24     0    Girls Open 100m Freestyle
3      1    Girls Open 200m Freestyle
4      1    Boys Open 200m Freestyle
13     2    Boys Open 400m Freestyle
14     2    Girls Open 400m Freestyle

5      3    Girls Open 100m Backstroke
6      3    Boys Open 100m Backstroke
15     4    Boys Open 200m Backstroke
16     4    Girls Open 200m Backstroke

9      5    Girls Open 100m Breaststroke
10     5    Boys Open 100m Breaststroke
21     6    Boys Open 200m Breaststroke
22     6    Girls Open 200m Breaststroke

17     7    Boys Open 100m Butterfly
18     7    Girls Open 100m Butterfly
1      8    Girls Open 200m Butterfly
2      8    Boys Open 200m Butterfly

11     9    Girls Open 100m IM
12     9    Boys Open 100m IM
19    10    Boys Open 200m IM
20    10    Girls Open 200m IM
7     11    Girls Open 400m IM
8     11    Boys Open 400m IM
*/
}