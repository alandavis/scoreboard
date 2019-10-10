package uk.org.bwscswim.scoreboard;

import org.junit.Before;
import org.junit.Test;
import uk.org.bwscswim.scoreboard.event.LineupEvent;
import uk.org.bwscswim.scoreboard.event.RaceEvent;
import uk.org.bwscswim.scoreboard.event.RaceSplitTimeEvent;
import uk.org.bwscswim.scoreboard.event.RaceTimerEvent;
import uk.org.bwscswim.scoreboard.event.ResultEvent;
import uk.org.bwscswim.scoreboard.event.TimeOfDayEvent;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.service.ModelHelper;

import java.io.IOException;
import java.util.List;

import static uk.org.bwscswim.scoreboard.State.RESULTS;

/**
 * @author adavis
 */
public class ScoreboardTest
{
    private Config config = new Config("config.properties");
    private Text text = new Text(config);
    private AbstractScoreboard scoreboard = new Scoreboard(config, false);
    private float speedFactor = 1f;
    private ModelHelper helper;

    private List<Event> events;

    @Before
    public void setup() throws IOException
    {
        helper = new ModelHelper("AcceptedTest.txt",
                "Events.txt", "Clubs.txt", "CountyTimes.txt");
        events = helper.getEvents();
    }

    private void setText(String linesFrom0, String line11)
    {
        setText(0, linesFrom0);
        setText(11, line11);
    }

    private void setText(int lineNumber, String string)
    {
        for (String line : string.split("\n"))
        {
            text.setText(lineNumber++, line);
        }
    }

    private void sleep(long ms) throws InterruptedException
    {
        Thread.sleep((long)((double)(ms*speedFactor)));
    }

    @Test
    public void startupTest() throws Exception
    {
        setText(
 "Girls 50m Backstroke                 \n" +
            "Ev 17,  Ht 1                         \n",
     "    0.0 ");

//        speedFactor = 0.25f;
        System.out.println(new TimeOfDayEvent(0));

                          scoreboard.update(new TimeOfDayEvent(0));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(1));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(2));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(3));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(4));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(5));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(6));

        setText(
 "Girls 50m Backstroke                 \n" +
            "Ev 17,  Ht 1                         \n" +
            "                                     \n" +
            "2  May Mackinder    WYCS             \n" +
            "3  Holly Frampton   CROL             \n" +
            "4  Shoshona Street  WYCS             \n" +
            "                                     \n" +
            "                                     \n",
      "    0.0 ");
        System.out.println(new LineupEvent(text,0));
        sleep(2000); scoreboard.update(new LineupEvent(text, 0));
        sleep(1000); scoreboard.update(new LineupEvent(text, 1));
        sleep(1000); scoreboard.update(new LineupEvent(text, 2));
        sleep(1000); scoreboard.update(new LineupEvent(text, 3));
        sleep(1000); scoreboard.update(new LineupEvent(text, 4));
        
        setText(
 "Girls 50m Backstroke                 \n" +
            "Ev 17,  Ht 1                         \n" +
            "                                     \n" +
            "2  May Mackinder    WYCS             \n" +
            "3  Holly Frampton   CROL             \n" +
            "4  Shoshona Street  WYCS             \n" +
            "                                     \n" +
            "                                     \n",
            "    0.0 ");
        System.out.println(new RaceTimerEvent("0.14"));
        sleep(3810); scoreboard.update(new RaceTimerEvent("0.14"));

        sleep(40); scoreboard.update(new RaceTimerEvent("0.14"));
        sleep(10); scoreboard.update(new RaceTimerEvent("0.21"));
        sleep(80); scoreboard.update(new RaceTimerEvent("0.30"));
        sleep(70); scoreboard.update(new RaceTimerEvent("0.37"));
        sleep(50); scoreboard.update(new RaceTimerEvent("0.44"));
        sleep(10); scoreboard.update(new RaceTimerEvent("0.50"));
        sleep(80); scoreboard.update(new RaceTimerEvent("0.58"));
        sleep(70); scoreboard.update(new RaceTimerEvent("0.66"));
        sleep(50); scoreboard.update(new RaceTimerEvent("0.75"));
        sleep(50); scoreboard.update(new RaceTimerEvent("0.84"));
        sleep(80); scoreboard.update(new RaceTimerEvent("0.92"));
        sleep(60); scoreboard.update(new RaceTimerEvent("0.95"));
        sleep(40); scoreboard.update(new RaceTimerEvent("1.04"));
        sleep(30); scoreboard.update(new RaceTimerEvent("1.12"));
        sleep(10); scoreboard.update(new RaceTimerEvent("1.21"));
        sleep(80); scoreboard.update(new RaceTimerEvent("1.29"));
        sleep(60); scoreboard.update(new RaceTimerEvent("1.35"));
        sleep(40); scoreboard.update(new RaceTimerEvent("1.44"));
        sleep(10); scoreboard.update(new RaceTimerEvent("1.50"));
        sleep(80); scoreboard.update(new RaceTimerEvent("1.60"));
        sleep(80); scoreboard.update(new RaceTimerEvent("1.67"));
        sleep(40); scoreboard.update(new RaceTimerEvent("1.74"));
        sleep(10); scoreboard.update(new RaceTimerEvent("1.80"));
        sleep(70); scoreboard.update(new RaceTimerEvent("1.88"));
        sleep(70); scoreboard.update(new RaceTimerEvent("1.96"));
        sleep(50); scoreboard.update(new RaceTimerEvent("2.05"));
        sleep(80); scoreboard.update(new RaceTimerEvent("2.12"));
        sleep(80); scoreboard.update(new RaceTimerEvent("2.17"));
        sleep(70); scoreboard.update(new RaceTimerEvent("2.27"));
        sleep(40); scoreboard.update(new RaceTimerEvent("2.34"));
        sleep(30); scoreboard.update(new RaceTimerEvent("2.43"));
        sleep(80); scoreboard.update(new RaceTimerEvent("2.50"));
        sleep(70); scoreboard.update(new RaceTimerEvent("2.57"));
        sleep(50); scoreboard.update(new RaceTimerEvent("2.64"));
        sleep(10); scoreboard.update(new RaceTimerEvent("2.71"));
        sleep(10); scoreboard.update(new RaceTimerEvent("2.81"));
        sleep(70); scoreboard.update(new RaceTimerEvent("2.88"));
        sleep(30); scoreboard.update(new RaceTimerEvent("2.92"));
        sleep(10); scoreboard.update(new RaceTimerEvent("3.01"));
        sleep(80); scoreboard.update(new RaceTimerEvent("3.08"));
        sleep(80); scoreboard.update(new RaceTimerEvent("3.17"));
        sleep(60); scoreboard.update(new RaceTimerEvent("3.25"));
        sleep(10); scoreboard.update(new RaceTimerEvent("3.30"));
        sleep(80); scoreboard.update(new RaceTimerEvent("3.37"));
        sleep(70); scoreboard.update(new RaceTimerEvent("3.46"));
        sleep(50); scoreboard.update(new RaceTimerEvent("3.55"));
        sleep(40); scoreboard.update(new RaceTimerEvent("3.63"));
        sleep(20); scoreboard.update(new RaceTimerEvent("3.72"));
        sleep(70); scoreboard.update(new RaceTimerEvent("3.79"));
        sleep(40); scoreboard.update(new RaceTimerEvent("3.84"));
        sleep(40); scoreboard.update(new RaceTimerEvent("3.93"));
        sleep(20); scoreboard.update(new RaceTimerEvent("4.02"));
        sleep(70); scoreboard.update(new RaceTimerEvent("4.09"));
        sleep(40); scoreboard.update(new RaceTimerEvent("4.14"));
        sleep(40); scoreboard.update(new RaceTimerEvent("4.23"));
        sleep(10); scoreboard.update(new RaceTimerEvent("4.30"));
        sleep(80); scoreboard.update(new RaceTimerEvent("4.38"));
        sleep(70); scoreboard.update(new RaceTimerEvent("4.46"));
        sleep(10); scoreboard.update(new RaceTimerEvent("4.51"));
        sleep(10); scoreboard.update(new RaceTimerEvent("4.60"));
        sleep(80); scoreboard.update(new RaceTimerEvent("4.68"));
        sleep(70); scoreboard.update(new RaceTimerEvent("4.76"));
        sleep(30); scoreboard.update(new RaceTimerEvent("4.83"));
        sleep(70); scoreboard.update(new RaceTimerEvent("4.90"));
        sleep(80); scoreboard.update(new RaceTimerEvent("4.97"));
        sleep(60); scoreboard.update(new RaceTimerEvent("5.06"));
        sleep(30); scoreboard.update(new RaceTimerEvent("5.12"));
        sleep(30); scoreboard.update(new RaceTimerEvent("5.22"));
        sleep(70); scoreboard.update(new RaceTimerEvent("5.29"));
        sleep(60); scoreboard.update(new RaceTimerEvent("5.36"));
        sleep(30); scoreboard.update(new RaceTimerEvent("5.42"));
        sleep(10); scoreboard.update(new RaceTimerEvent("5.51"));
        sleep(80); scoreboard.update(new RaceTimerEvent("5.58"));
        sleep(80); scoreboard.update(new RaceTimerEvent("5.67"));
        sleep(60); scoreboard.update(new RaceTimerEvent("5.76"));
        sleep(30); scoreboard.update(new RaceTimerEvent("5.82"));
        sleep(10); scoreboard.update(new RaceTimerEvent("5.91"));
        sleep(70); scoreboard.update(new RaceTimerEvent("5.98"));
        sleep(60); scoreboard.update(new RaceTimerEvent("6.06"));
        sleep(30); scoreboard.update(new RaceTimerEvent("6.12"));
        sleep(20); scoreboard.update(new RaceTimerEvent("6.22"));
        sleep(70); scoreboard.update(new RaceTimerEvent("6.29"));
        sleep(60); scoreboard.update(new RaceTimerEvent("6.35"));
        sleep(30); scoreboard.update(new RaceTimerEvent("6.43"));
        sleep(80); scoreboard.update(new RaceTimerEvent("6.50"));
        sleep(60); scoreboard.update(new RaceTimerEvent("6.56"));
        sleep(30); scoreboard.update(new RaceTimerEvent("6.62"));
        sleep(40); scoreboard.update(new RaceTimerEvent("6.74"));
        sleep(10); scoreboard.update(new RaceTimerEvent("6.80"));
        sleep(80); scoreboard.update(new RaceTimerEvent("6.88"));
        sleep(50); scoreboard.update(new RaceTimerEvent("6.94"));
        sleep(10); scoreboard.update(new RaceTimerEvent("49.61"));
        sleep(10); scoreboard.update(new RaceTimerEvent("49.70"));
        sleep(80); scoreboard.update(new RaceTimerEvent("49.78"));
        sleep(50); scoreboard.update(new RaceTimerEvent("49.84"));
        sleep(40); scoreboard.update(new RaceTimerEvent("49.93"));
        sleep(70); scoreboard.update(new RaceTimerEvent("50.00"));
        sleep(80); scoreboard.update(new RaceTimerEvent("50.07"));
        sleep(40); scoreboard.update(new RaceTimerEvent("50.14"));
        sleep(40); scoreboard.update(new RaceTimerEvent("50.23"));
        sleep(07); scoreboard.update(new RaceTimerEvent("50.30"));
        sleep(70); scoreboard.update(new RaceTimerEvent("50.37"));

        text.setText(4, "3  Holly Frampton   CROL    50.35 1  ");
        System.out.println(new RaceSplitTimeEvent(text, 0, 4));
        sleep(80); scoreboard.update(new RaceSplitTimeEvent(text, 0, 4));

        sleep(70); scoreboard.update(new RaceTimerEvent("50.35"));
        sleep(70); scoreboard.update(new RaceTimerEvent("50.42"));
        sleep(80); scoreboard.update(new RaceTimerEvent("50.50"));
        sleep(70); scoreboard.update(new RaceTimerEvent("50.58"));
        sleep(80); scoreboard.update(new RaceTimerEvent("50.65"));
        sleep(70); scoreboard.update(new RaceTimerEvent("50.73"));
        sleep(70); scoreboard.update(new RaceTimerEvent("50.80"));
        sleep(80); scoreboard.update(new RaceTimerEvent("50.87"));
        sleep(70); scoreboard.update(new RaceTimerEvent("50.95"));
        sleep(70); scoreboard.update(new RaceTimerEvent("51.02"));
        sleep(80); scoreboard.update(new RaceTimerEvent("51.10"));
        sleep(80); scoreboard.update(new RaceTimerEvent("51.17"));
        sleep(80); scoreboard.update(new RaceTimerEvent("51.25"));

        text.setText(3, "2  May Mackinder    WYCS    51.28 2  ");
        sleep(80); scoreboard.update(new RaceSplitTimeEvent(text, 0, 3));

        sleep(70); scoreboard.update(new RaceTimerEvent("51.32"));
        sleep(70); scoreboard.update(new RaceTimerEvent("51.40"));
        sleep(80); scoreboard.update(new RaceTimerEvent("51.47"));
        sleep(80); scoreboard.update(new RaceTimerEvent("51.55"));
        sleep(80); scoreboard.update(new RaceTimerEvent("51.63"));
        sleep(70); scoreboard.update(new RaceTimerEvent("51.70"));
        sleep(80); scoreboard.update(new RaceTimerEvent("51.77"));
        sleep(80); scoreboard.update(new RaceTimerEvent("51.85"));
        sleep(80); scoreboard.update(new RaceTimerEvent("51.93"));
        sleep(70); scoreboard.update(new RaceTimerEvent("52.00"));
        sleep(80); scoreboard.update(new RaceTimerEvent("52.08"));
        sleep(70); scoreboard.update(new RaceTimerEvent("52.15"));
        sleep(80); scoreboard.update(new RaceTimerEvent("52.23"));
        sleep(70); scoreboard.update(new RaceTimerEvent("52.30"));
        sleep(80); scoreboard.update(new RaceTimerEvent("52.37"));
        sleep(80); scoreboard.update(new RaceTimerEvent("52.45"));
        sleep(70); scoreboard.update(new RaceTimerEvent("52.52"));
        sleep(70); scoreboard.update(new RaceTimerEvent("52.60"));
        sleep(80); scoreboard.update(new RaceTimerEvent("52.68"));
        sleep(70); scoreboard.update(new RaceTimerEvent("52.75"));
        sleep(80); scoreboard.update(new RaceTimerEvent("52.82"));
        sleep(80); scoreboard.update(new RaceTimerEvent("52.90"));
        sleep(70); scoreboard.update(new RaceTimerEvent("52.97"));
        sleep(70); scoreboard.update(new RaceTimerEvent("53.05"));

        text.setText(5, "4  Shoshona Street  WYCS    53.17 3  ");
        sleep(80); scoreboard.update(new RaceSplitTimeEvent(text, 0, 5));

        sleep(1860); scoreboard.update(new RaceEvent(text, 0));
        sleep(1860); scoreboard.update(new RaceEvent(text, 1));
        sleep(1860); scoreboard.update(new RaceEvent(text, 2));

        sleep(1000);
        sleep(6510);

        text.clear();
        setText(0,"Girls 50m Backstroke                 \n" +
                        "Ev 17,  Ht 1                         \n" +
                        "P1  Holly Frampton   CROL    50.35 3 \n" +
                        "P2  May Mackinder    WYCS    51.28 2 \n" +
                        "P3  Shoshona Street  WYCS    53.17 4 ");
        text.setState(RESULTS);

        System.out.println(new ResultEvent(text, 0, events));
        sleep(2000); scoreboard.update(new ResultEvent(text, 0, events));
        sleep(2000); scoreboard.update(new ResultEvent(text, 1, events));
        sleep(2000); scoreboard.update(new ResultEvent(text, 2, events));
        sleep(2000); scoreboard.update(new ResultEvent(text, 3, events));
        sleep(2000); scoreboard.update(new ResultEvent(text, 4, events));
        sleep(2000); scoreboard.update(new ResultEvent(text, 5, events));
        sleep(2000); scoreboard.update(new ResultEvent(text, 6, events));
        sleep(2000); scoreboard.update(new ResultEvent(text, 7, events));
        sleep(2000); scoreboard.update(new ResultEvent(text, 8, events));
        sleep(2000); scoreboard.update(new ResultEvent(text, 9, events));

//        sleep(40000);

        sleep(1000); scoreboard.update(new TimeOfDayEvent(1));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(2));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(3));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(4));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(5));
        sleep(1000); scoreboard.update(new TimeOfDayEvent(6));

        setText(
                "Girls 50m Backstroke                 \n" +
                        "Ev 17,  Ht 2                         \n" +
                        "1  Annika Shenoy    WYCS             \n" +
                        "2  Lexie Baptiste   WYCS             \n" +
                        "3  Evie Pittaway    WYCS             \n" +
                        "4  Esme Thomas      BRKS             \n" +
                        "5  Phoebe Simpson   WYCS             \n" +
                        "6  Lucy Butler      WYCS             \n",
                "    0.0 ");
        sleep(2000); scoreboard.update(new LineupEvent(text, 0));
        sleep(1000); scoreboard.update(new LineupEvent(text, 1));
        sleep(1000); scoreboard.update(new LineupEvent(text, 2));
        sleep(1000); scoreboard.update(new LineupEvent(text, 3));
        sleep(1000); scoreboard.update(new LineupEvent(text, 4));
    }
}
