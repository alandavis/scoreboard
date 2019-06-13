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

public abstract class AbstractScoreboard extends BaseBorad
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
    protected JLabel clock  = new JLabel();
    protected List<Swimmer> swimmers = new ArrayList<>();
    protected GroupLayout layout = new GroupLayout(contentPane);
    protected int laneCount;

    public AbstractScoreboard(Config config, String name)
    {
        super(config, name.equals(config.getDisplayName()));
        laneCount = config.getLaneCount();
        contentPane.setLayout(layout);
        createSwimmers();
    }

    @Override
    protected void postConstructor()
    {
        setTestText();
        setFonts();

        super.postConstructor();
    }

    protected void createSwimmers()
    {
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = new Swimmer();
            swimmers.add(swimmer);
        }
    }

    private void setTestText()
    {
        String testTitle = config.getTest("title");
        testTitle = testTitle.substring(0,1)+"SB "+testTitle.substring(4);
        String testSubTitle = config.getTest("subTitle");
        String testClock = config.getTest("clock");
        String testName = config.getTest("name");
        String testClub = config.getTest("club");
        String testTime = config.getTest("time");

        title.setText(testTitle);
        subTitle.setText(testSubTitle);
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

    private void setFonts()
    {
        Font titleFont = config.getFont("title");
        Font laneFont = config.getFont("lane");

        title.setFont(titleFont);
        subTitle.setFont(titleFont);
        clock.setFont(titleFont);

        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setFont(laneFont);
            swimmer.name.setFont(laneFont);
            swimmer.club.setFont(laneFont);
            swimmer.time.setFont(laneFont);
            swimmer.place.setFont(laneFont);
        }
    }

    @Override
    public void setColors(Color background, Color titleForeground, Color laneForeground)
    {
        title.setForeground(titleForeground);
        subTitle.setForeground(titleForeground);
        clock.setForeground(titleForeground);

        title.setBackground(background);
        subTitle.setBackground(background);
        clock.setBackground(background);

        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setForeground(laneForeground);
            swimmer.name.setForeground(laneForeground);
            swimmer.club.setForeground(laneForeground);
            swimmer.time.setForeground(laneForeground);
            swimmer.place.setForeground(laneForeground);

            swimmer.lane.setBackground(background);
            swimmer.name.setBackground(background);
            swimmer.club.setBackground(background);
            swimmer.time.setBackground(background);
            swimmer.place.setBackground(background);
        }
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
        this.title.setText(pad(title, config.getInt("titleLength", 30)));
    }

    public void setSubTitle(String subTitle)
    {
        this.subTitle.setText(pad(subTitle, config.getInt("subTitleLength", 17)));
    }

    public void setClock(String clock)
    {
        this.clock.setText(pad(clock, config.getInt("clockLength", 8)));
    }

    public void setLaneValues(int line, int lane, int place, String name, String club, String time)
    {
        Swimmer swimmer = swimmers.get(line);
        swimmer.lane.setText(Integer.toString(lane));
        swimmer.place.setText(getPlace(place));
        swimmer.name.setText(pad(name, config.getInt("nameLength", 16)));
        swimmer.club.setText(pad(club, config.getInt("clubLength", 4)));
        swimmer.time.setText(pad(time, config.getInt("timeLength", 8)));
    }

    private String getPlace(int place)
    {
        return
            place <= 0 ? "" :
            place == 1 ? "1st" :
            place == 2 ? "2nd" :
            place == 3 ? "3rd" :
            place + "th";
    }

    private String pad(String value, int length)
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
                sb.append(' ');
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
                append("result=").append(result).append(", ").
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
