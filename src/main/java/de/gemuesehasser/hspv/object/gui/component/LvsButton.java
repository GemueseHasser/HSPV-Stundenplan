package de.gemuesehasser.hspv.object.gui.component;

import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

/**
 * Ein {@link LvsButton} stellt eine Instanz eines {@link JButton} dar, welcher explizit auf das Darstellen einer
 * Lehrveranstaltung zugeschnitten ist. Dabei sind die Ecken des Buttons abgerundet.
 */
public final class LvsButton extends JButton {

    //<editor-fold desc="LOCAL FIELDS">
    /** Die Größe der abgerundeten Ecken dieses Buttons. */
    private final int rounding;
    /** Die Form dieses Buttons. */
    private Shape shape;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt auf der Grundlage eines {@link JButton} einen neuen {@link LvsButton}, welcher explizit auf das
     * Darstellen einer Lehrveranstaltung zugeschnitten ist. Dabei sind die Ecken des Buttons abgerundet.
     *
     * @param text     Der Text, der auf diesem Button angezeigt werden soll.
     * @param rounding Die Größe der abgerundeten Ecken dieses Buttons.
     */
    public LvsButton(final String text, final int rounding) {
        super(text);
        this.rounding = rounding;

        super.setContentAreaFilled(false);
        super.setFocusable(false);
    }
    //</editor-fold>


    //<editor-fold desc="implementation">
    @Override
    protected void paintComponent(@NotNull final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);


        g2d.setColor(getModel().isArmed() ? Color.orange : getBackground());
        g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, rounding, rounding));

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(@NotNull final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setColor(getBackground().darker());
        g2d.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, rounding, rounding));
    }

    @Override
    public boolean contains(final int x, final int y) {
        if (shape == null || !shape.getBounds().equals(getBounds())) {
            this.shape = new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), rounding, rounding);
        }

        return shape.contains(x, y);
    }
    //</editor-fold>

}
