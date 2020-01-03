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
package uk.org.bwscswim.scoreboard;

/**
 * @author adavis
 */
public class Sleeper
{
    private float speedFactor = 1f;

    public void setSpeedFactor(float speedFactor)
    {
        this.speedFactor = speedFactor;
    }

    public void sleep(long normalMs) throws InterruptedException
    {
        normalMs = Math.min(normalMs, 20000); // Wait a maximum of 20 seconds
        long ms = convert(normalMs);
        if (ms > 0)
        {
            Thread.sleep(ms);
        }
    }

    public long convert(long normalMs)
    {
        long ms = (long) ((double) (normalMs * speedFactor));
        return ms == 0 ? 1 : ms;
    }

    public long convertBack(long ms)
    {
        return (long) ((double) (ms / speedFactor));
    }
}
