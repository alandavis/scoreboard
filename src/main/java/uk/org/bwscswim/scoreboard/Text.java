package uk.org.bwscswim.scoreboard;

import java.util.ArrayList;
import java.util.List;

public class Text
{
    List<String> lines = new ArrayList<>();

    public void setText(int lineNumber, int offset, String text)
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

    public String getText(int lineNumber, String defaultValue)
    {
        String line = lineNumber >= lines.size() ? null : lines.get(lineNumber);
        return line == null ? defaultValue : line;
    }

    public String getText(String range, String defaultValue)
    {
        int lineNumber = getLineNumber(range);
        String charRange = getCharRange(range);
        String text = getText(lineNumber, charRange, "");
        return text;
    }

    public String getText(int lineNumber, String charRange, String defaultValue)
    {
        return getText(lineNumber, charRange, 0, defaultValue);
    }

    public String getText(int lineNumber, String charRange, int offset, String defaultValue)
    {
        String line = getText(lineNumber, "");
        int from = getCharRangeFrom(charRange)+offset;
        int to = getCharRangeTo(charRange)+offset;
        int length = line.length();

        String text =
                length <= from ? "" :
                length < to ? line.substring(from, length) :
                line.substring(from, to);

        return text;
    }

    private int getLineNumber(String range)
    {
        checkRange(range);
        String lineStr = range.substring(0, 2);
        return Integer.parseInt(lineStr);
    }

    private String getCharRange(String range)
    {
        checkRange(range);
        return range.substring(2, 6)+range.substring(8,10);
    }

    public static int getCharRangeFrom(String charRange)
    {
        checkCharRange(charRange);
        return Integer.parseInt(charRange.substring(0,2));
    }

    public static int getCharRangeTo(String charRange)
    {
        checkCharRange(charRange);
        return Integer.parseInt(charRange.substring(4,6))+1;
    }

    private void checkRange(String range)
    {
        if (range == null || range.length() != 10 || !"..".equals(range.substring(4,6)) || !range.substring(0,2).equals(range.substring(6,8)))
        {
            throw new IllegalArgumentException("Range llc1..llc2 "+range+" is invalid");
        }
    }

    private static void checkCharRange(String charRange)
    {
        if (charRange == null || charRange.length() != 6 || !"..".equals(charRange.substring(2,4)))
        {
            throw new IllegalArgumentException("Char range c1..c2 "+charRange+" is invalid");
        }
    }

    public char getChar(int lineNumber, int offset, char defaultValue)
    {
        char c = defaultValue;
        String line = getText(lineNumber, null);
        if (line != null)
        {
            if (offset < line.length())
            {
                c = line.charAt(offset);
            }
        }
        return c;
    }

    public void clear()
    {
        lines.clear();
    }
}