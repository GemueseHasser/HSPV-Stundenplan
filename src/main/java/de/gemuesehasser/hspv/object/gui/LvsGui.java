package de.gemuesehasser.hspv.object.gui;

import com.bric.colorpicker.ColorPicker;
import de.gemuesehasser.hspv.Timetable;
import de.gemuesehasser.hspv.constant.ImageType;
import de.gemuesehasser.hspv.handler.UserHandler;
import de.gemuesehasser.hspv.object.Gui;
import de.gemuesehasser.hspv.object.LVS;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ein {@link LvsGui} stellt eine Instanz eines {@link Gui Fensters} dar, in welchem für eine bestimmte
 * Lehrveranstaltung genauere Daten angezeigt werden, als die, die im Stundenplan angezeigt werden.
 */
public final class LvsGui extends Gui implements MouseListener {

    //<editor-fold desc="CONSTANTS">
    /** Die minimale Breite dieses Fensters. */
    private static final int MINIMUM_WIDTH = 300;
    /** Die Höhe dieses Fensters. */
    private static final int HEIGHT = 240;
    /** Die Breite des Color-Chooser, mit dem sich die Farbe der LVS individualisieren lässt. */
    private static final int COLOR_PICKER_WIDTH = 600;
    /** Die Höhe des Color-Chooser, mit dem sich die Farbe der LVS individualisieren lässt. */
    private static final int COLOR_PICKER_HEIGHT = 400;
    /** Die x-Koordinate, an der der Text beginnt, welcher diese LVS beschreibt. */
    private static final int TEXT_BEGIN_X = 20;
    /** Die x-Koordinate des Buttons, mit welchem sich die Farbe der LVS ändern lässt. */
    private static final int COLOR_BUTTON_X = 100;
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Das jeweilige {@link TimetableGui} von dem aus dieses Lvs-Gui geöffnet wurde. */
    @NotNull
    private final TimetableGui timetableGui;
    /** Die jeweilige Lehrveranstaltung, auf dessen Grundlage dieses Fenster erzeugt werden soll. */
    @NotNull
    private final LVS lvs;
    /** Der Benutzername des aktuell angemeldeten Nutzers, für den der Stundenplan geladen wurde. */
    @NotNull
    private final String username;
    /** Der Name des jeweiligen Dozenten. */
    @NotNull
    private final String docentName;
    /** Der jeweilige Raum. */
    @NotNull
    private final String room;
    /** Der jeweilige Name der Lehrveranstaltung. */
    @NotNull
    private String lvsName = "name";
    /** Die jeweilige Modulbezeichnung der Lehrveranstaltung. */
    @NotNull
    private String module = "module";
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link LvsGui}. Ein {@link LvsGui} stellt eine Instanz eines {@link Gui Fensters}
     * dar, in welchem für eine bestimmte Lehrveranstaltung genauere Daten angezeigt werden, als die, die im Stundenplan
     * angezeigt werden.
     *
     * @param timetableGui Das {@link TimetableGui Fenster}, in welchem die LVS-Buttons platziert wurden, für die dieses
     *                     {@link LvsGui} instanziiert werden soll.
     * @param lvs          Die jeweilige Lehrveranstaltung, auf dessen Grundlage dieses Fenster erzeugt werden soll.
     * @param username     Der Benutzername des Nutzers, der aktuell angemeldet ist und für den der Stundenplan aktuell
     *                     geladen wurde.
     */
    public LvsGui(
        @NotNull final TimetableGui timetableGui,
        @NotNull final LVS lvs,
        @NotNull final String username
    ) {
        // create gui instance
        super("", 0, HEIGHT);

        // init vars
        this.timetableGui = timetableGui;
        this.lvs = lvs;
        this.username = username;

        assert lvs.getDescription() != null;
        final String[] descriptionParts = lvs.getDescription().split("\n");

        for (int i = 0; i < descriptionParts[1].length(); i++) {
            if (descriptionParts[1].charAt(i) != ' ') continue;

            this.module = descriptionParts[1].substring(0, i);
            this.lvsName = descriptionParts[1].substring(i);
            break;
        }
        this.room = (descriptionParts.length > 2 ? descriptionParts[2] : "");
        this.docentName = (descriptionParts.length > 3 ? descriptionParts[3] : "");

        // set gui properties
        super.setTitle(lvsName);
        super.setUndecorated(true);
        super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        super.addMouseListener(this);

        final int lvsNameWidth = super.getFontMetrics(Timetable.DEFAULT_FONT.deriveFont(18F)).stringWidth(lvsName) + 50;

        super.setSize(Math.max(lvsNameWidth, MINIMUM_WIDTH), HEIGHT);
        super.setShape(
                new RoundRectangle2D.Double(
                        0,
                        0,
                        super.getWidth(),
                        HEIGHT,
                        25,
                        25
                )
        );
        super.getRootPane().setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 3));

        // update draw size
        super.getDraw().setBounds(0, 0, super.getWidth(), HEIGHT);

        // create color button
        final JButton colorButton = new JButton();
        colorButton.setBounds(
                COLOR_BUTTON_X,
                130,
                50,
                30
        );
        colorButton.setBackground(lvs.getColor());
        colorButton.setFocusable(false);
        colorButton.addActionListener(e -> openColorPicker());

        super.add(colorButton);
    }
    //</editor-fold>


    /**
     * Öffnet einen {@link ColorPicker} für diese Lehrveranstaltung, mit dem sich die Hintergrundfarbe individualisieren
     * lässt.
     */
    private void openColorPicker() {
        super.dispose();

        final JFrame frame = new JFrame(lvsName);
        frame.setBounds(0, 0, COLOR_PICKER_WIDTH, COLOR_PICKER_HEIGHT + 70);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);

        final JPanel colorPickerPanel = new JPanel();
        colorPickerPanel.setBounds(10, 10, COLOR_PICKER_WIDTH - 50, COLOR_PICKER_HEIGHT - 30);

        final ColorPicker colorPicker = new ColorPicker(true, true, Locale.GERMAN);
        colorPicker.setColor(lvs.getColor());
        colorPickerPanel.add(colorPicker);

        final JButton saveButton = new JButton("Speichern");
        saveButton.setBounds(COLOR_PICKER_WIDTH / 2 - 50, COLOR_PICKER_HEIGHT - 30, 100, 40);
        saveButton.setBackground(Color.LIGHT_GRAY);
        saveButton.setFocusable(false);
        saveButton.addActionListener(e -> {
            frame.dispose();
            save(colorPicker.getColor());
        });

        frame.add(saveButton);
        frame.add(colorPickerPanel);
        frame.setVisible(true);
    }

    /**
     * Speichert alle Einstellungen in diesem {@link LvsGui} ab und lädt die aktuelle Woche neu.
     */
    private void save(@NotNull final Color color) {
        UserHandler.saveConfiguration(username, "color." + lvsName, String.valueOf(color.getRGB()));

        timetableGui.loadWeek(0);
    }

    //<editor-fold desc="implementation">

    @Override
    public void draw(@NotNull final Graphics2D g) {
        g.drawImage(ImageType.LVS_BACKGROUND.getImage(), 0, 0, super.getWidth(), HEIGHT, null);

        g.setColor(Color.BLACK);
        g.setFont(Timetable.DEFAULT_FONT.deriveFont(18F));
        final int nameWidth = g.getFontMetrics().stringWidth(lvsName);
        g.drawString(lvsName, super.getWidth() / 2 - nameWidth / 2 - 8, 40);
        g.drawLine(
                super.getWidth() / 2 - nameWidth / 2 - 5,
                42,
                super.getWidth() / 2 + nameWidth / 2 - 3,
                42
        );

        g.setFont(Timetable.DEFAULT_FONT);
        g.drawString("Modul:", TEXT_BEGIN_X, 80);
        g.drawString(module, COLOR_BUTTON_X, 80);
        g.drawString("Raum:", TEXT_BEGIN_X, 100);
        g.drawString(room, COLOR_BUTTON_X, 100);
        g.drawString("DozentIn:", TEXT_BEGIN_X, 120);
        g.drawString(docentName, COLOR_BUTTON_X, 120);
        g.drawString("LVS-Farbe:", TEXT_BEGIN_X, 150);
    }

    @Override
    public void mouseClicked(@NotNull final MouseEvent e) {

    }

    @Override
    public void mousePressed(@NotNull final MouseEvent e) {

    }

    @Override
    public void mouseReleased(@NotNull final MouseEvent e) {

    }

    @Override
    public void mouseEntered(@NotNull final MouseEvent e) {

    }

    @Override
    public void mouseExited(@NotNull final MouseEvent e) {
        if (super.contains(e.getPoint()) && e.getX() > 20 && e.getY() > 20) return;

        super.removeMouseListener(this);
        Executors.newScheduledThreadPool(1).schedule(super::dispose, 400, TimeUnit.MILLISECONDS);
    }
    //</editor-fold>
}
