package uk.org.bwscswim.scoreboard;

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
                dataReader.readDataInBackground();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}
