package uk.org.bwscswim.scoreboard.meet.model;

/**
 * @author adavis
 */
public class Swimmer
{
    private final String name;
    private final Integer yearOfBirth;
    private final Club club;

    private String asaNumber;

    public Swimmer(String name, Integer yearOfBirth, Club club)
    {
        this.name = name.trim();
        this.yearOfBirth = yearOfBirth;
        this.club = club;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Returns an abbreviated name that will fit the scoreboard. If the full name is too long, the first name is
     * replaced by an initial and if still too long the surname is truncated.
     * @param length the maximum length ofd the name.
     * @return the abbreviated name.
     */
    public String getAbbreviatedName(int length)
    {
        int l = name.length();
        if (l <= length)
        {
            return name;
        }

        String[] firstLastName = name.split(" ", 2);
        if (firstLastName.length == 1)
        {
            return name.substring(0, length);
        }

        String firstName = firstLastName[0];
        String lastName = firstLastName[1];

        if (lastName.length()+2 > length)
        {
            lastName = lastName.substring(0, length-2);
        }
        return ""+firstName.charAt(0)+' '+lastName;
    }

    public String getAsaNumber()
    {
        return asaNumber;
    }

    public void setAsaNumber(String asaNumber)
    {
        this.asaNumber = asaNumber;
    }

    public Integer getYearOfBirth()
    {
        return yearOfBirth;
    }

    public Club getClub()
    {
        return club;
    }
}
