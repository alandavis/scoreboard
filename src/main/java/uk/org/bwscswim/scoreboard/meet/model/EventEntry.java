package uk.org.bwscswim.scoreboard.meet.model;

import uk.org.bwscswim.scoreboard.meet.service.EntryTime;

/**
 * @author adavis
 */
public class EventEntry
{
    private final Swimmer swimmer;
    private final EntryTime entryTime;

    public EventEntry(Swimmer swimmer, EntryTime entryTime)
    {
        this.swimmer = swimmer;
        this.entryTime = entryTime;
    }

    public Swimmer getSwimmer()
    {
        return swimmer;
    }

    public EntryTime getEntryTime()
    {
        return entryTime;
    }
}
