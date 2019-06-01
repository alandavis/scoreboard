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
    private JLabel title = new JLabel();
    private JLabel subTitle = new JLabel();
    private JLabel clock  = new JLabel();
    private List<Swimmer> swimmers;
    private boolean result;
    private final Config config;

    public Scoreboard(Config config)
    {
        this.config = config;
        Container contentPane = getContentPane();
        JPanel line1 = new JPanel();
        JPanel lanes = new JPanel();

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
//        contentPane.setBackground(config.getBackground());
        contentPane.add(title);
        contentPane.add(line1);
        contentPane.add(lanes);

        line1.setLayout(new BoxLayout(line1, BoxLayout.X_AXIS));
        line1.add(subTitle);
        line1.add(clock);

//        int lineCount = config.getLineCount();
//        lines = new ArrayList<>(lineCount);
//        for (int lineNumber=0; lineNumber<lineCount; lineNumber++)
//        {
//            JLabel line = new JLabel();
//            line.setForeground(config.getForeground(lineNumber));
//            line.setBackground(config.getBackground(lineNumber));
//            line.setFont(config.getFont(lineNumber));
//
//            int lineLength = config.getLineLength(lineNumber);
//            lineLengths.add(lineLength);
//            lines.add(line);
//
//            if (config.isLineVisible(lineNumber))
//            {
//                contentPane.add(line);
//            }
//
//            setText(lineNumber, lineLength-1, " ");
//        }

        exitOnEscapeOrEnter();
        pack();
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

    public void reset()
    {
        setResult(false);
        setTitle("");
        setSubTitle("");
        setClock("");
        swimmers = new ArrayList<>();
    }

    public void setTitle(String title)
    {
        this.title.setText(title);
        setVisible(true);
    }

    public void setSubTitle(String subTitle)
    {
        this.subTitle.setText(subTitle);
        setVisible(true);
    }

    public void setClock(String clock)
    {
        this.clock.setText(clock);
        setVisible(true);
    }

    public void setResult(boolean result)
    {
        this.result = result;
        Container contentPane = getContentPane();
//        contentPane.setBackground(config.);
    }

    public List<Swimmer> getSwimmers()
    {
        return swimmers;
    }

    public void setLaneValues(int line, int lane, int place, String name, String club, String time)
    {
        while (line >= swimmers.size())
        {
            Swimmer scoreboardSwimmer = new Swimmer();
            swimmers.add(scoreboardSwimmer);
            if (result)
            {
                scoreboardSwimmer.setPlace(swimmers.size());
            }
            else
            {
                scoreboardSwimmer.setLane(swimmers.size());
            }
        }
        Swimmer scoreboardSwimmer = swimmers.get(line);

        scoreboardSwimmer.setLane(lane);
        scoreboardSwimmer.setPlace(place);
        scoreboardSwimmer.setName(name);
        scoreboardSwimmer.setClub(club);
        scoreboardSwimmer.setTime(time);
    }

    @Override
    public String toString()
    {
        return "Scoreboard{" +
                "title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", result=" + result +
                ", clock='" + clock + '\'' +
                ", swimmers=" + swimmers +
                '}';
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
