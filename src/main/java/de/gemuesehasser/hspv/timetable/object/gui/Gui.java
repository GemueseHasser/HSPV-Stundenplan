package de.gemuesehasser.hspv.timetable.object.gui;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.swing.*;
import java.awt.*;

/**
 * Ein {@link Gui} stellt die Super-Instanz eines Fensters dar, also die Grundlage, welche genutzt werden kann, um Fenster
 * in einheitlichem Format bzw. mit einheitlichen Eigenschaften zu erzeugen.
 */
@Getter
public abstract class Gui extends JFrame {

    //<editor-fold desc="LOCAL FIELDS">
    /** Das Zeichenobjekt, welches genutzt wird, um Grafiken auf dieses Fenster zu zeichnen. */
    private final Draw draw = new Draw();
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link Gui} auf der Grundlage eines {@link JFrame}. Ein {@link Gui} stellt die Super-Instanz eines Fensters dar, also die Grundlage, welche genutzt werden kann, um Fenster
     * in einheitlichem Format bzw. mit einheitlichen Eigenschaften zu erzeugen.
     *
     * @param title  Der Titel, den dieses Fenster haben soll.
     * @param width  Die Breite dieses Fensters.
     * @param height Die Höhe dieses Fensters.
     */
    public Gui(
            @NotNull final String title,
            @Range(from = 0, to = Integer.MAX_VALUE) final int width,
            @Range(from = 0, to = Integer.MAX_VALUE) final int height
    ) {
        super(title);
        super.setBounds(0, 0, width, height);
        super.setLocationRelativeTo(null);
        super.setLayout(null);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(false);

        draw.setBounds(0, 0, width, height);
    }
    //</editor-fold>


    /**
     * Fügt die aktuelle Instanz des Zeichen-Objekts hinzu und öffnet das Fenster.
     */
    public void open() {
        super.add(draw);
        super.setVisible(true);
    }

    /**
     * Übergibt ein {@link Graphics2D}, womit sich auf das Fenster zeichnen lässt.
     *
     * @param g Das {@link Graphics2D}, womit man auf das Fenster zeichnen kann.
     */
    public abstract void draw(@NotNull final Graphics2D g);


    /**
     * Die Zeichen-Instanz dieses Fensters, womit man auf dieses Fenster zeichnen kann.
     */
    private final class Draw extends JLabel {

        //<editor-fold desc="implementation">
        @Override
        protected void paintComponent(@NotNull final Graphics g) {
            super.paintComponent(g);

            draw((Graphics2D) g);
        }
        //</editor-fold>

    }

}
