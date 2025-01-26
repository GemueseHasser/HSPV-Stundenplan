package de.gemuesehasser.hspv.timetable.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

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
    //</editor-fold>
}
