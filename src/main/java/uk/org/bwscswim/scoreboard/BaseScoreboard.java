/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2019 Bracknell and Wokingham Swimming Club (BWSC)
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

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static uk.org.bwscswim.scoreboard.RawState.TEST;

/**
 * Abstract class containing methods used to setup the window onto the Scoreboard.
 *
 * @author adavis
 */
public abstract class BaseScoreboard extends javax.swing.JFrame
{
    protected final Config config;
    private final boolean secondScreen;
    protected final long showTestCardFor;
    private final boolean scoreboardVisible;

    protected Container contentPane;

    protected RawState state = TEST;

    protected Color background;
    protected Color titleForeground;
    protected Color laneForeground;

    private DataReader dataReader;

    public BaseScoreboard(Config config, boolean secondScreen)
    {
        this.config = config;
        this.secondScreen = secondScreen;
        showTestCardFor = config.getInt("showTestCardFor", 30);
        scoreboardVisible = !secondScreen || config.getBoolean("secondScoreboardVisible", false);

        contentPane = getContentPane();
    }

    protected void postConstructor()
    {
        getColors();
        setColors();

//        try
//        {
//            JLabel label = new JLabel(new ImageIcon(ImageIO.read(getClass().getClassLoader().getResourceAsStream(("BWSC.png")))));
//            setContentPane(label);
//        }
//        catch (IOException ignore)
//        {
//        }

        exitOnEscapeOrEnter();

        if (scoreboardVisible)
        {
            if (secondScreen)
            {
                pack();
                System.out.println("Second ScoreboardSize=" + getSize());
                // Trick to move this Frame to the required device
                GraphicsDevice graphicsDevice = getGraphicsDevice("graphicsDevice2");
                DisplayMode displayMode = graphicsDevice.getDisplayMode();
                System.out.println("Second GraphicsDevice \"" + graphicsDevice.getIDstring() + "\" " + displayMode.getWidth() + "x" + displayMode.getHeight());
                javax.swing.JFrame dualview = new javax.swing.JFrame(graphicsDevice.getDefaultConfiguration());
                setLocationRelativeTo(dualview);
                dualview.dispose();
                setVisible(true);

//                pack();
//                System.out.println("Second ScoreboardSize=" + getSize());
//                setVisible(true);
            }
            else
            {
                Boolean fullScreen = config.getBoolean(null, null, "fullScreen", true);
                if (config.getBoolean(null, null, "originalScreenSetup", false))
                {
                    System.out.println("Using original screen setup");
                    pack();
                    System.out.println("ScoreboardSize=" + getSize());
                    if (fullScreen)
                    {
                        GraphicsDevice graphicsDevice = getGraphicsDevice("graphicsDevice1");
                        DisplayMode displayMode = graphicsDevice.getDisplayMode();
                        System.out.println("Using GraphicsDevice \"" + graphicsDevice.getIDstring() + "\" " + displayMode.getWidth() + "x" + displayMode.getHeight());

                        if (graphicsDevice.isFullScreenSupported())
                        {
                            graphicsDevice.setFullScreenWindow(this);
                        }
                        else
                        {
                            System.err.println("Full screen not supported by " + graphicsDevice.getIDstring());
                        }
                    }
                    setVisible(true);
                }
                else
                {
                    if (fullScreen)
                    {
                        setUndecorated(true);
                    }

                    pack();
                    System.out.println("ScoreboardSize=" + getSize());

                    GraphicsDevice graphicsDevice = getGraphicsDevice("graphicsDevice1");
                    DisplayMode displayMode = graphicsDevice.getDisplayMode();
                    System.out.println("Using GraphicsDevice \"" + graphicsDevice.getIDstring() + "\" " + displayMode.getWidth() + "x" + displayMode.getHeight());

                    if (fullScreen)
                    {
                        graphicsDevice.setFullScreenWindow(this);
                    }
                    else
                    {
                        // Trick to move this Frame to the required device
                        javax.swing.JFrame dualview = new javax.swing.JFrame(graphicsDevice.getDefaultConfiguration());
                        setLocationRelativeTo(dualview);
                        dualview.dispose();
                        setVisible(true);
                    }
                }
            }
        }
    }

    protected void exitOnEscapeOrEnter()
    {
        if (dataReader != null)
        {
            dataReader.close();
        }
        setFocusable(true);
        addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                char c = e.getKeyChar();
                if (c == '\n' || c == 27)
                {
                    System.exit(0);
                }
                super.keyTyped(e);
            }
        });
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                System.exit(0);
            }
        });
    }

    protected void getColors()
    {
        background = config.getColor(state, null, "background", Color.BLACK);
        titleForeground = config.getColor(state, null, "title.foreground", Color.YELLOW);
        laneForeground = config.getColor(state, null, "lane.foreground", Color.WHITE);
    }

    protected void setColors()
    {
        contentPane.setBackground(background);
    }

    protected String getTest(String componentName)
    {
        String string = config.getString(null, null, componentName + "Test", "");
        if (showTestCardFor <= 0)
        {
            string = string.replaceAll(".", " ");
        }
        return string;
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (scoreboardVisible)
        {
            super.setVisible(visible);
        }
    }

    private GraphicsDevice getGraphicsDevice(String propertyName)
    {
        String graphicsDeviceId = config.getString(propertyName, "\\Display1");
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        System.out.println("\nGraphicsDevices");
        int i = 1;
        for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
        {
            String id = device.getIDstring();
            System.out.println((i++)+". "+ id);
            if (id.equals(graphicsDeviceId))
            {
                graphicsDevice = device;
            }
        }
        return graphicsDevice;
    }

    protected void setState(RawState state)
    {
        if (this.state != state && scoreboardVisible)
        {
            this.state = state;
            getColors();
            setColors();
        }
    }

    public abstract void clear();

    public void setDataReader(DataReader dataReader)
    {
        this.dataReader = dataReader;
    }

    public void beforeFirstRead()
    {
        if (showTestCardFor > 0)
        {
            try
            {
                Thread.sleep(showTestCardFor);
            }
            catch (InterruptedException ignore)
            {
            }
        }
    }
}
