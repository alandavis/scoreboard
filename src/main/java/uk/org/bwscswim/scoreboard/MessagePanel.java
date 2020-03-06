package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.event.ClubEvent;
import uk.org.bwscswim.scoreboard.event.MessageEvent;
import uk.org.bwscswim.scoreboard.event.Observer;

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
public class MessagePanel extends JPanel
{
    private JLabel title = new JLabel();
    private List<JLabel> messages = new ArrayList<>();

    public MessagePanel(Config config)
    {
        int leftGap = config.getInt(null, null, "messageLeftGap", 60);
        int messageWidth = config.getInt(null, null, "messageWidth", 1129);
        int rightGap = config.getInt(null, null, "messageRightGap", 0);

        int topGap = config.getInt(null, null, "messageTopGap", 10);
        int preLaneGap = config.getInt(null, null, "messagePreLaneGap", 10);
        int bottomGap = config.getInt(null, null, "messageBottomGap", 0);

        Font titleFont = config.getFont(null, "title");
        Font messageFont = config.getFont(null, "message");

        int laneCount = config.getInt("laneCount", 6);

        setBackground(BLACK);
        title.setForeground(YELLOW);
        title.setFont(titleFont);

        for (int lane=1; lane<=laneCount; lane++)
        {
            JLabel message = new JLabel();
            messages.add(message);
            message.setFont(messageFont);
            message.setForeground(WHITE);
            message.setOpaque(false);
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

        for (JLabel message : messages)
        {
            GroupLayout.ParallelGroup row = layout.createParallelGroup();
            rows.addGroup(row);

            row.addComponent(message);
            col1.addComponent(message, PREFERRED_SIZE, messageWidth, PREFERRED_SIZE);
        }
    }

    public void update(MessageEvent event)
    {
        this.title.setText(Scoreboard.trim(event.getTitle(), 29));

        int laneCount = messages.size();
        for (int laneIndex = 0; laneIndex < laneCount; laneIndex++)
        {
            JLabel message = messages.get(laneIndex);
            Scoreboard.setTrimmedText(message, event.getMeessage(laneIndex));
        }
    }
}
