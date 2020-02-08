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

import javax.swing.*;

import static javax.swing.GroupLayout.Alignment.CENTER;
import static javax.swing.GroupLayout.PREFERRED_SIZE;

/*
 * The scoreboard. Class provides the layout of components from super classes.
 *
 * @author adavis
 */
public class Scoreboard extends AbstractScoreboard
{
    public Scoreboard(Config config, DataReader dataReader, boolean useSecondScreen, boolean includeControls)
    {
        super(config, dataReader, useSecondScreen, includeControls);

        layoutScoreboard();
        layoutClubScoreboard();

        makeScoreboardVisible();
    }

    private void layoutScoreboard()
    {
        int leftGap = config.getInt(null, null, "leftGap", 30);
        int laneWidth = config.getInt(null, null, "laneWidth", 60);
        int nameWidth = config.getInt(null, null, "nameWidth", 669);
        int clubTimeWidth = config.getInt(null, null, "clubTimeWidth", 287);
        int placeWidth = config.getInt(null, null, "placeWidth", 113);
        int rightGap = config.getInt(null, null, "rightGap", 0);

        int topGap = config.getInt(null, null, "topGap", 10);
        int preLaneGap = config.getInt(null, null, "preLaneGap", 10);
        int bottomGap = config.getInt(null, null, "bottomGap", 0);

        GroupLayout layout = new GroupLayout(scoreboardPanel);
        scoreboardPanel.setLayout(layout);

        GroupLayout.ParallelGroup col1 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col2 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col3 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col4 = layout.createParallelGroup();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(leftGap)
                                .addComponent(title))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(leftGap)
                                .addGroup(col1)
                                .addGroup(col2)
                                .addGroup(col3)
                                .addGroup(col4)
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

            row.addComponent(swimmer.lane);
            col1.addComponent(swimmer.lane, PREFERRED_SIZE, laneWidth, PREFERRED_SIZE);

            row.addComponent(swimmer.name);
            col2.addComponent(swimmer.name, PREFERRED_SIZE, nameWidth, PREFERRED_SIZE);

            row.addComponent(swimmer.clubTime);
            col3.addComponent(swimmer.clubTime, PREFERRED_SIZE, clubTimeWidth, PREFERRED_SIZE);

            row.addComponent(swimmer.place);
            col4.addComponent(swimmer.place, PREFERRED_SIZE, placeWidth, PREFERRED_SIZE);
        }
    }

    private void layoutClubScoreboard()
    {
        int leftGap = config.getInt(null, null, "clubLeftGap", 30);
        int laneWidth = config.getInt(null, null, "clubLaneWidth", 60);
        int nameWidth = config.getInt(null, null, "clubNameWidth", 669);
        int clubTimeWidth = config.getInt(null, null, "clubScoreWidth", 287);
        int placeWidth = config.getInt(null, null, "clubPlaceWidth", 113);
        int rightGap = config.getInt(null, null, "clubRightGap", 0);

        int topGap = config.getInt(null, null, "clubTopGap", 10);
        int preLaneGap = config.getInt(null, null, "clubPreLaneGap", 10);
        int bottomGap = config.getInt(null, null, "clubBottomGap", 0);

        GroupLayout layout = new GroupLayout(clubScoreboardPanel);
        clubScoreboardPanel.setLayout(layout);

        GroupLayout.ParallelGroup col1 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col2 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col3 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col4 = layout.createParallelGroup();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(leftGap)
                                .addComponent(clubTitle))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(leftGap)
                                .addGroup(col1)
                                .addGroup(col2)
                                .addGroup(col3)
                                .addGroup(col4)
                                .addGap(rightGap)));

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(topGap)
                        .addComponent(clubTitle)
                        .addGap(preLaneGap)
                        .addGroup(rows)
                        .addGap(bottomGap));

        for (Club club : clubs)
        {
            GroupLayout.ParallelGroup row = layout.createParallelGroup();
            rows.addGroup(row);

            row.addComponent(club.lane);
            col1.addComponent(club.lane, PREFERRED_SIZE, laneWidth, PREFERRED_SIZE);

            row.addComponent(club.name);
            col2.addComponent(club.name, PREFERRED_SIZE, nameWidth, PREFERRED_SIZE);

            row.addComponent(club.score);
            col3.addComponent(club.score, PREFERRED_SIZE, clubTimeWidth, PREFERRED_SIZE);

            row.addComponent(club.place);
            col4.addComponent(club.place, PREFERRED_SIZE, placeWidth, PREFERRED_SIZE);
        }
    }
}
