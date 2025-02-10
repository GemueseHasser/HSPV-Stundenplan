package de.gemuesehasser.hspv.listener;

import de.gemuesehasser.hspv.object.LVS;
import de.gemuesehasser.hspv.object.gui.component.LvsButton;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Mithilfe des {@link LvsButtonMouseListener} wird jede Maus-Aktion verarbeitet, die mit einem bestimmten
 * {@link LvsButton} zu tun hat.
 */
@RequiredArgsConstructor
public final class LvsButtonMouseListener implements MouseListener {

    //<editor-fold desc="LOCAL FIELDS">
    /** Die Lehrveranstaltung, die durch den jeweiligen {@link LvsButton} abgebildet wird. */
    @NotNull
    private final LVS lvs;
    /** Der LVS-Button, durch den die Lehrveranstaltung abgebildet wird. */
    @NotNull
    private final LvsButton lvsButton;
    //</editor-fold>


    //<editor-fold desc="implementation">

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
        this.lvsButton.setBackground(getHoverColor(this.lvsButton.getBackground()));
    }

    @Override
    public void mouseExited(@NotNull final MouseEvent e) {
        this.lvsButton.setBackground(this.lvs.getColor());
    }
    //</editor-fold>


    //<editor-fold desc="utility">

    /**
     * Gibt auf der Grundlage einer Farbe, eine Farbe zurück, die angezeigt werden soll, wenn man den {@link LvsButton}
     * mit der Maus betritt.
     *
     * @param color Die Farbe, die die Grundlage für die Hover-Farbe bildet.
     *
     * @return Eine Farbe auf der Grundlage einer Farbe, die angezeigt werden soll, wenn man den {@link LvsButton} mit
     *      der Maus betritt.
     */
    @NotNull
    private static Color getHoverColor(@NotNull final Color color) {
        final float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        int rgb = Color.HSBtoRGB(hsb[0], 0.73F, 0.85F);

        return new Color(rgb);
    }
    //</editor-fold>
}
