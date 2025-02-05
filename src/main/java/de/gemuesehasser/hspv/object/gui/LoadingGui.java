package de.gemuesehasser.hspv.object.gui;

import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;

import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Auf diesem undekorierten Fenster wird ein einfacher Ladebildschirm dargestellt.
 */
public final class LoadingGui extends Gui implements Runnable {

    //<editor-fold desc="CONSTANTS">
    /** Die Breite dieses Ladebildschirms. */
    private static final int WIDTH = 80;
    /** Die Höhe dieses Ladebildschirms. */
    private static final int HEIGHT = 80;
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Der Scheduler, welcher dafür zuständig ist, den Ladebildschirm konstant zu aktualisieren. */
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    /** Die Grafik, welche die Grundlage für diese Lade-Animation darstellt. */
    private BufferedImage waitingImage;
    /** Die aktuelle Gradzahl, um die die Lade-Grafik gedreht ist. */
    private double angle = 0;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link LoadingGui}, welches gleichzeitig eine Instanz eines {@link Gui}
     * darstellt. Auf diesem undekorierten Fenster wird ein einfacher Ladebildschirm dargestellt.
     */
    public LoadingGui() {
        super("", WIDTH, HEIGHT);
        super.setUndecorated(true);
        super.setShape(new RoundRectangle2D.Double(0, 0, WIDTH, HEIGHT, 30, 30));

        try {
            waitingImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/loading.png")));
        } catch (@NotNull final IOException ignored) {
            System.out.println("Loading-Image konnte nicht geladen werden.");
        }
    }
    //</editor-fold>


    /**
     * Gibt die um eine bestimmte Gradzahl gedrehte Lade-Grafik in Form eines formatierten Bildes zurück.
     *
     * @param angle Die Gradzahl, um die die Lade-Grafik gedreht werden soll.
     *
     * @return Die um eine bestimmte Gradzahl gedrehte Lade-Grafik in Form eines formatierten Bildes.
     */
    @NotNull
    private BufferedImage getRotatedImage(Double angle) {
        final double sin = Math.abs(Math.sin(Math.toRadians(angle)));
        final double cos = Math.abs(Math.cos(Math.toRadians(angle)));
        final int w = waitingImage.getWidth();
        final int h = waitingImage.getHeight();
        final int neww = (int) Math.floor(w * cos + h * sin);
        final int newh = (int) Math.floor(h * cos + w * sin);

        final BufferedImage rotated = new BufferedImage(neww, newh, waitingImage.getType());
        final Graphics2D graphic = rotated.createGraphics();
        graphic.translate((neww - w) / 2, (newh - h) / 2);
        graphic.rotate(Math.toRadians(angle), (double) w / 2, (double) h / 2);
        graphic.drawRenderedImage(waitingImage, null);
        graphic.dispose();
        return rotated;
    }

    //<editor-fold desc="implementation">

    @Override
    public void draw(@NotNull Graphics2D g) {
        g.drawImage(getRotatedImage(angle), 0, 0, WIDTH, HEIGHT, null);

        repaint();
    }

    @Override
    public void run() {
        if (angle >= 360) angle = 0;

        angle += 2;
    }

    @Override
    public void open() {
        super.open();

        scheduler.scheduleAtFixedRate(this, 0, 20, TimeUnit.MILLISECONDS);
    }

    @Override
    public void dispose() {
        super.dispose();

        scheduler.shutdownNow();
    }
    //</editor-fold>
}
