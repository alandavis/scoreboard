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
                BaseBoard scoreboard1 = BaseBoard.createScoreboard(config, false);
                BaseBoard scoreboard2 = BaseBoard.createScoreboard(config, true);
                DataReader dataReader = new DataReader(config, scoreboard1, scoreboard2);
                dataReader.readDataInBackground();
                scoreboard1.setDataReader(dataReader);
                scoreboard2.setDataReader(dataReader);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}
