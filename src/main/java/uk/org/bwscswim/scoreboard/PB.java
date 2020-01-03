/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2020 Bracknell and Wokingham Swimming Club (BWSC)
 * %%
 * This file is part of BWSC Scoreboard.
 *
 * BWSC Scoreboard is free software: you can redistribute it and/or modify
 * it under the terms of the LGNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BWSC Scoreboard is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * LGNU Lesser General Public License for more details.
 *
 * You should have received a copy of the LGNU Lesser General Public License
 * along with BWSC Scoreboard.  If not, see <https://www.gnu.org/licenses/>.
 * #L%
 */
package uk.org.bwscswim.scoreboard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author adavis
 */
public class PB
{
    private static String BASE_URL = "https://www.swimmingresults.org/biogs/biogs_details.php?tiref=";

    private static final String GREP_REGEX = ".*Short Course PB.*";
    private static final String STRIP_TAGS_REGEX = "<[^>]*>";

    private static final String COL1 = ",,,([^,]*).*";
    private static final String COL2 = ",,, *[^,]*,, *([^,]*).*";

    private static final String SED_MATCH_REGEX =
            ".*,," +
            "Name"+COL1+
            "Member"+COL1+
            "Year of Birth"+COL1+
            "Gender"+COL1+
            "50m Freestyle"+ COL2 +
            "100m Freestyle"+ COL2 +
            "200m Freestyle"+ COL2 +
            "400m Freestyle"+ COL2 +
            "800m Freestyle"+ COL2 +
            "1500m Freestyle"+ COL2 +
            "50m Breaststroke"+ COL2 +
            "100m Breaststroke"+ COL2 +
            "200m Breaststroke"+ COL2 +
            "50m Butterfly"+ COL2 +
            "100m Butterfly"+ COL2 +
            "200m Butterfly"+ COL2 +
            "50m Backstroke"+ COL2 +
            "100m Backstroke"+ COL2 +
            "200m Backstroke"+ COL2 +
            "200m Individual Medley"+ COL2 +
            "400m Individual Medley"+ COL2 +
            "100m Individual Medley"+ COL2;

    Pattern linePatten = Pattern.compile(GREP_REGEX);
    Pattern sedPatten = Pattern.compile(SED_MATCH_REGEX);

    private final Config config;

    PB()
    {
        config = new Config("config.properties");
    }

    private void buildPBfile(String filename) throws IOException
    {
        try (Writer writer = new BufferedWriter(new FileWriter(filename)))
        {
            List<String> asaNumbers = getAsaNumbers("Entry file output.txt", config);
            for (String asaNumber : asaNumbers)
            {
                try
                {
                    // curl https://www.swimmingresults.org/biogs/biogs_details.php?tiref=1228544 | grep 'Individual Medley'
                    String line = readDataFromAsa(asaNumber);

                    // sed 's/<[^>]*>/,/g'
                    line = stripTags(line);

                    line = extractFields(line);
                    if (line != null)
                    {
                        writer.write(line + "\n");
                        writer.flush();
                    }
                }
                catch (Exception e)
                {
                    System.err.println("Failed to read PBs for "+asaNumber+" "+e.getMessage());
                }
            }
        }
    }

    private List<String> getAsaNumbers(String filename, Config config) throws IOException
    {
        List<String> asaNumbers = new ArrayList<>();
        try (BufferedReader reader = FileLoader.getBufferedReader(filename, config))
        {
            reader.lines().forEach(line -> asaNumbers.add(getAsaNumber(line)));
        }
        return asaNumbers;
    }

    private String getAsaNumber(String line)
    {
        String[] col = line.split("\t");
        String asaNumberPlus = col[3];
        String[] asaNumber = asaNumberPlus.split("  *");
        return asaNumber[0];
    }

    private int lineNumber = 1;
    String readDataFromAsa(String asaNumber) throws IOException
    {
        long start = System.currentTimeMillis();
        URL url = new URL(BASE_URL+asaNumber);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (grepForData(line))
                {
                    random1To4SecondSleep();
                    System.out.println((lineNumber++)+" Read "+asaNumber+" "+(System.currentTimeMillis()-start)+"ms");
                    return line;
                }
            }
        }
        throw new IllegalStateException("The page returned from "+url+" did not contain \""+ GREP_REGEX +"\"");
    }

    void random1To4SecondSleep()
    {
        long start = System.currentTimeMillis();
        try
        {
            Thread.sleep((long) (1000+Math.random()*3000));
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println("Sleep "+(System.currentTimeMillis()-start)+"ms");
    }

    boolean grepForData(String line)
    {
        return linePatten.matcher(line).matches();
    }

    String stripTags(String line)
    {
        // sed 's/<[^>]*>/,/g'
        return line.replaceAll(STRIP_TAGS_REGEX, ",");
    }

    String extractFields(String line)
    {
        StringJoiner sj = new StringJoiner(",");
        Matcher matcher = sedPatten.matcher(line);
        if (!matcher.matches())
        {
            throw new IllegalStateException("line did not match the sed expression: "+line);
        }
        int count = matcher.groupCount();
        for (int i=1; i<=count; i++)
        {
            sj.add(matcher.group(i));
        }
        return sj.toString();
    }

    public static void main(String[] args)
    {
        try
        {
            PB pb = new PB();
            pb.buildPBfile("BP.txt");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
