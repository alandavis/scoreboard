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
                BaseBorad scoreboard = BaseBorad.createScoreboard(config);
                DataReader dataReader = new DataReader(config, scoreboard);
                dataReader.readDataInBackground();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}
