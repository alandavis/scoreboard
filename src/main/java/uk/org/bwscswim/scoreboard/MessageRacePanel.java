package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.event.EventPublisher;
import uk.org.bwscswim.scoreboard.event.MessageEvent;
import uk.org.bwscswim.scoreboard.event.Observer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.GroupLayout.PREFERRED_SIZE;

/**
 * @author adavis
 */
public class MessageRacePanel extends Container
{
    private JTextField title = new JTextField();
    private List<JTextField> messages = new ArrayList<>();
    JButton publishButton;
    private EventPublisher eventPublisher;
    private int numberOfClubs;

    public MessageRacePanel(Config config, Observer observer, EventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
        eventPublisher.addObserver(observer);
        int laneCount = config.getInt("laneCount", 6);

        Font titleFont = config.getMonoFont(null, "messageRaceTitle");
        Font messageFont = config.getMonoFont(null, "messageRaceMessage");
        Font publishFont = config.getFont(null, "messageRacePublishButton", Font.MONOSPACED, Font.PLAIN, 32);

        int height = config.getInt(null, null, "messageRaceHeight", 48);
        int leftGap = config.getInt(null, null, "messageRaceLeftGap", 30);
        int titleWidth = config.getInt(null, null, "messageRaceTitleWidth", 650);
        int messageWidth = config.getInt(null, null, "messageRaceMessageWidth", 650);
        int publishWidth = config.getInt(null, null, "messageRacePublishWidth", 300);
        int rightGap = config.getInt(null, null, "messageRaceRightGap", 0);

        int topGap = config.getInt(null, null, "messageRaceTopGap", 10);
        int preLaneGap = config.getInt(null, null, "messageRacePreLaneGap", 50);
        int bottomGap = config.getInt(null, null, "messageRaceBottomGap", 0);

        int minMessageDisplayTime = config.getInt(null, null, "minMessageDisplayTime", 5000);

        title.setFont(titleFont);
        title.setText("New British Record");

        publishButton = new JButton("Publish");
        publishButton.setFont(publishFont);
        publishButton.addActionListener(e ->
        {
            eventPublisher.publishEvent(new MessageEvent(minMessageDisplayTime, title.getText(),
                    messages.get(0).getText(),
                    messages.get(1).getText(),
                    messages.get(2).getText(),
                    messages.get(3).getText(),
                    messages.get(4).getText(),
                    messages.get(5).getText()));
        });

        for (int lane=1; lane<=laneCount; lane++)
        {
            JTextField message = new JTextField();
            messages.add(message);
            message.setFont(messageFont);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        GroupLayout.ParallelGroup col1 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col2 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col3 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col4 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col5 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col6 = layout.createParallelGroup();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(title, PREFERRED_SIZE, titleWidth, PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(col1)
                                .addGroup(col2)
                                .addGroup(col3)
                                .addGroup(col4)
                                .addGroup(col5)
                                .addGroup(col6)));

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup().
                                addComponent(title, PREFERRED_SIZE, height, PREFERRED_SIZE))
                        .addGap(preLaneGap)
                        .addGroup(rows));

        int lane = 1;
        for (JTextField message : messages)
        {
            GroupLayout.ParallelGroup row = layout.createParallelGroup();
            rows.addGroup(row);

            row.addComponent(message, PREFERRED_SIZE, height, PREFERRED_SIZE);
            col1.addComponent(message, PREFERRED_SIZE, messageWidth, PREFERRED_SIZE);

            if (lane++ == 4)
            {
                row.addComponent(publishButton, PREFERRED_SIZE, height, PREFERRED_SIZE);
                col6.addComponent(publishButton, PREFERRED_SIZE, publishWidth, PREFERRED_SIZE);
            }
        }
    }

    public EventPublisher getEventPublisher()
    {
        return eventPublisher;
    }
}
