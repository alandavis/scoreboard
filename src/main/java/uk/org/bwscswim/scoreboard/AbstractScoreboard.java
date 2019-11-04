/*
 * #%L
 * BWSC Scoreboard
 * %%
 * Copyright (C) 2018-2019 Bracknell and Wokingham Swimming Club (BWSC)
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
import uk.org.bwscswim.scoreboard.event.Observer;
import uk.org.bwscswim.scoreboard.event.PageEvent;
import uk.org.bwscswim.scoreboard.event.RaceSplitTimeEvent;
import uk.org.bwscswim.scoreboard.event.RaceTimerEvent;
import uk.org.bwscswim.scoreboard.event.ResultEvent;
import uk.org.bwscswim.scoreboard.event.ScoreboardEvent;
import uk.org.bwscswim.scoreboard.event.StartEvent;
import uk.org.bwscswim.scoreboard.event.TimeOfDayEvent;

import javax.swing.*;
import java.awt.*;
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
abstract class AbstractScoreboard extends BaseScoreboard implements Observer
{

    class Swimmer
    {
        protected JLabel lane = new JLabel();
        protected JTextArea name = new JTextArea();
        protected String club;
        protected String time;
        protected String improvement;
        protected JLabel clubTime = new JLabel("", SwingConstants.RIGHT);
        protected JLabel place = new JLabel("", SwingConstants.CENTER);
    }

    private CardLayout cardLayout = new CardLayout();
    protected Container timeOfDayPanel = new JPanel();
    protected Container scoreboardPanel = new JPanel();
    public static final String TIME_OF_DAY_PANEL = "timeOfDay";
    public static final String SCOREBOARD_PANEL = "scoreboard";

    protected JLabel title = new JLabel();
    protected String clock;
    protected List<Swimmer> swimmers = new ArrayList<>();

    protected int laneCount;

    private Font timeOfDayFont;
    private Font singleTitleFont;
    private Font laneFont;
    private Font nameFont;
    private Font clubTimeFont;
    private Font placeFont;

    protected JLabel logo  = new JLabel(new ImageIcon("Logo600white.jpg"));
    protected JLabel timeOfDay  = new JLabel();

    AbstractScoreboard(Config config, boolean secondScreen)
    {
        super(config, secondScreen);

        laneCount = config.getInt("laneCount", 6);

        getFonts();

        Container contentPane = getContentPane();
        contentPane.setLayout(cardLayout);
        contentPane.add(scoreboardPanel, SCOREBOARD_PANEL);
        contentPane.add(timeOfDayPanel, TIME_OF_DAY_PANEL);

        createSwimmers();
        setColors();
        setTestText();
        setFonts();
    }

    private void createSwimmers()
    {
        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = new Swimmer();
            swimmers.add(swimmer);
        }
    }

    private void setTestText()
    {
        title.setText("3.6 Girls 100 Breaststroke");
        int lane=0;
        for (Swimmer swimmer : swimmers)
        {
            lane++;
            swimmer.lane.setText(showTestCardFor > 0 ? Integer.toString(lane) : " ");
            swimmer.name.setText("Emma Atanasova");
            swimmer.place.setText("CT");
            swimmer.clubTime.setText("2:38.23");
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
        clubTimeText =
                !timeText.isEmpty() ? (eventCount > 5 && hasImprovments ? swimmer.improvement.trim() : timeText) :
                        clockText.isEmpty() ? clubText : lane == getLaneOfFirstBlankTime() ? clockText : "";
        if (!clubTimeText.isEmpty() &&
                (!timeText.isEmpty() || !clockText.isEmpty()) &&
                clubTimeText.charAt(clubTimeText.length()-2) == '.')
        {
            clubTimeText = clubTimeText+' ';
        }
        swimmer.clubTime.setText(clubTimeText);
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
        else if (event instanceof StartEvent)
        {
            updated((StartEvent)event);
        }
    }

    private void update(RaceTimerEvent event)
    {
        setClock(event.getClock());
        setVisible(true);
    }

    private void update(TimeOfDayEvent event)
    {
        String time = event.getTimeOfDay();
        this.timeOfDay.setText(time);

        if (!timeOfDayPanel.isVisible())
        {
            cardLayout.show(getContentPane(), TIME_OF_DAY_PANEL);
        }
    }

    private void updated(StartEvent event)
    {
        if (showTestCardFor > 0)
        {
            try
            {
                Thread.sleep(showTestCardFor);
            }
            catch (InterruptedException ignore)
            {
            }
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
        if (!scoreboardPanel.isVisible())
        {
            cardLayout.show(getContentPane(), SCOREBOARD_PANEL);
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
                boolean countyTime = resultEvent.isCountyTime(laneIndex);
                if (!swimmer.improvement.isEmpty() || countyTime)
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
            swimmer.name.setText(event.getName(laneIndex));
            swimmer.club = event.getClub(laneIndex);
            swimmer.time = event.getTime(laneIndex);
            int place = event.getPlace(laneIndex);
            swimmer.place.setText(
                eventCount > 5 && hasImprovments
                ? (resultEvent.isCountyTime(laneIndex) ? "CT" : "")
                : place <= 0 ? " " : Integer.toString(place));
            setclubTime(laneIndex + 1, swimmer, event, hasImprovments);
        }
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
                        append("improvement='").append(swimmer.improvement.trim()).append("'}").
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
