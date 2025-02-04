package de.gemuesehasser.hspv.constant;

import de.gemuesehasser.hspv.Timetable;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;

/**
 * Ein {@link FileType Typ} beschreibt eine Datei, die im Cache-Ordner dieser Anwendung gespeichert wird. Beim Erzeugen
 * dieses Typs, wird überprüft, ob diese Datei bereits existiert. Wenn dies nicht der Fall ist, wird diese Datei neu
 * erstellt.
 */
@Getter
public enum FileType {

    //<editor-fold desc="VALUES">
    /** Die Datei, in welcher alle Benutzerdaten gespeichert werden. */
    DATA_FILE("data.properties"),
    /** Die Datei, in welcher die Konfigurationen aller Benutzer gespeichert werden. */
    CONFIG_FILE("config.properties");
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Die Datei, welche mithilfe dieses Typen generiert wird. */
    private final File file;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt einen neuen und vollständig unabhängigen {@link FileType Typen}. Ein {@link FileType Typ} beschreibt eine
     * Datei, die im Cache-Ordner dieser Anwendung gespeichert wird. Beim Erzeugen dieses Typs, wird überprüft, ob diese
     * Datei bereits existiert. Wenn dies nicht der Fall ist, wird diese Datei neu erstellt.
     *
     * @param fileName Der Name der Datei, die mithilfe dieses Typen generiert wird.
     */
    @SneakyThrows
    FileType(String fileName) {
        this.file = new File(Timetable.CACHE_FOLDER + File.separator + fileName);

        if (!file.createNewFile()) return;
        System.out.println("File created: " + file.getAbsolutePath());
    }
    //</editor-fold>

}
