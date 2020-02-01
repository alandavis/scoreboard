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

import uk.org.bwscswim.scoreboard.event.LineupEvent;
import uk.org.bwscswim.scoreboard.event.PageEvent;
import uk.org.bwscswim.scoreboard.event.RaceSplitTimeEvent;
import uk.org.bwscswim.scoreboard.event.RaceTimerEvent;
import uk.org.bwscswim.scoreboard.event.ResultEvent;
import uk.org.bwscswim.scoreboard.event.ScoreboardEvent;
import uk.org.bwscswim.scoreboard.event.TestcardEvent;
import uk.org.bwscswim.scoreboard.event.TimeOfDayEvent;
import uk.org.bwscswim.scoreboard.meet.model.Improvement;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;

/**
 * Contains fields used to display the scoreboard, but without any layout.
 *
 * @author adavis
 */
abstract class AbstractScoreboard extends BaseScoreboard
{
    public static final Color GREENISH  = new Color(0, 220, 60);

    class Swimmer
    {
        protected JLabel lane = new JLabel();
        protected JLabel name = new JLabel();
        protected String club;
        protected String time;
        protected Improvement improvement;
        protected JLabel clubTime = new JLabel("", SwingConstants.RIGHT);
        protected JLabel place = new JLabel("", SwingConstants.CENTER);
    }

    private JTabbedPane tabbedConfigPane;
    private Container racePanel;

    private CardLayout cardLayout = new CardLayout();
    protected Container timeOfDayPanel = new JPanel();
    protected Container splashPanel = new JPanel();
    protected Container scoreboardPanel = new JPanel();

    private static final String TIME_OF_DAY_PANEL = "timeOfDay";
    private static final String SPLASH = "splash";
    private static final String SCOREBOARD_PANEL = "scoreboard";

    protected JLabel title = new JLabel();
    protected String clock = "";
    protected List<Swimmer> swimmers = new ArrayList<>();

    protected int laneCount;

    private Font timeOfDayFont;
    private Font singleTitleFont;
    private Font laneFont;
    private Font nameFont;
    private Font clubTimeFont;
    private Font placeFont;

    protected JLabel logo  = new JLabel();
    protected JLabel timeOfDay  = new JLabel();

    private int timeOfDayMod;
    private int splashAt;
    private int splashFor;

    AbstractScoreboard(Config config, DataReader dataReader, boolean useSecondScreen, boolean includeControls)
    {
        super(config, dataReader, useSecondScreen, includeControls);

        laneCount = config.getInt("laneCount", 6);

        getFonts();

        Container contentPane = getContentPane();
        if (includeControls)
        {
            racePanel = new JPanel();
            Container configPanel = makeTextPanel("Screen Configuration"); // TODO create these somewhere. One per class.
            Container rawPanel = makeTextPanel("TODO: Display the raw text scoreboard");
            Container tracePanel = new TracePanel(config);
            Container countyPanel = new QualificationTimePanel(config.getCountyTimesFilename(), config);
            Container regionalPanel = new QualificationTimePanel(config.getRegionalTimesFilename(), config);
            Container swimmerPanel = makeTextPanel("Swimmers");
            Container exitPanel = new ExitPanel(this);

            tabbedConfigPane = new JTabbedPane();
            tabbedConfigPane.setTabPlacement(JTabbedPane.BOTTOM);
            tabbedConfigPane.addTab("Race", racePanel);
            tabbedConfigPane.addTab("Raw", rawPanel);
            tabbedConfigPane.addTab("Trace", tracePanel);
            tabbedConfigPane.addTab("Config", configPanel);
            tabbedConfigPane.addTab("County", countyPanel);
            tabbedConfigPane.addTab("Regional", regionalPanel);
            tabbedConfigPane.addTab("Swimmers", swimmerPanel);
            tabbedConfigPane.addTab("Exit", exitPanel);
            tabbedConfigPane.setSelectedComponent(racePanel);
            contentPane.add(tabbedConfigPane);
        }
        else
        {
            racePanel = contentPane;
        }

        int width = config.getInt("width", 1159);
        int height = config.getInt("height", 728);
        racePanel.setMinimumSize(new Dimension(width, height)); // height is 710 otherwise and we later end up with truncation.

        racePanel.setLayout(cardLayout);
        racePanel.add(scoreboardPanel, SCOREBOARD_PANEL);
        racePanel.add(timeOfDayPanel, TIME_OF_DAY_PANEL);
        racePanel.add(splashPanel, SPLASH);


        setSplash();
        createSwimmers();
        setColors();
        setFonts();
    }

    @Override
    protected void makeScoreboardVisible()
    {
        super.makeScoreboardVisible();
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

    private void createSwimmers()
    {
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = new Swimmer();
            swimmers.add(swimmer);
        }
    }

    protected void setSplash()
    {
        timeOfDayMod = config.getInt("timeOfDayMod", 60);
        splashAt = config.getInt("splashAt", 45);
        splashFor = config.getInt("splashFor", 15);
        try
        {
            splashPanel.add(new JLabel(new ImageIcon(FileLoader.getBytes(":Splash.jpg", config))));
            logo.setIcon(new ImageIcon(FileLoader.getBytes(":Logo.jpg", config)));
        }
        catch (FileNotFoundException e)
        {
            System.err.println("Filed to read image "+e.getMessage());
        }
    }

    private void getFonts()
    {
        timeOfDayFont = config.getFont(null, "timeOfDay");

        singleTitleFont = config.getFont(null, "title");

        laneFont = config.getFont(null, "lane");
        nameFont = config.getFont(null, "name");
        clubTimeFont = config.getFont(null, "clubTime");
        placeFont = config.getFont(null, "place");
    }

    private void setFonts()
    {
        timeOfDay.setFont(timeOfDayFont);

        title.setFont(singleTitleFont);

        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setFont(laneFont);
            swimmer.name.setFont(nameFont);
            swimmer.clubTime.setFont(clubTimeFont);
            swimmer.place.setFont(placeFont);
        }
    }

    protected void setColors()
    {
        timeOfDayPanel.setBackground(BLACK);
        Color splashBackground = config.getColor("splash", "background", WHITE);
        splashPanel.setBackground(splashBackground);
        scoreboardPanel.setBackground(BLACK);

        timeOfDay.setForeground(WHITE);


        title.setForeground(YELLOW);

        for (Swimmer swimmer : swimmers)
        {
            swimmer.lane.setForeground(WHITE);
            swimmer.name.setForeground(WHITE);
            swimmer.clubTime.setForeground(WHITE);
            swimmer.place.setForeground(YELLOW);

            swimmer.name.setOpaque(false);
        }
    }

    private void setClock(String clock)
    {
        this.clock = clock;
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = swimmers.get(lane-1);
            setclubTime(lane, swimmer, null, false);
        }
    }

    private void setclubTime(int lane, Swimmer swimmer, PageEvent event, boolean hasImprovments)
    {
        int eventCount = event == null ? -1 : event.getCount();
        String clubTimeText = "";
        String clubText = swimmer.club.trim();
        String timeText = swimmer.time.trim();
        String clockText = event instanceof LineupEvent ? "" : clock.trim();
        boolean normalFormat = eventCount <= 5 || !hasImprovments;
        clubTimeText =
                !timeText.isEmpty() ? (normalFormat ? timeText : swimmer.improvement.getReduction()) :
                        clockText.isEmpty() ? clubText : lane == getLaneOfFirstBlankTime() ? clockText : "";
        // Add a space if displaying a time
        if (!clubTimeText.isEmpty() &&
                (!timeText.isEmpty() || !clockText.isEmpty()) &&
                clubTimeText.charAt(clubTimeText.length()-2) == '.')
        {
            clubTimeText = clubTimeText+' ';
        }
        swimmer.clubTime.setText(clubTimeText);
        swimmer.clubTime.setForeground(normalFormat || !swimmer.improvement.isNewBand() ? WHITE : GREENISH);
    }

    private int getLaneOfFirstBlankTime()
    {
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = swimmers.get(lane-1);
            if (swimmer.time.trim().isEmpty() && !swimmer.name.getText().trim().isEmpty())
            {
                return lane;
            }
        }
        return 0;
    }

    private String trim(String value, int length)
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
        if (event instanceof RaceTimerEvent)
        {
            update((RaceTimerEvent)event);
        }
        else if (event instanceof TimeOfDayEvent)
        {
            update((TimeOfDayEvent)event);
        }
        else if (event instanceof RaceSplitTimeEvent)
        {
            int from = ((RaceSplitTimeEvent) event).getIndexOfLaneWithSplitTime();
            update((PageEvent)event, from, from+1);
        }
        else if (event instanceof PageEvent)
        {
            update((PageEvent)event, 0, ((PageEvent)event).getLaneCount());
        }
        else if (event instanceof TestcardEvent)
        {
            updated((TestcardEvent)event);
        }
    }

    private void update(RaceTimerEvent event)
    {
        setClock(event.getClock());
        if (racePanel.isVisible())
        {
            racePanel.setVisible(true);
        }
    }

    private void update(TimeOfDayEvent event)
    {
        String time = event.getTimeOfDay();
        this.timeOfDay.setText(time);

         int count = event.getCount() % timeOfDayMod;
        if (count >= splashAt && count < (splashAt + splashFor))
        {
            if (racePanel.isVisible() && !splashPanel.isVisible())
            {
                cardLayout.show(racePanel, SPLASH);
            }
        }
        else
        {
            if (racePanel.isVisible() && !timeOfDayPanel.isVisible())
            {
                cardLayout.show(racePanel, TIME_OF_DAY_PANEL);
            }
        }
    }

    private void updated(TestcardEvent event)
    {
        title.setText("23.16 Girls 400 Breaststroke");
        int lane=0;
        for (Swimmer swimmer : swimmers)
        {
            lane++;
            swimmer.lane.setText(Integer.toString(lane));
            setTrimmedText(swimmer.name, "Emma Atanasova");
            swimmer.place.setText("CT");
            swimmer.clubTime.setText("2:38.23");
        }
    }

    private void update(PageEvent event, int from, int to)
    {
        int eventCount = event.getCount();
        if (eventCount == 0)
        {
            scoreboardPanel.setBackground(event instanceof ResultEvent ? new Color(Integer.parseInt("0033cc", 16)) : BLACK);
        }
        setText(event, eventCount, from, to);
        if (racePanel.isVisible() && !scoreboardPanel.isVisible())
        {
            cardLayout.show(racePanel, SCOREBOARD_PANEL);
        }
    }

    private void setText(PageEvent event, int eventCount, int from, int to)
    {
        this.title.setText(trim(event.getCombinedTitle(), 29));

        boolean hasImprovments = false;
        ResultEvent resultEvent = null;
        if (event instanceof ResultEvent)
        {
            resultEvent = (ResultEvent)event;
            for (int laneIndex = from; laneIndex < to; laneIndex++)
            {
                Swimmer swimmer = swimmers.get(laneIndex);
                swimmer.improvement = resultEvent.getImprovement(laneIndex);
                if (!swimmer.improvement.isBlank())
                {
                    hasImprovments = true;
                }
            }
        }

        for (int laneIndex = from; laneIndex < to; laneIndex++)
        {
            Swimmer swimmer = swimmers.get(laneIndex);
            int lane = event.getLane(laneIndex);
            String laneText = lane <= 0 ? "" : Integer.toString(lane);
            swimmer.lane.setText(laneText);
            setTrimmedText(swimmer.name, event.getName(laneIndex));
            swimmer.club = event.getClub(laneIndex);
            swimmer.time = event.getTime(laneIndex);
            int place = event.getPlace(laneIndex);
            boolean normalFormat = eventCount <= 5 || !hasImprovments;
            swimmer.place.setText(normalFormat
                ? place <= 0 ? " " : Integer.toString(place)
                : swimmer.improvement.getLevel());
            swimmer.place.setForeground(normalFormat || !swimmer.improvement.isNewBand() ? YELLOW : GREENISH);
            setclubTime(laneIndex + 1, swimmer, event, hasImprovments);
        }
    }

    // Sets the label's text truncating it so that it fits. Avoids the ... display.
    private void setTrimmedText(JLabel label, String text)
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

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder("Scoreboard{").
                append("title='").append(title.getText().trim()).append("', ").
                append("clock='").append(clock.trim()).append("', ").
                append("swimmers=[");
        Iterator<Swimmer> iterator = swimmers.iterator();
        while (iterator.hasNext())
        {
            Swimmer swimmer = iterator.next();
            String lane = swimmer.lane.getText().trim();
            if (!lane.isEmpty())
            {
                sb.append("Swimmer{").
                        append("name='").append(swimmer.name.getText().trim()).append("', ").
                        append("club='").append(swimmer.club.trim()).append("', ").
                        append("lane='").append(lane).append("', ").
                        append("place='").append(swimmer.place.getText().trim()).append("', ").
                        append("time='").append(swimmer.time.trim()).append("'}").
                        append("improvement='").append(swimmer.improvement).append("'}").
                        append("clubTime='").append(swimmer.clubTime.getText().trim()).append("', ");
                if (iterator.hasNext())
                {
                    sb.append(", ");
                }
            }
        }
        sb.append("]}");
        return sb.toString();
    }
}
