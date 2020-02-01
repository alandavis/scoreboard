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
package uk.org.bwscswim.scoreboard;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import uk.org.bwscswim.scoreboard.event.EventPublisher;
import uk.org.bwscswim.scoreboard.event.LineupEvent;
import uk.org.bwscswim.scoreboard.event.RaceEvent;
import uk.org.bwscswim.scoreboard.event.RaceSplitTimeEvent;
import uk.org.bwscswim.scoreboard.event.RaceTimerEvent;
import uk.org.bwscswim.scoreboard.event.ResultEvent;
import uk.org.bwscswim.scoreboard.event.ScoreboardEvent;
import uk.org.bwscswim.scoreboard.event.TimeOfDayEvent;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.service.ModelHelper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static uk.org.bwscswim.scoreboard.State.RESULTS;
import static uk.org.bwscswim.scoreboard.State.TIME_OF_DAY;

/**
 * @author adavis
 */
public class ScoreboardTest
{
    private Config config = new Config("config.properties");
    private Text text = new Text(config);
    private EventPublisher eventPublisher = new EventPublisher();
    private AbstractScoreboard scoreboard;
    private StateTrace stateTrace = new StateTrace();
    private Sleeper sleeper;
    private ModelHelper helper;

    private List<Event> events;

    @Before
    public void setup() throws IOException, InvocationTargetException, InterruptedException
    {
        helper = new ModelHelper(":ClubsTest.txt", ":CountyTimesTest.txt",
                ":RegionalTimesTest.txt", ":AcceptedTest.txt",
                ":PBTest.txt", null, -1);
        events = helper.getEvents();
        java.awt.EventQueue.invokeAndWait(() ->
        {
            scoreboard = new Scoreboard(config, null, false, false);
            eventPublisher.addObserver(scoreboard);
            eventPublisher.setStateTrace(stateTrace);
        });
    }

    private void setText(String linesFrom0, String line11)
    {
        text.clear();
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

    private void publish(long ms, ScoreboardEvent event) throws InterruptedException
    {
        if (ms > 0)
        {
            sleeper.sleep(ms);
        }
        eventPublisher.publishEvent(event);
    }

    private void race(float speedFactor, boolean skip, int loops) throws Exception
    {
        this.sleeper = new Sleeper();
        sleeper.setSpeedFactor(speedFactor);
        stateTrace.setSleeper(sleeper);
        for (int i=loops; i>0; i--)
        {
            publish(0, new TimeOfDayEvent(0));
            publish(1000, new TimeOfDayEvent(1));
            publish(1000, new TimeOfDayEvent(2));
            if (!skip)
            {
                publish(1000, new TimeOfDayEvent(3));
                publish(1000, new TimeOfDayEvent(4));
                publish(1000, new TimeOfDayEvent(5));
                publish(1000, new TimeOfDayEvent(6));
            }
            setText("Girls 50m Backstroke                 \n" +
                            "Ev 17,  Ht 1                         \n" +
                            "                                     \n" +
                            "2  May Mackinder    WYCS             \n" +
                            "3  Holly Frampton   CROL             \n" +
                            "4  Shoshona Street  WYCS             \n" +
                            "                                     \n" +
                            "                                     \n",
                    "    0.0 ");
            publish(1000, new LineupEvent(text, 0));
            publish(1000, new LineupEvent(text, 1));
            if (!skip)
            {
                publish(1000, new LineupEvent(text, 2));
                publish(1000, new LineupEvent(text, 3));
                publish(1000, new LineupEvent(text, 4));
            }

            setText("Girls 50m Backstroke                 \n" +
                            "Ev 17,  Ht 1                         \n" +
                            "                                     \n" +
                            "2  May Mackinder    WYCS             \n" +
                            "3  Holly Frampton   CROL             \n" +
                            "4  Shoshona Street  WYCS             \n" +
                            "                                     \n" +
                            "                                     \n",
                    "    0.0 ");
            publish(10, new RaceTimerEvent("0.14"));
            publish(10, new RaceTimerEvent("0.21"));
            publish(80, new RaceTimerEvent("0.30"));
            publish(70, new RaceTimerEvent("0.37"));
            publish(50, new RaceTimerEvent("0.44"));
            publish(10, new RaceTimerEvent("0.50"));
            publish(80, new RaceTimerEvent("0.58"));
            publish(70, new RaceTimerEvent("0.66"));
            publish(50, new RaceTimerEvent("0.75"));
            publish(50, new RaceTimerEvent("0.84"));
            publish(80, new RaceTimerEvent("0.92"));
            publish(60, new RaceTimerEvent("0.95"));
            publish(40, new RaceTimerEvent("1.04"));
            if (!skip)
            {
                publish(30, new RaceTimerEvent("1.12"));
                publish(10, new RaceTimerEvent("1.21"));
                publish(80, new RaceTimerEvent("1.29"));
                publish(60, new RaceTimerEvent("1.35"));
                publish(40, new RaceTimerEvent("1.44"));
                publish(10, new RaceTimerEvent("1.50"));
                publish(80, new RaceTimerEvent("1.60"));
                publish(80, new RaceTimerEvent("1.67"));
                publish(40, new RaceTimerEvent("1.74"));
                publish(10, new RaceTimerEvent("1.80"));
                publish(70, new RaceTimerEvent("1.88"));
                publish(70, new RaceTimerEvent("1.96"));
                publish(50, new RaceTimerEvent("2.05"));
                publish(80, new RaceTimerEvent("2.12"));
                publish(80, new RaceTimerEvent("2.17"));
                publish(70, new RaceTimerEvent("2.27"));
                publish(40, new RaceTimerEvent("2.34"));
                publish(30, new RaceTimerEvent("2.43"));
                publish(80, new RaceTimerEvent("2.50"));
                publish(70, new RaceTimerEvent("2.57"));
                publish(50, new RaceTimerEvent("2.64"));
                publish(10, new RaceTimerEvent("2.71"));
                publish(10, new RaceTimerEvent("2.81"));
                publish(70, new RaceTimerEvent("2.88"));
                publish(30, new RaceTimerEvent("2.92"));
                publish(10, new RaceTimerEvent("3.01"));
                publish(80, new RaceTimerEvent("3.08"));
                publish(80, new RaceTimerEvent("3.17"));
                publish(60, new RaceTimerEvent("3.25"));
                publish(10, new RaceTimerEvent("3.30"));
                publish(80, new RaceTimerEvent("3.37"));
                publish(70, new RaceTimerEvent("3.46"));
                publish(50, new RaceTimerEvent("3.55"));
                publish(40, new RaceTimerEvent("3.63"));
                publish(20, new RaceTimerEvent("3.72"));
                publish(70, new RaceTimerEvent("3.79"));
                publish(40, new RaceTimerEvent("3.84"));
                publish(40, new RaceTimerEvent("3.93"));
                publish(20, new RaceTimerEvent("4.02"));
                publish(70, new RaceTimerEvent("4.09"));
                publish(40, new RaceTimerEvent("4.14"));
                publish(40, new RaceTimerEvent("4.23"));
                publish(10, new RaceTimerEvent("4.30"));
                publish(80, new RaceTimerEvent("4.38"));
                publish(70, new RaceTimerEvent("4.46"));
                publish(10, new RaceTimerEvent("4.51"));
                publish(10, new RaceTimerEvent("4.60"));
                publish(80, new RaceTimerEvent("4.68"));
                publish(70, new RaceTimerEvent("4.76"));
                publish(30, new RaceTimerEvent("4.83"));
                publish(70, new RaceTimerEvent("4.90"));
                publish(80, new RaceTimerEvent("4.97"));
                publish(60, new RaceTimerEvent("5.06"));
                publish(30, new RaceTimerEvent("5.12"));
                publish(30, new RaceTimerEvent("5.22"));
                publish(70, new RaceTimerEvent("5.29"));
                publish(60, new RaceTimerEvent("5.36"));
                publish(30, new RaceTimerEvent("5.42"));
                publish(10, new RaceTimerEvent("5.51"));
                publish(80, new RaceTimerEvent("5.58"));
                publish(80, new RaceTimerEvent("5.67"));
                publish(60, new RaceTimerEvent("5.76"));
                publish(30, new RaceTimerEvent("5.82"));
                publish(10, new RaceTimerEvent("5.91"));
                publish(70, new RaceTimerEvent("5.98"));
                publish(60, new RaceTimerEvent("6.06"));
                publish(30, new RaceTimerEvent("6.12"));
                publish(20, new RaceTimerEvent("6.22"));
                publish(70, new RaceTimerEvent("6.29"));
                publish(60, new RaceTimerEvent("6.35"));
                publish(30, new RaceTimerEvent("6.43"));
                publish(80, new RaceTimerEvent("6.50"));
                publish(60, new RaceTimerEvent("6.56"));
                publish(30, new RaceTimerEvent("6.62"));
                publish(40, new RaceTimerEvent("6.74"));
                publish(10, new RaceTimerEvent("6.80"));
                publish(80, new RaceTimerEvent("6.88"));
                publish(50, new RaceTimerEvent("6.94"));

                publish(10, new RaceTimerEvent("49.61"));
                publish(10, new RaceTimerEvent("49.70"));
                publish(80, new RaceTimerEvent("49.78"));
                publish(50, new RaceTimerEvent("49.84"));
                publish(40, new RaceTimerEvent("49.93"));

                publish(70, new RaceTimerEvent("50.00"));
                publish(80, new RaceTimerEvent("50.07"));
                publish(40, new RaceTimerEvent("50.14"));
                publish(40, new RaceTimerEvent("50.23"));
                publish(07, new RaceTimerEvent("50.30"));
                publish(70, new RaceTimerEvent("50.37"));
            }
            setText(4, "3  Holly Frampton   CROL    50.35 1  ");
            publish(80, new RaceSplitTimeEvent(text, 0, 4));

            publish(70, new RaceTimerEvent("50.35"));
            publish(70, new RaceTimerEvent("50.42"));
            publish(80, new RaceTimerEvent("50.50"));
            publish(70, new RaceTimerEvent("50.58"));
            if (!skip)
            {
                publish(80, new RaceTimerEvent("50.65"));
                publish(70, new RaceTimerEvent("50.73"));
                publish(70, new RaceTimerEvent("50.80"));
                publish(80, new RaceTimerEvent("50.87"));
                publish(70, new RaceTimerEvent("50.95"));
                publish(70, new RaceTimerEvent("51.02"));
                publish(80, new RaceTimerEvent("51.10"));
                publish(80, new RaceTimerEvent("51.17"));
                publish(80, new RaceTimerEvent("51.25"));
            }
            setText(3, "2  May Mackinder    WYCS    51.28 2  ");
            publish(80, new RaceSplitTimeEvent(text, 1, 3));

            publish(70, new RaceTimerEvent("51.32"));
            publish(70, new RaceTimerEvent("51.40"));
            publish(80, new RaceTimerEvent("51.47"));
            publish(80, new RaceTimerEvent("51.55"));
            publish(80, new RaceTimerEvent("51.63"));
            if (!skip)
            {
                publish(70, new RaceTimerEvent("51.70"));
                publish(80, new RaceTimerEvent("51.77"));
                publish(80, new RaceTimerEvent("51.85"));
                publish(80, new RaceTimerEvent("51.93"));
                publish(70, new RaceTimerEvent("52.00"));
                publish(80, new RaceTimerEvent("52.08"));
                publish(70, new RaceTimerEvent("52.15"));
                publish(80, new RaceTimerEvent("52.23"));
                publish(70, new RaceTimerEvent("52.30"));
                publish(80, new RaceTimerEvent("52.37"));
                publish(80, new RaceTimerEvent("52.45"));
                publish(70, new RaceTimerEvent("52.52"));
                publish(70, new RaceTimerEvent("52.60"));
                publish(80, new RaceTimerEvent("52.68"));
                publish(70, new RaceTimerEvent("52.75"));
                publish(80, new RaceTimerEvent("52.82"));
                publish(80, new RaceTimerEvent("52.90"));
                publish(70, new RaceTimerEvent("52.97"));
                publish(70, new RaceTimerEvent("53.05"));
            }
            setText(5, "4  Shoshona Street  WYCS    53.17 3  ");
            publish(80, new RaceSplitTimeEvent(text, 2, 5));

            publish(1860, new RaceEvent(text, 0));
            if (!skip)
            {
                publish(100, new RaceEvent(text, 1));
                publish(1000, new RaceEvent(text, 2));
            }
            setText("Girls 50m Backstroke                 \n" +
                            "Ev 17,  Ht 1                         \n" +
                            "P1  Holly Frampton   CROL    50.35 3 \n" +
                            "P2  May Mackinder    WYCS    51.28 2 \n" +
                            "P3  Shoshona Street  WYCS    53.17 4 ",
                    "");
            text.setState(RESULTS);

            publish(1000, new ResultEvent(text, 0, events));
            publish(1000, new ResultEvent(text, 1, events));
            if (!skip)
            {
                publish(1000, new ResultEvent(text, 2, events));
                publish(1000, new ResultEvent(text, 3, events));
                publish(1000, new ResultEvent(text, 4, events));
                publish(1000, new ResultEvent(text, 5, events));
                publish(1000, new ResultEvent(text, 6, events));
                publish(1000, new ResultEvent(text, 7, events));
                publish(1000, new ResultEvent(text, 8, events));
                publish(1000, new ResultEvent(text, 9, events));
            }
        }

        text.setState(TIME_OF_DAY);
        publish(1000, new TimeOfDayEvent(1));
        publish(1000, new TimeOfDayEvent(2));
        if (!skip)
        {
            publish(1000, new TimeOfDayEvent(3));
            publish(1000, new TimeOfDayEvent(4));
            publish(1000, new TimeOfDayEvent(5));
            publish(1000, new TimeOfDayEvent(6));
        }
        setText("Girls 50m Backstroke                 \n" +
                        "Ev 17,  Ht 2                         \n" +
                        "1  Annika Shenoy    WYCS             \n" +
                        "2  Lexie Baptiste   WYCS             \n" +
                        "3  Evie Pittaway    WYCS             \n" +
                        "4  Esme Thomas      BRKS             \n" +
                        "5  Phoebe Simpson   WYCS             \n" +
                        "6  Lucy Butler      WYCS             \n",
                "    0.0 ");
        publish(2000, new LineupEvent(text, 0));
        publish(1000, new LineupEvent(text, 1));
        if (!skip)
        {
            publish(1000, new LineupEvent(text, 2));
            publish(1000, new LineupEvent(text, 3));
            publish(1000, new LineupEvent(text, 4));
        }
    }

    @Ignore
    @Test
    public void longerTest() throws Exception
    {
        race(1f, false, 1);
    }

    @Ignore
    @Test
    public void skipTest() throws Exception
    {
        race(1f, true, 1);
    }

    @Ignore
    @Test
    public void fastTest() throws Exception
    {
        race(0.2f, false, 1);
    }

    @Test
    public void superFastTest() throws Exception
    {
        race(0.05f, false, 1);
    }

    @Ignore
    @Test
    public void skipLoopTest() throws Exception
    {
        race(1f, true, 5);
    }

    @Ignore
    @Test
    public void fastLoopTest() throws Exception
    {
        race(0.2f, false, 5);
    }

    @Ignore
    @Test
    public void fastSkipLoopTest() throws Exception
    {
        race(0.2f, true, 5);
    }
}
