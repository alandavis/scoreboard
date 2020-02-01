package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.event.RawTextEvent;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;
import static java.awt.Font.BOLD;

/**
 * @author adavis
 */
public class RawTextPanel extends JPanel
{
    private static final int LINES = 15;
    private static final String[] TEST = new String[]
            {
                "Ev131/3 50m Backstroke",
                "Girls                  38.18",
                "1  Danica Forster  CHWS    39.64 2",
                "2  Ellie Smith     AMES  2:14.97 3    51.41",
                "3  Olivia Callaman WINS  2:14.97 7    51.41",
                "4  Olivia Callaman WINS  2:14.97 7    51.41",
                "5  Olivia Callaman WINS  2:14.97 7    51.41",
                "6  Olivia Callaman WINS  2:14.97 7    51.41",
                "",
                "Test",
                "",
                "12",
                "13",
                "Line 14",
                "15"
            };

    List<JLabel> lines = new ArrayList<>(LINES);

    public RawTextPanel(Config config)
    {
        setBackground(BLACK);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        GroupLayout.ParallelGroup col = layout.createParallelGroup();
        GroupLayout.SequentialGroup row = layout.createSequentialGroup();
        layout.setVerticalGroup(row);
        layout.setHorizontalGroup(col);

        int defaultSize = 40;
        int titleSize = (defaultSize*120)/100;
        Font defaultFont = new Font("Courier", BOLD, defaultSize);
        Font titleFont = new Font("Courier", BOLD, titleSize);

        for (int i=0; i<LINES; i++)
        {
            JLabel line = new JLabel(TEST[i]);
            line.setFont(i < 2 ? titleFont : defaultFont);
            line.setForeground(i < 2 ? YELLOW : WHITE);
            line.setBackground(BLACK);
            lines.add(line);
            col.addComponent(line);
            row.addComponent(line);
        }
    }

    public void update(RawTextEvent event)
    {
        String data = event.getData();
        if (data == null)
        {
            lines.forEach(l->l.setText(""));
        }
        else
        {
            int lineNumber = event.getLineNumber();
            int offset = event.getOffset();
            JLabel line = lines.get(lineNumber);

            String text = line.getText();
            StringBuilder sb = new StringBuilder(text);
            while (sb.length() < offset)
            {
                sb.append(' ');
            }
            sb.replace(offset, offset+text.length(), data);
            line.setText(sb.toString());
        }
    }
}
