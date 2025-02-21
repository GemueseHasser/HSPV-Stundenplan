package de.gemuesehasser.hspv.constant;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@Getter
public enum ImageType {

    HSPV_LOGO("hspv.png"),
    LOADING_IMAGE("loading.png"),
    LVS_BACKGROUND("lvs_background.jpg");


    private BufferedImage image;


    ImageType(@NotNull final String name) {
        try {
            this.image = ImageIO.read(Objects.requireNonNull(getClass().getResource("/" + name)));
        } catch (@NotNull final IOException e) {
            System.out.println("failed to load image " + name);
        }
    }

}
