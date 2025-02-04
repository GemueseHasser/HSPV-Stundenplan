package de.gemuesehasser.hspv;

import de.gemuesehasser.hspv.object.LVS;
import de.gemuesehasser.hspv.object.gui.LoginGui;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

/**
 * Die Haupt- und Main-Klasse der HSPV-Stundenplan Anwendung.
 */
public class Timetable {

    //<editor-fold desc="CONSTANTS">
    /** Der Cache-Ordner dieser Anwendung, in welcher die Benutzer-Dateien gespeichert werden. */
    public static final String CACHE_FOLDER = System.getProperty("user.home") + File.separator + ".stundenplan_cache";
    //</editor-fold>

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
    public static void main(@NotNull final String @NotNull [] args) throws IOException {
        Files.createDirectories(Path.of(CACHE_FOLDER));

        final LoginGui loginGui = new LoginGui(false);
        loginGui.open();
    }
    //</editor-fold>

}
