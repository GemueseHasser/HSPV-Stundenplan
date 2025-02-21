package de.gemuesehasser.hspv.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@RequiredArgsConstructor
public enum ImageType {

    //<editor-fold desc="VALUES">
    /** Der Typ, für das HSPV-Logo. */
    HSPV_LOGO("hspv.png"),
    /** Der Typ, für das Bild, welches die Grundlage für die Lade-Animation darstellt. */
    LOADING_IMAGE("loading.png"),
    /** Der Typ, für das Hintergrundbild des Fensters, in dem die Details für die LVS dargestellt werden. */
    LVS_BACKGROUND("lvs_background.jpg");
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Der Name des Bildes im resources-Ordner. */
    @NotNull
    private final String name;
    /** Das Bild, welches durch diesen Typen erzeugt wird, sobald {@code initImage} aufgerufen wird. */
    @Getter
    private BufferedImage image;
    //</editor-fold>


    /**
     * Initialisiert das Bild dieses Typen auf der Grundlage des Namens, der durch diesen Typen üebrgeben wird.
     */
    public void initImage() {
        try {
            this.image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/" + name)));
        } catch (@NotNull final IOException e) {
            System.out.println("failed to load image " + name);
        }
    }

}
