package de.gemuesehasser.hspv.handler;

import de.gemuesehasser.hspv.constant.FileType;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Properties;

/**
 * Der {@link PasswordHandler} bietet utility-Methoden an, um ein Passwort mit einem bereits erzeugten Hash für das
 * Passwort eines Nutzers zu vergleichen. Damit kann dann festgestellt werden, ob der Nutzer das richtige Passwort
 * eingegeben hat. Alternativ lässt sich auch ein Passwort in Form eines Hash abspeichern. Bei jedem Mal, wenn ein
 * Passwort abgespeichert wird - egal ob eine Verbindung zum Internet besteht oder nicht - wird ein neues Salz genutzt,
 * wodurch sich der Hash permanent ändert.
 */
public final class PasswordHandler {

    //<editor-fold desc="CONSTANTS">
    /** Der Algorithmus, mit dem der hash für das jeweilige Passwort erzeugt wird. */
    private static final String HASH_ALGORITHM = "PBKDF2WithHmacSHA1";
    /** Die Anzahl an Iterationen, die der Algorithmus läuft, bis der Hash vollständig erzeugt wird. */
    private static final int HASH_ITERATIONS = 65536;
    /** Die Länge des Hash. */
    private static final int KEY_LENGTH = 256;
    /** Die Länge des Salzes, welches dem Hash hinzugefügt wird, um den Hash noch sicherer zu gestalten. */
    private static final int SALT_LENGTH = 16;
    //</editor-fold>


    //<editor-fold desc="utility">

    /**
     * Überprüft, ob das eingegebene Passwort mit dem Passwort übereinstimmt, welches als Hash für den Benutzer
     * abgespeichert wurde.
     *
     * @param username Der Benutzername des Nutzers, für den das Passwort überprüft werden soll.
     * @param password Das Passwort, welches mit einem bereits gespeicherten Passwort verglichen werden soll.
     *
     * @return Wenn das eingegebene Passwort mit dem bereits gespeicherten Passwort übereinstimmt {@code true},
     *     ansonsten {@code false} (auch wenn noch kein Passwort für den Benutzer gespeichert wurde).
     */
    @SneakyThrows
    public static boolean validatePassword(@NotNull final String username, @NotNull final String password) {
        final Properties data = new Properties();
        data.load(new FileInputStream(FileType.DATA_FILE.getFile()));

        if (!data.containsKey(username)) return false;

        final String[] userData = data.getProperty(username).split(":");
        final byte[] salt = Base64.getDecoder().decode(userData[0]);


        final String currentPasswordHash = getHashedPassword(password, salt);
        final String storedPasswordHash = data.getProperty(username);

        return storedPasswordHash.equals(currentPasswordHash);
    }

    /**
     * Speichert ein Passwort, welches zuvor in einen Hash umgewandelt wurde, für einen bestimmten Nutzer in der
     * Data-Datei ab.
     *
     * @param username Der Benutzername des Nutzers, für den das Passwort als Hash abgespeichert werden soll.
     * @param password Das Passwort, welches in Form eines Hash abgespeichert werden soll.
     */
    @SneakyThrows
    public static void saveHashedPassword(@NotNull final String username, @NotNull final String password) {
        final Properties data = new Properties();
        data.load(new FileInputStream(FileType.DATA_FILE.getFile()));

        data.setProperty(username, getHashedPassword(password, getNewSalt()));
        data.store(new FileOutputStream(FileType.DATA_FILE.getFile()), null);
    }

    /**
     * Gibt einen Text zurück, welcher in zwei Teile aufgeteilt ist: Der Erste Teil des Textes besteht aus dem Salz,
     * welches genutzt wird, um den Hash zu erzeugen und der zweite Teil des Textes besteht aus dem erzeugten Hash.
     *
     * @param password Das Passwort, aus dem der Hash erzeugt werden soll.
     * @param salt     Das jeweilige Salz, welches dazugegeben wird, um den Hash sicherer zu gestalten.
     *
     * @return Ein Text, welcher in zwei Teile aufgeteilt ist: Der Erste Teil des Textes besteht aus dem Salz, welches
     *     genutzt wird, um den Hash zu erzeugen und der zweite Teil des Textes besteht aus dem erzeugten Hash.
     */
    @SneakyThrows
    private static String getHashedPassword(@NotNull final String password, final byte @NotNull [] salt) {
        final KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, HASH_ITERATIONS, KEY_LENGTH);
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(HASH_ALGORITHM);

        final byte[] hash = keyFactory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    /**
     * Generiert ein neues Salz mit einer bestimmten {@value SALT_LENGTH}.
     *
     * @return Ein neues Salz miz einer bestimmten {@value SALT_LENGTH}.
     */
    private static byte[] getNewSalt() {
        final SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        return salt;
    }
    //</editor-fold>

}
