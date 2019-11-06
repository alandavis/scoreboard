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

    public String calculateImprovement(String time, Event event)
    {
        String improvement = "";
        if (entryTime != null)
        {
            RaceTime newTime = RaceTime.create(time);
            Integer yearOfBirth = swimmer.getYearOfBirth();
            if (newTime != null && yearOfBirth != null)
            {
                RaceTime countyTime = event.getCountyTime(yearOfBirth);
                if (countyTime != null)
                {
                    RaceTime entryTime = getEntryTime();
                    boolean isCountyTime = countyTime != null && newTime.compareTo(countyTime) <= 0;

                    if (isCountyTime && (entryTime == null || entryTime.compareTo(countyTime) > 0))
                    {
                        improvement = "CT"; // New county time
                    }
                    else if (entryTime != null)
                    {
                        String timeDifference = newTime.minus(entryTime);
                        improvement = timeDifference.startsWith("-")
                                ? timeDifference +
                                (isCountyTime
                                        ? "ct" // improved country time
                                        : "")
                                : isCountyTime
                                ? "ct" // county time but <=
                                : "";
                    }
                }
            }
        }
        return improvement;
    }
}
