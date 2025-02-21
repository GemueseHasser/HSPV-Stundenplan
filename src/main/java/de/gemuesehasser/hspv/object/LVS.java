package de.gemuesehasser.hspv.object;

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

    //<editor-fold desc="CONSTANTS">
    /** Der höchste zufällig generierte Wert der Farbe Rot / Grün / Blau. */
    private static final int HIGHEST_COLOR_VALUE = 250;
    /** Der niedrigste zufällig generierte Wert der Farbe Rot / Grün / Blau. */
    private static final int LOWEST_COLOR_VALUE = 115;
    //</editor-fold>


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


    //<editor-fold desc="utility">

    /**
     * Gibt eine zufällige, aber schlichte Farbe zurück, mit der die Lehrveranstaltung initialisiert wird, wenn der
     * Nutzer keine eigene Farbe festgelegt hat.
     *
     * @return Eine zufällige, aber schlichte Farbe, mit der die Lehrveranstaltung initialisiert wird, wenn der Nutzer
     *     keine eigene Farbe festgelegt hat.
     */
    @NotNull
    private static Color getRandomColor() {
        final Random random = new Random();
        final int colorChoice = random.nextInt(3);

        final int r = (colorChoice == 0) ? 255 : random.nextInt(HIGHEST_COLOR_VALUE - LOWEST_COLOR_VALUE + 1) + LOWEST_COLOR_VALUE;
        final int g = (colorChoice == 1) ? 255 : random.nextInt(HIGHEST_COLOR_VALUE - LOWEST_COLOR_VALUE + 1) + LOWEST_COLOR_VALUE;
        final int b = (colorChoice == 2) ? 255 : random.nextInt(HIGHEST_COLOR_VALUE - LOWEST_COLOR_VALUE + 1) + LOWEST_COLOR_VALUE;

        return new Color(r, g, b);
    }
    //</editor-fold>
}
