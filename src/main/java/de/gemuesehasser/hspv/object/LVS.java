package de.gemuesehasser.hspv.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.time.LocalDateTime;

/**
 * Eine {@link LVS Lehrveranstaltung} besteht aus einem Start- und End-Datum/Zeitpunkt und einer jeweiligen Beschreibung
 * der Veranstaltung.
 */
@Getter
@RequiredArgsConstructor
public final class LVS {

    //<editor-fold desc="CONSTANTS">
    /** Die Standard-Farbe einer LVS. */
    public static final Color DEFAULT_COLOR = Color.LIGHT_GRAY;
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
    private Color color = DEFAULT_COLOR;
    //</editor-fold>
}
