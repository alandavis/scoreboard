package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.event.TimeOfDayEvent;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static javax.swing.GroupLayout.Alignment.CENTER;

/**
 * @author adavis
 */
public class TimeOfDayPanel extends JPanel
{
    private JLabel logo  = new JLabel();
    private JLabel timeOfDay  = new JLabel();

    public TimeOfDayPanel(Config config)
    {
        int timeOfDayTopGap = config.getInt(null, null, "timeOfDayTopGap", 50);
        int timeOfDayLeftGap = config.getInt(null, null, "timeOfDayLeftGap", 50);
        int timeOfDayMiddleGap = config.getInt(null, null, "timeOfDayMiddleGap", 70);
        int timeOfDayBottomGap = config.getInt(null, null, "timeOfDayBottomGap", 0);
        Font timeOfDayFont = config.getFont(null, "timeOfDay");

        setBackground(BLACK);

        timeOfDay.setFont(timeOfDayFont);
        timeOfDay.setForeground(WHITE);

        try
        {
            logo.setIcon(new ImageIcon(FileLoader.getBytes(":Logo.jpg", config)));
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Filed to read image "+e.getMessage());
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(CENTER)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(timeOfDayLeftGap)
                                .addComponent(logo)
                                .addGap(timeOfDayMiddleGap)
                                .addComponent(timeOfDay)));

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(timeOfDayTopGap)
                        .addGroup(layout.createParallelGroup(CENTER)
                                .addComponent(logo)
                                .addComponent(timeOfDay))
                        .addGap(timeOfDayBottomGap));
    }

    public void update(TimeOfDayEvent event)
    {
        String time = event.getTimeOfDay();
        this.timeOfDay.setText(time);
    }
}