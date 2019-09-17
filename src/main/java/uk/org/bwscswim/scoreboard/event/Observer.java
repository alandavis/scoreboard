package uk.org.bwscswim.scoreboard.event;

/**
 * @author adavis
 */
public interface Observer
{
    void beforeFirstRead();

    void update(PageEvent event);

    void update(RaceTimerEvent event);
}
