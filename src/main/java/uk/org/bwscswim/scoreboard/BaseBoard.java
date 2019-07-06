package uk.org.bwscswim.scoreboard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

import static uk.org.bwscswim.scoreboard.ScoreboardState.TEST;

public abstract class BaseBoard extends javax.swing.JFrame
{
    protected final Config config;
    protected final String name;
    protected Container contentPane;
    protected boolean scoreboardVisible;

    protected ScoreboardState state = TEST;

    protected Color background;
    protected Color titleForeground;
    protected Color laneForeground;
    protected Boolean testCard;

    private DataReader dataReader;

    public BaseBoard(Config config, String name)
    {
        this.config = config;
        this.name = name;
        contentPane = getContentPane();
        String activeScoreboardName = getActiveScoreboardName(config);
        this.scoreboardVisible = name.equals(activeScoreboardName);
        testCard = config.getBoolean("testCard", true);
    }

    public static BaseBoard createScoreboard(Config config)
    {
        String activeScoreboardName = getActiveScoreboardName(config);
        return activeScoreboardName.equalsIgnoreCase("raw") ? new RawDisplay(config) :
               activeScoreboardName.equalsIgnoreCase("old") ? new OldScoreboard(config) :
               activeScoreboardName.equalsIgnoreCase("new1") ? new New1Scoreboard(config) :
               activeScoreboardName.equalsIgnoreCase("new2") ? new New2Scoreboard(config) :
               activeScoreboardName.equalsIgnoreCase("new3") ? new New3Scoreboard(config) :
               activeScoreboardName.equalsIgnoreCase("new4") ? new New4Scoreboard(config) :
               new OriginalScoreboard(config);
    }

    private static String getActiveScoreboardName(Config config)
    {
        return config.getString("scoreboardName", "new4");
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
            Boolean fullScreen = config.getBoolean(name, null, null, "fullScreen", true);
            if (config.getBoolean(name, null, null, "originalScreenSetup", false))
            {
                System.out.println("Using original screen setup");
                pack();
                System.out.println("ScoreboardSize=" + getSize());
                if (fullScreen)
                {
                    GraphicsDevice graphicsDevice = getGraphicsDevice();
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
                System.out.println("Using new screen setup");
                if (fullScreen)
                {
                    setUndecorated(true);
                }

                pack();
                System.out.println("ScoreboardSize=" + getSize());

                GraphicsDevice graphicsDevice = getGraphicsDevice();
                DisplayMode displayMode = graphicsDevice.getDisplayMode();
                System.out.println("Using GraphicsDevice \"" + graphicsDevice.getIDstring() + "\" " + displayMode.getWidth() + "x" + displayMode.getHeight());

                if (fullScreen)
                {
                    graphicsDevice.setFullScreenWindow(this);
                }
                else
                {
                    // Tick to move this Frame to the required device
                    javax.swing.JFrame dualview = new javax.swing.JFrame(graphicsDevice.getDefaultConfiguration());
                    setLocationRelativeTo(dualview);
                    dualview.dispose();
                    setVisible(true);
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
        addKeyListener(new KeyAdapter() {
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
    }

    protected void getColors()
    {
        background = config.getColor(name, state, null, "background", Color.BLACK);
        titleForeground = config.getColor(name, state, name, "title.foreground", Color.YELLOW);
        laneForeground = config.getColor(name, state, name, "lane.foreground", Color.WHITE);
    }

    protected void setColors()
    {
        contentPane.setBackground(background);
    }

    protected String getTest(String componentName)
    {
        String string = config.getString(null, null, null, componentName + "Test", "");
        if (!testCard)
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

    private GraphicsDevice getGraphicsDevice()
    {
        String graphicsDeviceId = config.getString("graphicsDevice", null);
        GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        System.out.println("GraphicsDevices");
        for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
        {
            String id = device.getIDstring();
            System.out.println("    "+ id);
            if (id.equals(graphicsDeviceId))
            {
                graphicsDevice = device;
            }
        }
        return graphicsDevice;
    }

    protected void setState(ScoreboardState state)
    {
        if (this.state != state)
        {
            this.state = state;
            System.out.println(state);
            getColors();
            setColors();
        }
    }

    public abstract void clear();

    protected String pad(String value, int length)
    {
        return pad(value, length, ' ');
    }

    protected String pad(String value, int length, char c)
    {
        if (value.length() >= length)
        {
            value = value.substring(0, length);
        }
        else
        {
            StringBuilder sb = new StringBuilder(value);
            while (sb.length() < length)
            {
                sb.append(c);
            }
            value = sb.toString();
        }
        return value;
    }

    protected String lpad(String value, int length)
    {
        if (value.length() >= length)
        {
            value = value.substring(0, length);
        }
        else
        {
            StringBuilder sb = new StringBuilder(value);
            while (sb.length() < length)
            {
                sb.insert(0, ' ');
            }
            value = sb.toString();
        }
        return value;
    }

    public void setDataReader(DataReader dataReader)
    {
        this.dataReader = dataReader;
    }
}
