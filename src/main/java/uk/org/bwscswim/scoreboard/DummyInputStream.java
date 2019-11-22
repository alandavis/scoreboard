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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

/**
 * Turns a String into an InputStream that may only contain ASCII characters. Control characters are supplied
 * as hex in the form {@code "[xx]"}. The first byte of each line to be returned must be a control character.
 * A delay in milliseconds may be optionally specified at the start of each line.
 *
 * @author adavis
 */
class DummyInputStream extends InputStream
{
    private boolean includeDelay = true;
    private BufferedReader reader;
    private String line;
    private int i;
    private long time = System.currentTimeMillis();
    private boolean ignoreFirstDelay = true;
    private Sleeper sleeper;

    DummyInputStream(String string, boolean includeDelay)
    {
        sleeper = new Sleeper();
        this.includeDelay = includeDelay;
        reader = new BufferedReader(new StringReader(string));
    }

    DummyInputStream(String filename, Sleeper sleeper, Config config) throws FileNotFoundException
    {
        this.sleeper = sleeper;
        // TODO do we want to remove ignoreFirstDelay now that there is a time of day clock?
        ignoreFirstDelay = false;
        reader = FileLoader.getBufferedReader(filename, config);
    }

    @Override
    public void close() throws IOException
    {
        super.close();
        if (reader != null)
        {
            reader.close();
        }
    }

    @Override
    public int read() throws IOException
    {
        if (line == null)
        {
            do
            {
                line = reader.readLine();
                i = line == null ? -1 : line.indexOf('[');
            }
            while (line != null && i == -1);

            if (line != null)
            {
                if (includeDelay)
                {
                    String delay = line.substring(0, i).trim();
                    if (!delay.isEmpty())
                    {
                        try
                        {
                            long t = ignoreFirstDelay
                                ? 0
                                : Long.parseLong(delay) - System.currentTimeMillis() + time - 3; // -3 to allow for some processing;
                            if (t > 0)
                            {
                                try
                                {
                                    sleeper.sleep(t);
                                }
                                catch (InterruptedException e)
                                {
                                    reader.close();
                                    line = null;
                                }
                            }
                        }
                        catch (NumberFormatException ignore)
                        {
                        }
                        time = System.currentTimeMillis();
                    }
                }
                ignoreFirstDelay = false;
            }
        }

        int b = -1;
        if (line != null)
        {
            if (line.charAt(i) == '[')
            {
                String hex = line.substring(i+1, i+3);
                b = Integer.parseInt(hex, 16);
                i += 4;
            }
            else
            {
                b = line.charAt(i++)%256;
            }
            if (i >= line.length())
            {
                line = null;
            }
        }
        return b;
    }
}

