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

import uk.org.bwscswim.scoreboard.event.ClubEvent;
import uk.org.bwscswim.scoreboard.event.EventPublisher;
import uk.org.bwscswim.scoreboard.event.RawTextEvent;
import uk.org.bwscswim.scoreboard.event.ScoreboardEvent;
import uk.org.bwscswim.scoreboard.event.TimeOfDayEvent;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Contains Panels used to display the scoreboard and controls their display.
 *
 * @author adavis
 */
public class Scoreboard extends BaseScoreboard
{
    private JTabbedPane tabbedConfigPane;
    private Container racePanel;
    private RawTextPanel rawTextPanel;

    private CardLayout cardLayout = new CardLayout();
    private TimeOfDayPanel timeOfDayPanel;
    private SplashPanel splashPanel;
    private MainScoreboardPanel mainScoreboardPanel;
    private ClubScoreboardPanel clubScoreboardPanel;

    private static final String TIME_OF_DAY = "timeOfDay";
    private static final String SPLASH = "splash";
    private static final String MAIN_SCOREBOARD = "mainScoreboard";
    private static final String CLUB_SCOREBOARD = "clubScoreboard";
    private String currentPanel = MAIN_SCOREBOARD;
    private EventPublisher clubEventPublisher;

    Scoreboard(Config config, DataReader dataReader, List<String> clubEvents, boolean useSecondScreen, boolean includeControls)
    {
        super(config, dataReader, useSecondScreen, includeControls);

        Container contentPane = getContentPane();
        if (includeControls)
        {
            racePanel = new JPanel();
            Container configPanel = makeTextPanel("Screen Configuration");
            rawTextPanel = new RawTextPanel(config);
            Container tracePanel = new TracePanel(config);
            Container countyPanel = new QualificationTimePanel(config.getCountyTimesFilename(), config);
            Container regionalPanel = new QualificationTimePanel(config.getRegionalTimesFilename(), config);
            Container swimmerPanel = makeTextPanel("Swimmers");
            ClubRacePanel clubRacePanel = new ClubRacePanel(config, clubEvents, this);
            clubEventPublisher = clubRacePanel.getEventPublisher();
            Container exitPanel = new ExitPanel(this);

            tabbedConfigPane = new JTabbedPane();
            tabbedConfigPane.setTabPlacement(JTabbedPane.BOTTOM);
            tabbedConfigPane.addTab("Race", racePanel);
            tabbedConfigPane.addTab("Raw", rawTextPanel);
            tabbedConfigPane.addTab("Trace", tracePanel);
            tabbedConfigPane.addTab("Config", configPanel);
            tabbedConfigPane.addTab("County", countyPanel);
            tabbedConfigPane.addTab("Regional", regionalPanel);
            tabbedConfigPane.addTab("Swimmers", swimmerPanel);
            tabbedConfigPane.addTab("TVJL", clubRacePanel);
            tabbedConfigPane.addTab("Exit", exitPanel);
            tabbedConfigPane.setSelectedComponent(racePanel);
            contentPane.add(tabbedConfigPane);

            tabbedConfigPane.addChangeListener(e ->
            {
                JTabbedPane tabbedConfigPane = (JTabbedPane)e.getSource();
                Component selectedComponent = tabbedConfigPane.getSelectedComponent();
                if (selectedComponent == racePanel)
                {
                    cardLayout.show(racePanel, currentPanel);
                }
            });
        }
        else
        {
            racePanel = contentPane;
        }

        timeOfDayPanel = new TimeOfDayPanel(config);
        splashPanel = new SplashPanel(config);
        mainScoreboardPanel = new MainScoreboardPanel(config);
        clubScoreboardPanel = new ClubScoreboardPanel(config);

        int width = config.getInt("width", 1159);
        int height = config.getInt("height", 728);
        racePanel.setMinimumSize(new Dimension(width, height)); // height is 710 otherwise and we later end up with truncation.

        racePanel.setLayout(cardLayout);
        racePanel.add(mainScoreboardPanel, MAIN_SCOREBOARD);
        racePanel.add(clubScoreboardPanel, CLUB_SCOREBOARD);
        racePanel.add(timeOfDayPanel, TIME_OF_DAY);
        racePanel.add(splashPanel, SPLASH);
        currentPanel = MAIN_SCOREBOARD;

        makeScoreboardVisible();
    }

    public EventPublisher getClubEventPublisher()
    {
        return clubEventPublisher;
    }

    @Override
    protected void toggleFullScreen(boolean alreadyFullScreen, GraphicsDevice graphicsDevice)
    {
        if (alreadyFullScreen)
        {
            Container contentPane = getContentPane();
            contentPane.remove(racePanel);
            contentPane.add(tabbedConfigPane);
            tabbedConfigPane.insertTab("Race", null, racePanel, null, 0);
            tabbedConfigPane.setSelectedComponent(racePanel);
        }
        else
        {
            Container contentPane = getContentPane();
            contentPane.remove(tabbedConfigPane);
            contentPane.add(racePanel);
            tabbedConfigPane.remove(racePanel);
        }
        super.toggleFullScreen(alreadyFullScreen, graphicsDevice);
    }

    protected JComponent makeTextPanel(String text)
    {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }

    public static String trim(String value, int length)
    {
        value = value.trim();
        if (value.length() >= length)
        {
            value = value.substring(0, length);
        }
        return value;
    }

    @Override
    public void update(ScoreboardEvent event)
    {
        if (event instanceof TimeOfDayEvent)
        {
            timeOfDayPanel.update((TimeOfDayEvent)event);

            int count = ((TimeOfDayEvent)event).getCount();
            switchPanel(splashPanel.showSplash(count) ? SPLASH : TIME_OF_DAY);
        }
        else if (event instanceof ClubEvent)
        {
            clubScoreboardPanel.update((ClubEvent)event);
            switchPanel(CLUB_SCOREBOARD);
        }
        else if (event instanceof RawTextEvent && includeControls)
        {
            rawTextPanel.update((RawTextEvent)event);
        }
        else
        {
            mainScoreboardPanel.update(event);
            switchPanel(MAIN_SCOREBOARD);
        }
    }

    // Sets the label's text truncating it so that it fits. Avoids the ... display.
    public static void setTrimmedText(JLabel label, String text)
    {
        int width = label.getWidth();
        Font font = label.getFont();
        FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        int length = text.length();
        while (true)
        {
            int textWidth = fontMetrics.stringWidth(text);
            if (textWidth < width || length == 0)
            {
                break;
            }
            text = text.substring(0, --length);
        }
        label.setText(text);
    }

    private void switchPanel(String panel)
    {
        if (!currentPanel.equals(panel))
        {
            currentPanel = panel;
            if (racePanel.isVisible())
            {
                cardLayout.show(racePanel, panel);
            }
        }
    }

    @Override
    public String toString()
    {
        return mainScoreboardPanel.toString();
    }
}
