package uk.org.bwscswim.scoreboard;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

import static java.awt.Color.WHITE;

/**
 * @author adavis
 */
public class SplashPanel extends Container
{
    private int timeOfDayMod;
    private int splashAt;
    private int splashFor;

    public SplashPanel(Config config)
    {
        timeOfDayMod = config.getInt("timeOfDayMod", 60);
        splashAt = config.getInt("splashAt", 45);
        splashFor = config.getInt("splashFor", 15);

        Color splashBackground = config.getColor("splash", "background", WHITE);
        setBackground(splashBackground);

        try
        {
            add(new JLabel(new ImageIcon(FileLoader.getBytes(":Splash.jpg", config))));
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Filed to read image "+e.getMessage());
        }
    }

    public boolean showSplash(int count)
    {
        count = count % timeOfDayMod;
        return count >= splashAt && count < (splashAt + splashFor);
    }
}
