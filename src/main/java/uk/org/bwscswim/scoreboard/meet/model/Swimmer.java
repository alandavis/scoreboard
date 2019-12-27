package uk.org.bwscswim.scoreboard.meet.model;

/**
 * @author adavis
 */
public class Swimmer
{
    private final String name;
    private final Integer yearOfBirth;
    private final Club club;

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
     * Returns a truncated name that will fit the scoreboard. If the full name is too long, the first name is
     * replaced by an initial and if still too long the surname is truncated. It also Camel cases names.
     * @param length the maximum length ofd the name.
     * @return the abbreviated name.
     */
    public String getStandardName(int length)
    {
        return getStandardName(name, length);
    }

    public static String getStandardName(String name, int length)
    {
        return getCamelCaseName(getTruncatedName(name, length));
    }

    private static String getCamelCaseName(String name)
    {
        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (char c : name.toCharArray())
        {
            if (Character.isAlphabetic(c) && Character.isUpperCase(c))
            {
                if (upperCase)
                {
                    c = Character.toLowerCase(c);
                }
                upperCase = true;
            }
            else
            {
                upperCase = false;
            }
            sb.append(c);
        }


        return sb.toString();
    }

    private static String getTruncatedName(String name, int length)
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

    public Integer getYearOfBirth()
    {
        return yearOfBirth;
    }

    public Club getClub()
    {
        return club;
    }
}
