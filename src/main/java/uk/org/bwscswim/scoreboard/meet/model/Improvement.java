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
package uk.org.bwscswim.scoreboard.meet.model;

import java.time.LocalDate;

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
                int age = getAge(yearOfBirth);
                if (age < 25)
                {
                    countyTime = event.getCountyTime(yearOfBirth);
                    regionalBaseTime = event.getRegionalBaseTime(yearOfBirth);
                    regionalAutoTime = event.getRegionalAutoTime(yearOfBirth);
                }
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

    private int getAge(int yearOfBirth)
    {
        int year = LocalDate.now().getYear();
        return year - yearOfBirth;
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
