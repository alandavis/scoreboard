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

import javax.swing.*;

import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.GroupLayout.Alignment.TRAILING;

/**
 * The scoreboard. Class provides the layout of components from super classes.
 *
 * @author adavis
 */
public class Scoreboard extends AbstractScoreboard
{
    public Scoreboard(Config config, boolean secondScreen)
    {
        super(config, secondScreen);

        layoutScoreboard();
        layoutTimeOfDay();
        postConstructor();
    }

    private void layoutScoreboard()
    {
        //    T1111111111111111111111111
        //    2 NNNNNNNNNNNNNN CLUBBBB P
        //                     TIMEEEE

        GroupLayout.ParallelGroup col1 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col2 = layout.createParallelGroup(TRAILING);
        GroupLayout.ParallelGroup col3 = layout.createParallelGroup();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(leftGap)
                                .addComponent(title))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(leftGap)
                                .addGroup(col1)
                                .addGroup(col2)
                                .addGap(horizontalGap)
                                .addGroup(col3)
                                .addGap(rightGap)));

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(topGap)
                        .addComponent(title)
                        .addGap(preLaneGap)
                        .addGroup(rows)
                        .addGap(bottomGap));

        for (Swimmer swimmer : swimmers)
        {
            GroupLayout.ParallelGroup row = layout.createParallelGroup();
            rows.addGroup(row);

            if (laneVisible)
            {
                row.addComponent(swimmer.lane);
                col1.addGroup(layout.createSequentialGroup()
                        .addComponent(swimmer.lane)
                        .addGap(horizontalGap)
                        .addComponent(swimmer.name));
            }
            else
            {
                col1.addComponent(swimmer.name);
            }

            row.addComponent(swimmer.name);
            row.addComponent(swimmer.combinedClubTimeClock);
            col2.addComponent(swimmer.combinedClubTimeClock);

            if (placeVisible)
            {
                row.addComponent(swimmer.place);
                col3.addComponent(swimmer.place);
            }
        }
    }

    private void layoutTimeOfDay()
    {
        layout2.setHorizontalGroup(
                layout2.createParallelGroup(CENTER)
                        .addGroup(layout2.createSequentialGroup()
                                .addGap(timeOfDayLeftGap)
                                .addComponent(logo)
                                .addGap(timeOfDayMiddleGap)
                                .addComponent(timeOfDay)));

        layout2.setVerticalGroup(
                layout2.createSequentialGroup()
                        .addGap(timeOfDayTopGap)
                        .addGroup(layout2.createParallelGroup(CENTER)
                            .addComponent(logo)
                            .addComponent(timeOfDay))
                        .addGap(bottomGap));
    }
}
