/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2019 Bracknell and Wokingham Swimming Club (BWSC)
 * %%
 * This file is part of BWSC Scoreboard.
 *
 * BWSC Scoreboard is free software: you can redistribute it and/or modify
 * it under the terms of the LGNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BWSC Scoreboard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGNU Lesser General Public License for more details.
 *
 * You should have received a copy of the LGNU Lesser General Public License
 * along with BWSC Scoreboard.  If not, see <https://www.gnu.org/licenses/>.
 * #L%
 */
package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.meet.service.ModelHelper;

/**
 * Main entry class used to start the scoreboard.
 *
 * @author adavis
 */
public class Application
{
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(() ->
        {
            try
            {
                Config config = new Config("config.properties");
                ModelHelper helper = new ModelHelper("Accepted.txt",
                        "Events.txt", "Clubs.txt", "CountyTimes.txt");
                AbstractScoreboard scoreboard1 = new Scoreboard(config, false);
//                AbstractScoreboard scoreboard2 = new Scoreboard(config, true);
                DataReader dataReader = new DataReader(config);
                dataReader.addObserver(scoreboard1);
//                dataReader.addObserver(scoreboard2);
                dataReader.setEvents(helper.getEvents());
                dataReader.readDataInBackground();

                scoreboard1.setDataReader(dataReader);
//                scoreboard2.setDataReader(dataReader);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }
}
