package uk.org.bwscswim.scoreboard;

import javax.swing.*;

/**
 * Scoreboard with fields that overlay.
 */
public class New1Scoreboard extends AbstractScoreboard
{
    public New1Scoreboard(Config config)
    {
        super(config, "new1");

        //    1 NNNNNNNNNNNNNN CLUBBBB PLA
        //                     TIMEEEE
        //                     CLOCKKK

        GroupLayout.ParallelGroup lanes = layout.createParallelGroup();
        GroupLayout.ParallelGroup names = layout.createParallelGroup();
        GroupLayout.ParallelGroup combinedClubTimeClocks = layout.createParallelGroup();
        GroupLayout.ParallelGroup places = layout.createParallelGroup();

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        int horizontalGap = getHorizontalGap();

        GroupLayout.SequentialGroup cols = layout.createSequentialGroup();
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(combinedTitle)
                        .addGroup(cols));
        if (laneVisible)
        {
            cols.addGroup(lanes);
            cols.addGap(horizontalGap);
        }

        cols.addGroup(names);
        cols.addGap(horizontalGap);
        cols.addGroup(combinedClubTimeClocks);

        if (placeVisible)
        {
            cols.addGap(horizontalGap);
            cols.addGroup(places);
        }

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(combinedTitle)
                        .addGap(getPreLaneGap())
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
            row.addComponent(swimmer.combinedClubTimeClock);
            names.addComponent(swimmer.name);
            combinedClubTimeClocks.addComponent(swimmer.combinedClubTimeClock);

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
        return "NEW1";
    }

    @Override
    public void setClock(String clock)
    {
        super.setClock(clock);
        if (state == State.TIME_OF_DAY)
        {
            swimmers.get(2).combinedClubTimeClock.setText(this.clock.getText());
        }
    }
}
