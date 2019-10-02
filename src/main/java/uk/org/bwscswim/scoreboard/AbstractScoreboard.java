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

    class Swimmer
    {
        protected JLabel lane = new JLabel();
        protected JLabel name = new JLabel();
        protected JLabel club = new JLabel();
        protected JLabel time = new JLabel();
        protected JLabel place = new JLabel();
        protected JLabel combinedClubTimeClock = new JLabel();
    }

    protected JLabel title = new JLabel();
    protected JLabel subTitle = new JLabel();
    protected JLabel singleTitle = new JLabel();
    protected JLabel clock  = new JLabel();
    protected List<Swimmer> swimmers = new ArrayList<>();

    protected GroupLayout layout = new GroupLayout(contentPane);
    protected int laneCount;

    protected boolean combinedClubTimeClockEnabledabledClock;

    protected boolean laneVisible;
    protected boolean placeVisible;

    protected int topGap;
    protected int bottomGap;
    protected int leftGap;
    protected int rightGap;
    protected int horizontalGap;
    protected int preLaneGap;

    private int titleLength;
    private int subTitleLength;
    private int singleTitleLength;
    private int clockLength;
    private int laneLength;
    private int nameLength;
    private int clubLength;
    private int timeLength;
    private int placeLength;
    private int combinedClubTimeClockLength;

    private Color subTitleForeground;
    private Color singleTitleForeground;
    private Color clockForeground;
    private Color nameForeground;
    private Color clubForeground;
    private Color timeForeground;
    private Color placeForeground;
    private Color combinedClubTimeClockForeground;

    private Font titleFont;
    private Font subTitleFont;
    private Font singleTitleFont;
    private Font clockFont;
    private Font laneFont;
    private Font nameFont;
    private Font clubFont;
    private Font timeFont;
    private Font placeFont;
    private Font combinedClubTimeClockFont;

    private String testTitle;
    private String testSubTitle;
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

        contentPane.setLayout(layout);
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
        topGap = getTopGap();
        bottomGap = getBottomGap();
        leftGap = getLeftGap();
        rightGap = getRightGap();
        horizontalGap = getHorizontalGap();
        preLaneGap = getPreLaneGap();
    }

    private void getLengths()
    {
        titleLength = config.getInt(null, null, "titleLength", 30);
        subTitleLength = config.getInt(null, null, "subTitleLength", 17);
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
        testTitle = pad(getTest("title"), titleLength, 't');
        testSubTitle = pad(getTest("subTitle"), subTitleLength, 's');
        testSingleTitle = pad(getTest("singleTitle"), singleTitleLength, 's');
        testClock = pad(getTest("clock"), clockLength, 'c');
        testName = pad(getTest("name"), nameLength, 'n');
        testClub = pad(getTest("club"), clubLength, 'c');
        testTime = pad(getTest("time"), timeLength, 't');
    }

    private void setTestText()
    {
        title.setText(testTitle);
        subTitle.setText(testSubTitle);
        singleTitle.setText(testSingleTitle);
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
        titleFont = config.getFont(state, "title");
        subTitleFont = config.getFont(state, "subTitle");
        singleTitleFont = config.getFont(state, "singleTitle");
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
        title.setFont(titleFont);
        subTitle.setFont(subTitleFont);
        singleTitle.setFont(singleTitleFont);
        clock.setFont(clockFont);

        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setFont(laneFont);
            swimmer.name.setFont(nameFont);
            swimmer.club.setFont(clubFont);
            swimmer.time.setFont(timeFont);
            swimmer.place.setFont(placeFont);
            swimmer.combinedClubTimeClock.setFont(combinedClubTimeClockFont);
        }
    }

    @Override
    protected void getColors()
    {
        super.getColors();

        subTitleForeground = config.getColor(state, null, "subTitle.foreground", Color.YELLOW);
        singleTitleForeground = config.getColor(state, null, "singleTitle.foreground", Color.YELLOW);
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

        title.setForeground(titleForeground);
        subTitle.setForeground(subTitleForeground);
        singleTitle.setForeground(singleTitleForeground);
        clock.setForeground(clockForeground);

        title.setBackground(background);
        subTitle.setBackground(background);
        singleTitle.setBackground(background);
        clock.setBackground(background);

        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setForeground(laneForeground);
            swimmer.name.setForeground(nameForeground);
            swimmer.club.setForeground(clubForeground);
            swimmer.time.setForeground(timeForeground);
            swimmer.place.setForeground(placeForeground);
            swimmer.combinedClubTimeClock.setForeground(combinedClubTimeClockForeground);

            swimmer.lane.setBackground(background);
            swimmer.name.setBackground(background);
            swimmer.club.setBackground(background);
            swimmer.time.setBackground(background);
            swimmer.place.setBackground(background);
            swimmer.combinedClubTimeClock.setBackground(background);
        }
    }

    private int getTopGap()
    {
        return config.getInt(null, null, "topGap", 10);
    }

    private int getBottomGap()
    {
        return config.getInt(null, null, "bottomGap", 0);
    }

    private int getLeftGap()
    {
        return config.getInt(null, null, "leftGap", 30);
    }

    private int getRightGap()
    {
        return config.getInt(null, null, "rightGap", 0);
    }

    private int getHorizontalGap()
    {
        return config.getInt(null, null, "horizontalGap", 20);
    }

    private int getPreLaneGap()
    {
        return config.getInt(null, null, "preLaneGap", 10);
    }

    public void clear()
    {
        setTitle("");
        setSubTitle("");
        setClock("");
        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setText("");
            swimmer.name.setText("");
            swimmer.club.setText("");
            swimmer.time.setText("");
            swimmer.place.setText("");
            swimmer.combinedClubTimeClock.setText("");
        }
    }

    public void setTitle(String title)
    {
        String text = pad(title, titleLength);
        this.title.setText(text);
        singleTitle.setText(text);
    }

    void setSubTitle(String subTitle)
    {
        String text = pad(subTitle, subTitleLength);
        this.subTitle.setText(text);

        text = title.getText();
        if (subTitle.startsWith("Ev "))
        {
            int i = subTitle.indexOf(",  Ht ");
            text = subTitle.substring(3, i)+"/"+subTitle.substring(i+6).trim()+" "+text;
        }
        text = pad(text, singleTitleLength);
        singleTitle.setText(text);
    }

    void setClock(String clock)
    {
        String clockText = pad(clock, clockLength);
        this.clock.setText(clockText);
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = swimmers.get(lane-1);
            setCombinedClubTimeClock(lane, swimmer);
        }
    }

    void setLaneValues(int index, int lane, int place, String name, String club, String time)
    {
        Swimmer swimmer = swimmers.get(index);
        swimmer.lane.setText(lane == 0 ? " "  : Integer.toString(lane));
        swimmer.place.setText(getPlace(place));
        swimmer.name.setText(pad(name, nameLength));
        swimmer.club.setText(pad(club, clubLength));
        swimmer.time.setText(pad(time, timeLength));
        setCombinedClubTimeClock(lane, swimmer);
    }

    private void setCombinedClubTimeClock(int lane, Swimmer swimmer)
    {
        String combinedClubTimeClockText = "";
        if (state != TIME_OF_DAY)
        {
            String clubText = swimmer.club.getText().trim();
            String timeText = swimmer.time.getText().trim();
            String clockText = state == LINEUP || state == LINEUP_COMPLETE ? "" : clock.getText().trim();
            combinedClubTimeClockText =
                    !timeText.isEmpty() ? timeText :
                    clockText.isEmpty() ? clubText :
                    combinedClubTimeClockEnabledabledClock && lane == getLaneOfFirstBlankTime() ? clockText :
                    "";
            if (!combinedClubTimeClockText.isEmpty() &&
                (!timeText.isEmpty() || !clockText.isEmpty()) &&
                combinedClubTimeClockText.charAt(combinedClubTimeClockText.length()-2) == '.')
            {
                combinedClubTimeClockText = combinedClubTimeClockText+' ';
            }
        }
        combinedClubTimeClockText = lpad(combinedClubTimeClockText, combinedClubTimeClockLength);
        swimmer.combinedClubTimeClock.setText(combinedClubTimeClockText);
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
                append("subTitle='").append(subTitle.getText().trim()).append("', ").
                append("singleTitle='").append(singleTitle.getText().trim()).append("', ").
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
        state =   event instanceof LineupEvent ? LINEUP_COMPLETE
                : event instanceof RaceEvent ? RACE
                : event instanceof RaceSplitTimeEvent ? RACE
                : RESULTS_COMPLETE;

        int from = 0;
        int to = event.getLaneCount();

        if (event instanceof RaceSplitTimeEvent)
        {
            from = ((RaceSplitTimeEvent)event).getIndexOfLaneWithSplitTime();
            to = from+1;
        }
        else
        {
            setTitle(event.getTitle());
            setSubTitle(event.getSubtitle());
            setClock(event.getClock());
        }

        for (int laneIndex = from; laneIndex < to; laneIndex++)
        {
            Swimmer swimmer = swimmers.get(laneIndex);
            swimmer.lane.setText(Integer.toString(event.getLane(laneIndex)));
            swimmer.name.setText(event.getName(laneIndex));
            swimmer.club.setText(event.getClub(laneIndex));
            swimmer.time.setText(event.getTime(laneIndex));
            swimmer.place.setText(getPlace(event.getPlace(laneIndex)));
            setCombinedClubTimeClock(laneIndex+1, swimmer);
        }

        getColors();
        setColors();
        setVisible(true);
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
