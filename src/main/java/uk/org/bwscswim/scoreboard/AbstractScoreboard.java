/*
 * #%L
 * BWSC AbstractScoreboard
 * %%
 * Copyright (C) 2018-2019 Bracknell and Wokingham Swimming Club (BWSC)
 * %%
 * This file is part of BWSC AbstractScoreboard.
 *
 * BWSC AbstractScoreboard is free software: you can redistribute it and/or modify
 * it under the terms of the LGNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BWSC AbstractScoreboard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGNU Lesser General Public License for more details.
 *
 * You should have received a copy of the LGNU Lesser General Public License
 * along with BWSC AbstractScoreboard.  If not, see <https://www.gnu.org/licenses/>.
 * #L%
 */
package uk.org.bwscswim.scoreboard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class AbstractScoreboard extends BaseBoard
{
    class Swimmer
    {
        protected JLabel lane = new JLabel();
        protected JLabel name = new JLabel();
        protected JLabel club = new JLabel();
        protected JLabel time = new JLabel();
        protected JLabel place = new JLabel();
        protected JLabel combinedClubTime = new JLabel();
    }

    protected JLabel title = new JLabel();
    protected JLabel subTitle = new JLabel();
    protected JLabel combinedTitle = new JLabel();
    protected JLabel clock  = new JLabel();
    protected List<Swimmer> swimmers = new ArrayList<>();

    protected GroupLayout layout = new GroupLayout(contentPane);
    protected int laneCount;

    protected int titleLength;
    protected int subTitleLength;
    protected int combinedTitleLength;
    protected int clockLength;
    protected int laneLength;
    protected int nameLength;
    protected int clubLength;
    protected int timeLength;
    protected int placeLength;
    protected int combinedClubTimeLength;

    protected Color subTitleForeground;
    protected Color combinedTitleForeground;
    protected Color clockForeground;
    protected Color nameForeground;
    protected Color clubForeground;
    protected Color timeForeground;
    protected Color placeForeground;
    protected Color combinedClubTimeForeground;

    protected Font titleFont;
    protected Font subTitleFont;
    protected Font combinedTitleFont;
    protected Font clockFont;
    protected Font laneFont;
    protected Font nameFont;
    protected Font clubFont;
    protected Font timeFont;
    protected Font placeFont;
    protected Font combinedClubTimeFont;

    protected String testTitle;
    protected String testSubTitle;
    protected String testClock;
    protected String testName;
    protected String testClub;
    protected String testTime;

    public AbstractScoreboard(Config config, String name)
    {
        super(config, name);
        laneCount = config.getInt("laneCount", 6);
        contentPane.setLayout(layout);
        createSwimmers();
        if (scoreboardVisible)
        {
            System.out.println("Using display: "+name);
        }
    }

    protected void createSwimmers()
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
        getLengths();

        getTestText();
        setTestText();

        getFonts();
        setFonts();

        super.postConstructor();
    }

    private void getLengths()
    {
        titleLength = config.getInt(name, null, null, "titleLength", 30);
        subTitleLength = config.getInt(name, null, null, "subTitleLength", 17);
        combinedTitleLength = config.getInt(name, null, null, "combinedTitleLength", 17);
        clockLength = config.getInt(name, null, null, "clockLength", 8);
        laneLength = config.getInt(name, null, null, "laneLength", 1);
        nameLength = config.getInt(name, null, null, "nameLength", 16);
        clubLength = config.getInt(name, null, null, "clubLength", 4);
        timeLength = config.getInt(name, null, null, "timeLength", 8);
        combinedClubTimeLength = config.getInt(name, null, null, "combinedClubTimeLength", 8);
        combinedClubTimeLength = Math.min(Math.max(clubLength, timeLength), combinedClubTimeLength);
        clubLength = Math.min(clubLength, combinedClubTimeLength);
        timeLength = Math.min(timeLength, combinedClubTimeLength);
        placeLength = config.getInt(name, null, null, "placeLength", 3);
    }

    protected void getTestText()
    {
        testTitle = getTest("title");
        if (testCard)
        {
            String tla = getScoreboardTLA();
            testTitle = pad(testTitle.substring(0, 1) + tla + testTitle.substring(tla.length() + 1), titleLength);
        }
        testSubTitle = pad(getTest("subTitle"), subTitleLength);
        testClock = pad(getTest("clock"), clockLength);
        testName = pad(getTest("name"), nameLength);
        testClub = pad(getTest("club"), clubLength);
        testTime = pad(getTest("time"), timeLength);
    }

    protected void setTestText()
    {
        title.setText(testTitle);
        subTitle.setText(testSubTitle);
        combinedTitle.setText(testTitle+' '+testSubTitle);
        clock.setText(testClock);

        int lane=0;
        for (Swimmer swimmer : swimmers)
        {
            lane++;
            swimmer.lane.setText(testCard ? Integer.toString(lane) : " ");
            swimmer.name.setText(testName);
            swimmer.club.setText(testClub);
            swimmer.time.setText(testTime);
            swimmer.place.setText(getPlace(testCard ? lane : 0));
            swimmer.combinedClubTime.setText(testTime);
        }
    }

    protected abstract String getScoreboardTLA();

    protected void getFonts()
    {
        titleFont = config.getFont(name, state, "title");
        subTitleFont = config.getFont(name, state, "subTitle");
        combinedTitleFont = config.getFont(name, state, "combinedTitle");
        clockFont = config.getFont(name, state, "clock");
        laneFont = config.getFont(name, state, "lane");
        nameFont = config.getFont(name, state, "name");
        clubFont = config.getFont(name, state, "club");
        timeFont = config.getFont(name, state, "time");
        placeFont = config.getFont(name, state, "place");
        combinedClubTimeFont = config.getFont(name, state, "combinedClubTime");
    }

    protected void setFonts()
    {
        title.setFont(titleFont);
        subTitle.setFont(subTitleFont);
        combinedTitle.setFont(combinedTitleFont);
        clock.setFont(clockFont);

        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setFont(laneFont);
            swimmer.name.setFont(nameFont);
            swimmer.club.setFont(clubFont);
            swimmer.time.setFont(timeFont);
            swimmer.place.setFont(placeFont);
            swimmer.combinedClubTime.setFont(combinedClubTimeFont);
        }
    }

    @Override
    protected void getColors()
    {
        super.getColors();

        subTitleForeground = config.getColor(name, state, name, "subTitle.foreground", Color.YELLOW);
        combinedTitleForeground = config.getColor(name, state, name, "combinedTitle.foreground", Color.YELLOW);
        clockForeground = config.getColor(name, state, name, "clock.foreground", Color.YELLOW);
        nameForeground = config.getColor(name, state, name, "lane.foreground", Color.WHITE);
        clubForeground = config.getColor(name, state, name, "club.foreground", Color.WHITE);
        timeForeground = config.getColor(name, state, name, "time.foreground", Color.WHITE);
        placeForeground = config.getColor(name, state, name, "place.foreground", Color.WHITE);
        combinedClubTimeForeground = config.getColor(name, state, name, "combinedClubTime.foreground", Color.WHITE);
    }

    @Override
    protected void setColors()
    {
        super.setColors();

        title.setForeground(titleForeground);
        subTitle.setForeground(subTitleForeground);
        combinedTitle.setForeground(combinedTitleForeground);
        clock.setForeground(clockForeground);

        title.setBackground(background);
        subTitle.setBackground(background);
        combinedTitle.setBackground(background);
        clock.setBackground(background);

        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setForeground(laneForeground);
            swimmer.name.setForeground(nameForeground);
            swimmer.club.setForeground(clubForeground);
            swimmer.time.setForeground(timeForeground);
            swimmer.place.setForeground(placeForeground);
            swimmer.combinedClubTime.setForeground(combinedClubTimeForeground);

            swimmer.lane.setBackground(background);
            swimmer.name.setBackground(background);
            swimmer.club.setBackground(background);
            swimmer.time.setBackground(background);
            swimmer.place.setBackground(background);
            swimmer.combinedClubTime.setBackground(background);
        }
    }

    public int getHorizontalGap()
    {
        return config.getInt(name, null, null, "horizontalGap", 40);
    }

    public int getPreLaneGap()
    {
        return config.getInt(name, null, null, "preLaneGap", 40);
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
            swimmer.combinedClubTime.setText("");
        }
    }

    public void setTitle(String title)
    {
        String text = pad(title, titleLength);
        this.title.setText(text);
        combinedTitle.setText(text);
    }

    public void setSubTitle(String subTitle)
    {
        String text = pad(subTitle, subTitleLength);
        this.subTitle.setText(text);
        combinedTitle.setText(title.getText()+' '+text);
    }

    public void setClock(String clock)
    {
        String clockText = pad(clock, clockLength);
        this.clock.setText(clockText);
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = swimmers.get(lane-1);
            setCombinedClubTime(lane, swimmer);
        }
    }

    public void setLaneValues(int index, int lane, int place, String name, String club, String time)
    {
        Swimmer swimmer = swimmers.get(index);
        swimmer.lane.setText(lane == 0 ? " "  : Integer.toString(lane));
        swimmer.place.setText(getPlace(place));
        swimmer.name.setText(pad(name, nameLength));
        swimmer.club.setText(pad(club, clubLength));
        swimmer.time.setText(pad(time, timeLength));
        setCombinedClubTime(lane, swimmer);
    }

    private void setCombinedClubTime(int lane, Swimmer swimmer)
    {
        String combinedClubTimeText = "";
        if (state != State.TIME_OF_DAY)
        {
            String clubText = swimmer.club.getText().trim();
            String timeText = swimmer.time.getText().trim();
            String clockText = state == State.LINEUP || state == State.READY ? "" : clock.getText().trim();
            combinedClubTimeText =
                    !timeText.isEmpty() ? timeText :
                    clockText.isEmpty() ? clubText :
                    lane == getLaneOfFirstBlankTime() ? clockText :
                    "";
            if (!combinedClubTimeText.isEmpty() &&
                (!timeText.isEmpty() || !clockText.isEmpty()) &&
                combinedClubTimeText.charAt(combinedClubTimeText.length()-2) == '.')
            {
                combinedClubTimeText = combinedClubTimeText+' ';
            }
        }
        combinedClubTimeText = lpad(combinedClubTimeText, combinedClubTimeLength);
        swimmer.combinedClubTime.setText(combinedClubTimeText);
    }

    private int getLaneOfFirstBlankTime()
    {
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = swimmers.get(lane-1);
            if (swimmer.time.getText().trim().isEmpty())
            {
                return lane;
            }
        }
        return 0;
    }

    protected String getPlace(int place)
    {
        String str =
            place <= 0 ? "   " :
            place == 1 ? "1st" :
            place == 2 ? "2nd" :
            place == 3 ? "3rd" :
            place + "th";
        return pad(str, placeLength);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Scoreboard{").
                append("title='").append(title.getText().trim()).append("', ").
                append("subTitle='").append(subTitle.getText().trim()).append("', ").
                append("combinedTitle='").append(combinedTitle.getText().trim()).append("', ").
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
                        append("combinedClubTime='").append(swimmer.combinedClubTime.getText().trim()).append("', ");
                if (iterator.hasNext())
                {
                    sb.append(", ");
                }
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
