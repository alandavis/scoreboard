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

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Scoreboard
{
    private String title;
    private String subTitle;
    private boolean result;
    private String clock;
    private List<Swimmer> swimmers;

    public Scoreboard()
    {
        reset();
    }

    public void reset()
    {
        title = "";
        subTitle = "";
        clock = "";
        result = false;
        swimmers = new ArrayList<>();
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getSubTitle()
    {
        return subTitle;
    }

    public void setSubTitle(String subTitle)
    {
        this.subTitle = subTitle;
    }

    public String getClock()
    {
        return clock;
    }

    public void setClock(String clock)
    {
        this.clock = clock;
    }

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public List<Swimmer> getSwimmers()
    {
        return swimmers;
    }

    public void setLaneValues(int line, int lane, int place, String name, String club, String time)
    {
        while (line >= swimmers.size())
        {
            Swimmer scoreboardSwimmer = new Swimmer();
            swimmers.add(scoreboardSwimmer);
            if (result)
            {
                scoreboardSwimmer.setPlace(swimmers.size());
            }
            else
            {
                scoreboardSwimmer.setLane(swimmers.size());
            }
        }
        Swimmer scoreboardSwimmer = swimmers.get(line);

        scoreboardSwimmer.setLane(lane);
        scoreboardSwimmer.setPlace(place);
        scoreboardSwimmer.setName(name);
        scoreboardSwimmer.setClub(club);
        scoreboardSwimmer.setTime(time);
    }

    @Override
    public String toString()
    {
        return "Scoreboard{" +
                "title='" + title + '\'' +
                ", subTitle='" + subTitle + '\'' +
                ", result=" + result +
                ", clock='" + clock + '\'' +
                ", swimmers=" + swimmers +
                '}';
    }
}
