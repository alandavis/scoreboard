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

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the text of the raw scoreboard read from the timing equipment.
 *
 * @author adavis
 */
class RawText
{
    private final List<String> lines = new ArrayList<>();

    RawText(RawText orig)
    {
        if (orig != null)
        {
            lines.addAll(orig.lines);
        }
    }

    private static int getLineNumber(String range)
    {
        String lineStr = range.substring(0, 2);
        return Integer.parseInt(lineStr);
    }

    static String getFromRange(String range)
    {
        return range.substring(0, 4);
    }

    static int getCharRangeFrom(String charRange)
    {
        return Integer.parseInt(charRange.substring(0,2));
    }

    static int getCharRangeTo(String charRange)
    {
        return Integer.parseInt(charRange.substring(4,6))+1;
    }

    void setText(int lineNumber, int offset, String text)
    {
        String line = getText(lineNumber, "");
        StringBuilder sb = new StringBuilder(line);
        while (sb.length() < offset)
        {
            sb.append(' ');
        }
        sb.replace(offset, offset+text.length(), text);
        line = sb.toString();
        while (lines.size() <= lineNumber)
        {
            lines.add(null);
        }
        lines.set(lineNumber, line);
    }

    private String getText(int lineNumber, String defaultValue)
    {
        String line = lineNumber >= lines.size() ? null : lines.get(lineNumber);
        return line == null ? defaultValue : line;
    }

    String getText(String range, String defaultValue)
    {
        int lineNumber = getLineNumber(range);
        String charRange = getCharRange(range);
        return getText(lineNumber, charRange, 0, defaultValue);
    }

    int getInt(int lineNumber, String charRange, int offset, int defaultValue)
    {
        String text = getText(lineNumber, charRange, offset, Integer.toString(defaultValue)).trim();
        int i = defaultValue;
        try
        {
            i = Integer.parseInt(text);
        }
        catch (NumberFormatException ignore)
        {
        }
        return i;
    }

    String getText(int lineNumber, String charRange, int offset, String defaultValue)
    {
        String line = getText(lineNumber, "");
        int from = getCharRangeFrom(charRange)+offset;
        int to = getCharRangeTo(charRange)+offset;
        int length = line.length();

        return length <= from
                ? ""
                : length < to
                  ? line.substring(from, length)
                  : line.substring(from, to);
    }

    private String getCharRange(String range)
    {
        return range.substring(2, 6)+range.substring(8,10);
    }

    void clear()
    {
        lines.clear();
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (String line : lines)
        {
            if (line != null)
            {
                if (sb.length() > 0)
                {
                    sb.append('\n');
                }
                sb.append("        ").append(line);
            }
        }
        return sb.toString();
    }
}
