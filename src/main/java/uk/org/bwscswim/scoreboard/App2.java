package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.event.EventPublisher;
import uk.org.bwscswim.scoreboard.meet.model.Event;
import uk.org.bwscswim.scoreboard.meet.service.ModelHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class App2
{
    private static class TestJFrame extends JFrame
    {
        private final boolean second;
        public TestJFrame(String name)
        {
            second = name.startsWith("two");

            JButton exit = new JButton(name);
            exit.addActionListener(e->System.exit(0));
            setUndecorated(true);

            if (second)
            {
                GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice graphicsDevice = localGraphicsEnvironment.getDefaultScreenDevice();
                GraphicsDevice[] screenDevices = localGraphicsEnvironment.getScreenDevices();
                graphicsDevice = screenDevices[0].equals(graphicsDevice) ? screenDevices[1] : screenDevices[0];

//                javax.swing.JFrame dualview = new javax.swing.JFrame(graphicsDevice.getDefaultConfiguration());
//                setLocationRelativeTo(dualview);
//                dualview.dispose();
                graphicsDevice.setFullScreenWindow(this);
            }
            getContentPane().add(exit);
            pack();
            requestFocus();
            setVisible(true);
        }
    }

    public static void main(String args[])
    {
        try
        {
            java.awt.EventQueue.invokeAndWait(() ->
            {
                TestJFrame f1 = new TestJFrame("one exit");
                TestJFrame f2 = new TestJFrame("two exit");
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
