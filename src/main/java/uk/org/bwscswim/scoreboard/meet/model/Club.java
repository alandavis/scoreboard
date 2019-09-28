package uk.org.bwscswim.scoreboard.meet.model;

/**
 * @author adavis
 */
public class Club
{
    private final String name;
    private final Abbreviations abbreviations;

    public Club(String name, Abbreviations abbreviations)
    {
        this.name = name;
        this.abbreviations = abbreviations;
    }

    public String getName()
    {
        return name;
    }

    public String getAbbreviation()
    {
        return abbreviations.lookup(name);
    }
}
