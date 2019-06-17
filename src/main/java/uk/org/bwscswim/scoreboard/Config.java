package uk.org.bwscswim.scoreboard;

import com.fazecast.jSerialComm.SerialPort;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

public class Config
{
    public static final String FONT_NAME = "fontName";
    public static final String FONT_STYLE = "fontStyle";
    public static final String FONT_SIZE = "fontSize";

    private final Properties properties;
    private final Map<String, String> fontFamilyNames = new HashMap();

    Config(String configFilename)
    {
        properties = getProperties(configFilename);
        for (String fontFamilyName: Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()))
        {
            fontFamilyNames.put(fontFamilyName.toLowerCase(), fontFamilyName);
        }
    }

    private static Properties getProperties(String configFilename)
    {
        Properties defaultProperties = new Properties();
        try
        {
            defaultProperties = getDefaultProperties();
            Properties properties = new Properties(defaultProperties);
            if (configFilename != null)
            {
                try (InputStream input = new FileInputStream(configFilename))
                {
                    properties.load(input);
                }
            }
            return properties;
        }
        catch (IOException e)
        {
            System.err.println("");
            System.err.println(e.getMessage());
            return defaultProperties;
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

    public Font getFont(String scoreboardName, State state, String componentName)
    {
        return new Font(
                getFontName(scoreboardName, state, null, componentName+'.'+FONT_NAME, Font.MONOSPACED),
                getFontStyle(scoreboardName, state, null, componentName+'.'+FONT_STYLE, Font.PLAIN),
                getInt(scoreboardName, state, null, componentName+'.'+FONT_SIZE, 16));
    }

    private String getFontName(String scoreboardName, State state, String componentName, String attributeName,
                               String defaultValue)
    {
        String stateName = getStateName(state);
        for (String key: getKeys(scoreboardName, stateName, componentName, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                String lowerCaseValue = value.toLowerCase();
                String fontFamilyName = fontFamilyNames.get(lowerCaseValue);
                if (fontFamilyName != null)
                {
                    return fontFamilyName;
                }
                System.err.println("Invalid font family name " + value + " from property " + key);
            }
        }
        return defaultValue;
    }

    private int getFontStyle(String scoreboardName, State state, String componentName, String attributeName,
                             int defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(scoreboardName, stateName, componentName, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                switch (value.toLowerCase())
                {
                    case "plain": return Font.PLAIN;
                    case "bold": return Font.BOLD;
                    case "italic": return Font.ITALIC;
                    default:
                        System.err.println("Invalid font style " + value + " from property " + key);
                        break;
                }
            }
        }
        return defaultValue;
    }

    public String getString(String attributeName, String defaultValue)
    {
        return getString(null, null, null, attributeName, defaultValue);
    }

    public String getString(String scoreboardName, State state, String componentName, String attributeName, String defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(scoreboardName, stateName, componentName, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                return value;
            }
        }
        return defaultValue;
    }

    public int getInt(String attributeName, int defaultValue)
    {
        return getInt(null, null, null, attributeName, defaultValue);
    }

    public int getInt(String scoreboardName, State state, String componentName, String attributeName, int defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(scoreboardName, stateName, componentName, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                try
                {
                    return Integer.parseInt(value.trim());
                }
                catch (NumberFormatException e)
                {
                    System.err.println("Invalid int " + value + " from property " + key);
                }
            }
        }
        return defaultValue;
    }

    public Boolean getBoolean(String attributeName, Boolean defaultValue)
    {
        return getBoolean(null, null, null, attributeName, defaultValue);
    }

    public Boolean getBoolean(String scoreboardName, State state, String componentName, String attributeName, Boolean defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(scoreboardName, stateName, componentName, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                if (value.equalsIgnoreCase("true"))
                {
                    return true;
                }
                else if (value.equalsIgnoreCase("false"))
                {
                    return false;
                }
                else
                {
                    System.err.println("Invalid boolean " + value + " from property " + key);
                }
            }
        }
        return defaultValue;
    }

    public Color getColor(String scoreboardName, State state, String componentName, String attributeName, Color defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(scoreboardName, stateName, componentName, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                if (value.startsWith("#") && value.length() == 7)
                {
                    int rgb = Integer.parseInt(value.substring(2), 16);
                    return new Color(rgb);
                }
                else
                {
                    switch (value.toLowerCase())
                    {
                        case "black": return Color.black;
                        case "white": return Color.white;
                        case "red": return Color.red;
                        case "green": return Color.green;
                        case "blue": return Color.blue;
                        case "cyan": return Color.cyan;
                        case "yellow": return Color.yellow;
                        case "magenta": return Color.magenta;
                    }
                    System.err.println("Invalid color " + value + " from property " + key);
                }
            }
        }
        return defaultValue;
    }

    public String getRange(String attributeName, String defaultValue)
    {
        return getRange(null, null, null, attributeName, defaultValue);
    }

    private String getRange(String scoreboardName, State state, String componentName, String attributeName, String defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(scoreboardName, stateName, componentName, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                if (value.length() == 10 && "..".equals(value.substring(4,6)) && value.substring(0,2).equals(value.substring(6,8)))
                {
                    return value;
                }
                System.err.println("Invalid range llc1..llc2 "+value+" from property " + key);
            }
        }
        return defaultValue;
    }

    public String getCharRange(String attributeName, String defaultValue)
    {
        return getCharRange(null, null, null, attributeName, defaultValue);
    }

    private String getCharRange(String scoreboardName, State state, String componentName, String attributeName, String defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(scoreboardName, stateName, componentName, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                if (value.length() == 6 && "..".equals(value.substring(2,4)))
                {
                    return value;
                }
                System.err.println("Invalid char range c1..c2 "+value+" from property " + key);
            }
        }
        return defaultValue;
    }


    public SerialPort getPort()
    {
        String port = getString("port", "COM4");
        SerialPort commPort = SerialPort.getCommPort(port);

        commPort.setBaudRate(getInt("baudRate", 19200));
        commPort.setNumDataBits(getInt("numDataBits", 8));
        commPort.setNumStopBits(getInt("numStopBits", 1));
        commPort.setParity(getInt("parity", SerialPort.NO_PARITY)); // 0
        commPort.setFlowControl(getInt("flowControl", SerialPort.FLOW_CONTROL_DISABLED)); // 0
        commPort.setComPortTimeouts(
                getInt("timeoutMode", SerialPort.TIMEOUT_READ_BLOCKING), // 2
                getInt("readTimeout", 0),
                getInt("writeTimeout", 0));

        System.err.println(
                "port="+port+
                        " baudRate="+commPort.getBaudRate()+
                        " numDataBits="+commPort.getNumDataBits()+
                        " numStopBits="+commPort.getNumStopBits()+
                        " parity="+commPort.getParity()+
                        " flowControl="+commPort.getFlowControlSettings()+
                        " readTimeout="+commPort.getReadTimeout()+
                        " writeTimeout="+commPort.getWriteTimeout());

        return commPort;
    }

    private String getStateName(State state)
    {
        return state == null ? null : state.toString().toLowerCase();
    }

    /**
     * Returns an Iterable of keys, removing a level of nesting each time and then leading components of the
     * attributeName which itself may contain dots.
     */
    static Iterable<String> getKeys(String... keyComp)
    {
        // Remove nulls and pull out the attributeName (the final parameter)
        int end = keyComp.length - 1;
        String attributeName = keyComp[end];
        List<String> keyCompList = new ArrayList<>();
        for (int i=0; i<end; i++)
        {
            String comp = keyComp[i];
            if (comp != null)
            {
                keyCompList.add(comp);
            }
        }

        return () -> new Iterator<String>()
        {
            int keyLength=keyCompList.size();
            String attrName = attributeName;

            @Override
            public boolean hasNext()
            {
                if (keyLength >= 0)
                {
                    return true;
                }

                int i = attrName.indexOf('.');
                if (i == -1)
                {
                    return false;
                }

                attrName = attrName.substring(i+1);
                keyLength = keyCompList.size();
                return hasNext();
            }

            @Override
            public String next()
            {
                if (!hasNext())
                {
                    throw new NoSuchElementException();
                }

                StringBuilder sb = new StringBuilder();
                if (keyLength > 0)
                {
                    sb.append(keyCompList.get(0)).append('.');
                    for (int i = 1; i < keyLength; i++)
                    {
                        sb.append(keyCompList.get(i)).append('.');
                    }
                }
                keyLength--;
                sb.append(attrName);
                return sb.toString();
            }
        };
    }
}
