package uk.org.bwscswim.scoreboard;

import javax.swing.*;

/**
 * Scoreboard with fields that overlay.
 */
public class New3Scoreboard extends AbstractScoreboard
{
    public New3Scoreboard(Config config)
    {
        super(config, "new3");

        //    T111111111111111 CLOCKKK
        //    2 NNNNNNNNNNNNNN CLUBBBB PLA
        //                     TIMEEEE

        GroupLayout.ParallelGroup col1 = layout.createParallelGroup()
                .addComponent(singleTitle);

        GroupLayout.ParallelGroup col2 = layout.createParallelGroup()
                .addComponent(clock);
        GroupLayout.ParallelGroup col3 = layout.createParallelGroup();

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGap(leftGap)
                        .addGroup(col1)
                        .addGap(horizontalGap)
                        .addGroup(col2)
                        .addGap(horizontalGap)
                        .addGroup(col3)
                        .addGap(rightGap));

        GroupLayout.SequentialGroup line1a = layout.createSequentialGroup()
                .addComponent(singleTitle);

        GroupLayout.SequentialGroup line1b = layout.createSequentialGroup()
                .addComponent(clock);

        GroupLayout.ParallelGroup line1 = layout.createParallelGroup()
                .addGroup(line1a)
                .addGroup(line1b);

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(topGap)
                        .addGroup(line1)
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
        return "NEW3";
    }
}
