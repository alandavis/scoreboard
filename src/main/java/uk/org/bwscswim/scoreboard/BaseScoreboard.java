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

import uk.org.bwscswim.scoreboard.event.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Abstract class containing methods used to setup the windows onto the Scoreboard. When there are two screens
 * the main one is used as the control window (on the laptop's screen) with the second one as the main scoreboard
 * (on the monitor).
 *
 * @author adavis
 */
public abstract class BaseScoreboard extends JDialog implements Observer
{
    protected final Config config;
    private final boolean useSecondScreen;
    protected final boolean includeControls;

    private DataReader dataReader;

    public BaseScoreboard(Config config, DataReader dataReader, boolean useSecondScreen, boolean includeControls)
    {
        this.config = config;
        if (dataReader != null)
        {
            this.dataReader = dataReader;
            dataReader.addObserver(this);
        }
        this.useSecondScreen = useSecondScreen;
        this.includeControls = includeControls;
    }

    protected void makeScoreboardVisible()
    {
        GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice graphicsDevice = localGraphicsEnvironment.getDefaultScreenDevice();
        boolean windows = System.getProperty("os.name").startsWith("Windows");
        if (useSecondScreen)
        {
            GraphicsDevice[] screenDevices = localGraphicsEnvironment.getScreenDevices();
            graphicsDevice = screenDevices[0].equals(graphicsDevice) ? screenDevices[1] : screenDevices[0];

            setUndecorated(true);
            setFocusable(false);
            graphicsDevice.setFullScreenWindow(this);
        }
        else
        {
            if (windows)
            {
                setUndecorated(true);
            }
            addExitAndFullScreenListeners(graphicsDevice);
        }
        pack();
        setVisible(true);

        DisplayMode displayMode = graphicsDevice.getDisplayMode();
        String id = "Scoreboard " + (useSecondScreen ? "(second) " : "(laptop)");
        System.out.println(id+" \"" + graphicsDevice.getIDstring() + "\" " + displayMode.getWidth() + "x" + displayMode.getHeight());
        System.out.println(id + " size=" + getSize());
    }

    private void addExitAndFullScreenListeners(GraphicsDevice graphicsDevice)
    {
        setFocusable(true);
        addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                if (e.getKeyChar() == 27)
                {
                    if (includeControls)
                    {
                        boolean alreadyFullScreen = graphicsDevice.getFullScreenWindow() != null;
                        toggleFullScreen(alreadyFullScreen, graphicsDevice);
                    }
                    else
                    {
                        exit();
                    }
                }
                super.keyTyped(e);
            }
        });

        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                exit();
            }
        });
    }

    protected void toggleFullScreen(boolean alreadyFullScreen, GraphicsDevice graphicsDevice)
    {
        graphicsDevice.setFullScreenWindow((alreadyFullScreen ? null : BaseScoreboard.this));
    }

    void exit()
    {
        if (dataReader != null)
        {
            dataReader.close();
        }
        System.exit(0);
    }
}
