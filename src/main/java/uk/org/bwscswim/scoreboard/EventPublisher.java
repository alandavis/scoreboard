package uk.org.bwscswim.scoreboard;

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

    private StateTrace stateTrace = new StateTrace();

    public void addObserver(Observer observer)
    {
        observers.add(observer);
    }

    public void setStateTrace(StateTrace stateTrace)
    {
        this.stateTrace = stateTrace;
    }

    void publishEvent(ScoreboardEvent event)
    {
        stateTrace.trace(event.toString());
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
