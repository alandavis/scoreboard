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

import uk.org.bwscswim.scoreboard.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Scoreboard extends BaseBorad
{
    class Swimmer
    {
        private JLabel lane = new JLabel();
        private JLabel name = new JLabel();
        private JLabel club = new JLabel();
        private JLabel time = new JLabel();
        private JLabel place = new JLabel();
    }

    private JLabel title = new JLabel();
    private JLabel subTitle = new JLabel();
    private JLabel clock  = new JLabel();
    private List<Swimmer> swimmers = new ArrayList<>();

    public Scoreboard(Config config)
    {
        super(config, "scoreboard".equals(config.getDisplayName()));

        GroupLayout layout = new GroupLayout(contentPane);
        contentPane.setLayout(layout);

//        layout.setAutoCreateGaps(true);
//        layout.setAutoCreateContainerGaps(true);

        GroupLayout.ParallelGroup lanes = layout.createParallelGroup();
        GroupLayout.ParallelGroup names = layout.createParallelGroup();
        GroupLayout.ParallelGroup clubs = layout.createParallelGroup();
        GroupLayout.ParallelGroup times = layout.createParallelGroup();
        GroupLayout.ParallelGroup places = layout.createParallelGroup();

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        int horizontalGap = config.getHorizontalGap();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(title)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(subTitle)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // force apart
                                .addComponent(clock))
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(lanes)
                                .addGap(horizontalGap)
                                .addGroup(names)
                                .addGap(horizontalGap)
                                .addGroup(clubs)
                                .addGap(horizontalGap)
                                .addGroup(times)
                                .addGap(horizontalGap)
                                .addGroup(places)));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(title)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(subTitle)
                                .addComponent(clock))
                        .addGap(config.getPreLaneGap())
                        .addGroup(rows));

        Font titleFont = config.getFont("title");
        Font laneFont = config.getFont("lane");

        String testTitle = config.getTest("title");
        testTitle = testTitle.substring(0,1)+"SB "+testTitle.substring(4);
        String testSubTitle = config.getTest("subTitle");
        String testClock = config.getTest("clock");
        String testName = config.getTest("name");
        String testClub = config.getTest("club");
        String testTime = config.getTest("time");

        title.setFont(titleFont);
        subTitle.setFont(titleFont);
        clock.setFont(titleFont);

        title.setText(testTitle);
        subTitle.setText(testSubTitle);
        clock.setText(testClock);

        int laneCount = config.getLaneCount();
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = new Swimmer();
            swimmers.add(swimmer);

            rows.addGroup(layout.createParallelGroup()
                    .addComponent(swimmer.lane)
                    .addComponent(swimmer.name)
                    .addComponent(swimmer.club)
                    .addComponent(swimmer.time)
                    .addComponent(swimmer.place));

            lanes.addComponent(swimmer.lane);
            names.addComponent(swimmer.name);
            clubs.addComponent(swimmer.club);
            times.addComponent(swimmer.time);
            places.addComponent(swimmer.place);

            swimmer.lane.setFont(laneFont);
            swimmer.name.setFont(laneFont);
            swimmer.club.setFont(laneFont);
            swimmer.time.setFont(laneFont);
            swimmer.place.setFont(laneFont);

            swimmer.lane.setText(Integer.toString(lane));
            swimmer.name.setText(testName);
            swimmer.club.setText(testClub);
            swimmer.time.setText(testTime);
            swimmer.place.setText(getPlace(lane));
        }
        postConstructor();
        setVisible(true);
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
