package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.event.ClubEvent;
import uk.org.bwscswim.scoreboard.event.EventPublisher;
import uk.org.bwscswim.scoreboard.event.Observer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.GroupLayout.PREFERRED_SIZE;

/**
 * @author adavis
 */
public class ClubRacePanel extends Container
{
    private static class Club
    {
        protected JLabel lane = new JLabel();
        protected JTextField name = new JTextField();
        protected JTextField score = new JTextField("", SwingConstants.LEFT);
        protected JLabel place = new JLabel("", SwingConstants.CENTER);
        JButton placeButton = new JButton();
        float eventPoints;
    }

    private JTextField title = new JTextField();
    private BasicArrowButton up = new BasicArrowButton(SwingConstants.NORTH);
    private BasicArrowButton down = new BasicArrowButton(SwingConstants.SOUTH);
    private List<Club> clubs = new ArrayList<>();
    JButton publishButton;
    private int eventIndex = 0;
    private List<String> clubEvents;
    private EventPublisher eventPublisher = new EventPublisher();
    private JButton placeButtonJustSet;
    private int placesSet;
    private int numberOfClubs;

    public ClubRacePanel(Config config, List<String> clubNamesAndEvents, Observer observer)
    {
        eventPublisher.addObserver(observer);
        int laneCount = config.getInt("laneCount", 6);
        this.clubEvents = clubNamesAndEvents.subList(laneCount, clubNamesAndEvents.size());

        Font titleFont = config.getMonoFont(null, "clubRaceTitle");
        Font laneFont = config.getMonoFont(null, "clubRaceLane");
        Font nameFont = config.getMonoFont(null, "clubRaceName");
        Font clubTimeFont = config.getMonoFont(null, "clubRaceClubTime");
        Font placeFont = config.getMonoFont(null, "clubRacePlace");
        Font placeButtonFont = config.getMonoFont(null, "clubRacePlaceButton");
        Font publishFont = config.getFont(null, "clubRacePublishButton", Font.MONOSPACED, Font.PLAIN, 32);

        int height = config.getInt(null, null, "clubRaceHeight", 48);
        int leftGap = config.getInt(null, null, "clubRaceLeftGap", 30);
        int titleWidth = config.getInt(null, null, "clubRaceTitleWidth", 700);
        int laneWidth = config.getInt(null, null, "clubRaceLaneWidth", 48);
        int nameWidth = config.getInt(null, null, "clubRaceNameWidth", 540);
        int scoreWidth = config.getInt(null, null, "clubRaceScoreWidth", 140);
        int placeWidth = config.getInt(null, null, "clubRacePlaceWidth", 48);
        int placeButtonWidth = config.getInt(null, null, "clubRacePlaceButtonWidth", 48);
        int publishWidth = config.getInt(null, null, "clubRacePublishWidth", 300);
        int rightGap = config.getInt(null, null, "clubRaceRightGap", 0);

        int topGap = config.getInt(null, null, "clubRaceTopGap", 10);
        int preLaneGap = config.getInt(null, null, "clubRacePreLaneGap", 50);
        int bottomGap = config.getInt(null, null, "clubRaceBottomGap", 0);

        title.setFont(titleFont);
        title.setText(clubEvents.get(eventIndex));
        up.addActionListener(e -> setTitle(-1));
        down.addActionListener(e -> setTitle(+1));

        publishButton = new JButton("Publish");
        publishButton.setFont(publishFont);
        publishButton.addActionListener(e ->
        {
            placesSet = 0;
            placeButtonJustSet = null;
            clubs.forEach(club->
            {
                club.placeButton.setText("");
                club.eventPoints = 0;
                int place = 0;
                if (!club.name.getText().isEmpty())
                {
                    float score = getFloat(club.score);
                    place = 1;
                    for (Club c : clubs)
                    {
                        if (!c.name.getText().isEmpty() && score < getFloat(c.score))
                        {
                            place++;
                        }
                    }
                }
                setText(club.place, place);
            });
            eventPublisher.publishEvent(new ClubEvent(title.getText(),
                    clubs.get(0).name.getText(), clubs.get(0).score.getText(), clubs.get(0).place.getText(),
                    clubs.get(1).name.getText(), clubs.get(1).score.getText(), clubs.get(1).place.getText(),
                    clubs.get(2).name.getText(), clubs.get(2).score.getText(), clubs.get(2).place.getText(),
                    clubs.get(3).name.getText(), clubs.get(3).score.getText(), clubs.get(3).place.getText(),
                    clubs.get(4).name.getText(), clubs.get(4).score.getText(), clubs.get(4).place.getText(),
                    clubs.get(5).name.getText(), clubs.get(5).score.getText(), clubs.get(5).place.getText()));
            setTitle(+1);
        });

        List<String> names = clubNamesAndEvents.subList(0, laneCount);

        for (int lane=1; lane<=laneCount; lane++)
        {
            Club club = new Club();
            clubs.add(club);

            String name = names.get(lane-1);
            club.name.setText(name);
            boolean noClub = name.isEmpty();
            club.lane.setText(noClub ? "" : Integer.toString(lane));
            numberOfClubs += noClub ? 0 : 1;
            // TODO add listener on the name to reset various fields including the numberOfClubs

            club.lane.setFont(laneFont);
            club.name.setFont(nameFont);
            club.score.setFont(clubTimeFont);
            club.place.setFont(placeFont);
            club.placeButton.setFont(placeButtonFont);
            club.placeButton.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    int prevPosition = getInt(club.placeButton);
                    int position = prevPosition;
                    // Club's position was blank
                    if (prevPosition == 0)
                    {
                        if (!club.name.getText().isEmpty())
                        {
                            position = placesSet+1;
                            placeButtonJustSet = club.placeButton;
                        }
                    }
                    else
                    {
                        // If a draw with previous club
                        if (placeButtonJustSet == club.placeButton)
                        {
                            position = 0;
                            for (Club c:clubs)
                            {
                                int p = getInt(c.placeButton);
                                if (position < p && c != club)
                                {
                                    position = p;
                                }
                            }
                        }
                        else
                        {
                            // Clear positions
                            position = 0;
                            for (Club c:clubs)
                            {
                                if (c != club)
                                {
                                    setPlaceAndAdjustScore(c, 0);
                                }
                            }
                        }
                        placeButtonJustSet = null;
                    }
                    setPlaceAndAdjustScore(club, position);
                }

                private void setPlaceAndAdjustScore(Club club, int position)
                {
                    int prevPosition = getInt(club.placeButton);
                    if (prevPosition != position)
                    {
                        setText(club.placeButton, position);
                        placesSet += position != 0 && prevPosition == 0 ? 1 : position == 0 ? -1 : 0;

                        for (Club c:clubs)
                        {
                            int p = getInt(c.placeButton);
                            if (c == club || (p != 0 && (p == prevPosition || p == position)))
                            {
                                float points = getPoints(p);
                                float prevScore = getFloat(c.score);
                                float score = prevScore + points - c.eventPoints;
                                setText(c.score, score);
                                c.eventPoints = points;
                            }
                        }
                    }
                }

                private float getPoints(int position)
                {
                    if (position == 0)
                    {
                        return 0;
                    }

                    float sharedPoints = 0;
                    int equalTeams = 0;
                    for (Club c : clubs)
                    {
                        int p = getInt(c.placeButton);
                        if (position == p)
                        {
                            sharedPoints += getSimplePoints(position + equalTeams);
                            equalTeams++;
                        }
                    }
                    return sharedPoints/equalTeams;
                }

                private float getSimplePoints(int position)
                {
                    return position == 0 ? 0 : numberOfClubs + 1 - position;
                }
            });
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);

        GroupLayout.ParallelGroup col2 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col3 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col4 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col5 = layout.createParallelGroup();
        GroupLayout.ParallelGroup col6 = layout.createParallelGroup();

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(title, PREFERRED_SIZE, titleWidth, PREFERRED_SIZE)
                                .addComponent(down, PREFERRED_SIZE, height, PREFERRED_SIZE)
                                .addComponent(up, PREFERRED_SIZE, height, PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(col2)
                                .addGroup(col3)
                                .addGroup(col4)
                                .addGroup(col5)
                                .addGroup(col6)));

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup().
                                addComponent(title, PREFERRED_SIZE, height, PREFERRED_SIZE).
                                addComponent(down, PREFERRED_SIZE, height, PREFERRED_SIZE).
                                addComponent(up, PREFERRED_SIZE, height, PREFERRED_SIZE))
                        .addGap(preLaneGap)
                        .addGroup(rows));

        int lane = 1;
        for (Club club : clubs)
        {
            GroupLayout.ParallelGroup row = layout.createParallelGroup();
            rows.addGroup(row);

            row.addComponent(club.name, PREFERRED_SIZE, height, PREFERRED_SIZE);
            col2.addComponent(club.name, PREFERRED_SIZE, nameWidth, PREFERRED_SIZE);

            row.addComponent(club.score, PREFERRED_SIZE, height, PREFERRED_SIZE);
            col3.addComponent(club.score, PREFERRED_SIZE, scoreWidth, PREFERRED_SIZE);

            row.addComponent(club.lane, PREFERRED_SIZE, height, PREFERRED_SIZE);
            col4.addComponent(club.lane, PREFERRED_SIZE, laneWidth, PREFERRED_SIZE);

            row.addComponent(club.placeButton, PREFERRED_SIZE, height, PREFERRED_SIZE);
            col5.addComponent(club.placeButton, PREFERRED_SIZE, placeButtonWidth, PREFERRED_SIZE);

            if (lane++ == 4)
            {
                row.addComponent(publishButton, PREFERRED_SIZE, height, PREFERRED_SIZE);
                col6.addComponent(publishButton, PREFERRED_SIZE, publishWidth, PREFERRED_SIZE);
            }
        }

        publishButton.doClick();
    }

    private int getInt(JTextField component)
    {
        try
        {
            return Integer.parseInt(component.getText());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    private float getFloat(JTextField component)
    {
        try
        {
            return Float.parseFloat(component.getText());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    private int getInt(AbstractButton component)
    {
        try
        {
            return Integer.parseInt(component.getText());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    private int getInt(JLabel component)
    {
        try
        {
            return Integer.parseInt(component.getText());
        }
        catch (NumberFormatException e)
        {
            return 0;
        }
    }

    private void setText(JTextField component, int i)
    {
        component.setText(i <= 0 ? "" : Integer.toString(i));
    }

    private void setText(JTextField component, float f)
    {
        String text = f <= 0.0 ? "" : Float.toString(f);
        if (text.endsWith(".0"))
        {
            text = text.substring(0, text.length()-2);
        }
        component.setText(text);
    }

    private void setText(JLabel component, int i)
    {
        component.setText(i <= 0 ? "" : Integer.toString(i));
    }

    private void setText(AbstractButton component, int i)
    {
        component.setText(i <= 0 ? "" : Integer.toString(i));
    }

    private int clubCount()
    {
        int count = 0;
        for (Club club: clubs)
        {
            if (!club.name.getText().isEmpty())
            {
                count++;
            }
        }
        return count;
    }

    private void setTitle(int inc)
    {
        eventIndex += inc;
        if (eventIndex < 0)
        {
            eventIndex = clubEvents.size()-1;
        }
        if (eventIndex >= clubEvents.size())
        {
            eventIndex = 0;
        }
        title.setText(clubEvents.get(eventIndex));
    }

    public EventPublisher getEventPublisher()
    {
        return eventPublisher;
    }
}
