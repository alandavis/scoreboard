/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2019 Bracknell and Wokingham Swimming Club (BWSC)
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

import uk.org.bwscswim.scoreboard.event.LineupEvent;
import uk.org.bwscswim.scoreboard.event.Observer;
import uk.org.bwscswim.scoreboard.event.PageEvent;
import uk.org.bwscswim.scoreboard.event.RaceEvent;
import uk.org.bwscswim.scoreboard.event.RaceSplitTimeEvent;
import uk.org.bwscswim.scoreboard.event.RaceTimerEvent;
import uk.org.bwscswim.scoreboard.event.ResultEvent;
import uk.org.bwscswim.scoreboard.event.TimeOfDayEvent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static uk.org.bwscswim.scoreboard.State.LINEUP;
import static uk.org.bwscswim.scoreboard.State.LINEUP_COMPLETE;
import static uk.org.bwscswim.scoreboard.State.RACE;
import static uk.org.bwscswim.scoreboard.State.RESULTS_COMPLETE;
import static uk.org.bwscswim.scoreboard.State.TIME_OF_DAY;

/**
 * Abstract class contains fields used to display the scoreboard, but without any layout.
 *
 * @author adavis
 */
abstract class AbstractScoreboard extends BaseScoreboard implements Observer
{
    private static final long serialVersionUID = 8350711464804648105L;

    public static final String TIME_OF_DAY_PANEL = "timeOfDay";
    public static final String SCOREBOARD_PANEL = "scoreboard";

    private CardLayout cardLayout = new CardLayout();

    private Container timeOfDayPanel = new Panel();
    protected JLabel logo  = new JLabel(new ImageIcon("Logo600white.jpg"));
    protected JLabel timeOfDay  = new JLabel();
    protected GroupLayout layout2 = new GroupLayout(timeOfDayPanel);

    class Swimmer
    {
        protected JLabel lane = new JLabel();
        protected JLabel name = new JLabel();
        protected JLabel club = new JLabel();
        protected JLabel time = new JLabel();
        protected JLabel place = new JLabel();
        protected JLabel combinedClubTimeClock = new JLabel();
        protected JLabel improvement = new JLabel();
    }

    protected JLabel title = new JLabel();
    protected JLabel clock  = new JLabel();
    protected List<Swimmer> swimmers = new ArrayList<>();

    private Container scoreboardPanel = new Panel();

    protected GroupLayout layout = new GroupLayout(scoreboardPanel);
    protected int laneCount;

    protected boolean combinedClubTimeClockEnabledabledClock;

    protected boolean laneVisible;
    protected boolean placeVisible;

    protected int timeOfDayTopGap;
    protected int timeOfDayLeftGap;
    protected int timeOfDayMiddleGap;

    protected int topGap;
    protected int bottomGap;
    protected int leftGap;
    protected int rightGap;
    protected int horizontalGap;
    protected int preLaneGap;

    private int timeOfDayLength;
    private int singleTitleLength;
    private int clockLength;
    private int laneLength;
    private int nameLength;
    private int clubLength;
    private int timeLength;
    private int placeLength;
    private int combinedClubTimeClockLength;

    private Color timeOfDayForeground;
    private Color singleTitleForeground;
    private Color clockForeground;
    private Color nameForeground;
    private Color clubForeground;
    private Color timeForeground;
    private Color placeForeground;
    private Color combinedClubTimeClockForeground;

    private Font timeOfDayFont;
    private Font singleTitleFont;
    private Font clockFont;
    private Font laneFont;
    private Font nameFont;
    private Font clubFont;
    private Font timeFont;
    private Font placeFont;
    private Font combinedClubTimeClockFont;

    private String testSingleTitle;
    private String testClock;
    private String testName;
    private String testClub;
    private String testTime;

    AbstractScoreboard(Config config, boolean secondScreen)
    {
        super(config, secondScreen);

        laneCount = config.getInt("laneCount", 6);

        combinedClubTimeClockEnabledabledClock = config.getBoolean(null, null, "combinedClubTimeClock.enabledabledClock", true);

        laneVisible = config.getBoolean(null, null, "lane.visible", true);
        placeVisible = config.getBoolean(null, null, "place.visible", true);

        getGaps();
        getLengths();
        getTestText();
        getFonts();

        scoreboardPanel.setLayout(layout);
        timeOfDayPanel.setLayout(layout2);

        contentPane.setLayout(cardLayout);
        contentPane.add(scoreboardPanel, SCOREBOARD_PANEL);
        contentPane.add(timeOfDayPanel, TIME_OF_DAY_PANEL);

        createSwimmers();
    }

    private void createSwimmers()
    {
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = new Swimmer();
            swimmers.add(swimmer);
        }
    }

    @Override
    protected void postConstructor()
    {
        setTestText();
        setFonts();

        super.postConstructor();
    }

    private void getGaps()
    {
        timeOfDayTopGap = config.getInt(null, null, "timeOfDayTopGap", 50);
        timeOfDayLeftGap = config.getInt(null, null, "timeOfDayLeftGap", 50);
        timeOfDayMiddleGap = config.getInt(null, null, "timeOfDayMiddleGap", 70);

        topGap = config.getInt(null, null, "topGap", 10);
        bottomGap = config.getInt(null, null, "bottomGap", 0);
        leftGap = config.getInt(null, null, "leftGap", 30);
        rightGap = config.getInt(null, null, "rightGap", 0);
        horizontalGap = config.getInt(null, null, "horizontalGap", 20);
        preLaneGap = config.getInt(null, null, "preLaneGap", 10);
    }

    private void getLengths()
    {
        timeOfDayLength = config.getInt(null, null, "timeOfDayLength", 8);

        singleTitleLength = config.getInt(null, null, "singleTitleLength", 29);
        clockLength = config.getInt(null, null, "clockLength", 8);
        laneLength = config.getInt(null, null, "laneLength", 1);
        nameLength = config.getInt(null, null, "nameLength", 16);
        clubLength = config.getInt(null, null, "clubLength", 4);
        timeLength = config.getInt(null, null, "timeLength", 8);
        combinedClubTimeClockLength = config.getInt(null, null, "combinedClubTimeClockLength", 8);
        combinedClubTimeClockLength = Math.min(Math.max(clubLength, timeLength), combinedClubTimeClockLength);
        clubLength = Math.min(clubLength, combinedClubTimeClockLength);
        timeLength = Math.min(timeLength, combinedClubTimeClockLength);
        placeLength = config.getInt(null, null, "placeLength", 1);
    }

    private void getTestText()
    {
        testSingleTitle = pad(getTest("title"), singleTitleLength, 's');
        testClock = pad(getTest("clock"), clockLength, 'c');
        testName = pad(getTest("name"), nameLength, 'n');
        testClub = pad(getTest("club"), clubLength, 'c');
        testTime = pad(getTest("time"), timeLength, 't');
    }

    private void setTestText()
    {
        title.setText(testSingleTitle);
        clock.setText(testClock);

        int lane=0;
        for (Swimmer swimmer : swimmers)
        {
            lane++;
            swimmer.lane.setText(showTestCardFor > 0 ? Integer.toString(lane) : " ");
            swimmer.name.setText(testName);
            swimmer.club.setText(testClub);
            swimmer.time.setText(testTime);
            swimmer.place.setText(getPlace(showTestCardFor > 0 ? lane : 0));
            swimmer.combinedClubTimeClock.setText(testTime);
        }
    }

    private void getFonts()
    {
        timeOfDayFont = config.getFont(state, "timeOfDay");

        singleTitleFont = config.getFont(state, "title");
        clockFont = config.getFont(state, "clock");
        laneFont = config.getFont(state, "lane");
        nameFont = config.getFont(state, "name");
        clubFont = config.getFont(state, "club");
        timeFont = config.getFont(state, "time");
        placeFont = config.getFont(state, "place");
        combinedClubTimeClockFont = config.getFont(state, "combinedClubTimeClock");
    }

    private void setFonts()
    {
        timeOfDay.setFont(timeOfDayFont);

        title.setFont(singleTitleFont);
        clock.setFont(clockFont);

        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setFont(laneFont);
            swimmer.name.setFont(nameFont);
            swimmer.club.setFont(clubFont);
            swimmer.time.setFont(timeFont);
            swimmer.improvement.setFont(timeFont);
            swimmer.place.setFont(placeFont);
            swimmer.combinedClubTimeClock.setFont(combinedClubTimeClockFont);
        }
    }

    @Override
    protected void getColors()
    {
        super.getColors();

        timeOfDayForeground = config.getColor(state, null, "timeOfDay.foreground", Color.WHITE);

        singleTitleForeground = config.getColor(state, null, "title.foreground", Color.YELLOW);
        clockForeground = config.getColor(state, null, "clock.foreground", Color.YELLOW);
        nameForeground = config.getColor(state, null, "lane.foreground", Color.WHITE);
        clubForeground = config.getColor(state, null, "club.foreground", Color.WHITE);
        timeForeground = config.getColor(state, null, "time.foreground", Color.WHITE);
        placeForeground = config.getColor(state, null, "place.foreground", Color.YELLOW);
        combinedClubTimeClockForeground = config.getColor(state, null, "combinedClubTimeClock.foreground", Color.WHITE);
    }

    @Override
    protected void setColors()
    {
        super.setColors();

        timeOfDayPanel.setBackground(background);
        timeOfDay.setForeground(timeOfDayForeground);

        scoreboardPanel.setBackground(background);

        title.setForeground(singleTitleForeground);
        clock.setForeground(clockForeground);

        title.setBackground(background);
        clock.setBackground(background);

        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setForeground(laneForeground);
            swimmer.name.setForeground(nameForeground);
            swimmer.club.setForeground(clubForeground);
            swimmer.time.setForeground(timeForeground);
            swimmer.improvement.setForeground(timeForeground);
            swimmer.place.setForeground(placeForeground);
            swimmer.combinedClubTimeClock.setForeground(combinedClubTimeClockForeground);

            swimmer.lane.setBackground(background);
            swimmer.name.setBackground(background);
            swimmer.club.setBackground(background);
            swimmer.time.setBackground(background);
            swimmer.improvement.setBackground(background);
            swimmer.place.setBackground(background);
            swimmer.combinedClubTimeClock.setBackground(background);
        }
    }

    private boolean isCountyTime(String text)
    {
        return text.indexOf("CT") != -1 || text.indexOf("County") != -1;
    }

    public void clear()
    {
        setCombinedTitle("");
        setClock("");
        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setText("");
            swimmer.name.setText("");
            swimmer.club.setText("");
            swimmer.time.setText("");
            swimmer.improvement.setText("");
            swimmer.place.setText("");
            swimmer.combinedClubTimeClock.setText("");
        }
    }

    public void setCombinedTitle(String title)
    {
        String text = pad(title, singleTitleLength);
        this.title.setText(text);
    }

    void setClock(String clock)
    {
        String clockText = pad(clock, clockLength);
        this.clock.setText(clockText);
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = swimmers.get(lane-1);
            setCombinedClubTimeClock(lane, swimmer, -1, false);
        }
    }

    private void setCombinedClubTimeClock(int lane, Swimmer swimmer, int eventCount, boolean hasImprovments)
    {
        String combinedClubTimeClockText = "";
        if (state != TIME_OF_DAY)
        {
            String clubText = swimmer.club.getText().trim();
            String timeText = swimmer.time.getText().trim();
            String improvement = swimmer.improvement.getText().trim();
            String clockText = state == LINEUP || state == LINEUP_COMPLETE ? "" : clock.getText().trim();
            combinedClubTimeClockText =
                    !timeText.isEmpty() ? (eventCount > 5 && hasImprovments ? improvement : timeText) :
                    clockText.isEmpty() ? clubText :
                    combinedClubTimeClockEnabledabledClock && lane == getLaneOfFirstBlankTime() ? clockText :
                    "";
            if (!combinedClubTimeClockText.isEmpty() &&
                (!timeText.isEmpty() || !clockText.isEmpty()) &&
                combinedClubTimeClockText.charAt(combinedClubTimeClockText.length()-2) == '.')
            {
                combinedClubTimeClockText = combinedClubTimeClockText+' ';
            }
            combinedClubTimeClockText = lpad(combinedClubTimeClockText, combinedClubTimeClockLength);
            swimmer.combinedClubTimeClock.setText(combinedClubTimeClockText);
        }
    }

    private int getLaneOfFirstBlankTime()
    {
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = swimmers.get(lane-1);
            if (swimmer.time.getText().trim().isEmpty() && !swimmer.name.getText().trim().isEmpty())
            {
                return lane;
            }
        }
        return 0;
    }

    private String getPlace(int place)
    {
        String str =
            place <= 0 ? "   " :
            place == 1 ? "1st" :
            place == 2 ? "2nd" :
            place == 3 ? "3rd" :
            place + "th";
        return pad(str, placeLength);
    }

    private String pad(String value, int length)
    {
        return pad(value, length, ' ');
    }

    private String pad(String value, int length, char c)
    {
        if (value.length() >= length)
        {
            value = value.substring(0, length);
        }
        else
        {
            StringBuilder sb = new StringBuilder(value);
            while (sb.length() < length)
            {
                sb.append(c);
            }
            value = sb.toString();
        }
        return value;
    }

    private String lpad(String value, int length)
    {
        if (value.length() >= length)
        {
            value = value.substring(0, length);
        }
        else
        {
            StringBuilder sb = new StringBuilder(value);
            while (sb.length() < length)
            {
                sb.insert(0, ' ');
            }
            value = sb.toString();
        }
        return value;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Scoreboard{").
                append("title='").append(title.getText().trim()).append("', ").
                append("state=").append(state.name().toLowerCase()).append(", ").
                append("clock='").append(clock.getText().trim()).append("', ").
                append("swimmers=[");
        Iterator<Swimmer> iterator = swimmers.iterator();
        while (iterator.hasNext())
        {
            Swimmer swimmer = iterator.next();
            String lane = swimmer.lane.getText().trim();
            if (!lane.isEmpty())
            {
                sb.append("Swimmer{").
                        append("name='").append(swimmer.name.getText().trim()).append("', ").
                        append("club='").append(swimmer.club.getText().trim()).append("', ").
                        append("lane='").append(lane).append("', ").
                        append("place='").append(swimmer.place.getText().trim()).append("', ").
                        append("time='").append(swimmer.time.getText().trim()).append("'}").
                        append("improvment='").append(swimmer.improvement.getText().trim()).append("'}").
                        append("combinedClubTimeClock='").append(swimmer.combinedClubTimeClock.getText().trim()).append("', ");
                if (iterator.hasNext())
                {
                    sb.append(", ");
                }
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    @Override
    public void update(PageEvent event)
    {
        // TODO remove the need for the state in the rest of the code in this class.
        state =   event instanceof TimeOfDayEvent ? TIME_OF_DAY
                : event instanceof LineupEvent ? LINEUP_COMPLETE
                : event instanceof RaceEvent ? RACE
                : event instanceof RaceSplitTimeEvent ? RACE
                : RESULTS_COMPLETE;

        int eventCount = event.getCount();
        if (event instanceof TimeOfDayEvent)
        {
            background = config.getColor(state, null, "background", Color.BLACK);
            timeOfDayForeground = config.getColor(state, null, "timeOfDay.foreground", Color.WHITE);

            timeOfDayPanel.setBackground(background);
            timeOfDay.setForeground(timeOfDayForeground);

            String time = ((TimeOfDayEvent) event).getTimeOfDay();
            this.timeOfDay.setText(time);

            if (!timeOfDayPanel.isVisible())
            {
                cardLayout.show(contentPane, TIME_OF_DAY_PANEL);
            }
        }
        else
        {
            int from = 0;
            int to = event.getLaneCount();

            if (event instanceof RaceSplitTimeEvent)
            {
                from = ((RaceSplitTimeEvent) event).getIndexOfLaneWithSplitTime();
                to = from + 1;
            }
            else
            {
                setCombinedTitle(event.getCombinedTitle());
                setClock(event.getClock());
            }

            boolean hasImprovments = false;
            ResultEvent resultEvent = null;
            if (event instanceof ResultEvent)
            {
                resultEvent = (ResultEvent)event;
                for (int laneIndex = from; laneIndex < to; laneIndex++)
                {
                    Swimmer swimmer = swimmers.get(laneIndex);
                    String improvement = resultEvent.getImprovement(laneIndex);
                    swimmer.improvement.setText(improvement);
                    boolean countyTime = resultEvent.isCountyTime(laneIndex);
                    if (!improvement.isEmpty() || countyTime)
                    {
                        hasImprovments = true;
                    }
                }
            }

            for (int laneIndex = from; laneIndex < to; laneIndex++)
            {
                Swimmer swimmer = swimmers.get(laneIndex);
                int lane = event.getLane(laneIndex);
                String laneText = lane <= 0 ? "" : Integer.toString(lane);
                swimmer.lane.setText(laneText);
                swimmer.name.setText(event.getName(laneIndex));
                swimmer.club.setText(event.getClub(laneIndex));
                swimmer.time.setText(event.getTime(laneIndex));
                swimmer.place.setText(
                    eventCount > 5 && hasImprovments
                    ? (resultEvent.isCountyTime(laneIndex) ? "CT" : "")
                    : getPlace(event.getPlace(laneIndex)));
                setCombinedClubTimeClock(laneIndex + 1, swimmer, eventCount, hasImprovments);
            }
            getColors();
            setColors();

            if (!scoreboardPanel.isVisible())
            {
                cardLayout.show(contentPane, SCOREBOARD_PANEL);
            }
            setVisible(true);
        }
    }

    @Override
    public void update(RaceTimerEvent event)
    {
        // TODO remove the need for the state from the rest of the code in this class.
        state = RACE;
        setClock(event.getClock());
        setVisible(true);
    }
}
