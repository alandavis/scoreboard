package uk.org.bwscswim.scoreboard;

/**
 * Holds a copy of a scoreboard state so that it may be displayed once a previous state has had long enough being displayed.
 *
 * @author adavis
 */
public class StateData
{
    private final ScoreboardState prevState;
    private final ScoreboardState state;
    private final Text text;
    private int lanesWithTimes;

    StateData(ScoreboardState prevState, ScoreboardState state, Text text, int lanesWithTimes)
    {
        this.prevState = prevState;
        this.state = state;
        this.text = new Text(text);
        this.lanesWithTimes = lanesWithTimes;
    }

    public ScoreboardState getPrevState()
    {
        return prevState;
    }

    public ScoreboardState getState()
    {
        return state;
    }

    public Text getText()
    {
        return text;
    }

    public int getLanesWithTimes()
    {
        return lanesWithTimes;
    }
}
