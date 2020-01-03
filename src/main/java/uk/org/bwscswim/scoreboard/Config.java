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

import com.fazecast.jSerialComm.SerialPort;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Reads the Scoreboard's configuration properties. Values in a file called {code}config.properties{code} override those
 * found in a resource file called {code}defaultConfig.properties{code}. The getter methods also provide a default value
 * parameter if there is not a value in ether file.
 *
 * @author adavis
 */
public class Config
{
    private static final String FONT_NAME = "fontName";
    private static final String FONT_STYLE = "fontStyle";
    private static final String FONT_SIZE = "fontSize";

    private final Properties properties;
    private final Map<String, String> fontFamilyNames = new HashMap<>();

    Config(String configFilename)
    {
        properties = getProperties(configFilename);
        for (String fontFamilyName: GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
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
            System.err.println();
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

    Font getFont(State state, String componentName)
    {
        return new Font(
                getFontName(state, null, componentName+'.'+FONT_NAME, "Arial"),
                getFontStyle(state, null, componentName+'.'+FONT_STYLE, Font.BOLD),
                getInt(state, null, componentName+'.'+FONT_SIZE, 85));
    }

    private String getFontName(State state, String componentName, String attributeName,
                               String defaultValue)
    {
        String stateName = getStateName(state);
        for (String key: getKeys(stateName, componentName, attributeName))
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

    private int getFontStyle(State state, String componentName, String attributeName,
                             int defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(stateName, componentName, attributeName))
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

    String getString(String attributeName, String defaultValue)
    {
        return getString(null, null, attributeName, defaultValue);
    }

    String getString(State state, String componentName, String attributeName, String defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(stateName, componentName, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                return value;
            }
        }
        return defaultValue;
    }

    int getInt(String attributeName, int defaultValue)
    {
        return getInt(null, null, attributeName, defaultValue);
    }

    int getInt(State state, String componentName, String attributeName, int defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(stateName, componentName, attributeName))
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

    public float getFloat(String attributeName, float defaultValue)
    {
        for (String key : getKeys(null, null, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                try
                {
                    return Float.parseFloat(value.trim());
                }
                catch (NumberFormatException e)
                {
                    System.err.println("Invalid float " + value + " from property " + key);
                }
            }
        }
        return defaultValue;
    }

    public Boolean getBoolean(String attributeName, Boolean defaultValue)
    {
        return getBoolean(null, null, attributeName, defaultValue);
    }

    Boolean getBoolean(State state, String componentName, String attributeName, Boolean defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(stateName, componentName, attributeName))
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

    Color getColor(String componentName, String attributeName, Color defaultValue)
    {
        return getColor(null, componentName, attributeName, defaultValue);
    }

    Color getColor(State state, String componentName, String attributeName, Color defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(stateName, componentName, attributeName))
        {
            String value = properties.getProperty(key);
            if (value != null)
            {
                if (value.startsWith("#") && value.length() == 7)
                {
                    int rgb = Integer.parseInt(value.substring(1), 16);
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
        return getRange(null, null, attributeName, defaultValue);
    }

    private String getRange(State state, String componentName, String attributeName, String defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(stateName, componentName, attributeName))
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
        return getCharRange(null, null, attributeName, defaultValue);
    }

    private String getCharRange(State state, String componentName, String attributeName, String defaultValue)
    {
        String stateName = getStateName(state);
        for (String key : getKeys(stateName, componentName, attributeName))
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
            int start = 0;
            int end = keyCompList.size();
            String origAttrName = attributeName;
            String attrName = attributeName;

            @Override
            public boolean hasNext()
            {
                if (start <= end)
                {
                    return true;
                }

                return !attrName.isEmpty();
            }

            @Override
            public String next()
            {
                if (!hasNext())
                {
                    throw new NoSuchElementException();
                }

                StringBuilder sb = new StringBuilder();
                if (start <= end)
                {
                    for (int i = start; i < end; i++)
                    {
                        sb.append(keyCompList.get(i)).append('.');
                    }
                    end--;
                    if (start == end)
                    {
                        if (start < keyCompList.size()-1)
                        {
                            start++;
                            end = keyCompList.size();
                        }
                     }
                }
                sb.append(attrName);

                if (start == end)
                {
                    int i = attrName.indexOf('.');
                    if (i != -1)
                    {
                        attrName = attrName.substring(i+1);
                        start = 0;
                        end = keyCompList.size();
                    }
                    else
                    {
                        attrName = origAttrName;
                    }
                }
                else if (start > end)
                {
                    int i = attrName.indexOf('.');
                    attrName = i == -1 ? "" : attrName.substring(i+1);
                }
                return sb.toString();
            }
        };
    }
}
