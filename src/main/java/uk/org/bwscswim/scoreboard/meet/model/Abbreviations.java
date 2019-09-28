package uk.org.bwscswim.scoreboard.meet.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author adavis
 */
public class Abbreviations
{
    Map<String, String> map = new HashMap<>();

    public Abbreviations(String filename) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            reader.lines().forEach(line ->
            {
                String[] split = line.split(",", 2);
                String fullValue = split[0].trim();
                String abbreviation = split[1].trim();
                map.put(fullValue, abbreviation);
            });
        }
    }

    public String lookup(String fullValue)
    {
        String abbreviation = map.get(fullValue);
        return abbreviation == null ? fullValue : abbreviation;
    }
}
