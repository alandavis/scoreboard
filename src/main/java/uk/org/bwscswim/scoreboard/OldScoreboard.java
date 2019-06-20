package uk.org.bwscswim.scoreboard;

import javax.swing.*;

/**
 * AbstractScoreboard tries to be like the original Excel version.
 */
public class OldScoreboard extends AbstractScoreboard
{
    protected JLabel lanePlaces = new JLabel();

    public OldScoreboard(Config config)
    {
        super(config, "old");

        GroupLayout.ParallelGroup lanes1 = layout.createParallelGroup();
        GroupLayout.ParallelGroup places1 = layout.createParallelGroup();
        GroupLayout.ParallelGroup times1 = layout.createParallelGroup();
        GroupLayout.ParallelGroup lanes2 = layout.createParallelGroup();
        GroupLayout.ParallelGroup places2 = layout.createParallelGroup();
        GroupLayout.ParallelGroup times2 = layout.createParallelGroup();

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        int horizontalGap = getHorizontalGap();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(combinedTitle)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(lanes1)
                                .addGap(horizontalGap)
                                .addGroup(places1)
                                .addGap(horizontalGap)
                                .addGroup(times1)
                                .addGap(horizontalGap)
                                .addGroup(lanes2)
                                .addGap(horizontalGap)
                                .addGroup(places2)
                                .addGap(horizontalGap)
                                .addGroup(times2)).
                        addGroup(layout.createSequentialGroup()
                                .addComponent(clock)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE) // force apart
                                .addComponent(lanePlaces)));
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(combinedTitle)
                        .addGap(getPreLaneGap())
                        .addGroup(rows)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(clock)
                                .addComponent(lanePlaces)));

        int halfLaneCount = laneCount / 2;
        for (int lane = 0; lane<halfLaneCount; lane++)
        {
            Swimmer swimmer1 = swimmers.get(lane);
            Swimmer swimmer2 = swimmers.get(lane+halfLaneCount);
            rows.addGroup(layout.createParallelGroup()
                    .addComponent(swimmer1.lane)
                    .addComponent(swimmer1.place)
                    .addComponent(swimmer1.time)
                    .addComponent(swimmer2.lane)
                    .addComponent(swimmer2.place)
                    .addComponent(swimmer2.time)
            );

             lanes1.addComponent(swimmer1.lane);
            places1.addComponent(swimmer1.place);
             times1.addComponent(swimmer1.time);
             lanes2.addComponent(swimmer2.lane);
            places2.addComponent(swimmer2.place);
             times2.addComponent(swimmer2.time);
        }

        postConstructor();
    }

    @Override
    protected void setTestText()
    {
        super.setTestText();
        setLanePlaces();
    }

    @Override
    protected void setFonts()
    {
        super.setFonts();
        lanePlaces.setFont(placeFont);
    }

    @Override
    protected void setColors()
    {
        super.setColors();
        lanePlaces.setForeground(placeForeground);
        lanePlaces.setBackground(background);
    }

    @Override
    protected String getScoreboardTLA()
    {
        return "OLD";
    }

    @Override
    public void clear()
    {
        super.clear();
        lanePlaces.setText("");
    }

    @Override
    protected String getPlace(int place)
    {
        return place <= 0 ? " " : Integer.toString(place);
    }

    public void setLaneValues(int index, int lane, int place, String name, String club, String time)
    {
        super.setLaneValues(index, lane, place, name, club, time);
        setLanePlaces();
    }

    private void setLanePlaces()
    {
        if (state != State.RESULT)
        {
            StringBuilder sb = new StringBuilder("");
            for (Swimmer swimmer : swimmers)
            {
                String text = swimmer.place.getText();
                text = text.isEmpty() ? " " : text;
                sb.append(text).append(' ');
            }
            lanePlaces.setText(sb.toString());
        }
    }
}
