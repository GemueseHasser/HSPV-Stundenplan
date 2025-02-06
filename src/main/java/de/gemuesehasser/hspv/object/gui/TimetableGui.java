package de.gemuesehasser.hspv.object.gui;

import de.gemuesehasser.hspv.handler.UserHandler;
import de.gemuesehasser.hspv.handler.WeekTimetableHandler;
import de.gemuesehasser.hspv.object.LVS;
import de.gemuesehasser.hspv.object.gui.component.LvsButton;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.BorderFactory;
import javax.swing.JButton;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Das {@link TimetableGui} stellt eine Instanz eines {@link Gui} dar, welches den Stundenplan anzeigt.
 */
public final class TimetableGui extends Gui implements KeyListener {

    //<editor-fold desc="CONSTANTS">
    /** Der Titel dieses Fensters. */
    private static final String TITLE = "HSPV-Stundenplan";
    /** Die Breite dieses Fensters */
    private static final int WIDTH = 550;
    /** Die optimale Höhe dieses Fensters. */
    private static final int OPTIMUM_HEIGHT = 800;
    /** Die Höhe des Bildschirms des Gerätes, auf dem diese Anwendung gestartet wurde. */
    private static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
    /** Die Höhe dieses Fensters. */
    private static final int HEIGHT = (SCREEN_HEIGHT > OPTIMUM_HEIGHT) ? OPTIMUM_HEIGHT : (SCREEN_HEIGHT - 70) - (SCREEN_HEIGHT - 70) % 55;
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Alle Buttons, der aktuellen Lehrveranstaltungen, die angezeigt werden in der aktuellen Woche. */
    private final List<JButton> lvsButtons = new ArrayList<>();
    /** Der Benutzername des Nutzers, für den der Stundenplan geladen wird. */
    private final String username;
    /** Eine Liste, der aktuellen Lehrveranstaltungen der aktuellen Woche. */
    private LinkedList<LVS> currentLvs;
    /** Das Datum, an welchem die aktuelle Woche startet (jeweils der Montag der Woche). */
    private LocalDate weekStartDate;
    /** Die aktuelle Anzahl an Wochen, welche die angezeigte Woche von der aktuellen Woche abweicht. */
    private int currentWeek = 0;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link TimetableGui} auf der Grundlage eines {@link Gui}. Das
     * {@link TimetableGui} stellt eine Instanz eines {@link Gui} dar, welches den Stundenplan anzeigt.
     */
    public TimetableGui(@NotNull final String username, @NotNull final LoadingGui loadingGui) {
        super(TITLE + " - " + username, WIDTH, HEIGHT);
        super.addKeyListener(this);
        super.setBounds(0, 0, WIDTH, HEIGHT);
        super.setLocationRelativeTo(null);
        loadingGui.dispose();

        this.username = username;
        this.currentLvs = WeekTimetableHandler.getWeekLvs(currentWeek);
        this.weekStartDate = currentLvs.getFirst().getStart().toLocalDate();

        loadLvsButtons();

        final JButton left = getWeekSwitchButton("<");
        left.setBounds(45, HEIGHT - 100, 50, 50);
        left.addActionListener(e -> loadWeek(-1));

        final JButton right = getWeekSwitchButton(">");
        right.setBounds(WIDTH - 80, HEIGHT - 100, 50, 50);
        right.addActionListener(e -> loadWeek(1));

        super.add(left);
        super.add(right);
    }
    //</editor-fold>


    /**
     * Wechselt die aktuelle Woche in diesem {@link TimetableGui}, die angezeigt wird. Dabei werden die LVS der Woche
     * neu geladen und das Fenster wird komplett neu gezeichnet.
     *
     * @param weekAddition Die Anzahl an Wochen, um die die aktuelle Woche geändert werden soll. Dabei funktionieren
     *                     positive und auch negative ganze Zahlen.
     */
    public void loadWeek(final int weekAddition) {
        currentWeek += weekAddition;
        this.currentLvs = WeekTimetableHandler.getWeekLvs(currentWeek);
        super.remove(super.getDraw());
        reloadLvsButtons();
        super.repaint();
        super.add(super.getDraw());
    }

    /**
     * Lädt alle Buttons, welche für das Abbilden der Lehrveranstaltungen genutzt werden neu.
     */
    private void reloadLvsButtons() {
        for (@NotNull final JButton button : lvsButtons) {
            super.remove(button);
        }

        lvsButtons.clear();

        if (currentLvs.size() == 1 && currentLvs.getFirst().getEnd() == null && currentLvs.getFirst().getDescription() == null) {
            weekStartDate = currentLvs.getFirst().getStart().toLocalDate();
            currentLvs.clear();
            return;
        }

        if (currentLvs.getFirst().getStart().getDayOfWeek() != DayOfWeek.MONDAY) {
            weekStartDate = currentLvs.getFirst().getStart().toLocalDate().minusDays(
                currentLvs.getFirst().getStart().getDayOfWeek().getValue() - 1
            );
        } else {
            weekStartDate = currentLvs.getFirst().getStart().toLocalDate();
        }

        loadLvsButtons();
    }

    /**
     * Lädt alle Buttons, die zum Darstellen der aktuellen Lehrveranstaltungen erstmalig.
     */
    private void loadLvsButtons() {
        for (@NotNull final LVS lvs : currentLvs) {
            final int absoluteDurationInMinutes = (int) Duration.between(lvs.getStart(), lvs.getEnd()).toMinutes();
            final int lvsDurationWithoutBreaks = ((absoluteDurationInMinutes - (absoluteDurationInMinutes / 135 * 15)) / 45) * 45;

            final int lvsBeginDuration = (int) Duration.between(
                lvs.getStart().withHour(8).withMinute(0),
                lvs.getStart()
            ).toMinutes();
            final int lvsBeginDurationWithoutBreaks = ((lvsBeginDuration - (lvsBeginDuration / 135 * 15)) / 45) * 45;

            final int x = 50 + ((lvs.getStart().getDayOfWeek().getValue() - 1) * 90);
            final int y = 30 + (lvsBeginDurationWithoutBreaks / 45) * 55 + ((lvsBeginDurationWithoutBreaks / 90) * 15) + (lvsBeginDurationWithoutBreaks / 270 == 1 ? 15 : 0);
            final int width = 90;
            final int height = (lvsDurationWithoutBreaks / 45) * 55 + (absoluteDurationInMinutes - lvsDurationWithoutBreaks);

            assert lvs.getDescription() != null;
            final String[] descriptionParts = lvs.getDescription().split("\n");
            final String room = (descriptionParts.length > 2 ? descriptionParts[2] : StringUtils.EMPTY);
            String description = "description";

            for (int i = 0; i < descriptionParts[1].length(); i++) {
                if (descriptionParts[1].charAt(i) != ' ') continue;

                description = descriptionParts[1].substring(i);
                break;
            }

            final String colorRgb = UserHandler.getConfiguration(username, "color." + description);

            if (colorRgb != null) {
                lvs.setColor(new Color(Integer.parseInt(colorRgb)));
            }

            final LvsButton button = new LvsButton(
                "<html><a style='margin: 20px'>" + description + "<br><br>" + (Objects.equals(
                    room,
                    StringUtils.EMPTY
                ) ? "Kein Raum" : room) + "</a>",
                25
            );
            button.setBounds(x, y, width, height);
            button.setBackground(lvs.getColor());
            button.setFocusable(false);
            button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            button.addActionListener(e -> new LvsGui(this, lvs, username).open());

            this.lvsButtons.add(button);
            super.add(button);
        }
    }

    /**
     * Erzeugt einen neuen {@link JButton} zum Wechseln der aktuellen Woche, welcher bereits in der Art und Weise
     * formatiert ist, sodass dieser visuell zum {@link TimetableGui} passt.
     *
     * @param text Der Text, der auf diesem Button angezeigt werden soll.
     *
     * @return Ein neuer {@link JButton} zum Wechseln der aktuellen Woche, welcher bereits in der Art und Weise
     *     formatiert ist, sodass dieser visuell zum {@link TimetableGui} passt.
     */
    private JButton getWeekSwitchButton(final String text) {
        final JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusable(false);

        return button;
    }

    //<editor-fold desc="implementation">

    /* This drawing method includes everything under the buttons. */
    @Override
    public void draw(@NotNull Graphics2D g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.BLACK);
        g.drawRect(0, 0, 50, HEIGHT);

        g.setColor(Color.WHITE);
        g.drawString(weekStartDate.getMonth().getDisplayName(TextStyle.SHORT_STANDALONE, Locale.GERMANY), 5, 20);

        // draw vertical components
        for (int i = 0; i < 5; i++) {
            g.setColor(Color.BLACK);
            g.drawRect(50 + i * 90, 0, 90, HEIGHT);

            g.setColor(Color.WHITE);
            g.drawString(
                weekStartDate.plusDays(i).getDayOfWeek().getDisplayName(
                    TextStyle.SHORT_STANDALONE,
                    Locale.GERMANY
                ) + " " + weekStartDate.plusDays(i).getDayOfMonth(), 55 + i * 90, 20
            );
        }

        // draw horizontal components
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, WIDTH, 30);
        for (int i = 0; i < HEIGHT / 55; i++) {
            final int breakAddition = (i / 2) * 15 + (i / 6 == 1 ? 15 : 0);

            g.setColor(Color.BLACK);
            g.drawRect(0, 30 + i * 55 + breakAddition, WIDTH, 55);

            final int absoluteMinutePlus = (i * 45) + breakAddition;

            final int hourPlus = absoluteMinutePlus / 60;
            final int minutePlus = absoluteMinutePlus - hourPlus * 60;

            final int hourPlusPlus = (absoluteMinutePlus + 45) / 60;
            final int minutePlusPlus = (absoluteMinutePlus + 45) - hourPlusPlus * 60;

            g.setColor(Color.WHITE);
            g.drawString((8 + hourPlus) + ":" + (minutePlus == 0 ? "00" : minutePlus), 3, 45 + i * 55 + breakAddition);
            g.drawString(
                (8 + hourPlusPlus) + ":" + (minutePlusPlus == 0 ? "00" : minutePlusPlus),
                3,
                80 + i * 55 + breakAddition
            );
        }
    }

    /* This drawing method includes everything over the buttons -> needs more resources. */
    @Override
    public void paint(@NotNull final Graphics graphics) {
        super.paint(graphics);

        final Graphics2D g = (Graphics2D) graphics;

        // get current local time
        final LocalDateTime currentTime = LocalDateTime.now();

        // display current local time as red line
        final int currentTimeMinuteAddition = (currentTime.getHour() - 8) * 60 + currentTime.getMinute();
        final int lvsAmount = (currentTimeMinuteAddition - ((currentTimeMinuteAddition / 90) * 15)) / 45;
        final int breakAddition = (lvsAmount / 2) * 15 + (lvsAmount / 6 == 1 ? 15 : 0);

        final int currentTimeY = (lvsAmount * 55) + breakAddition + (currentTimeMinuteAddition - (lvsAmount * 45 + breakAddition));

        g.setColor(Color.RED);
        g.fillRect(40, 59 + currentTimeY, WIDTH, 3);

        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                loadWeek(-1);
                break;
            case KeyEvent.VK_RIGHT:
                loadWeek(1);
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    //</editor-fold>
}
