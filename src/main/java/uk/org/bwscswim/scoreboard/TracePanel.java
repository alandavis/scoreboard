package uk.org.bwscswim.scoreboard;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;

import static java.awt.Font.PLAIN;

/**
 * @author adavis
 */
public class TracePanel extends JPanel
{
    public TracePanel(Config config)
    {
        int maxLines = config.getInt("maxTraceLines", 500);

        JTextArea textArea = new JTextArea(30, 150);
        textArea.setEditable(false);
        Font font = new Font("Courier", PLAIN, 13);
        textArea.setFont(font);
        JScrollPane scrollPane = new JScrollPane(textArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        int width = config.getInt("width", 1159);
        int height = config.getInt("height", 728);
        scrollPane.setPreferredSize(new Dimension(width-50, height-100));
        add(scrollPane);

        System.setErr(new PrintStream(new OutputStream()
        {
            private int lineCount = 0;
            @Override
            public void write(int b)
            {
                System.out.print((char)b);
                if (b == '\n')
                {
                    try
                    {
                        if (++lineCount > maxLines)
                        {
                            textArea.replaceRange("", 0, textArea.getLineStartOffset(1));
                        }
                    }
                    catch (BadLocationException ignore)
                    {
                    }
                }
                textArea.append(String.valueOf((char) b));
// TODO: Displays the end, but we should only do that if the scrollbar is at the end.
//                textArea.setCaretPosition(textArea.getDocument().getLength());
            }
        }));
    }
}
