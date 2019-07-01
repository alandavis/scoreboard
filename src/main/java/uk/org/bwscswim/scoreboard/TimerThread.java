package uk.org.bwscswim.scoreboard;

import static uk.org.bwscswim.scoreboard.State.RUNNING;

/**
 * @author adavis
 */
public class TimerThread extends Thread
{
    private boolean terminate;
    private DataReader dataReader;
    private int time;
    private long timeZero;

    public TimerThread(DataReader dataReader, String clock)
    {
        this.dataReader = dataReader;
        setClock(clock);
        setDaemon(true);
        setName("TimerThread");
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
                Thread.sleep(wakeIn);
//System.err.println("  wakeIn="+wakeIn);
                now = System.currentTimeMillis();
                int timeNow;
                synchronized (this)
                {
                    if (terminate || dataReader.getState() != RUNNING)
                    {
                        break;
                    }
                    timeNow = (int)((now - timeZero)/10)*10;
                }
                setTime(timeNow);
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
        long now = System.currentTimeMillis();
        synchronized(this)
        {
            timeZero = now - time;
        }
//        System.err.println("  setClock "+clock+" "+mins+"-"+secs+"-"+hunds+" --------- "+timeZero);
    }

    private void setTime(int timeNow)
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

    public synchronized void terminate()
    {
        terminate = true;
    }
}
