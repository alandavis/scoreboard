package uk.org.bwscswim.scoreboard.event;

import uk.org.bwscswim.scoreboard.Text;
import uk.org.bwscswim.scoreboard.meet.model.Event;

/**
 * Text change event on the original raw text scoreboard.
 *
 * @author adavis
 */
public class RawTextEvent implements ScoreboardEvent
{
    private final int lineNumber;
    private final int offset;
    private final String data;

    /**
     * Clear screen
     */
    public RawTextEvent()
    {
        lineNumber = 0;
        offset = 0;
        data = null;
    }

    /**
     * Change some text on the screen.
     * @param lineNumber to change starting at 0
     * @param offset offset into line starting at 0
     * @param data string to be set
     */
    public RawTextEvent(int lineNumber, int offset, String data)
    {
        this.lineNumber = lineNumber;
        this.offset = offset;
        this.data = data;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public int getOffset()
    {
        return offset;
    }

    public String getData()
    {
        return data;
    }

    @Override
    public String toString()
    {
        if (data == null)
        {
            return "<<Clear Screen>>";
        }
        else
        {
            StringBuilder sb = new StringBuilder(getClass().getSimpleName()).
                    append(" ").append(lineNumber).
                    append(',').
                    append(offset).
                    append(' ').
                    append(data);
            return sb.toString();
        }
    }
}
