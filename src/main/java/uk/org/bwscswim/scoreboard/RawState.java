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
 * The states of the state engine providing scoreboard data.
 *
 * @author adavis
 */
public enum RawState
{
    /** Test page */
    TEST(false),
    /** Showing time of day or about to do so */
    TIME_OF_DAY(false),
    /** Clear the board */
    CLEAR(false),
    /* About to receive the lineup */
    LINEUP(false),

    /** The lineup is complete and the timer has been set to 0.0 but is not running */
    LINEUP_COMPLETE(true),
    /** The race timer is running */
    RACE(true),
    /** The race timer has stopped for longer than 2.1 seconds so is not a split time, but there may be more result lines */
    RACE_FINISHING(true),
    /** All result lines have been set after and the race timer has stopped */
    RACE_COMPLETE(true),
    /** Displaying the results */
    RESULTS(true),
    /** The display of the results is finished */
    RESULTS_COMPLETE(true);

    private final boolean queueable;

    RawState(boolean queueable)
    {
        this.queueable = queueable;
    }

    RawState nextQueueableState()
    {
        int i = ordinal();
        RawState[] values = values();
        i = i == values.length-1 ? 0 : i+1;

        while (!values[i].queueable)
        {
            i++;
        }

        return values[i];
    }

    boolean isQueueable()
    {
        return queueable;
    }

    public static void main(String[] args) // TODO remove
    {
        for (RawState state : RawState.values())
        {
            System.out.println("state:"+state+" next:"+state.nextQueueableState());
        }
    }
}
