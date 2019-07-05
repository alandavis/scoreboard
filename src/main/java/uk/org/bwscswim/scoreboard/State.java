package uk.org.bwscswim.scoreboard;

/**
 * @author adavis
 */
public enum State
{
    /** Test page */
    TEST,
    /** Clear the board */
    CLEAR,
    /** Showing time of day or about to do so */
    TIME_OF_DAY,
    /* About to receive the lineup */
    LINEUP,
    /** The lineup is complete and the timer has been set to 0.0 but is not running */
    LINEUP_COMPLETE,
    /** The race timer is running */
    RACE,
    /** The race timer has stopped for longer than 2.1 seconds so is not a split time, but there may be more result lines */
    RACE_FISHING,
    /** All result lines have been set after and the race timer has stopped */
    RACE_COMPLETE,
    /** About to display the results */
    RESULT,
    /** The results are complete */
    RESULT_COMPLETE;
}
