package de.gemuesehasser.hspv.object.gui.component;

import org.jetbrains.annotations.NotNull;

import javax.swing.JLabel;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.time.LocalDateTime;

/**
 * Mithilfe eines {@link TimelineDraw} wird, kann auf dem {@link de.gemuesehasser.hspv.object.gui.TimetableGui} eine
 * Linie eingezeichnet werden, die die aktuelle Zeit widerspiegelt.
 */
public final class TimelineDraw extends JLabel {

    //<editor-fold desc="implementation">

    @Override
    protected void paintComponent(@NotNull final Graphics g) {
        super.paintComponent(g);

        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // get current local time
        final LocalDateTime currentTime = LocalDateTime.now();

        // display current local time as red line
        final int currentTimeMinuteAddition = (currentTime.getHour() - 8) * 60 + currentTime.getMinute();
        final int lvsAmount = (currentTimeMinuteAddition - ((currentTimeMinuteAddition / 90) * 15)) / 45;
        final int breakAddition = (lvsAmount / 2) * 15 + (lvsAmount / 6 == 1 ? 15 : 0);

        final int currentTimeY = (lvsAmount * 55) + breakAddition + (currentTimeMinuteAddition - (lvsAmount * 45 + breakAddition));

        g2d.setColor(Color.RED);
        g2d.fillRect(40, 30 + currentTimeY, super.getWidth(), 3);

        repaint();
    }
    //</editor-fold>
}
