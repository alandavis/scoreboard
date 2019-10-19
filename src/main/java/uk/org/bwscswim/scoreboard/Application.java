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

import uk.org.bwscswim.scoreboard.event.TimeOfDayEvent;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.service.ModelHelper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Main entry class used to start the scoreboard.
 *
 * @author adavis
 */
public class Application
{
    public static void main(String args[])
    {
        Config config = new Config("config.properties");
        ModelHelper helper = null;
        try
        {
            helper = new ModelHelper("Accepted.txt",
                    "Events.txt", "Clubs.txt", "CountyTimes.txt");
            List<Event> events = helper.getEvents();
            DataReader dataReader = new DataReader(config);
            dataReader.setEvents(events);

            java.awt.EventQueue.invokeAndWait(() ->
            {
                AbstractScoreboard scoreboard1 = new Scoreboard(config, false);
//                AbstractScoreboard scoreboard2 = new Scoreboard(config, true);
                dataReader.addObserver(scoreboard1);
//                dataReader.addObserver(scoreboard2);
            });

            dataReader.readDataInBackground();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
