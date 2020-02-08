package uk.org.bwscswim.scoreboard;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author adavis
 */
public class RawTextTest
{
    RawText raw = new RawText(null);

    @Test
    public void padTest()
    {
        String s[][] = new String[][]
        {
                {"1",     "   1", "1   "},
                {"2222",  "2222", "2222"},
                {"12345", "2345", "1234"},
                {"12",    "  12", "12  "},
        };

        for (String[] srcExpected : s)
        {
            assertEquals(srcExpected[1], raw.pad(srcExpected[0], 4, true));
            assertEquals(srcExpected[2], raw.pad(srcExpected[0], 4, false));
        }
    }

    @Test
    public void setTextTest()
    {
        String text = "Test Title";
        String range = "0000..0037";
        String actual;
        raw.setText(range, text, false);
        actual = raw.getText(range, "xxx");
        assertTrue(actual.startsWith(text));
        assertEquals(38, actual.length());
        assertEquals("Test T", raw.getText("0000..0005", "xxx"));
        raw.setText(range, text, true);
        actual = raw.getText(range, "xxx");
        assertTrue(actual.endsWith(text));
        assertEquals(38, actual.length());

        range = "1002..1006";
        text = "the";
        raw.setText(range, text, false);
        actual = raw.getText(range, "xxx");
        assertEquals("the  ", raw.getText(range, "xxx"));
        raw.setText(range, text, true);
        assertEquals("  the", raw.getText(range, "xxx"));

        range = "1000..1006";
        assertEquals("    the", raw.getText(range, "xxx"));
        text = "X";
        raw.setText("1003..1004", text, false);
        assertEquals("   X he", raw.getText(range, "xxx"));
    }
}
