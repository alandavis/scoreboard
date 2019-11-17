package uk.org.bwscswim.scoreboard.meet.model;

/**
 * @author adavis
 */
public class Improvement
{
    private String reduction = "";
    private String level = "";
    private boolean newBand;

    public Improvement()
    {
    }

    Improvement(EventEntry eventEntry, String time, Event event)
    {
        RaceTime raceTime = RaceTime.create(time);
        if (raceTime != null)
        {
            Swimmer swimmer = eventEntry.getSwimmer();
            RaceTime entryTime = eventEntry.getEntryTime();
            RaceTime pb = event.getPb(swimmer);
            pb = pb == null || (entryTime != null && entryTime.compareTo(pb) <= 0) ? entryTime : pb; // PB may not have been updated.
            boolean isPb = pb != null && raceTime.compareTo(pb) < 0;
            RaceTime countyTime = null;
            RaceTime regionalBaseTime = null;
            RaceTime regionalAutoTime = null;
            Integer yearOfBirth = swimmer.getYearOfBirth();
            if (yearOfBirth != null)
            {
                countyTime = event.getCountyTime(yearOfBirth);
                regionalBaseTime = event.getRegionalBaseTime(yearOfBirth);
                regionalAutoTime = event.getRegionalAutoTime(yearOfBirth);
            }
            boolean isCountyTime = countyTime != null && raceTime.compareTo(countyTime) <= 0;
            boolean isRegionalBaseTime = regionalBaseTime != null && raceTime.compareTo(regionalBaseTime) <= 0;
            boolean isRegionalAutoTime = regionalAutoTime != null && raceTime.compareTo(regionalAutoTime) <= 0;

            boolean isNewCountyTime = isCountyTime && (pb == null || pb.compareTo(countyTime) > 0);
            boolean isNewRegionalBaseTime = isRegionalBaseTime && (pb == null || pb.compareTo(regionalBaseTime) > 0);
            boolean isNewRegionalAutoTime = isRegionalAutoTime && (pb == null || pb.compareTo(regionalAutoTime) > 0);
            boolean isNewBand = isNewCountyTime || isNewRegionalBaseTime || isNewRegionalAutoTime;

            reduction = isPb ? raceTime.minus(pb) : "";
            level = isRegionalAutoTime ? "RT" : isRegionalBaseTime ? "rt" : isCountyTime ? "CT" : "";
            newBand = isNewBand;
        }
    }

    public String getReduction()
    {
        return reduction;
    }

    public String getLevel()
    {
        return level;
    }

    public boolean isNewBand()
    {
        return newBand;
    }

    public boolean isNewPb()
    {
        return !reduction.isEmpty();
    }

    public boolean isBlank()
    {
        return reduction.isEmpty() && !newBand;
    }

    @Override
    public String toString()
    {
        return reduction+level+(newBand ? "*" : "");
    }
}
