package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.model.Scoreboard;

public class Application
{
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(() ->
        {
            try
            {
                Config config = new Config("config.properties");
                Scoreboard scoreboard = new Scoreboard(config);
                RawDisplay rawDisplay = new RawDisplay(config);
                DataReader dataReader = new DataReader(config, scoreboard, rawDisplay);
                if (config.isFullScreen())
                {
                    rawDisplay.makeFrameFullSize();
                }
                scoreboard.setVisible(true);
                dataReader.readDataInBackground();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}
