/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2020 Bracknell and Wokingham Swimming Club (BWSC)
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

import uk.org.bwscswim.scoreboard.event.EventPublisher;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.service.ModelHelper;

import java.awt.*;
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
            helper = new ModelHelper(config);
            List<Event> events = helper.getEvents();
            List<String> clubEvents = helper.getClubEvents();
            DataReader dataReader = new DataReader(config);
            dataReader.setEvents(events);

            java.awt.EventQueue.invokeAndWait(() ->
            {
                boolean multipleScreens = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length > 1;
                Scoreboard controlScoreboard = new Scoreboard(config, dataReader, clubEvents, false, true);
                if (multipleScreens)
                {
                    Scoreboard secondScoreboard = new Scoreboard(config, dataReader, null, true, false);
                    EventPublisher clubEventPublisher = controlScoreboard.getClubEventPublisher();
                    clubEventPublisher.addObserver(secondScoreboard);
                }
                controlScoreboard.requestFocus();
            });

            if (!config.getBoolean("TVJL", false))
            {
                dataReader.readDataInBackground();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
