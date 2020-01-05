package uk.org.bwscswim.scoreboard;

import javax.swing.*;
import java.awt.*;

/**
 * @author adavis
 */
public class ExitPanel extends JPanel
{
    public ExitPanel(BaseScoreboard scoreboard)
    {
        JButton confirm = new JButton("Confirm Exit");
        confirm.addActionListener(event -> scoreboard.exit());

        setLayout(new GridBagLayout());
        add(confirm, new GridBagConstraints());
    }
}
