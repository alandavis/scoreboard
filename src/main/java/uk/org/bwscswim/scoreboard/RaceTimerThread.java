package uk.org.bwscswim.scoreboard;

import static uk.org.bwscswim.scoreboard.ScoreboardState.RACE;

/**
 * @author adavis
 */
public class RaceTimerThread extends Thread
{
    private boolean terminate;
    private DataReader dataReader;
    private long lastClock;
    private long timeZero;

    public RaceTimerThread(DataReader dataReader, String clock)
    {
        this.dataReader = dataReader;
        setClock(clock);
        setDaemon(true);
        setName("RaceTimerThread");
        start();
    }

    @Override
    public void run()
    {
        try
        {
            long now = System.currentTimeMillis();
            for(;;)
            {
                long wakeIn = 75 - (now % 75);
//              System.err.println("  wakeIn="+wakeIn);
                Thread.sleep(wakeIn);
                now = System.currentTimeMillis();
                int timeNow;
                synchronized (this)
                {
                    // The race winner has finished if there has not been a clock for more than 2.1 seconds (add a buffer of 0.4)
                    if (now-lastClock > 2500)
                    {
                        dataReader.setRaceFinishing();
                        terminate = true;
                    }

                    if (terminate || dataReader.getState() != RACE)
                    {
                        dataReader.setClock("");
                        break;
                    }
                    timeNow = (int)((now - timeZero)/10)*10;
                }
                setThreadTime(timeNow);
            }
        }
        catch (InterruptedException ignoreAndJustExist)
        {
        }
    }

    public void setClock(String clock)
    {
        clock = clock.trim();
        int i = clock.indexOf(':');
        int j = clock.indexOf('.');
        int mins = i == -1 ? 0 : Integer.parseInt(clock.substring(0, i));
        int secs = Integer.parseInt(clock.substring(i == -1 ? 0 : i+1, j));
        int hunds = Integer.parseInt(clock.substring(j+1)+(clock.length()-2 == j ? "0" : ""));
        int time = (mins*60+secs)*1000 + hunds*10;
        lastClock = System.currentTimeMillis();
        synchronized(this)
        {
            timeZero = lastClock - time;
        }
//      System.err.println("  setClock "+clock+" "+mins+"-"+secs+"-"+hunds+" --------- "+timeZero);
    }

    private void setThreadTime(int timeNow)
    {
        int hunds = (timeNow % 1000)/10;
        int secs = timeNow/1000;
        int mins = secs / 60;
        secs = secs % 60;
        String clock = "    "+
            (mins == 0 ? "" : Integer.toString(mins)+':')+
            (mins > 0 && secs <= 9 ? "0" : "")+Integer.toString(secs)+'.'+
            (hunds <= 9 ? "0" : "")+Integer.toString(hunds);
        clock = clock.substring(clock.length()-8);
//        System.err.println("  timeNow="+timeNow+" '"+clock+"' "+mins+"-"+secs+"-"+hunds+" ++++++++");
        dataReader.setClock(clock);
    }
}
