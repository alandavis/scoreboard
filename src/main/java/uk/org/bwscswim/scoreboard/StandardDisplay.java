package uk.org.bwscswim.scoreboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class StandardDisplay extends javax.swing.JFrame
{
    private boolean result;
    private List<JLabel> lines = new ArrayList<>();
    private List<Integer> lineLengths = new ArrayList<>();
    private final Config config;

    public StandardDisplay(Config config)
    {
        this.config = config;

        Container contentPane = getContentPane();
        BoxLayout layout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
        contentPane.setLayout(layout);

        contentPane.setBackground(config.getBackground());

        int lineCount = config.getLineCount();
        lines = new ArrayList<>(lineCount);
        for (int lineNumber=0; lineNumber<lineCount; lineNumber++)
        {
            JLabel line = new JLabel();
            line.setForeground(config.getForeground(lineNumber));
            line.setBackground(config.getBackground(lineNumber));
            line.setFont(config.getFont(lineNumber));

            int lineLength = config.getLineLength(lineNumber);
            lineLengths.add(lineLength);
            lines.add(line);

            if (config.isLineVisible(lineNumber))
            {
                contentPane.add(line);
                setText(lineNumber, lineLength-1, " ");
            }
        }

//        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice gd = g.getDefaultScreenDevice();
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

//        String text = "         1         2         3         4         5         6         7         8";
//               text = "P6  Millie sab               23.24 6 ";
//        line1.setText(text);
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
//        int hgt = metrics.getHeight();
//        // get the advance of my text in this font and render context
//        int adv = metrics.stringWidth(text);
//        // calculate the size of a box to hold the text with some padding.
//        Dimension s = new Dimension(adv + 2, hgt + 2);
//        System.out.println("size: " + size + " screenSize: " + screenSize + " Dim " + s);

        exitOnEscapeOrEnter();
        pack();
    }

//    private String getText(int lineNumber)
//    {
//        String text = null;
//        switch (lineNumber)
//        {
//            case 0: text = "Ev16/2 100m Freestyle                      X"; break;
//            case 1: text = "Girls                                       "; break;
//            case 2: text = "P1  Emily Norris     BRKS    36.10 3        "; break;
//            case 3: text = "P2  Nora Djotni      BRKS    36.62 6       X"; break;
//            case 4: text = "P3  K Martin         BRKS    36.94 5        "; break;
//            case 5: text = "P4  Evie Mackay      WOKS    37.60 1        "; break;
//            case 6: text = "P5  Keeley Rees      BRKS    37.78 2        "; break;
//            case 7: text = "P6  Amber Moir       BRKS    38.13 4        "; break;
//        }
//        return text;
//    }

    private void exitOnEscapeOrEnter()
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

    public void makeFrameFullSize()
    {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported())
        {
            gd.setFullScreenWindow(this);
        }
        else
        {
            System.err.println("Full screen not supported by defaultScreenDevice.");
        }
    }

    public void clear()
    {
        int i = 0;
        for (JLabel line : lines)
        {
            line.setText(" ");
        }
        setVisible(true);
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public void setText(int line, String text)
    {
        lines.get(line).setText(text);
        setVisible(true);
    }

    public void setText(int lineNumber, int offset, String text)
    {
        if (lineNumber > lines.size())
        {
            lineNumber = 1;
        }
        JLabel line = lines.get(lineNumber);
        String orig = line.getText();
        StringBuilder sb = new StringBuilder(orig);
        while (sb.length() < offset)
        {
            sb.append(' ');
        }
        sb.replace(offset, offset+text.length(), text);
        int lineLength = lineLengths.get(lineNumber);
        while (sb.length() > lineLength)
        {
            sb.setLength(sb.length()-1);
        }
        text = sb.toString();
        line.setText(text);
        setVisible(true);
    }

    @Override
    public void setVisible(boolean visible)
    {
        if (config.isStandardDisplayVisible())
        {
            super.setVisible(visible);
        }
    }
}
