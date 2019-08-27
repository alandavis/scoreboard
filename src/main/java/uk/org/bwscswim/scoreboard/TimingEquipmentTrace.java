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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import static uk.org.bwscswim.scoreboard.DataReader.SOL;
import static uk.org.bwscswim.scoreboard.DataReader.ETB;

/**
 * Records the incoming data from the timing equipment.
 *
 * @author adavis
 */
class TimingEquipmentTrace
{
    private final Writer writer;
    private final StateTrace stateTrace;

    private int prevByte;
    private boolean waitingForFirstByteOfTransmission = true;
    private int traceZeroCount = 0;
    private long time = -1;

    TimingEquipmentTrace(Config config, StateTrace stateTrace)
    {
        this.stateTrace = stateTrace;
        reset();

        Writer writer = null;
        if (config.getBoolean("traceFile", false))
        {
            String filename = System.currentTimeMillis()+".log";

            try
            {
                writer = new BufferedWriter(new FileWriter(filename));
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        this.writer = writer;
    }

    void reset()
    {
        waitingForFirstByteOfTransmission = true;
        time = -1;
        stateTrace.setTime(time);
    }

    void trace(int b) throws InterruptedException
    {
        if (b == SOL)
        {
            if (waitingForFirstByteOfTransmission)
            {
                waitingForFirstByteOfTransmission = false;
                trace("\n");
            }
            long now = System.currentTimeMillis();
            if (time != -1)
            {
                long delay = ((now - time + 5) / 10) * 10; // round to 10 ms
                trace(((delay == 0) ? "      " : String.format("%5d ", delay)));
            }
            time = now;
            stateTrace.setTime(time);
        }
        // Some ports just return 0 endlessly in disconnected.
        if (b == 0)
        {
            if (traceZeroCount < 30)
            {
                trace(format(b));
                if (++traceZeroCount == 30)
                {
                    trace("...\n");
                }
            }
            Thread.sleep(100); // don't take all the CPU if disconnected.
        }
        else
        {
            traceZeroCount = 0;
            if (prevByte != ETB || b != ETB)
            {
                trace(format(b));
                if (b == ETB)
                {
                    trace("\n");
                }
            }
            prevByte = b;
        }
    }

    private void trace(String str)
    {
        System.err.print(str);
        if (writer != null)
        {
            try
            {
                writer.write(str);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    static String format(int b)
    {
        return ((b >= 0x20 && b <= 0x7F)) ? Character.toString((char) b) : String.format("[%02x]", b);
    }

    static String format(String str)
    {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray())
        {
            sb.append(format(c));
        }
        return sb.toString();
    }

    void close()
    {
        if (writer != null)
        {
            try
            {
                writer.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
