package uk.org.bwscswim.scoreboard;

import javax.swing.*;

/**
 * Scoreboard with fields that overlay.
 */
public class New2Scoreboard extends AbstractScoreboard
{
    public New2Scoreboard(Config config)
    {
        super(config, "new2");
        int horizontalGap = getHorizontalGap();

        //    T111111111111111 CLOCKKK
        //    T222222222222222
        //    2 NNNNNNNNNNNNNN CLUBBBB PLA
        //                     TIMEEEE

        GroupLayout.ParallelGroup lanes = layout.createParallelGroup();
        GroupLayout.ParallelGroup names = layout.createParallelGroup();
        GroupLayout.ParallelGroup combinedClubTimeClocks = layout.createParallelGroup();

        GroupLayout.ParallelGroup col1 = layout.createParallelGroup()
                .addComponent(title)
                .addComponent(subTitle);

        GroupLayout.ParallelGroup col2 = layout.createParallelGroup()
                .addComponent(clock);
        GroupLayout.ParallelGroup col3 = layout.createParallelGroup();

        layout.setHorizontalGroup(
            layout.createSequentialGroup()
                .addGroup(col1)
                .addGap(horizontalGap)
                .addGroup(col2)
                .addGap(horizontalGap)
                .addGroup(col3));

        GroupLayout.SequentialGroup line1a = layout.createSequentialGroup()
                .addComponent(title)
                .addComponent(subTitle);

        GroupLayout.SequentialGroup line1b = layout.createSequentialGroup()
                .addComponent(clock);

        GroupLayout.ParallelGroup line1 = layout.createParallelGroup()
                .addGroup(line1a)
                .addGroup(line1b);

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addGroup(line1)
                .addGap(getPreLaneGap())
                .addGroup(rows));

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
        return "NEW2";
    }
}
