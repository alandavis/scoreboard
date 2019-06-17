package uk.org.bwscswim.scoreboard;

import javax.swing.*;

/**
 * AbstractScoreboard tries to be like the original hardware scoreboard at HW.
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

        int horizontalGap = getHorizontalGap();

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
                        .addGap(getPreLaneGap())
                        .addGroup(rows));

        for (Swimmer swimmer : swimmers)
        {
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
        }

        postConstructor();
    }

    @Override
    protected String getScoreboardTLA()
    {
        return "ORG";
    }
}
