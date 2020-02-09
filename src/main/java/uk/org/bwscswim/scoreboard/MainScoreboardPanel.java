package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.event.LineupEvent;
import uk.org.bwscswim.scoreboard.event.PageEvent;
import uk.org.bwscswim.scoreboard.event.RaceSplitTimeEvent;
import uk.org.bwscswim.scoreboard.event.RaceTimerEvent;
import uk.org.bwscswim.scoreboard.event.ResultEvent;
import uk.org.bwscswim.scoreboard.event.ScoreboardEvent;
import uk.org.bwscswim.scoreboard.event.TestcardEvent;
import uk.org.bwscswim.scoreboard.meet.model.Improvement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;
import static javax.swing.GroupLayout.PREFERRED_SIZE;
import static uk.org.bwscswim.scoreboard.Scoreboard.setTrimmedText;
import static uk.org.bwscswim.scoreboard.Scoreboard.trim;

/**
 * @author adavis
 */
public class MainScoreboardPanel extends JPanel
{
    private static final Color GREENISH  = new Color(0, 220, 60);

    private static class Swimmer
    {
        JLabel lane = new JLabel();
        JLabel name = new JLabel();
        String club;
        String time;
        Improvement improvement;
        JLabel clubTime = new JLabel("", SwingConstants.RIGHT);
        JLabel place = new JLabel("", SwingConstants.CENTER);
    }

    private JLabel title = new JLabel();
    private String clock = "";
    private List<Swimmer> swimmers = new ArrayList<>();
    private int laneCount;

    public MainScoreboardPanel(Config config)
    {
        int leftGap = config.getInt(null, null, "clubLeftGap", 30);
        int laneWidth = config.getInt(null, null, "clubLaneWidth", 60);
        int nameWidth = config.getInt(null, null, "clubNameWidth", 669);
        int clubTimeWidth = config.getInt(null, null, "clubScoreWidth", 287);
        int placeWidth = config.getInt(null, null, "clubPlaceWidth", 113);
        int rightGap = config.getInt(null, null, "clubRightGap", 0);

        int topGap = config.getInt(null, null, "clubTopGap", 10);
        int preLaneGap = config.getInt(null, null, "clubPreLaneGap", 10);
        int bottomGap = config.getInt(null, null, "clubBottomGap", 0);

        Font titleFont = config.getFont(null, "title");
        Font laneFont = config.getFont(null, "lane");
        Font nameFont = config.getFont(null, "name");
        Font clubTimeFont = config.getFont(null, "clubTime");
        Font placeFont = config.getFont(null, "place");

        laneCount = config.getInt("laneCount", 6);

        setBackground(BLACK);
        title.setForeground(YELLOW);
        title.setFont(titleFont);

        for (int lane=1; lane<=laneCount; lane++)
        {
            Swimmer swimmer = new Swimmer();
            swimmers.add(swimmer);

            swimmer.lane.setFont(laneFont);
            swimmer.name.setFont(nameFont);
            swimmer.clubTime.setFont(clubTimeFont);
            swimmer.place.setFont(placeFont);

            swimmer.lane.setForeground(WHITE);
            swimmer.name.setForeground(WHITE);
            swimmer.clubTime.setForeground(WHITE);
            swimmer.place.setForeground(YELLOW);

            swimmer.name.setOpaque(false);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        GroupLayout.ParallelGroup col1 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col2 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col3 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col4 = layout.createParallelGroup();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(leftGap)
                                .addComponent(title))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(leftGap)
                                .addGroup(col1)
                                .addGroup(col2)
                                .addGroup(col3)
                                .addGroup(col4)
                                .addGap(rightGap)));

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGap(topGap)
                        .addComponent(title)
                        .addGap(preLaneGap)
                        .addGroup(rows)
                        .addGap(bottomGap));

        for (Swimmer swimmer : swimmers)
        {
            GroupLayout.ParallelGroup row = layout.createParallelGroup();
            rows.addGroup(row);

            row.addComponent(swimmer.lane);
            col1.addComponent(swimmer.lane, PREFERRED_SIZE, laneWidth, PREFERRED_SIZE);

            row.addComponent(swimmer.name);
            col2.addComponent(swimmer.name, PREFERRED_SIZE, nameWidth, PREFERRED_SIZE);

            row.addComponent(swimmer.clubTime);
            col3.addComponent(swimmer.clubTime, PREFERRED_SIZE, clubTimeWidth, PREFERRED_SIZE);

            row.addComponent(swimmer.place);
            col4.addComponent(swimmer.place, PREFERRED_SIZE, placeWidth, PREFERRED_SIZE);
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

    public void update(ScoreboardEvent event)
    {
        if (event instanceof RaceTimerEvent)
        {
            update((RaceTimerEvent)event);
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
            setBackground(event instanceof ResultEvent ? new Color(Integer.parseInt("0033cc", 16)) : BLACK);
        }

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
