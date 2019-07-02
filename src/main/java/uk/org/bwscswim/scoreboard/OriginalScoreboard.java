package uk.org.bwscswim.scoreboard;

import javax.swing.*;

/**
 * Scoreboard with all the fields, but a bit big.
 */
public class OriginalScoreboard extends AbstractScoreboard
{
    public OriginalScoreboard(Config config)
    {
        super(config, "original");
        GroupLayout.ParallelGroup lanes = layout.createParallelGroup();
        GroupLayout.ParallelGroup names = layout.createParallelGroup();
        GroupLayout.ParallelGroup clubs = layout.createParallelGroup();
        GroupLayout.ParallelGroup times = layout.createParallelGroup();
        GroupLayout.ParallelGroup places = layout.createParallelGroup();

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        GroupLayout.SequentialGroup cols = layout.createSequentialGroup();
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(title)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(subTitle)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // force apart
                                .addComponent(clock))
                        .addGroup(cols));
        if (laneVisible)
        {
            cols.addGroup(lanes);
            cols.addGap(horizontalGap);
        }
        cols.addGroup(names);
        cols.addGap(horizontalGap);
        cols.addGroup(clubs);
        cols.addGap(horizontalGap);
        cols.addGroup(times);
        if (placeVisible)
        {
            cols.addGap(horizontalGap);
            cols.addGroup(places);
        }

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(title)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(subTitle)
                                .addComponent(clock))
                        .addGap(preLaneGap)
                        .addGroup(rows));

        for (Swimmer swimmer : swimmers)
        {
            GroupLayout.ParallelGroup row = layout.createParallelGroup();
            rows.addGroup(row);

            if (laneVisible)
            {
                row.addComponent(swimmer.lane);
                lanes.addComponent(swimmer.lane);
            }

            row.addComponent(swimmer.name);
            row.addComponent(swimmer.club);
            row.addComponent(swimmer.time);

            names.addComponent(swimmer.name);
            clubs.addComponent(swimmer.club);
            times.addComponent(swimmer.time);

            if (placeVisible)
            {
                row.addComponent(swimmer.place);
                places.addComponent(swimmer.place);
            }
        }

        postConstructor();
    }

    @Override
    protected String getScoreboardTLA()
    {
        return "ORG";
    }
}
