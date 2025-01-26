package de.gemuesehasser.hspv.object.gui;

import de.gemuesehasser.hspv.object.LVS;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import java.awt.*;

/**
 * Ein {@link LvsGui} stellt eine Instanz eines {@link Gui Fensters} dar, in welchem für eine bestimmte
 * Lehrveranstaltung genauere Daten angezeigt werden, als die, die im Stundenplan angezeigt werden.
 */
public final class LvsGui extends Gui {

    //<editor-fold desc="CONSTANTS">
    /** Die Breite dieses Fensters. */
    private static final int WIDTH = 450;
    /** Die Höhe dieses Fensters. */
    private static final int HEIGHT = 200;
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
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
    public LvsGui(@NotNull final LVS lvs) {
        super("", WIDTH, HEIGHT);
        super.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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
    }
    //</editor-fold>


    //<editor-fold desc="implementation">

    @Override
    public void draw(@NotNull Graphics2D g) {
        g.drawString("LVS: " + lvsName, 20, 20);
        g.drawString("Modul: " + module, 20, 40);
        g.drawString("Raum: " + room, 20, 60);
        g.drawString("DozentIn: " + docentName, 20, 80);
    }
    //</editor-fold>
}
