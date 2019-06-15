package uk.org.bwscswim.scoreboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class RawDisplay extends BaseBorad
{
    private List<JLabel> lines = new ArrayList<>();
    private List<Integer> lineLengths = new ArrayList<>();

    public RawDisplay(Config config)
    {
        super(config, "raw".equals(config.getDisplayName()));

        BoxLayout layout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
        contentPane.setLayout(layout);

        Font titleFont = config.getFont("title");
        Font laneFont = config.getFont("lane");

        String testLine0 = config.getTest("title");
        testLine0 = testLine0.substring(0,1)+"RAW "+testLine0.substring(5);
        String testLine1 = config.getTest("subTitle")+"          "+config.getTest("clock");
        String testLane =
                config.getTest("name")+" "+
                config.getTest("club")+" "+
                config.getTest("time")+" ";

        int lineCount = config.getLineCount();
        int titleLineLength = config.getTitleLineLength();
        int laneLineLength = config.getLaneLineLength();
        lines = new ArrayList<>(lineCount);
        for (int lineNumber=0; lineNumber<lineCount; lineNumber++)
        {
            JLabel line = new JLabel();
            line.setFont(lineNumber <= 1 ? titleFont : laneFont);
            int lane = lineNumber-2;
            String text = lineNumber == 0 ? testLine0 : lineNumber == 1 ? testLine1 : lane+"  "+testLane+" "+lane;
            lineLengths.add(lineNumber <= 1 ? titleLineLength : laneLineLength);
            lines.add(line);

            if (config.isLineVisible(lineNumber))
            {
                contentPane.add(line);
                setText(lineNumber, 0, text);
            }
        }
        postConstructor();
    }

    @Override
    public void setColors(Color background, Color titleForeground, Color laneForeground)
    {
        int lineCount = config.getLineCount();
        for (int lineNumber=0; lineNumber<lineCount; lineNumber++)
        {
            JLabel line = lines.get(lineNumber);
            line.setForeground(lineNumber <= 1 ? titleForeground : laneForeground);
            line.setBackground(background);
        }
    }

    @Override
    public void clear()
    {
        for (JLabel line : lines)
        {
            line.setText(" ");
        }
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
    }
}
