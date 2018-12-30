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
package uk.org.bwscswim.scoreboard.model;

public class Swimmer
{
    private int lane;
    private int place;
    private String name = "";
    private String club = "";
    private String time = "";

    public String getLane()
    {
        return lane >= 1 ? Integer.toString(lane) : "";
    }

    public void setLane(int lane)
    {
        this.lane = lane;
    }

    public String getPlace()
    {
        return place >= 1 ? Integer.toString(place) : "";
    }

    public void setPlace(int place)
    {
        this.place = place;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getClub()
    {
        return club;
    }

    public void setClub(String club)
    {
        this.club = club;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    @Override
    public String toString()
    {
        return "Swimmer{" +
                "name='" + name + '\'' +
                ", club='" + club + '\'' +
                ", lane='" + getLane() + '\'' +
                ", place='" + getPlace() + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}