package uk.org.bwscswim.scoreboard.meet.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author adavis
 */
public class Abbreviations
{
    Map<String, List<String>> map = new HashMap<>();
    Map<String, String> reverseMap = new HashMap<>();

    public Abbreviations(String filename) throws IOException
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            reader.lines().forEach(line ->
            {
                if (!line.trim().isEmpty())
                {
                    String[] split = line.split(",", 2);
                    String name = split[0].trim();
                    String abbreviation = split[1].trim();

                    reverseMap.put(name, abbreviation);

                    List<String> list = map.get(abbreviation);
                    if (list == null)
                    {
                        list = new ArrayList<>(1);
                        map.put(abbreviation, list);
                    }
                    list.add(name);
                    list.stream().sorted((name1, name2) -> name1.length()-name2.length());
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
        List<String> list = map.get(abbreviation);
        return list == null ? abbreviation : list.get(list.size()-1);
    }

    public String lookupShortName(String abbreviation)
    {
        List<String> list = map.get(abbreviation);
        return list == null ? abbreviation : list.get(0);
    }
}
