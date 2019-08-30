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

import java.util.List;
import java.util.StringJoiner;

/**
 * Records the incoming data from the timing equipment.
 *
 * @author adavis
 */
class StateTrace
{
    private long time = -1;

    void trace(String msg)
    {
        long now = System.currentTimeMillis();
        String prefix = "      ";
        if (time != -1)
        {
            long delay = ((now - time + 5) / 10) * 10; // round to 10 ms
            if (delay > 0)
            {
                prefix = String.format("%5d ", delay);
            }
        }
        time = now;
        System.out.println(prefix+msg);
    }

    public void trace(String prefix, List<StateData> queuedStateData)
    {
        StringJoiner q = new StringJoiner(", ", prefix, "");
        queuedStateData.forEach(sd->q.add(sd.getState().toString()));
        trace(q.toString());
    }

    public void setTime(long time)
    {
        this.time = time;
    }
}
