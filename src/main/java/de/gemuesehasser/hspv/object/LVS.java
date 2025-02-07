package de.gemuesehasser.hspv.object;

import de.gemuesehasser.hspv.handler.UserHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Eine {@link LVS Lehrveranstaltung} besteht aus einem Start- und End-Datum/Zeitpunkt und einer jeweiligen Beschreibung
 * der Veranstaltung.
 */
@Getter
@RequiredArgsConstructor
public final class LVS {

    //<editor-fold desc="LOCAL FIELDS">
    /** Das Startdatum/Zeitpunkt der Veranstaltung. */
    @NotNull
    private final LocalDateTime start;
    /** Das Enddatum/Zeitpunkt der Veranstaltung. */
    @Nullable
    private final LocalDateTime end;
    /** Die Beschreibung der Veranstaltung. */
    @Nullable
    private final String description;
    /** Die Farbe dieser Lehrveranstaltung. */
    @Setter
    @NotNull
    private Color color = getRandomColor();
    //</editor-fold>


    /**
     * Gibt eine zufällige, aber schlichte Farbe zurück, mit der die Lehrveranstaltung initialisiert wird, wenn der
     * Nutzer keine eigene Farbe festgelegt hat.
     *
     * @return Eine zufällige, aber schlichte Farbe, mit der die Lehrveranstaltung initialisiert wird, wenn der Nutzer
     *     keine eigene Farbe festgelegt hat.
     */
    private Color getRandomColor() {
        final Random random = new Random();

        final int colorChoice = random.nextInt(3);

        final int lowest = 190;
        final int highest = 250;

        int r = (colorChoice == 0) ? 255 : random.nextInt(highest - lowest + 1) + lowest;
        int g = (colorChoice == 1) ? 255 : random.nextInt(highest - lowest + 1) + lowest;
        int b = (colorChoice == 2) ? 255 : random.nextInt(highest - lowest + 1) + lowest;

        return new Color(r, g, b);
    }
}
