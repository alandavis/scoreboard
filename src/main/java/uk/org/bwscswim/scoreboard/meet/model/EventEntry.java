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

        RaceTime newTime = RaceTime.create(time);
        if (newTime != null)
        {
            Integer yearOfBirth = swimmer.getYearOfBirth();
            RaceTime countyTime = event.getCountyTime(yearOfBirth);

            RaceTime entryTime = getEntryTime();
            boolean isCountyTime = countyTime != null && newTime.compareTo(countyTime) <= 0;

            if (isCountyTime && (entryTime == null || entryTime.compareTo(countyTime) > 0))
            {
                improvement = "* CT"; // New county time
            }
            else if (entryTime != null)
            {
                String timeDifference = newTime.minus(entryTime);
                improvement = timeDifference.startsWith("-")
                    ? timeDifference + // .substring(1) +
                      (isCountyTime && entryTime != null
                      ? " CT" // improved country time
                      : "")
                    : isCountyTime && entryTime != null
                      ? "CT" // county time but <=
                      : "";
            }
        }
        return improvement;
    }
}
