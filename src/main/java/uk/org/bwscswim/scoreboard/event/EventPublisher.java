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
package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.StateTrace;
import uk.org.bwscswim.scoreboard.event.Observer;
import uk.org.bwscswim.scoreboard.event.ScoreboardEvent;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author adavis
 */
public class EventPublisher extends SwingWorker<Void, ScoreboardEvent>
{
    private final List<Observer> observers = new ArrayList<>();

    private StateTrace stateTrace;

    public void addObserver(Observer observer)
    {
        observers.add(observer);
    }

    public void setStateTrace(StateTrace stateTrace)
    {
        this.stateTrace = stateTrace;
    }

    public void publishEvent(ScoreboardEvent event)
    {
        if (!(event instanceof RawTextEvent) && !(event instanceof RaceTimerEvent))
        {
            stateTrace.trace(event.toString());
        }
        publish(event);
    }

    @Override
    // Runs in Swing's event dispatch thread
    protected void process(List<ScoreboardEvent> scoreboardEvents)
    {
        scoreboardEvents.forEach(event->observers.forEach(observer -> observer.update(event)));
    }

    @Override
    protected Void doInBackground() throws Exception
    {
        return null;
    }
}
