package uk.org.bwscswim.scoreboard.meet.model;

/**
 * @author adavis
 */
public class Club
{
    private final String abbreviation;
    private final Abbreviations abbreviations;

    public Club(String name, Abbreviations abbreviations)
    {
        this.abbreviation = abbreviations.lookupAbbreviation(name);
        this.abbreviations = abbreviations;
    }

    public String getAbbreviation()
    {
        return abbreviation;
    }

    public String getShortName()
    {
        return abbreviations.lookupShortName(abbreviation);
    }

    public String getLongName()
    {
        return abbreviations.lookupLongName(abbreviation);
    }
}
