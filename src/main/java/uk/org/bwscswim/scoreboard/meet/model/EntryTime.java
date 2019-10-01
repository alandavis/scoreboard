package uk.org.bwscswim.scoreboard.meet.model;


/**
 * @author adavis
 */
public class EntryTime
{
    private final String entryTime;

    /**
     * @param entryTime in either 999:88.77 or 9998877 format.
     */
    public EntryTime(String entryTime)
    {
        int size = entryTime.length();
        this.entryTime = entryTime.charAt(size-3) == '.'
            ? (size <= 5 ? "" : entryTime.substring(0, size-6))+
                entryTime.substring(size-5, size-3)+entryTime.substring(size-2)
            : entryTime;
    }

    public String toDigits()
    {
        return entryTime;
    }

    public String toString()
    {
        int size = entryTime.length();
        return (size <= 4 ? "" : entryTime.substring(0, size-4)+':')+
                entryTime.substring(size-4, size-2)+'.'+entryTime.substring(size-2);
    }
}
