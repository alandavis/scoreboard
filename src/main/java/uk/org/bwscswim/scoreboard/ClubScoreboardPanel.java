package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.event.ClubEvent;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;
import static javax.swing.GroupLayout.PREFERRED_SIZE;

/**
 * @author adavis
 */
public class ClubScoreboardPanel extends JPanel
{
    private static class Club
    {
        protected JLabel lane = new JLabel();
        protected JLabel name = new JLabel();
        protected JLabel score = new JLabel("", SwingConstants.RIGHT);
        protected JLabel place = new JLabel("", SwingConstants.CENTER);
    }

    private JLabel title = new JLabel();
    private List<Club> clubs = new ArrayList<>();

    public ClubScoreboardPanel(Config config)
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

        Font titleFont = config.getFont(null, "title");
        Font laneFont = config.getFont(null, "lane");
        Font nameFont = config.getFont(null, "name");
        Font clubTimeFont = config.getFont(null, "clubTime");
        Font placeFont = config.getFont(null, "place");

        int laneCount = config.getInt("laneCount", 6);

        setBackground(BLACK);
        title.setForeground(YELLOW);
        title.setFont(titleFont);

        for (int lane=1; lane<=laneCount; lane++)
        {
            Club club = new Club();
            clubs.add(club);

            club.lane.setFont(laneFont);
            club.name.setFont(nameFont);
            club.score.setFont(clubTimeFont);
            club.place.setFont(placeFont);

            club.lane.setForeground(WHITE);
            club.name.setForeground(WHITE);
            club.score.setForeground(WHITE);
            club.place.setForeground(YELLOW);

            club.name.setOpaque(false);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

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

    public void update(ClubEvent event)
    {
        this.title.setText(Scoreboard.trim(event.getTitle(), 29));

        int to = event.getLaneCount();
        for (int laneIndex = 0; laneIndex < to; laneIndex++)
        {
            Club club = clubs.get(laneIndex);
            int lane = event.getLane(laneIndex);
            String laneText = lane <= 0 ? "" : Integer.toString(lane);
            club.lane.setText(laneText);
            Scoreboard.setTrimmedText(club.name, event.getName(laneIndex));
            int place = event.getPlace(laneIndex);
            club.place.setText(place <= 0 ? " " : Integer.toString(place));
            club.score.setText(event.getScore(laneIndex));
        }
    }
}
