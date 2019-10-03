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

import static uk.org.bwscswim.scoreboard.State.RACE;
import static uk.org.bwscswim.scoreboard.State.RESULTS;
import static uk.org.bwscswim.scoreboard.State.RESULTS_COMPLETE;
import static uk.org.bwscswim.scoreboard.State.TEST;

/**
 * Provides access to fields in the raw scoreboard text held by the super class.
 *
 * @author adavis
 */
public class Text extends RawText
{
    private final Config config;

    private final String TITLE_RANGE;
    private final String SUBTITLE_RANGE;
    private final String CLOCK_RANGE;

    private final String LANE_RANGE;
    private final String NAME_RANGE;
    private final String CLUB_RANGE;
    private final String TIME_RANGE;
    private final String PLACE_RANGE;

    private final int FIRST_LANE_LINE_NUMBER;
    private final int LANE_COUNT;

    private State state;
    private int splitCount;

    Text(Config config)
    {
        this(config, null, TEST);
    }

    /**
     * Creates a clone. As the scoreboard needs to display some data for a minimum amounts of time, copies are made
     * at these points.
     */
    Text(Text orig, State state)
    {
        this(orig.config, orig, state);
    }

    private Text(Config config, Text orig, State state)
    {
        super(orig);

        this.config = config;
        this.state = state;

        String LANES_RANGE = config.getCharRange("lanesRange", "02..07");
        FIRST_LANE_LINE_NUMBER = getCharRangeFrom(LANES_RANGE);
        int lastLaneLineNumber = getCharRangeTo(LANES_RANGE);
        LANE_COUNT = lastLaneLineNumber- FIRST_LANE_LINE_NUMBER;

        TITLE_RANGE = config.getRange("titleRange", "0000..0037");
        SUBTITLE_RANGE = config.getRange("subTitleRange", "0100..0116");
        CLOCK_RANGE = config.getRange("clockRange", "1100..1107");

        LANE_RANGE = config.getCharRange("laneRange", "00..00");
        NAME_RANGE = config.getCharRange("nameRange", "03..18");
        CLUB_RANGE = config.getCharRange("clubRange", "20..23");
        TIME_RANGE = config.getCharRange("timeRange", "25..32");
        PLACE_RANGE = config.getCharRange("placeRange", "34..37");
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
        if (state == RACE)
        {
            splitCount = 0;
        }
    }

    public int getSplitCountAndIncrement()
    {
        return splitCount++;
    }

    String getClockFromRange()
    {
        return getFromRange(CLOCK_RANGE);
    }

    public int getLaneCount()
    {
        return LANE_COUNT;
    }

    boolean isLaneLineNumber(int lineNumber)
    {
        return lineNumber >= FIRST_LANE_LINE_NUMBER && lineNumber < FIRST_LANE_LINE_NUMBER + LANE_COUNT;
    }

    public int getLaneIndex(int lineNumber)
    {
        return isLaneLineNumber(lineNumber) ? lineNumber-FIRST_LANE_LINE_NUMBER : -1;
    }

    int countLanesWithNames(State state)
    {
        int count = 0;
        for (int laneIndex = 0; laneIndex< LANE_COUNT; laneIndex++)
        {
            int lineNumber = FIRST_LANE_LINE_NUMBER + laneIndex;

            boolean result = state == RESULTS || state == RESULTS_COMPLETE;
            int indent = result ? 1 : 0;

            String name = getText(lineNumber, NAME_RANGE, indent, "").trim();
            if (!name.isEmpty())
            {
                count++;
            }
        }
        return count;
    }

    int countLanesWithTimes(State state)
    {
        int count = 0;
        for (int laneIndex = 0; laneIndex< LANE_COUNT; laneIndex++)
        {
            int lineNumber = FIRST_LANE_LINE_NUMBER + laneIndex;
            boolean result = state == RESULTS || state == RESULTS_COMPLETE;
            int indent = result ? 1 : 0;

            String time = getText(lineNumber, TIME_RANGE, indent, "").trim();
            if (!time.isEmpty())
            {
                count++;
            }
        }
        return count;
    }

    public String getTitle()
    {
        return getText(TITLE_RANGE, "");
    }

    public String getSubtitle()
    {
        return getText(SUBTITLE_RANGE, "");
    }

    String getClock()
    {
        return getText(CLOCK_RANGE, "");
    }

    public String getNonZeroClock()
    {
        String clock = getText(CLOCK_RANGE, "");
        if ("0.0".equals(clock.trim()))
        {
            clock = "";
        }
        return clock;
    }

    public int getLane(int laneIndex)
    {
        return getLaneOrPlace(laneIndex, false);
    }

    public int getPlace(int laneIndex)
    {
        return getLaneOrPlace(laneIndex, true);
    }

    public String getName(int laneIndex)
    {
        return getNameClubOrTime(laneIndex, NAME_RANGE);
    }

    public String getClub(int laneIndex)
    {
        return getNameClubOrTime(laneIndex, CLUB_RANGE);
    }

    public String getTime(int laneIndex)
    {
        return getNameClubOrTime(laneIndex, TIME_RANGE);
    }

    private int getLaneOrPlace(int laneIndex, boolean place)
    {
        int lineNumber = FIRST_LANE_LINE_NUMBER + laneIndex;
        boolean result = state == RESULTS || state == RESULTS_COMPLETE;
        int indent = result ? 1 : 0;
        String range = place
                ? result ? LANE_RANGE : PLACE_RANGE
                : result ? PLACE_RANGE : LANE_RANGE;
        return getInt(lineNumber, range, indent, 0);
    }

    public void clearLanes()
    {
        for (int laneIndex = 0; laneIndex < LANE_COUNT; laneIndex++)
        {
            int lineNumber = FIRST_LANE_LINE_NUMBER + laneIndex;
            setText(lineNumber, "");
        }
    }

    private String getNameClubOrTime(int laneIndex, String range)
    {
        int lineNumber = FIRST_LANE_LINE_NUMBER + laneIndex;
        int indent = state == RESULTS || state == RESULTS_COMPLETE ? 1 : 0;
        return getText(lineNumber, range, indent, "").trim();
    }
}
