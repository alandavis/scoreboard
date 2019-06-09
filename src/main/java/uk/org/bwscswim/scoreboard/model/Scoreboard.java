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
package uk.org.bwscswim.scoreboard.model;

import uk.org.bwscswim.scoreboard.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Scoreboard extends javax.swing.JFrame
{
    class Swimmer extends JPanel
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
    JPanel subTitleAndClock = new JPanel();
    private List<Swimmer> swimmers = new ArrayList<>();
    private boolean result;
    private final Config config;

    public Scoreboard(Config config)
    {
        this.config = config;
        Container contentPane = getContentPane();

        subTitleAndClock.setLayout(new BoxLayout(subTitleAndClock, BoxLayout.X_AXIS));
        subTitleAndClock.add(subTitle);
        subTitleAndClock.add(clock);

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(title);
        contentPane.add(subTitleAndClock);

        title.setFont(config.getFont("title"));
        subTitle.setFont(config.getFont("subTitle"));
        clock.setFont(config.getFont("clock"));

        title.setText(config.getTest("Title"));
        subTitle.setText(config.getTest("SubTitle"));
        clock.setText(config.getTest("Clock"));

        int laneCount = config.getLaneCount();
        for (int lane=0; lane<laneCount; lane++)
        {
            Swimmer swimmer = new Swimmer();
            swimmer.add(swimmer.lane);
            swimmer.add(swimmer.name);
            swimmer.add(swimmer.club);
            swimmer.add(swimmer.time);
            swimmer.add(swimmer.place);

            swimmers.add(swimmer);
            contentPane.add(swimmer);

            swimmer.lane.setFont(config.getFont("lane"));
            swimmer.name.setFont(config.getFont("name"));
            swimmer.club.setFont(config.getFont("club"));
            swimmer.time.setFont(config.getFont("time"));
            swimmer.place.setFont(config.getFont("place"));

            swimmer.lane.setText(config.getTest("Lane"));
            swimmer.name.setText(config.getTest("Name"));
            swimmer.club.setText(config.getTest("Club"));
            swimmer.time.setText(config.getTest("Time"));
            swimmer.place.setText(config.getTest("Place"));
        }
        setColors();

        exitOnEscapeOrEnter();
        pack();
    }

    private void setColors()
    {
        Container contentPane = getContentPane();
        contentPane.setBackground(config.getBackground(result));
        subTitleAndClock.setBackground(config.getBackground(result));

        title.setForeground(config.getForeground("title", result));
        title.setBackground(config.getBackground("title", result));

        subTitle.setForeground(config.getForeground("subTitle", result));
        subTitle.setBackground(config.getBackground("subTitle", result));

        clock.setForeground(config.getForeground("clock", result));
        clock.setBackground(config.getBackground("clock", result));

        for (Swimmer swimmer : swimmers)
        {
            swimmer.setBackground(config.getBackground(result));
            swimmer.lane.setForeground(config.getForeground("lane", result));
            swimmer.lane.setBackground(config.getBackground("lane", result));

            swimmer.name.setForeground(config.getForeground("name", result));
            swimmer.name.setBackground(config.getBackground("name", result));

            swimmer.club.setForeground(config.getForeground("club", result));
            swimmer.club.setBackground(config.getBackground("club", result));

            swimmer.time.setForeground(config.getForeground("time", result));
            swimmer.time.setBackground(config.getBackground("time", result));

            swimmer.place.setForeground(config.getForeground("place", result));
            swimmer.place.setBackground(config.getBackground("place", result));
        }
    }

    private void exitOnEscapeOrEnter()
    {
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e)
            {
                char c = e.getKeyChar();
                if (c == '\n' || c == 27)
                {
                    System.exit(0);
                }
                super.keyTyped(e);
            }
        });
    }

    public void makeFrameFullSize()
    {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported())
        {
            gd.setFullScreenWindow(this);
        }
        else
        {
            System.err.println("Full screen not supported by defaultScreenDevice.");
        }
    }

    public void clear()
    {
        setResult(false);
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
        this.title.setText(title);
    }

    public void setSubTitle(String subTitle)
    {
        this.subTitle.setText(subTitle);
    }

    public void setClock(String clock)
    {
        this.clock.setText(clock);
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public void setLaneValues(int line, int lane, int place, String name, String club, String time)
    {
        Swimmer swimmer = swimmers.get(line);
        swimmer.lane.setText(Integer.toString(lane));
        swimmer.place.setText(
            place <= 0 ? "" :
            place == 1 ? "1st" :
            place == 2 ? "2nd" :
            place == 3 ? "3rd" :
            place + "th");
        swimmer.name.setText(name);
        swimmer.club.setText(club);
        swimmer.time.setText(time);
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (config.isScoreboardVisible())
        {
            super.setVisible(visible);
        }
    }
}
