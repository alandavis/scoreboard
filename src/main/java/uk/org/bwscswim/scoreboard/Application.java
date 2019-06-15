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
                String displayName = config.getDisplayName();
                BaseBorad scoreboard =
                        displayName.equalsIgnoreCase("raw") ? new RawDisplay(config) :
                        displayName.equalsIgnoreCase("old") ? new OldScorboard(config) :
                        new OriginalScoreboard(config);
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
