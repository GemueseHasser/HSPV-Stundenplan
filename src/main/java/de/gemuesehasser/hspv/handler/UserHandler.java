package de.gemuesehasser.hspv.handler;

import de.gemuesehasser.hspv.Timetable;
import de.gemuesehasser.hspv.constant.PropertyType;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

/**
 * Mithilfe des {@link UserHandler} lässt sich ein bestimmter Benutzer und dessen Stundenplan laden, abspeichern und
 * überprüfen, ob dieser existiert.
 */
public final class UserHandler {

    //<editor-fold desc="utility">

    /**
     * Speichert einen bestimmten Stundenplan ab.
     *
     * @param username Der Benutzername des Nutzers, dessen Stundenplan abgespeichert werden soll.
     * @param content  Der Inhalt des Stundenplans in ICS-Syntax.
     */
    public static void saveTimetable(
        @NotNull final String username,
        @NotNull final String content
    ) throws IOException {
        final File file = new File(Timetable.CACHE_FOLDER + File.separator + "stundenplan_" + username + ".ics");
        if (file.delete()) {
            file.createNewFile();
        }
        FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
    }

    /**
     * Prüft, ob ein Stundenplan für einen bestimmten Benutzer bereits lokal existiert.
     *
     * @param username Der Benutzername, für den überprüft werden soll, ob bereits einen lokal abgespeicherter
     *                 Stundenplan existiert.
     *
     * @return Wenn ein Stundenplan für einen bestimmten Benutzer bereits lokal existiert {@code true}, ansonsten
     *     {@code false}.
     */
    public static boolean exists(@NotNull final String username) {
        return Files.exists(new File(Timetable.CACHE_FOLDER + File.separator + "stundenplan_" + username + ".ics").toPath());
    }

    /**
     * Gibt die Datei des Stundenplans eines bestimmten Benutzers zurück.
     *
     * @param username Der Benutzername des Nutzers, dessen Stundenplan zurückgegeben werden soll.
     *
     * @return Die Datei des Stundenplans eines bestimmten Benutzers.
     */
    @NotNull
    public static File getTimetable(@NotNull final String username) {
        return new File(Timetable.CACHE_FOLDER + File.separator + "stundenplan_" + username + ".ics");
    }

    /**
     * Speichert eine gewisse Individualisierung eines Benutzers in der Konfigurationsdatei ab.
     *
     * @param username      Der Benutzername des Nutzers, für den eine gewisse Individualisierung abgespeichert werden
     *                      soll.
     * @param path          Der Pfad, unter dem diese Individualisierung abgespeichert werden soll in der
     *                      Konfigurationsdatei.
     * @param configuration Die Konfiguration in Form eines Textes, die abgespeichert werden soll.
     */
    public static void saveConfiguration(
        @NotNull final String username,
        @NotNull final String path,
        @NotNull final String configuration
    ) {
        final Properties configurations = PropertyType.CONFIG_FILE.getProperties();
        configurations.setProperty(username + "." + path, configuration);
        PropertyType.CONFIG_FILE.saveProperties();
    }

    /**
     * Gibt eine gewisse Individualisierung für einen bestimmten Nutzer aus der Konfigurationsdatei zurück. Falls diese
     * Individualisierung nicht vorhanden ist, wird {@code null} zurückgegeben.
     *
     * @param username Der Benutzername des Nutzers, für den die Individualisierung zurückgegeben werden soll.
     * @param path     Der Pfad unter dem diese Individualisierung in der Konfigurationsdatei zu finden ist.
     *
     * @return eine gewisse Individualisierung für einen bestimmten Nutzer aus der Konfigurationsdatei zurück. Falls
     *     diese Individualisierung nicht vorhanden ist, {@code null}.
     */
    @Nullable
    public static String getConfiguration(
        @NotNull final String username,
        @NotNull final String path
    ) {
        final Properties configurations = PropertyType.CONFIG_FILE.getProperties();
        return configurations.getProperty(username + "." + path);
    }
    //</editor-fold>

}
