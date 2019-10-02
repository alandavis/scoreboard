package uk.org.bwscswim.scoreboard.meet.model;

/**
 * @author adavis
 */
public class EventEntry
{
    private final Swimmer swimmer;
    private final RaceTime entryTime;

    public EventEntry(Swimmer swimmer, RaceTime entryTime)
    {
        this.swimmer = swimmer;
        this.entryTime = entryTime;
    }

    public Swimmer getSwimmer()
    {
        return swimmer;
    }

    public RaceTime getEntryTime()
    {
        return entryTime;
    }
}
