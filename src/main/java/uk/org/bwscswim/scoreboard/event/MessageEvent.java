package uk.org.bwscswim.scoreboard.event;

import java.util.Arrays;
import java.util.List;

/**
 * @author adavis
 */
public class MessageEvent implements ScoreboardEvent
{
    private final int minDisplayTime;
    private final String title;
    private final List<String> messages;

    public MessageEvent(int minDisplayTime, String title, String... messages)
    {
        this.minDisplayTime = minDisplayTime;
        this.title = title;
        this.messages = Arrays.asList(messages);
    }

    public int getMinDisplayTime()
    {
        return minDisplayTime;
    }

    public String getTitle()
    {
        return title;
    }

    public String getMeessage(int laneIndex)
    {
        return messages.get(laneIndex);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append("\n  ").append(title);
        messages.forEach(lane->sb.append("\n    ").append(lane));
        return sb.toString();
    }
}
