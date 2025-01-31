package de.gemuesehasser.hspv.handler;

import de.gemuesehasser.hspv.Timetable;
import org.apache.commons.io.FileUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;
import org.jasypt.properties.EncryptableProperties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Mithilfe des {@link UserHandler} lässt sich ein bestimmter Benutzer und dessen Stundenplan laden, abspeichern und
 * überprüfen, ob dieser existiert.
 */
public final class UserHandler {

    //<editor-fold desc="CONSTANTS">
    /** Das Verschlüsselungspasswort für jede Verschlüsselung. */
    private static final String ENCRYPTOR_PASSWORD = "timetable";
    /** Der Verschlüsselungsalgorithmus, der für die Passwort-Verschlüsselung genutzt wird. */
    private static final String ENCRYPTOR_ALGORITHM = "PBEWithHMACSHA512AndAES_256";
    //</editor-fold>


    //<editor-fold desc="utility">

    /**
     * Speichert einen bestimmten Stundenplan ab.
     *
     * @param username Der Benutzername des Nutzers, dessen Stundenplan abgespeichert werden soll.
     * @param content  Der Inhalt des Stundenplans in ICS-Syntax.
     */
    public static void saveTimetable(@NotNull final String username, @NotNull final String content) throws IOException {
        final File file = new File(Timetable.CACHE_FOLDER + File.separator + "stundenplan_" + username + ".ics");
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
        return Files.exists(Path.of(Timetable.CACHE_FOLDER + File.separator + "stundenplan_" + username + ".ics"));
    }

    /**
     * Gibt die Datei des Stundenplans eines bestimmten Benutzers zurück.
     *
     * @param username Der Benutzername des Nutzers, dessen Stundenplan zurückgegeben werden soll.
     * @return Die Datei des Stundenplans eines bestimmten Benutzers.
     */
    public static File getTimetable(@NotNull final String username) {
        return new File(Timetable.CACHE_FOLDER + File.separator + "stundenplan_" + username + ".ics");
    }

    /**
     * Lädt das verschlüsselte Passwort des Benutzers aus der PROPERTIES-Datei, entschlüsselt das Passwort und gibt
     * dieses zurück.
     *
     * @param username Der Benutzername des Nutzers, dessen Passwort abgefragt wird.
     * @return Das entschlüsselte Passwort des Benutzers, welches zuvor aus der PROPERTIES-Datei geladen wurde.
     */
    @Nullable
    public static String getDecryptedPassword(@NotNull final String username) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(ENCRYPTOR_PASSWORD + username);
        encryptor.setAlgorithm(ENCRYPTOR_ALGORITHM);
        encryptor.setIvGenerator(new RandomIvGenerator());

        final EncryptableProperties properties = new EncryptableProperties(encryptor);
        try {
            properties.load(new FileInputStream(Timetable.CACHE_FOLDER + File.separator + "data.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (properties.containsKey(username)) return encryptor.decrypt(properties.getProperty(username));
        return null;
    }

    /**
     * Verschlüsselt ein bestimmtes Passwort und speichert dieses zusammen mit dem Benutzernamen in der PROPERTIES-Datei ab.
     *
     * @param username Der Benutzername des Nutzers.
     * @param password Das Passwort des Nutzers, das verschlüsselt gespeichert wird.
     */
    public static void savePassword(@NotNull final String username, @NotNull final String password) {
        final StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(ENCRYPTOR_PASSWORD + username);
        encryptor.setAlgorithm(ENCRYPTOR_ALGORITHM);
        encryptor.setIvGenerator(new RandomIvGenerator());

        final EncryptableProperties properties = new EncryptableProperties(encryptor);
        final String dataPath = Timetable.CACHE_FOLDER + File.separator + "data.properties";

        try {
            properties.load(new FileInputStream(dataPath));
            properties.setProperty(username, encryptor.encrypt(password));
            properties.store(new FileOutputStream(dataPath), null);
        } catch (@NotNull final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Speichert eine gewisse Individualisierung eines Benutzers in der Konfigurationsdatei ab.
     *
     * @param username      Der Benutzername des Nutzers, für den eine gewisse Individualisierung abgespeichert werden soll.
     * @param path          Der Pfad, unter dem diese Individualisierung abgespeichert werden soll in der Konfigurationsdatei.
     * @param configuration Die Konfiguration in Form eines Textes, die abgespeichert werden soll.
     */
    public static void saveConfiguration(
            @NotNull final String username,
            @NotNull final String path,
            @NotNull final String configuration
    ) {
        final Properties configurations = new Properties();
        final String configPath = Timetable.CACHE_FOLDER + File.separator + "config.properties";

        try {
            configurations.load(new FileInputStream(configPath));
            configurations.setProperty(username + "." + path, configuration);
            configurations.store(new FileOutputStream(configPath), null);
        } catch (@NotNull final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gibt eine gewisse Individualisierung für einen bestimmten Nutzer aus der Konfigurationsdatei zurück. Falls diese
     * Individualisierung nicht vorhanden ist, wird {@code null} zurückgegeben.
     *
     * @param username Der Benutzername des Nutzers, für den die Individualisierung zurückgegeben werden soll.
     * @param path     Der Pfad unter dem diese Individualisierung in der Konfigurationsdatei zu finden ist.
     *
     * @return eine gewisse Individualisierung für einen bestimmten Nutzer aus der Konfigurationsdatei zurück. Falls diese
     * Individualisierung nicht vorhanden ist, {@code null}.
     */
    @Nullable
    public static String getConfiguration(
            @NotNull final String username,
            @NotNull final String path
    ) {
        final Properties configurations = new Properties();
        final String configPath = Timetable.CACHE_FOLDER + File.separator + "config.properties";

        try {
            configurations.load(new FileInputStream(configPath));
            return configurations.getProperty(username + "." + path);
        } catch (@NotNull final IOException e) {
            return null;
        }
    }
    //</editor-fold>

}
