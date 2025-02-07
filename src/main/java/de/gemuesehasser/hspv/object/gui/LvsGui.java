package de.gemuesehasser.hspv.object.gui;

import com.bric.colorpicker.ColorPicker;
import de.gemuesehasser.hspv.handler.UserHandler;
import de.gemuesehasser.hspv.object.LVS;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JFrame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Locale;

/**
 * Ein {@link LvsGui} stellt eine Instanz eines {@link Gui Fensters} dar, in welchem für eine bestimmte
 * Lehrveranstaltung genauere Daten angezeigt werden, als die, die im Stundenplan angezeigt werden.
 */
public final class LvsGui extends Gui {

    //<editor-fold desc="CONSTANTS">
    /** Die Breite dieses Fensters. */
    private static final int WIDTH = 450;
    /** Die Höhe dieses Fensters. */
    private static final int HEIGHT = 270;
    /** Die Breite des Color-Chooser, mit dem sich die Farbe der LVS individualisieren lässt. */
    private static final int COLOR_PICKER_WIDTH = 500;
    /** Die Höhe des Color-Chooser, mit dem sich die Farbe der LVS individualisieren lässt. */
    private static final int COLOR_PICKER_HEIGHT = 400;
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Das jeweilige {@link TimetableGui} von dem aus dieses Lvs-Gui geöffnet wurde. */
    private final TimetableGui timetableGui;
    /** Die jeweilige Lehrveranstaltung, auf dessen Grundlage dieses Fenster erzeugt werden soll. */
    private final LVS lvs;
    /** Der Benutzername des aktuell angemeldeten Nutzers, für den der Stundenplan geladen wurde. */
    private final String username;
    /** Der Name des jeweiligen Dozenten. */
    private final String docentName;
    /** Der jeweilige Raum. */
    private final String room;
    /** Der jeweilige Name der Lehrveranstaltung. */
    private String lvsName = "name";
    /** Die jeweilige Modulbezeichnung der Lehrveranstaltung. */
    private String module = "module";
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link LvsGui}. Ein {@link LvsGui} stellt eine Instanz eines {@link Gui Fensters}
     * dar, in welchem für eine bestimmte Lehrveranstaltung genauere Daten angezeigt werden, als die, die im Stundenplan
     * angezeigt werden.
     *
     * @param lvs Die jeweilige Lehrveranstaltung, auf dessen Grundlage dieses Fenster erzeugt werden soll.
     */
    public LvsGui(
        @NotNull final TimetableGui timetableGui,
        @NotNull final LVS lvs,
        @NotNull final String username
    ) {
        super("", WIDTH, HEIGHT);
        super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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

        super.setTitle(lvsName);

        final JButton colorButton = new JButton();
        colorButton.setBounds(100, 90, 50, 30);
        colorButton.setBackground(lvs.getColor());
        colorButton.setFocusable(false);
        colorButton.addActionListener(e -> openColorPicker(colorButton));

        final JButton saveButton = new JButton("Speichern");
        saveButton.setBounds(WIDTH / 2 - 50, HEIGHT - 80, 100, 30);
        saveButton.setBackground(Color.LIGHT_GRAY);
        saveButton.setFocusable(false);
        saveButton.addActionListener(e -> {
            super.dispose();
            save();
        });

        super.add(colorButton);
        super.add(saveButton);
    }
    //</editor-fold>


    /**
     * Öffnet einen {@link ColorPicker} für diese Lehrveranstaltung, mit dem sich die Hintergrundfarbe individualisieren
     * lässt.
     *
     * @param colorButton Der Button, von dem aus dieser Color-Picker geöffnet wurde. Dieser Button wird nach Auswahl
     *                    der Farbe in derselben eingefärbt.
     */
    private void openColorPicker(@NotNull final JButton colorButton) {
        final JFrame frame = new JFrame();
        frame.setBounds(0, 0, COLOR_PICKER_WIDTH, COLOR_PICKER_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        final ColorPicker colorPicker = new ColorPicker(true, true, Locale.GERMAN);
        colorPicker.setColor(lvs.getColor());
        colorPicker.addColorListener(colorModel -> {
            final Color color = colorModel.getColor();

            colorButton.setBackground(color);
            lvs.setColor(color);
        });

        frame.add(colorPicker);
        frame.setVisible(true);
    }

    /**
     * Speichert alle Einstellungen in diesem {@link LvsGui} ab und lädt die aktuelle Woche neu.
     */
    private void save() {
        UserHandler.saveConfiguration(username, "color." + lvsName, String.valueOf(lvs.getColor().getRGB()));

        timetableGui.loadWeek(0);
    }

    //<editor-fold desc="implementation">

    @Override
    public void draw(@NotNull Graphics2D g) {
        g.drawString("LVS: " + lvsName, 20, 20);
        g.drawString("Modul: " + module, 20, 40);
        g.drawString("Raum: " + room, 20, 60);
        g.drawString("DozentIn: " + docentName, 20, 80);
        g.drawString("LVS-Farbe: ", 20, 108);
    }
    //</editor-fold>
}
