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
    }

    protected JLabel title = new JLabel();
    protected JLabel subTitle = new JLabel();
    protected JLabel combinedTitle = new JLabel();
    protected JLabel clock  = new JLabel();
    protected List<Swimmer> swimmers = new ArrayList<>();

    protected GroupLayout layout = new GroupLayout(contentPane);
    protected int laneCount;

    protected Color subTitleForeground;
    protected Color combinedTitleForeground;
    protected Color clockForeground;
    protected Color nameForeground;
    protected Color clubForeground;
    protected Color timeForeground;
    protected Color placeForeground;

    protected Font titleFont;
    protected Font subTitleFont;
    protected Font combinedTitleFont;
    protected Font clockFont;
    protected Font laneFont;
    protected Font nameFont;
    protected Font clubFont;
    protected Font timeFont;
    protected Font placeFont;

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
        getTestText();
        setTestText();

        getFonts();
        setFonts();

        super.postConstructor();
    }

    protected void getTestText()
    {
        testTitle = getTest("title");
        String tla = getScoreboardTLA();
        testTitle = testTitle.substring(0, 1) + tla + testTitle.substring(tla.length() + 1);
        testSubTitle = getTest("subTitle");
        testClock = getTest("clock");
        testName = getTest("name");
        testClub = getTest("club");
        testTime = getTest("time");
    }

    protected void setTestText()
    {
        title.setText(testTitle);
        subTitle.setText(testSubTitle);
        combinedTitle.setText(testTitle+' '+testSubTitle);
        clock.setText(testClock);

        int lane=1;
        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setText(Integer.toString(lane));
            swimmer.name.setText(testName);
            swimmer.club.setText(testClub);
            swimmer.time.setText(testTime);
            swimmer.place.setText(getPlace(lane++));
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

            swimmer.lane.setBackground(background);
            swimmer.name.setBackground(background);
            swimmer.club.setBackground(background);
            swimmer.time.setBackground(background);
            swimmer.place.setBackground(background);
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
        }
    }

    public void setTitle(String title)
    {
        String text = pad(title, config.getInt("titleLength", 30));
        this.title.setText(text);
        combinedTitle.setText(text);
    }

    public void setSubTitle(String subTitle)
    {
        String text = pad(subTitle, config.getInt("subTitleLength", 17));
        this.subTitle.setText(text);
        combinedTitle.setText(title.getText()+' '+text);
    }

    public void setClock(String clock)
    {
        this.clock.setText(pad(clock, config.getInt("clockLength", 8)));
    }

    public void setLaneValues(int line, int lane, int place, String name, String club, String time)
    {
        Swimmer swimmer = swimmers.get(line);
        swimmer.lane.setText(lane == 0 ? " "  : Integer.toString(lane));
        swimmer.place.setText(getPlace(place));
        swimmer.name.setText(pad(name, config.getInt("nameLength", 16)));
        swimmer.club.setText(pad(club, config.getInt("clubLength", 4)));
        swimmer.time.setText(pad(time, config.getInt("timeLength", 8)));
    }

    protected String getPlace(int place)
    {
        return
            place <= 0 ? "   " :
            place == 1 ? "1st" :
            place == 2 ? "2nd" :
            place == 3 ? "3rd" :
            place + "th";
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
                        append("time='").append(swimmer.time.getText().trim()).append("'}");
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
