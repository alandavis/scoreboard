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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * Turns a String into an InputStream that may only contain ASCII characters. Control characters are supplied
 * as hex in the form {@code "[xx]"}. The first byte of each line to be returned must be a control character.
 * A delay in milliseconds may be optionally specified at the start of each line.
 */
public class DummyInputStream extends InputStream
{
    private boolean includeDelay = true;
    private BufferedReader reader;
    private String line;
    private int i;
    private long time = System.currentTimeMillis();

    public DummyInputStream(String string, boolean includeDelay)
    {
        this.includeDelay = includeDelay;
        reader = new BufferedReader(new StringReader(string));
    }

    public DummyInputStream(String filename) throws FileNotFoundException
    {
        try
        {
            InputStreamReader inputStreamReader = filename.startsWith(":")
                    ? new InputStreamReader(getClass().getClassLoader().getResource(filename.substring(1)).openStream())
                    : new FileReader(filename);
            reader = new BufferedReader(inputStreamReader);
        }
        catch (IOException e)
        {
            throw new FileNotFoundException(e.getMessage());
        }
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
            line = reader.readLine();
            if (line != null)
            {
                i = line.indexOf('[');
                if (includeDelay)
                {
                    String delay = line.substring(0, i).trim();
                    if (!delay.isEmpty())
                    {
                        long t = Long.parseLong(delay);
                        long now = System.currentTimeMillis();
                        t = t - now + time - 3; // -3 to allow for some processing
                        if (t > 0)
                        {
                            try
                            {
                                Thread.sleep(t);
                            }
                            catch (InterruptedException e)
                            {
                                reader.close();
                                line = null;
                            }
                            time = System.currentTimeMillis();
                        }
                    }
                }
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
};

