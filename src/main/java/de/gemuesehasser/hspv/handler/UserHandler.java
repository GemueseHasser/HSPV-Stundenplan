package de.gemuesehasser.hspv.handler;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Mithilfe des {@link UserHandler} lässt sich ein bestimmter Benutzer und dessen Stundenplan laden, abspeichern und
 * überprüfen, ob dieser existiert.
 */
public final class UserHandler {

    //<editor-fold desc="CONSTANTS">
    /** Der Cache-Ordner dieser Anwendung, in welcher die Benutzer-Dateien gespeichert werden. */
    private static final String CACHE_FOLDER = System.getProperty("user.home") + File.separator + ".timetable_cache";
    //</editor-fold>


    //<editor-fold desc="utility">

    /**
     * Speichert einen bestimmten Stundenplan ab.
     *
     * @param username Der Benutzername des Nutzers, dessen Stundenplan abgespeichert werden soll.
     * @param content  Der Inhalt des Stundenplans in ICS-Syntax.
     */
    public static void saveTimetable(@NotNull final String username, @NotNull final String content) throws IOException {
        final File file = new File(CACHE_FOLDER + File.separator + "stundenplan_" + username + ".ics");
        if (file.delete()) {
            file.createNewFile();
        }
        FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
    }

    /**
     * Prüft, ob ein Stundenplan für einen bestimmten Benutzer bereits lokal existiert.
     *
     * @param username Der Benutzername, für den überprüft werden soll, ob bereits einen lokal abgespeicherter Stundenplan existiert.
     * @return Wenn ein Stundenplan für einen bestimmten Benutzer bereits lokal existiert {@code true}, ansonsten {@code false}.
     */
    public static boolean exists(@NotNull final String username) {
        return Files.exists(Path.of(CACHE_FOLDER + File.separator + "stundenplan_" + username + ".ics"));
    }

    /**
     * Gibt die Datei des Stundenplans eines bestimmten Benutzers zurück.
     *
     * @param username Der Benutzername des Nutzers, dessen Stundenplan zurückgegeben werden soll.
     * @return Die Datei des Stundenplans eines bestimmten Benutzers.
     */
    public static File getTimetable(@NotNull final String username) {
        return new File(CACHE_FOLDER + File.separator + "stundenplan_" + username + ".ics");
    }
    //</editor-fold>

}
