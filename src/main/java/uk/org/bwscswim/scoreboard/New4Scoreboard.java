package uk.org.bwscswim.scoreboard;

import javax.swing.*;

/**
 * Scoreboard with fields that overlay.
 */
public class New4Scoreboard extends AbstractScoreboard
{
    public New4Scoreboard(Config config, boolean secondScreen)
    {
        super(config, "new4");
        this.secondScreen = secondScreen;
        if (secondScreen && !config.getBoolean("secondScoreboardVisible", true))
        {
            scoreboardVisible = false;
        }

        //    T1111111111111111111111111
        //    2 NNNNNNNNNNNNNN CLUBBBB P
        //                     TIMEEEE

        GroupLayout.ParallelGroup col1 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col2 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col3 = layout.createParallelGroup();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                    .addGroup(layout.createSequentialGroup()
                        .addGap(leftGap)
                        .addComponent(singleTitle))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(leftGap)
                        .addGroup(col1)
                        .addGap(horizontalGap)
                        .addGroup(col2)
                        .addGap(horizontalGap)
                        .addGroup(col3)
                        .addGap(rightGap)));

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(topGap)
                        .addComponent(singleTitle)
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

        postConstructor();
    }

    @Override
    protected String getScoreboardTLA()
    {
        return "NEW4";
    }
}
