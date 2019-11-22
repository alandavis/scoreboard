package uk.org.bwscswim.scoreboard;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

public class PBTest
{
    private static String TAG_LINE =
            "<div style=\"display: table; width:100%;\"><div style=\"display: table-row;\"><div style=\"display:" +
            " table-cell; vertical-align:top; width:100%\"><table width=\"100%\"><tr><td width=\"20%\" " +
            "style=\"background-color:#ECECEC; padding:4px\"><b>Name</b></td><td width=\"30%\" style=\"" +
            "background-color:#ECECEC; padding:4px\">Tess Davis</td><td width=\"20%\" style=\"background-color" +
            ":#ECECEC; padding:4px\"><b>Member</b></td><td width=\"30%\" style=\"background-color:#ECECEC; " +
            "padding:4px\">1228544</td></tr><tr><td width=\"20%\" style=\"background-color:#F8F8F8; " +
            "padding:4px\"><b>Year of Birth</b></td><td width=\"30%\" style=\"background-color:#F8F8F8; " +
            "padding:4px\">08</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px\">" +
            "<b>Gender</b></td><td width=\"30%\" style=\"background-color:#F8F8F8; padding:4px\">Female</td>" +
            "</tr><tr><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px\"><b>CoIR</b></td>" +
            "<td width=\"30%\" style=\"background-color:#ECECEC; padding:4px\">England</td><td width=\"20%\" " +
            "style=\"background-color:#ECECEC; padding:4px\"></td><td width=\"30%\" style=\"background-color" +
            ":#ECECEC; padding:4px\"></td></tr><tr><td width=\"20%\" style=\"background-color:#F8F8F8; " +
            "padding:4px\"><b>Club 1</b></td><td width=\"30%\" style=\"background-color:#F8F8F8; padding:4px\">" +
            "Bracknell & Wokingham SC</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px\">" +
            "<b>Ranked Club</b></td><td width=\"30%\" style=\"background-color:#F8F8F8; padding:4px\">" +
            "Bracknell & Wokingham SC</td></tr></table></div></div></div><br><table width=\"100%\" " +
            "style=\"page-break-before:always\"><tr><td width=\"20%\" style=\"background-color:#FFFFBB; " +
            "padding:4px; text-align:center\"><b><a href=/individualbest/personal_best.php?tiref=1228544&mode" +
            "=A&back=biogs>Click For Personal Best</a></b></td><td width=\"20%\" style=\"background-color:" +
            "#FFFFBB; padding:4px; text-align:center\"><b>2019 Short Course</b></td><td width=\"20%\" " +
            "style=\"background-color:#FFFFBB; padding:4px; text-align:center\"><b>Short Course PB</b></td>" +
            "<td width=\"20%\" style=\"background-color:#FFFFBB; padding:4px; text-align:center\"><b>" +
            "2019 Long Course</b></td><td width=\"20%\" style=\"background-color:#FFFFBB; padding:4px; " +
            "text-align:center\"><b>Long Course PB</b></td></tr><tr><td width=\"20%\" style=\"background-color" +
            ":#ECECEC; padding:4px; text-align:right\"><b>50m Freestyle</b></td><td width=\"20%\" style=\"" +
            "background-color:#ECECEC; padding:4px; text-align:right\">   36.57</td><td width=\"20%\" " +
            "style=\"background-color:#ECECEC; padding:4px; text-align:right\">   36.57</td><td width=\"20%\" " +
            "style=\"background-color:#ECECEC; padding:4px; text-align:right\">   40.42</td><td width=\"20%\"" +
            " style=\"background-color:#ECECEC; padding:4px; text-align:right\">   40.42</td></tr><tr><td " +
            "width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"><b>100m Freestyle" +
            "</b></td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"> " +
            "1:25.18</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"> " +
            "1:25.18</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\">" +
            "</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"></td></tr>" +
            "<tr><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; text-align:right\"><b>" +
            "200m Freestyle</b></td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\"> 2:58.93</td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\"> 2:58.93</td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\"></td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\"> 3:18.15</td></tr><tr><td width=\"20%\" style=\"background-color:#F8F8F8; " +
            "padding:4px; text-align:right\"><b>400m Freestyle</b></td><td width=\"20%\" style=\"background-color" +
            ":#F8F8F8; padding:4px; text-align:right\"></td><td width=\"20%\" style=\"background-color:#F8F8F8; " +
            "padding:4px; text-align:right\"> 6:45.46</td><td width=\"20%\" style=\"background-color:#F8F8F8; " +
            "padding:4px; text-align:right\"></td><td width=\"20%\" style=\"background-color:#F8F8F8; " +
            "padding:4px; text-align:right\"></td></tr><tr><td width=\"20%\" style=\"background-color:#ECECEC; " +
            "padding:4px; text-align:right\"><b>800m Freestyle</b></td><td width=\"20%\" " +
            "style=\"background-color:#ECECEC; padding:4px; text-align:right\"></td><td width=\"20%\" " +
            "style=\"background-color:#ECECEC; padding:4px; text-align:right\"></td><td width=\"20%\" " +
            "style=\"background-color:#ECECEC; padding:4px; text-align:right\"></td><td width=\"20%\" " +
            "style=\"background-color:#ECECEC; padding:4px; text-align:right\"></td></tr><tr><td width=\"20%\" " +
            "style=\"background-color:#F8F8F8; padding:4px; text-align:right\"><b>1500m Freestyle</b></td><td " +
            "width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"></td><td " +
            "width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"></td><td " +
            "width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"></td><td " +
            "width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"></td></tr>" +
            "<tr><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; text-align:right\">" +
            "<b>50m Breaststroke</b></td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\">   49.03</td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\">   49.03</td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\">   53.68</td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\">   53.27</td></tr><tr><td width=\"20%\" style=\"background-color:#F8F8F8; " +
            "padding:4px; text-align:right\"><b>100m Breaststroke</b></td><td width=\"20%\" " +
            "style=\"background-color:#F8F8F8; padding:4px; text-align:right\"> 1:42.47</td><td " +
            "width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"> 1:42.47</td>" +
            "<td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"></td><td " +
            "width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"></td></tr><tr>" +
            "<td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; text-align:right\">" +
            "<b>200m Breaststroke</b></td><td width=\"20%\" style=\"background-color:#ECECEC; padding:" +
            "4px; text-align:right\"> 3:39.42</td><td width=\"20%\" style=\"background-color:#ECECEC; " +
            "padding:4px; text-align:right\"> 3:39.42</td><td width=\"20%\" style=\"background-color:#ECECEC; " +
            "padding:4px; text-align:right\"></td><td width=\"20%\" style=\"background-color:#ECECEC; " +
            "padding:4px; text-align:right\"> 3:54.49</td></tr><tr><td width=\"20%\" " +
            "style=\"background-color:#F8F8F8; padding:4px; text-align:right\"><b>50m Butterfly</b></td>" +
            "<td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\">   40.15</td>" +
            "<td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\">   40.15</td>" +
            "<td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\">   42.93</td>" +
            "<td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\">   42.62</td>" +
            "</tr><tr><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; text-align:right\">" +
            "<b>100m Butterfly</b></td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\"> 1:31.20</td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\"> 1:31.20</td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\"> 1:38.99</td><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\"> 1:38.99</td></tr><tr><td width=\"20%\" style=\"background-color:#F8F8F8; " +
            "padding:4px; text-align:right\"><b>200m Butterfly</b></td><td width=\"20%\" style=\"background-" +
            "color:#F8F8F8; padding:4px; text-align:right\"></td><td width=\"20%\" style=\"background-color:" +
            "#F8F8F8; padding:4px; text-align:right\"></td><td width=\"20%\" style=\"background-color:#F8F8F8; " +
            "padding:4px; text-align:right\"></td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; " +
            "text-align:right\"></td></tr><tr><td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; " +
            "text-align:right\"><b>50m Backstroke</b></td><td width=\"20%\" style=\"background-color:#ECECEC; " +
            "padding:4px; text-align:right\">   39.09</td><td width=\"20%\" style=\"background-color:#ECECEC; " +
            "padding:4px; text-align:right\">   39.09</td><td width=\"20%\" style=\"background-color:#ECECEC; " +
            "padding:4px; text-align:right\">   42.91</td><td width=\"20%\" style=\"background-color:#ECECEC; " +
            "padding:4px; text-align:right\">   42.91</td></tr><tr><td width=\"20%\" style=\"background-color:#F8F8F8; " +
            "padding:4px; text-align:right\"><b>100m Backstroke</b></td><td width=\"20%\" style=\"background-" +
            "color:#F8F8F8; padding:4px; text-align:right\"> 1:26.05</td><td width=\"20%\" style=\"background-" +
            "color:#F8F8F8; padding:4px; text-align:right\"> 1:26.05</td><td width=\"20%\" style=\"background-" +
            "color:#F8F8F8; padding:4px; text-align:right\"> 1:32.13</td><td width=\"20%\" style=\"background-" +
            "color:#F8F8F8; padding:4px; text-align:right\"> 1:32.13</td></tr><tr><td width=\"20%\" style=\"" +
            "background-color:#ECECEC; padding:4px; text-align:right\"><b>200m Backstroke</b></td><td " +
            "width=\"20%\" style=\"background-color:#ECECEC; padding:4px; text-align:right\"> 3:02.10</td>" +
            "<td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; text-align:right\"> 3:02.10</td>" +
            "<td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; text-align:right\"> 3:14.83</td>" +
            "<td width=\"20%\" style=\"background-color:#ECECEC; padding:4px; text-align:right\"> 3:14.83</td>" +
            "</tr><tr><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\">" +
            "<b>200m Individual Medley</b></td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px;" +
            " text-align:right\"> 3:11.91</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; " +
            "text-align:right\"> 3:11.91</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; " +
            "text-align:right\"> 3:30.87</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; " +
            "text-align:right\"> 3:30.87</td></tr><tr><td width=\"20%\" style=\"background-color:#ECECEC; " +
            "padding:4px; text-align:right\"><b>400m Individual Medley</b></td><td width=\"20%\" style=\"" +
            "background-color:#ECECEC; padding:4px; text-align:right\"></td><td width=\"20%\" style=\"" +
            "background-color:#ECECEC; padding:4px; text-align:right\"></td><td width=\"20%\" style=\"" +
            "background-color:#ECECEC; padding:4px; text-align:right\"></td><td width=\"20%\" style=\"" +
            "background-color:#ECECEC; padding:4px; text-align:right\"></td></tr><tr><td width=\"20%\" " +
            "style=\"background-color:#F8F8F8; padding:4px; text-align:right\"><b>100m Individual Medley</b>" +
            "</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"> " +
            "1:32.32</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"> " +
            "1:32.32</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\">" +
            "</td><td width=\"20%\" style=\"background-color:#F8F8F8; padding:4px; text-align:right\"></td>" +
            "</tr></table><br>";
    private static String TAG_STRIPPED1 =
            ",,,,,,,Name,,,Tess Davis,,,Member,,,1228544,,,,,Year of Birth,,,08,,,Gender,,,Female" +
            ",,,,,CoIR,,,England,,,,,,,,,Club 1,,,Bracknell & Wokingham SC,,,Ranked Club,,,Bracknell & Wokingham SC" +
            ",,,,,,,,,,,,Click For Personal Best,,,,,2019 Short Course,,,,Short Course PB,,,,2019 Long Course" +
            ",,,,Long Course PB,,,,,,50m Freestyle,,,   36.57,,   36.57,,   40.42,,   40.42,,,,,100m Freestyle" +
            ",,, 1:25.18,, 1:25.18,,,,,,,,,200m Freestyle,,, 2:58.93,, 2:58.93,,,, 3:18.15,,,,,400m Freestyle" +
            ",,,,, 6:45.46,,,,,,,,,800m Freestyle,,,,,,,,,,,,,,1500m Freestyle,,,,,,,,,,,,,,50m Breaststroke" +
            ",,,   49.03,,   49.03,,   53.68,,   53.27,,,,,100m Breaststroke,,, 1:42.47,, 1:42.47,,,,,,,,," +
            "200m Breaststroke,,, 3:39.42,, 3:39.42,,,, 3:54.49,,,,,50m Butterfly,,,   40.15,,   40.15,,   42.93" +
            ",,   42.62,,,,,100m Butterfly,,, 1:31.20,, 1:31.20,, 1:38.99,, 1:38.99,,,,,200m Butterfly" +
            ",,,,,,,,,,,,,,50m Backstroke,,,   39.09,,   39.09,,   42.91,,   42.91,,,,,100m Backstroke" +
            ",,, 1:26.05,, 1:26.05,, 1:32.13,, 1:32.13,,,,,200m Backstroke,,, 3:02.10,, 3:02.10,, 3:14.83,, 3:14.83" +
            ",,,,,200m Individual Medley,,, 3:11.91,, 3:11.91,, 3:30.87,, 3:30.87,,,,,400m Individual Medley" +
            ",,,,,,,,,,,,,,100m Individual Medley,,, 1:32.32,, 1:32.32,,,,,,,,";

    private static String TAG_STRIPPED2 =
            ",,,,,,,Name,,,Antoni Kepa,,,Member,,,1281918,,,,,Year of Birth,,,06,,,Gender,,,Male,,,,,CoIR,,,England" +
            ",,,,,,,,,Club 1,,,Bracknell & Wokingham SC,,,Ranked Club,,,Bracknell & Wokingham SC,,,,,Coach,,,David Brazil," +
            ",,,,,,,,,,,,,Regional Achievements,,,,,REGAG18,100FR 3rd,,,,,,,,Ambition,,,,,Get to Nationals " +
            "in 2020 and to Olympics in the future. ,,,,,,,,,Click For Personal Best,,,,,2019 Short Course,,,," +
            "Short Course PB,,,,2019 Long Course,,,,Long Course PB,,,,,,50m Freestyle,,,,,   28.91,,   28.48,," +
            "   28.48,,,,,100m Freestyle,,,   58.11,,   58.11,, 1:00.72,, 1:00.72,,,,,200m Freestyle,,,,, 2:13.16" +
            ",, 2:14.92,, 2:14.92,,,,,400m Freestyle,,, 4:39.92,, 4:39.92,, 4:49.50,, 4:49.50,,,,,800m Freestyle" +
            ",,,,, 9:49.30,,,,,,,,,1500m Freestyle,,,17:56.70,,17:56.70,,19:59.13,,19:59.13,,,,,50m Breaststroke" +
            ",,,,,   40.34,,   40.00,,   40.00,,,,,100m Breaststroke,,,,, 1:31.84,, 1:28.64,, 1:28.64" +
            ",,,,,200m Breaststroke,,, 3:11.62,, 3:11.62,, 3:13.82,, 3:13.82,,,,,50m Butterfly" +
            ",,,,,   31.66,,   29.04,,   29.04,,,,,100m Butterfly,,, 1:08.91,, 1:08.91,, 1:09.17,, 1:09.17,,,,," +
            "200m Butterfly,,,,, 2:50.22,, 2:42.78,, 2:42.78,,,,,50m Backstroke,,,,,   32.29,,   32.60,,   32.60" +
            ",,,,,100m Backstroke,,, 1:04.35,, 1:04.35,, 1:08.21,, 1:08.21,,,,,200m Backstroke,,, 2:28.39,, 2:28.39" +
            ",, 2:29.01,, 2:29.01,,,,,200m Individual Medley,,, 2:35.42,, 2:35.04,, 2:33.68,, 2:33.68,,,,," +
            "400m Individual Medley,,,,, 5:46.03,, 5:24.87,, 5:24.87,,,,,100m Individual Medley,,,,, 1:18.78,,,,,,,,";

    private static String DATA1 =
            "Tess Davis," +               // Name
            "1228544," +                  // Member
            "08," +                       // Year of Birth
            "Female," +                   // Gender
//          "England," +                  // CoIR
//          "Bracknell & Wokingham SC," + // Ranked Club
            "36.57," +                    // 50m Freestyle
            "1:25.18," +                  // 100m Freestyle
            "2:58.93," +                  // 200m Freestyle
            "6:45.46," +                  // 400m Freestyle
            "," +                         // 800m Freestyle
            "," +                         // 1500m Freestyle
            "49.03," +                    // 50m Breaststroke
            "1:42.47," +                  // 100m Breaststroke
            "3:39.42," +                  // 200m Breaststroke
            "40.15," +                    // 50m Butterfly
            "1:31.20," +                  // 100m Butterfly
            "," +                         // 200m Butterfly
            "39.09," +                    // 50m Backstroke
            "1:26.05," +                  // 100m Backstroke
            "3:02.10," +                  // 200m Backstroke
            "3:11.91," +                  // 200m Individual Medley
            "," +                         // 400m Individual Medley
            "1:32.32";                    // 100m Individual Medley

    private static String DATA2 =
            "Antoni Kepa," +              // Name
            "1281918," +                  // Member
            "06," +                       // Year of Birth
            "Male," +                     // Gender
//          "England," +                  // CoIR
//          "Bracknell & Wokingham SC," + // Ranked Club
            "28.91," +                    // 50m Freestyle
            "58.11," +                    // 100m Freestyle
            "2:13.16," +                  // 200m Freestyle
            "4:39.92," +                  // 400m Freestyle
            "9:49.30," +                  // 800m Freestyle
            "17:56.70," +                 // 1500m Freestyle
            "40.34," +                    // 50m Breaststroke
            "1:31.84," +                  // 100m Breaststroke
            "3:11.62," +                  // 200m Breaststroke
            "31.66," +                    // 50m Butterfly
            "1:08.91," +                  // 100m Butterfly
            "2:50.22," +                  // 200m Butterfly
            "32.29," +                    // 50m Backstroke
            "1:04.35," +                  // 100m Backstroke
            "2:28.39," +                  // 200m Backstroke
            "2:35.04," +                  // 200m Individual Medley
            "5:46.03," +                  // 400m Individual Medley
            "1:18.78";                    // 100m Individual Medley

    private PB pb = new PB();

    @Test
    @Ignore // Don't run each time as Tess' data will change over time. Useful to have ready though.
    public void testReadDataFromAsa() throws Exception
    {
        assertEquals(TAG_LINE, pb.readDataFromAsa("1228544"));
    }

    @Test
    public void grepForDataTest()
    {
        assertFalse(pb.grepForData("pre1"));
        assertTrue(pb.grepForData(TAG_LINE));
    }

    @Test
    public void stripTagsTest()
    {
        assertEquals(",fred,", pb.stripTags("<td>fred</td>"));
        assertEquals(TAG_STRIPPED1, pb.stripTags(TAG_LINE));
    }

    @Test
    public void testExtractFields()
    {
        assertEquals(DATA1, pb.extractFields(TAG_STRIPPED1));
    }

    @Test
    public void testExtractFields2()
    {
        // 200 IM is not the 2nd column PB
        assertEquals(DATA2, pb.extractFields(TAG_STRIPPED2));
    }

    @Test
    @Ignore
    public void testRandomSleep()
    {
        for (int i=0; i<100; i++)
        {
            pb.random1To4SecondSleep();
        }
    }

    // Failed to read PBs for 1281918 line did not match the sed expression: 100FR 3rd,,,,,,,,Ambition,,,,,Get to Nationals in 2020 and to Olympics in the future. ,,,,,,,,,Click For Personal Best,,,,,2019 Short Course,,,,Short Course PB,,,,2019 Long Course,,,,Long Course PB,,,,,,50m Freestyle,,,,,   28.91,,   28.48,,   28.48,,,,,100m Freestyle,,,   58.11,,   58.11,, 1:00.72,, 1:00.72,,,,,200m Freestyle,,,,, 2:13.16,, 2:14.92,, 2:14.92,,,,,400m Freestyle,,, 4:39.92,, 4:39.92,, 4:49.50,, 4:49.50,,,,,800m Freestyle,,,,, 9:49.30,,,,,,,,,1500m Freestyle,,,17:56.70,,17:56.70,,19:59.13,,19:59.13,,,,,50m Breaststroke,,,,,   40.34,,   40.00,,   40.00,,,,,100m Breaststroke,,,,, 1:31.84,, 1:28.64,, 1:28.64,,,,,200m Breaststroke,,, 3:11.62,, 3:11.62,, 3:13.82,, 3:13.82,,,,,50m Butterfly,,,,,   31.66,,   29.04,,   29.04,,,,,100m Butterfly,,, 1:08.91,, 1:08.91,, 1:09.17,, 1:09.17,,,,,200m Butterfly,,,,, 2:50.22,, 2:42.78,, 2:42.78,,,,,50m Backstroke,,,,,   32.29,,   32.60,,   32.60,,,,,100m Backstroke,,, 1:04.35,, 1:04.35,, 1:08.21,, 1:08.21,,,,,200m Backstroke,,, 2:28.39,, 2:28.39,, 2:29.01,, 2:29.01,,,,,200m Individual Medley,,, 2:35.42,, 2:35.04,, 2:33.68,, 2:33.68,,,,,400m Individual Medley,,,,, 5:46.03,, 5:24.87,, 5:24.87,,,,,100m Individual Medley,,,,, 1:18.78,,,,,,,,
}