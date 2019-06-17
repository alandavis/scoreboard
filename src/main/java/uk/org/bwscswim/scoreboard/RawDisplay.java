package uk.org.bwscswim.scoreboard;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RawDisplay extends BaseBorad
{
    private List<JLabel> lines = new ArrayList<>();
    private List<Integer> lineLengths = new ArrayList<>();

    public RawDisplay(Config config)
    {
        super(config, "raw");

        BoxLayout layout = new BoxLayout(contentPane, BoxLayout.Y_AXIS);
        contentPane.setLayout(layout);

        Font titleFont = config.getFont(name, state, "title");
        Font laneFont = config.getFont(name, state, "lane");

        String testLine0 = getTest("title");
        testLine0 = testLine0.substring(0,1)+"RAW "+testLine0.substring(5);
        String testLine1 = getTest("subTitle")+"          "+getTest("clock");
        String testLane =
                getTest("name")+" "+
                getTest("club")+" "+
                getTest("time")+" ";

        int lineCount = getLineCount();
        int titleLineLength = getTitleLineLength();
        int laneLineLength = getLaneLineLength();
        lines = new ArrayList<>(lineCount);
        for (int lineNumber=0; lineNumber<lineCount; lineNumber++)
        {
            JLabel line = new JLabel();
            line.setFont(lineNumber <= 1 ? titleFont : laneFont);
            int lane = lineNumber-2;
            String text = lineNumber == 0 ? testLine0 : lineNumber == 1 ? testLine1 : lane+"  "+testLane+" "+lane;
            lineLengths.add(lineNumber <= 1 ? titleLineLength : laneLineLength);
            lines.add(line);

            if (config.getBoolean(name, state, null, "line"+lineNumber+"Visible", true))
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
        int lineCount = getLineCount();
        for (int lineNumber=0; lineNumber<lineCount; lineNumber++)
        {
            JLabel line = lines.get(lineNumber);
            line.setForeground(lineNumber <= 1 ? titleForeground : laneForeground);
            line.setBackground(background);
        }
    }

    private int getLineCount()
    {
        return config.getInt(name, null, null, "lineCount", 12);
    }

    private int getTitleLineLength()
    {
        return config.getInt(name, null, "title", "lineLength", 38);
    }

    private int getLaneLineLength()
    {
        return config.getInt(name, null, "title", "laneLength", 38);
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
        int lineLength = lineLengths.get(lineNumber);
        String orig = line.getText();
        orig = pad(orig, lineLength);
        StringBuilder sb = new StringBuilder(orig);
        sb.replace(offset, offset+text.length(), text);
        text = pad(sb.toString(), lineLength);
        System.err.println("len="+text.length());
        line.setText(text);
    }
}
