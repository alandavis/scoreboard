package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.meet.model.RaceTime;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author adavis
 */
public class QualificationTimePanel extends JPanel
{
    private static class TimeEditor extends DefaultCellEditor
    {
        final JTextField field;

        TimeEditor()
        {
            super(new JTextField());
            field = (JTextField)getComponent();
            field.setHorizontalAlignment(SwingConstants.RIGHT);
            setClickCountToStart(1);
        }
    };

    private static class TimeRenderer extends JLabel implements TableCellRenderer
    {
        TimeRenderer()
        {
            setHorizontalAlignment(RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            setText(value == null ? "" : value.toString());
            if (isSelected)
            {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            }
            else
            {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            return this;
        }
    }

    private static class TimeTableModel extends AbstractTableModel
    {
        private final List<List<String>> lines;

        public TimeTableModel(List<List<String>> lines)
        {
            this.lines = lines;
        }

        public String getColumnName(int col)
        {
            boolean includeConsiderationTimes = includeConsiderationTimes(lines);
            return !includeConsiderationTimes || col % 2 == 0
                    ? getDataValue(0, includeConsiderationTimes ? col / 2 : col)
                    : "";
        }

        public int getRowCount()
        {
            return lines.size()-1;
        }

        public int getColumnCount()
        {
            return lines.size() > 1 ? lines.get(1).size() : 8;
        }

        public Object getValueAt(int row, int col)
        {
            String string = getDataValue(row + 1, col);
            Object object = string;
            if (col > 0)
            {
                object = RaceTime.create(string);
            }
            return object;
        }

        public boolean isCellEditable(int row, int col)
        {
            return true;
        }

        public void setValueAt(Object value, int row, int col)
        {
            setDataValue(row+1, col, value.toString());
            fireTableCellUpdated(row, col);
        }

        public Class getColumnClass(int c)
        {
            return c == 0 ? String.class : RaceTime.class;
        }

        private String getDataValue(int row, int col)
        {
            return lines.size() <= row || lines.get(row).size() <= col
                    ? ""
                    : lines.get(row).get(col);
        }

        private void setDataValue(int row, int col, String value)
        {
            while (lines.size() <= row)
            {
                lines.add(new ArrayList<>());
            }

            while (lines.get(row).size() <= col)
            {
                lines.get(row).add("");
            }

            lines.get(row).set(col, value);
        }
    };

    private static class TimeJTable extends JTable
    {
        private static Color tableSelectionBackground = UIManager.getColor("Table.selectionBackground");
        private static Color tableSelectionForeground = UIManager.getColor("Table.selectionForeground");

        public TimeJTable(List<List<String>> lines)
        {
            super(new TimeTableModel(lines));
            setFillsViewportHeight(true);

            TimeRenderer timeRenderer = new TimeRenderer();
            setDefaultRenderer(RaceTime.class, timeRenderer);

            TimeEditor timeEditor = new TimeEditor();
            setDefaultEditor(RaceTime.class, timeEditor);

            ((DefaultCellEditor)getDefaultEditor(String.class)).setClickCountToStart(1);
        }

        @Override
        public Component prepareEditor(TableCellEditor editor, int row, int col) {
            Component c = super.prepareEditor(editor, row, col);
            if (c instanceof JTextField)
            {
                JTextField field = (JTextField)c;
                field.setForeground(tableSelectionForeground);
                field.setBackground(tableSelectionBackground);
                field.setCaretColor(tableSelectionForeground);
            }
            return c;
        }

//        @Override
//        public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
//        {
//            Component c = super.prepareRenderer(renderer, row, column);
//            if (c instanceof JTextField)
//            {
//                JTextField field = (JTextField)c;
//                field.setForeground(tableSelectionForeground);
//                field.setBackground(tableSelectionBackground);
//            }
//            return c;
//        }
    }

    final JTextField filename = new JTextField();
    JTable table;
    final JLabel error = new JLabel();
    final Config config;

    final List<List<JTextField>> rowsOfFields = new ArrayList<>();

    QualificationTimePanel(String filename, Config config)
    {
        this.config = config;
        layout(filename, config);
    }

    private void layout(String filename, Config config)
    {
        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        List<List<String>> lines = load(filename, config);
        table = new TimeJTable(lines);
        JScrollPane tableScrollPane = new JScrollPane(table);

        JLabel filenameLabel = new JLabel("Filename");
        error.setText("ERROR: test message");
        this.filename.setText(filename);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(error)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(filenameLabel)
                    .addComponent(this.filename, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addComponent(tableScrollPane));

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(error)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(filenameLabel)
                    .addComponent(this.filename))
                .addComponent(tableScrollPane));
    }

    private static boolean includeConsiderationTimes(List<List<String>> lines)
    {
        int numberOfColumns = lines.size() > 1 ? lines.get(0).size() : 0;
        return lines.size() > 2
                ? lines.get(1).size() > numberOfColumns
                : false;
    }

    private List<List<String>> load(String filename, Config config)
    {
        final List<List<String>> lines = new ArrayList<>();
        try
        {
            try (BufferedReader reader = FileLoader.getBufferedReader(filename, config))
            {
                reader.lines().forEach(line ->lines.add(Arrays.asList(line.split(","))));
            }
        }
        catch (IOException e)
        {
            error.setText("failed to load "+filename);
            lines.clear();
        }
        return lines;
    }
}
