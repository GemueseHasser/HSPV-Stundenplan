package de.gemuesehasser.hspv.timetable;

import de.gemuesehasser.hspv.timetable.object.LVS;
import de.gemuesehasser.hspv.timetable.object.gui.LoginGui;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

/**
 * Die Haupt- und Main-Klasse der HSPV-Timetable Anwendung.
 */
public class HspvTimetable {

    //<editor-fold desc="STATIC FIELDS">
    /** Alle geladenen Lehrveranstaltungen aus der aktuellen ICS-Datei. */
    @Getter
    @Setter
    private static LinkedHashMap<LocalDateTime, LVS> lvsMap;
    //</editor-fold>


    //<editor-fold desc="main">

    /**
     * Die Main-Methode der Anwendung, welche als Erstes von der JRE aufgerufen wird.
     *
     * @param args Die Argumente, die von der JRE beim Ausführen dieser Main-Methode übergeben werden.
     */
    public static void main(@NotNull final String @NotNull [] args) {
        final LoginGui loginGui = new LoginGui(false);
        loginGui.open();
    }
    //</editor-fold>

}
