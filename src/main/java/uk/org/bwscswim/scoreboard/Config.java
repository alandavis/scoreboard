package uk.org.bwscswim.scoreboard;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config
{
    private final Properties properties;

    Config(String configFilename)
    {
        properties = getProperties(configFilename);
    }

    private static Properties getProperties(String configFilename)
    {
        try
        {
            Properties defaultProperties = getDefaultProperties();
            Properties prop = new Properties(defaultProperties);
            if (configFilename != null)
            {
                try (InputStream input = new FileInputStream(configFilename))
                {
                    prop.load(input);
                }
            }
            return prop;
        }
        catch (IOException e)
        {
            error(e.getMessage());
            return null;
        }
    }

    private static Properties getDefaultProperties() throws IOException
    {
        Properties prop = new Properties();
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("defaultConfig.properties"))
        {
            prop.load(input);
        }
        return prop;
    }

    public int getLineCount()
    {
        return getInt("lineCount", 12);
    }

    public int getTitleLineLength()
    {
        return getInt("titleLineLength", 38);
    }

    public int getLaneLineLength()
    {
        return getInt("laneLineLength", 38);
    }

    public int getLaneCount()
    {
        return getInt("laneCount", 6);
    }

    public boolean isLineVisible(int lineNumber)
    {
        return getBoolean("line"+lineNumber+"Visible", true);
    }

    public String getDisplayName()
    {
        return getString("displayName", "scoreboard");
    }

    public boolean isRawDisplayVisible()
    {
        return getBoolean("rawDisplayVisible", false);
    }

    public boolean isFullScreen()
    {
        return getBoolean("fullScreen", true);
    }

    public String getTest(String name)
    {
        return getString(name+"Test", "unknown");
    }

    public int getHorizontalGap()
    {
        return getInt("horizontalGap", 40);
    }

    public int getPreLaneGap()
    {
        return getInt("preLaneGap", 80);
    }

    // Font

    public Font getFont(String name)
    {
        return new Font(getFontName(name), getFontStyle(name), getFontSize(name));
    }

    private String getFontName(String name)
    {
        return getFontName(name+"FontName", getDefaultFontName());
    }

    private String getDefaultFontName()
    {
        return getFontName("fontName", Font.MONOSPACED);
    }

    private String getFontName(String key, String defaultValue)
    {
        String value = properties.getProperty(key);
        if (value != null)
        {
            switch (value.toLowerCase())
            {
                case "dialog": value = Font.DIALOG; break;
                case "dialoginput": value = Font.DIALOG_INPUT; break;
                case "sansserif": value = Font.SANS_SERIF; break;
                case "serif": value = Font.SERIF; break;
                case "monospaced": value = Font.MONOSPACED; break;
            }
        }
        return value == null ? defaultValue : value;
    }

    private int getFontStyle(String name)
    {
        return getFontStyle(name+"FontStyle", getDefaultFontStyle());
    }

    private int getDefaultFontStyle()
    {
        return getInt("fontStyle", Font.PLAIN);
    }

    private int getFontStyle(String key, int defaultValue)
    {
        int style = -1;
        String value = properties.getProperty(key);
        if (value != null)
        {
            switch (value.toLowerCase())
            {
                case "plain": style = Font.PLAIN; break;
                case "regular": style = Font.BOLD; break;
                case "italic": style = Font.ITALIC; break;
            }
        }
        return style == -1 ? defaultValue : style;
    }

    private int getFontSize(String name)
    {
        return getInt(name+"FontSize", getDefaultFontSize());
    }

    private int getDefaultFontSize()
    {
        return getInt("fontSize", 16);
    }

    // Foreground Color

    private Color getForeground(boolean result)
    {
        return getColor(result ? "resultForeground" : "foreground", Color.WHITE);
    }

    public Color getForeground(String name, boolean result)
    {
        return getColor(name + (result ? "Result" : "") + "Foreground", getForeground(result));
    }

    public Color getForeground(int lineNumber, boolean result)
    {
        return getColor("line" + lineNumber + (result ? "Result" : "") + "Foreground", getForeground(result));
    }

    // Background Color

    public Color getBackground(boolean result)
    {
        return getColor(result ? "resultBackground" : "background", Color.BLACK);
    }

    private Color getColor(String key, Color defaultValue)
    {
        Color color = null;
        String value = properties.getProperty(key);
        if (value != null)
        {
            if (value.startsWith("#") && value.length() == 7)
            {
                int rgb = Integer.parseInt(value.substring(2), 16);
                color = new Color(rgb);
            }
            else
            {
                switch (value.toLowerCase())
                {
                    case "black": color = Color.black; break;
                    case "white": color = Color.white; break;
                    case "red": color = Color.red; break;
                    case "green": color = Color.green; break;
                    case "blue": color = Color.blue; break;
                    case "cyan": color = Color.cyan; break;
                    case "yellow": color = Color.yellow; break;
                    case "magenta": color = Color.magenta; break;
                }
            }
        }
        return color == null ? defaultValue : color;
    }

    public SerialPort getPort()
    {
        String port = getString("port", "COM4");
        return SerialPort.getCommPort(port);
    }

    public String getTestFilename()
    {
        return getString("testFilename", null);
    }

    public boolean isTestLoop()
    {
        return getBoolean("testLoop", false);
    }

    public int getF0()
    {
        return getInt("f0", 0);
    }

    public int getF1()
    {
        return getInt("f1", 3);
    }

    public int getF2()
    {
        return getInt("f2", 20);
    }

    public int getF3()
    {
        return getInt("f3", 25);
    }

    public int getF4()
    {
        return getInt("f4", 34);
    }

    // Basic types

    public String getString(String key, String defaultValue)
    {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : value;
    }

    private Boolean getBoolean(String key, Boolean defaultValue)
    {
        String value = properties.getProperty(key);
        return value == null ? defaultValue : Boolean.parseBoolean(value);
    }

    public int getInt(String key, int defaultValue)
    {
        String value = properties.getProperty(key);
        try
        {
            return value == null ? defaultValue : Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            String message = "Invalid " + key + ": " + value;
            error(message);
            return -1;
        }
    }

    private static void error(String message)
    {
        System.err.println("");
        System.err.println(message);
        System.exit(-1);
    }
}
