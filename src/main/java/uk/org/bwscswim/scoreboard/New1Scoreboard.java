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
        GroupLayout.ParallelGroup lanes = layout.createParallelGroup();
        GroupLayout.ParallelGroup names = layout.createParallelGroup();
        GroupLayout.ParallelGroup combinedClubTimes = layout.createParallelGroup();
        GroupLayout.ParallelGroup places = layout.createParallelGroup();

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        int horizontalGap = getHorizontalGap();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(combinedTitle)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(lanes)
                                .addGap(horizontalGap)
                                .addGroup(names)
                                .addGap(horizontalGap)
                                .addGroup(combinedClubTimes)
                                .addGap(horizontalGap)
                                .addGroup(places)));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(combinedTitle)
                        .addGap(getPreLaneGap())
                        .addGroup(rows));

        for (Swimmer swimmer : swimmers)
        {
            rows.addGroup(layout.createParallelGroup()
                    .addComponent(swimmer.lane)
                    .addComponent(swimmer.name)
                    .addComponent(swimmer.combinedClubTime)
                    .addComponent(swimmer.place));

            lanes.addComponent(swimmer.lane);
            names.addComponent(swimmer.name);
            combinedClubTimes.addComponent(swimmer.combinedClubTime);
            places.addComponent(swimmer.place);
        }

        postConstructor();
    }

    @Override
    protected String getScoreboardTLA()
    {
        return "ORG";
    }

    @Override
    public void setClock(String clock)
    {
        super.setClock(clock);
        if (state == State.TIME_OF_DAY)
        {
            swimmers.get(2).combinedClubTime.setText(this.clock.getText());
        }
    }
}
