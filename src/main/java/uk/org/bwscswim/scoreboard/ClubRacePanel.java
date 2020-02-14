package uk.org.bwscswim.scoreboard;

import uk.org.bwscswim.scoreboard.event.ClubEvent;
import uk.org.bwscswim.scoreboard.event.EventPublisher;
import uk.org.bwscswim.scoreboard.event.Observer;

import javax.swing.*;
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
        protected JTextField score = new JTextField("", SwingConstants.CENTER);
        protected JLabel place = new JLabel("", SwingConstants.CENTER);
        JButton placeButton = new JButton();
    }

    private JTextField title = new JTextField();
    private List<Club> clubs = new ArrayList<>();
    JButton publishButton;
    private int eventIndex = 0;
    private List<String> clubEvents;
    private EventPublisher eventPublisher = new EventPublisher();
    private JButton placeButtonJustSet;
    private int placesSet;

    public ClubRacePanel(Config config, List<String> clubEvents, Observer observer)
    {
        eventPublisher.addObserver(observer);
        this.clubEvents = clubEvents;

        int laneCount = config.getInt("laneCount", 6);

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
        int scoreWidth = config.getInt(null, null, "clubRaceScoreWidth", 96);
        int placeWidth = config.getInt(null, null, "clubRacePlaceWidth", 48);
        int placeButtonWidth = config.getInt(null, null, "clubRacePlaceButtonWidth", 48);
        int publishWidth = config.getInt(null, null, "clubRacePublishWidth", 300);
        int rightGap = config.getInt(null, null, "clubRaceRightGap", 0);

        int topGap = config.getInt(null, null, "clubRaceTopGap", 10);
        int preLaneGap = config.getInt(null, null, "clubRacePreLaneGap", 50);
        int bottomGap = config.getInt(null, null, "clubRaceBottomGap", 0);

        title.setFont(titleFont);
        title.setText(clubEvents.get(eventIndex));

        publishButton = new JButton("Publish");
        publishButton.setFont(publishFont);
        publishButton.addActionListener(e ->
        {
            placesSet = 0;
            placeButtonJustSet = null;
            clubs.forEach(club->
            {
                club.placeButton.setText("");
                int place = 0;
                if (!club.name.getText().isEmpty())
                {
                    int score = getInt(club.score);
                    place = 1;
                    for (Club c : clubs)
                    {
                        if (!c.name.getText().isEmpty() && score < getInt(c.score))
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
            setTitle(++eventIndex);
        });

        String[] names = new String[] {
                "Amersham 'B'",
                "Bracknell",
                "Windsor",
                "Maidenhead",
                "Reading",
                ""
        };
        for (int lane=1; lane<=laneCount; lane++)
        {
            Club club = new Club();
            clubs.add(club);

            String name = names[lane - 1];
            club.name.setText(name);
            club.lane.setText(name.isEmpty() ? "" : Integer.toString(lane));

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
                            position = 0;
                            for (Club c:clubs)
                            {
                                if (c != club)
                                {
                                    int prevP = getInt(c.placeButton);
                                    setPlaceAndAdjustScore(c, 0, prevP);
                                }
                            }
                        }
                        placeButtonJustSet = null;
                    }
                    setPlaceAndAdjustScore(club, position, prevPosition);
                }
                private void setPlaceAndAdjustScore(Club club, int position, int prevPosition)
                {
                    if (prevPosition != position)
                    {
                        setText(club.placeButton, position);

                        int pointsIncrement = getPoints(position) - (getPoints(prevPosition));
                        int prevScore = getInt(club.score);
                        int score = prevScore + pointsIncrement;
                        setText(club.score, score);
                        placesSet += position != 0 && prevPosition == 0 ? 1 : position == 0 ? -1 : 0;
//                        System.out.println("----- " + club.name.getText() + " " + prevPosition + " " + position + " points=" + pointsIncrement + " score=" + prevScore + "->" + score + " placesSet=" + placesSet);
                    }
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
                                .addComponent(title, PREFERRED_SIZE, titleWidth, PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(col2)
                                .addGroup(col3)
                                .addGroup(col4)
                                .addGroup(col5)
                                .addGroup(col6)));

        GroupLayout.SequentialGroup rows = layout.createSequentialGroup();

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(title, PREFERRED_SIZE, height, PREFERRED_SIZE)
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

    private void setText(JTextField component, int i)
    {
        component.setText(i <= 0 ? "" : Integer.toString(i));
    }

    private void setText(JLabel component, int i)
    {
        component.setText(i <= 0 ? "" : Integer.toString(i));
    }

    private void setText(AbstractButton component, int i)
    {
        component.setText(i <= 0 ? "" : Integer.toString(i));
    }

    private int getPoints(int position)
    {
        return position == 0 ? 0 : 6+1-position;
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

    private void setTitle(int eventIndex)
    {
        if (eventIndex >= clubEvents.size())
        {
            title.setText("Final Results");
        }
        else
        {
            title.setText(clubEvents.get(eventIndex));
        }
    }

    public EventPublisher getEventPublisher()
    {
        return eventPublisher;
    }
}
