package de.gemuesehasser.hspv.constant;

import de.gemuesehasser.hspv.Timetable;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.Properties;

/**
 * Ein {@link PropertyType Typ} beschreibt eine Datei, die im Cache-Ordner dieser Anwendung gespeichert wird. Beim
 * Erzeugen dieses Typs, wird überprüft, ob diese Datei bereits existiert. Wenn dies nicht der Fall ist, wird diese
 * Datei neu erstellt.
 */
public enum PropertyType {

    //<editor-fold desc="VALUES">
    /** Die Datei, in welcher alle Benutzerdaten gespeichert werden. */
    DATA_FILE("data.properties"),
    /** Die Datei, in welcher die Konfigurationen aller Benutzer gespeichert werden. */
    CONFIG_FILE("config.properties");
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Die {@link Properties}, die auf Grundlage dieses Typen erzeugt werden im Cache-Ordner dieser Anwendung. */
    @Getter
    @NotNull
    private final Properties properties = new Properties();
    /** Die Datei, welche mithilfe dieses Typen generiert wird. */
    @NotNull
    private final File file;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt einen neuen und vollständig unabhängigen {@link PropertyType Typen}. Ein {@link PropertyType Typ}
     * beschreibt eine Datei, die im Cache-Ordner dieser Anwendung gespeichert wird. Beim Erzeugen dieses Typs, wird
     * überprüft, ob diese Datei bereits existiert. Wenn dies nicht der Fall ist, wird diese Datei neu erstellt.
     *
     * @param fileName Der Name der Datei, die mithilfe dieses Typen generiert wird.
     */
    @SneakyThrows
    PropertyType(@NotNull final String fileName) {
        this.file = new File(Timetable.CACHE_FOLDER + File.separator + fileName);

        if (file.createNewFile()) {
            System.out.println("File created: " + file.getAbsolutePath());
        }

        this.properties.load(Files.newInputStream(file.toPath()));
    }
    //</editor-fold>


    /**
     * Speichert die {@link Properties}, die auf der Grundlage dieses Typen erzeugt wurden im Cache-Ordner dieser
     * Anwendung ab.
     */
    @SneakyThrows
    public void saveProperties() {
        this.properties.store(Files.newOutputStream(file.toPath()), null);
    }

}
