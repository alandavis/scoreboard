package uk.org.bwscswim.scoreboard.meet.model;

import uk.org.bwscswim.scoreboard.Config;
import uk.org.bwscswim.scoreboard.FileLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author adavis
 */
public class Abbreviations
{
    Map<String, String> longNameMap = new HashMap<>();
    Map<String, String> shortNameMap = new HashMap<>();
    Map<String, String> reverseMap = new HashMap<>();

    public Abbreviations(String filename, Config config) throws IOException
    {
        try (BufferedReader reader = FileLoader.getBufferedReader(filename, config))
        {
            reader.lines().forEach(line ->
            {
                if (!line.trim().isEmpty())
                {
                    String[] split = line.split(",");
                    String shortName = split[0].trim();
                    String abbreviation = split[1].trim();

                    reverseMap.put(shortName, abbreviation);
                    shortNameMap.put(abbreviation, shortName);

                    if (split.length == 3)
                    {
                        String longName = split[2].trim();
                        reverseMap.put(longName, abbreviation);
                        longNameMap.put(abbreviation, longName);
                    }
                }
            });
        }
    }

    public String lookupAbbreviation(String name)
    {
        String abbreviation = reverseMap.get(name);
        return abbreviation == null ? name : abbreviation;
    }

    public String lookupLongName(String abbreviation)
    {
        String longName = longNameMap.get(abbreviation);
        return longName == null ? abbreviation : longName;
    }

    public String lookupShortName(String abbreviation)
    {
        String shortName = shortNameMap.get(abbreviation);
        return shortName == null ? abbreviation : shortName;
    }
}
