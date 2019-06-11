package uk.org.bwscswim.scoreboard;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public abstract class BaseBorad extends javax.swing.JFrame
{
    protected final Config config;
    protected Container contentPane;
    protected boolean displayVisible;

    protected boolean result;

    public BaseBorad(Config config, boolean displayVisible)
    {
        this.config = config;
        contentPane = getContentPane();
        this.displayVisible = displayVisible;
    }

    protected void postConstructor()
    {
        setColors();
        exitOnEscapeOrEnter();
        pack();
        if (config.isFullScreen())
        {
            makeFrameFullSize();
        }
        else
        {
            setVisible(true);
        }
    }

    protected void exitOnEscapeOrEnter()
    {
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

    protected void setColors()
    {
        Color background = config.getBackground(result);
        Color titleForeground = config.getForeground("title", result);
        Color laneForeground = config.getForeground("lane", result);

        contentPane.setBackground(background);

        setColors(background, titleForeground, laneForeground);
    }

    public abstract void setColors(Color background, Color titleForeground, Color laneForeground);

    @Override
    public void setVisible(boolean visible)
    {
        if (displayVisible)
        {
            super.setVisible(visible);
        }
    }

    private void makeFrameFullSize()
    {
        if (displayVisible)
        {
            String graphicsDeviceId = config.getString("graphicsDevice", null);
            GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            for (GraphicsDevice device : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices())
            {
                String id = device.getIDstring();
                System.out.println("GraphicsDevice "+ id);
                if (id.equals(graphicsDeviceId))
                {
                    graphicsDevice = device;
                }
            }
            System.out.println("Using GraphicsDevice \""+ graphicsDevice.getIDstring()+"\"");

            if (graphicsDevice.isFullScreenSupported())
            {
                graphicsDevice.setFullScreenWindow(this);

//            GraphicsDevice gd1 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
//            Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
//            Dimension screenSize = defaultToolkit.getScreenSize();

//        line1.setForeground(Color.WHITE);
////        line1.setSize(500, 500);
//        Font font = line1.getFont();
////        float size = font.getSize()*5;
//        int size = 86; //screenSize.height/3;
//        line1.setFont(font.deriveFont(size));
//        font = new Font("Monospaced", Font.PLAIN, size);
//        line1.setFont(font);
//
//        // get metrics from the graphics
//        FontMetrics metrics = getFontMetrics(font);
//        // get the height of a line of text in this font and render context
//        int
//        = metrics.getHeight();
//        // get the advance of my text in this font and render context
//        int adv = metrics.stringWidth(text);
//        // calculate the size of a box to hold the text with some padding.
//        Dimension s = new Dimension(adv + 2, hgt + 2);
//        System.out.println("size: " + size + " screenSize: " + screenSize + " Dim " + s);
            }
            else
            {
                System.err.println("Full screen not supported by defaultScreenDevice.");
            }
        }
    }

    protected void setResult(boolean result)
    {
        if (this.result != result)
        {
            this.result = result;
            setColors();
        }
    }

    public abstract void clear();
}
