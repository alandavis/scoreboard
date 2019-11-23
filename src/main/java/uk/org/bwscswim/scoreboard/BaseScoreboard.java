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

import static uk.org.bwscswim.scoreboard.State.RESULTS;

/**
 * Abstract class containing methods used to setup the window onto the Scoreboard.
 *
 * @author adavis
 */
public abstract class BaseScoreboard extends javax.swing.JFrame
{
    protected final Config config;
    private final boolean secondScreen;
    private final boolean scoreboardVisible;

    private DataReader dataReader;

    public BaseScoreboard(Config config, boolean secondScreen)
    {
        this.config = config;
        this.secondScreen = secondScreen;
        scoreboardVisible = !secondScreen || config.getBoolean("secondScoreboardVisible", false);
    }

    protected void makeScoreboardVisible()
    {
        exitOnEscapeOrEnter();

        if (scoreboardVisible)
        {
            if (secondScreen)
            {
                Boolean fullScreen = config.getBoolean(null, null, "fullScreen2", true);
                if (fullScreen)
                {
                    setUndecorated(true);
                }

                packAndSetSize();
                System.out.println("Second ScoreboardSize=" + getSize());

                GraphicsDevice graphicsDevice = getGraphicsDevice("graphicsDevice2");
                DisplayMode displayMode = graphicsDevice.getDisplayMode();
                System.out.println("Second GraphicsDevice \"" + graphicsDevice.getIDstring() + "\" " + displayMode.getWidth() + "x" + displayMode.getHeight());

                if (fullScreen)
                {
                    graphicsDevice.setFullScreenWindow(this);
                }
                else
                {
                    javax.swing.JFrame dualview = new javax.swing.JFrame(graphicsDevice.getDefaultConfiguration());
                    setLocationRelativeTo(dualview);
                    dualview.dispose();
                    setVisible(true);
                }
            }
            else
            {
                Boolean fullScreen = config.getBoolean(null, null, "fullScreen1", true);
                if (fullScreen)
                {
                    setUndecorated(true);
                }

                packAndSetSize();
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
                    javax.swing.JFrame dualview = new javax.swing.JFrame(graphicsDevice.getDefaultConfiguration());
                    setLocationRelativeTo(dualview);
                    dualview.dispose();
                    setVisible(true);
                }
            }
        }
    }

    private void packAndSetSize()
    {
        pack();
        int width = config.getInt("width", -1);
        int height = config.getInt("height", -1);
        width = 1159;
        height = 728;
        if (width != -1 && height != -1)
        {
            setMinimumSize(new Dimension(width, height));
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

    public void setDataReader(DataReader dataReader)
    {
        this.dataReader = dataReader;
    }
}
