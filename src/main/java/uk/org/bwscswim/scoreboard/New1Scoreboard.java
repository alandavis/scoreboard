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
        GroupLayout.ParallelGroup combinedClubTimeClocks = layout.createParallelGroup();
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
                                .addGroup(combinedClubTimeClocks)
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
                    .addComponent(swimmer.combinedClubTimeClock)
                    .addComponent(swimmer.place));

            lanes.addComponent(swimmer.lane);
            names.addComponent(swimmer.name);
            combinedClubTimeClocks.addComponent(swimmer.combinedClubTimeClock);
            places.addComponent(swimmer.place);
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
