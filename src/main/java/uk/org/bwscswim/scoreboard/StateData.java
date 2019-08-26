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
package uk.org.bwscswim.scoreboard;

/**
 * Holds a copy of a scoreboard state so that it may be displayed once a previous state has been displayed long enough.
 *
 * @author adavis
 */
public class StateData
{
    private final ScoreboardState prevState;
    private final ScoreboardState state;
    private final Text text;
    private int lanesWithTimes;

    StateData(ScoreboardState prevState, ScoreboardState state, Text text, int lanesWithTimes)
    {
        this.prevState = prevState;
        this.state = state;
        this.text = new Text(text);
        this.lanesWithTimes = lanesWithTimes;
    }

    public ScoreboardState getPrevState()
    {
        return prevState;
    }

    public ScoreboardState getState()
    {
        return state;
    }

    public Text getText()
    {
        return text;
    }

    public int getLanesWithTimes()
    {
        return lanesWithTimes;
    }
}
