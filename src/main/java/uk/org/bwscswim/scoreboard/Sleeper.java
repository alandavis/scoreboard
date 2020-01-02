package uk.org.bwscswim.scoreboard;

/**
 * @author adavis
 */
public class Sleeper
{
    private float speedFactor = 1f;

    public void setSpeedFactor(float speedFactor)
    {
        this.speedFactor = speedFactor;
    }

    public void sleep(long normalMs) throws InterruptedException
    {
        normalMs = Math.min(normalMs, 20000); // Wait a maximum of 20 seconds
        long ms = convert(normalMs);
        if (ms > 0)
        {
            Thread.sleep(ms);
        }
    }

    public long convert(long normalMs)
    {
        long ms = (long) ((double) (normalMs * speedFactor));
        return ms == 0 ? 1 : ms;
    }

    public long convertBack(long ms)
    {
        return (long) ((double) (ms / speedFactor));
    }
}
