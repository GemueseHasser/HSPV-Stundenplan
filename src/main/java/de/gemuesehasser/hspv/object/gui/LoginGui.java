package de.gemuesehasser.hspv.object.gui;

import de.gemuesehasser.hspv.Timetable;
import de.gemuesehasser.hspv.constant.ImageType;
import de.gemuesehasser.hspv.constant.PropertyType;
import de.gemuesehasser.hspv.handler.ICalHandler;
import de.gemuesehasser.hspv.handler.PasswordHandler;
import de.gemuesehasser.hspv.handler.UserHandler;
import de.gemuesehasser.hspv.object.Gui;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Ein {@link LoginGui} bietet dem Nutzer eine grafische Oberfläche, auf der dieser seinen Benutzernamen und sein
 * Passwort eingeben kann. Von dem diesem Fenster aus wird dann beim Abschicken der Benutzerdaten das {@link LoadingGui}
 * geöffnet und sobald die nötige Datei geladen wurde, wird das {@link TimetableGui} geöffnet.
 */
public final class LoginGui extends Gui implements KeyListener {

    //<editor-fold desc="CONSTANTS">
    /** Der Titel dieses Fensters. */
    @NotNull
    private static final String TITLE = "HSPV-Stundenplan";
    /** Die Breite dieses Fensters. */
    private static final int WIDTH = 300;
    /** Die Höhe dieses Fensters. */
    private static final int HEIGHT = 500;
    /** Die Breite der Eingabefelder für die Benutzerdaten. */
    private static final int TEXT_FIELD_WIDTH = 150;
    /** Die Höhe der Eingabefelder für die Benutzerdaten. */
    private static final int TEXT_FIELD_HEIGHT = 50;
    /** Die Breite des HSPV-Logos. */
    private static final int LOGO_WIDTH = 300;
    /** Die Höhe des HSPV-Logos. */
    private static final int LOGO_HEIGHT = 200;
    /** Der Name der Eigenschaft, unter der der letzte Benutzername abgespeichert wird, der sich eingeloggt hat. */
    @NotNull
    private static final String LAST_LOGIN_PROPERTY = "lastLoginName";
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Das Textfeld, in welchem der Benutzername eingegeben werden soll. */
    @NotNull
    private final JTextField usernameField = new JTextField();
    /** Das Textfeld, in welchem das Passwort eingegeben werden soll. */
    @NotNull
    private final JPasswordField passwordField = new JPasswordField();
    /** Die {@link LoadingGui Lade-Animation}, die angezeigt wird, sobald die Benutzerdaten abgeschickt wurden. */
    @NotNull
    private final LoadingGui loadingGui = new LoadingGui();
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue Instanz eines {@link LoginGui}, welches eine Instanz eines {@link Gui} darstellt. Ein
     * {@link LoginGui} bietet dem Nutzer eine grafische Oberfläche, auf der dieser seinen Benutzernamen und sein
     * Passwort eingeben kann. Von dem diesem Fenster aus wird dann beim Abschicken der Benutzerdaten das
     * {@link LoadingGui} geöffnet und sobald die nötige Datei geladen wurde, wird das {@link TimetableGui} geöffnet.
     *
     * @param error Der Zustand, ob zuvor bereits ein fehlerhafter Login vorgelegen hat.
     */
    public LoginGui(final boolean error) {
        super((error ? "Fehlerhafter Login" : TITLE), WIDTH, HEIGHT);
        super.addKeyListener(this);

        usernameField.setBounds(WIDTH / 2 - TEXT_FIELD_WIDTH / 2, 200, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
        usernameField.addKeyListener(this);
        usernameField.setText(PropertyType.DATA_FILE.getProperties().getProperty(LAST_LOGIN_PROPERTY));
        usernameField.selectAll();

        passwordField.setBounds(WIDTH / 2 - TEXT_FIELD_WIDTH / 2, 300, TEXT_FIELD_WIDTH, TEXT_FIELD_HEIGHT);
        passwordField.addKeyListener(this);
        passwordField.setEchoChar('*');

        final JButton loginButton = new JButton("Anmelden");
        loginButton.setBounds(
            WIDTH / 2 - (TEXT_FIELD_WIDTH - 30) / 2,
            370,
            TEXT_FIELD_WIDTH - 30,
            TEXT_FIELD_HEIGHT - 20
        );
        loginButton.addActionListener(e -> login());
        loginButton.setFocusable(false);

        super.add(usernameField);
        super.add(passwordField);
        super.add(loginButton);
    }
    //</editor-fold>


    /**
     * Leitet den gesamten Login-Prozess ein.
     */
    private void login() {
        loadingGui.open();
        super.dispose();

        final PropertyType dataType = PropertyType.DATA_FILE;
        dataType.getProperties().setProperty(LAST_LOGIN_PROPERTY, usernameField.getText());
        dataType.saveProperties();

        Executors.newScheduledThreadPool(1).schedule(this::loginAttemp, 80, TimeUnit.MILLISECONDS);
    }

    /**
     * Startet ausschließlich den Online-Loginversuch mit den eingegebenen Benutzerdaten.
     */
    private void loginAttemp() {
        final StringBuilder passwordBuilder = new StringBuilder();
        for (final char pwChar : passwordField.getPassword()) {
            passwordBuilder.append(pwChar);
        }

        final String username = usernameField.getText().replaceAll("\\s", "");
        final String passwordText = passwordBuilder.toString();

        final ICalHandler iCalHandler = new ICalHandler(username, passwordText);
        final int icalReturnCode = iCalHandler.loadICalFile();

        if (icalReturnCode == ICalHandler.NEW_USER_LOGIN) {
            PasswordHandler.saveHashedPassword(username, passwordText);
        }

        if (icalReturnCode == ICalHandler.WRONG_LOGIN) {
            loadingGui.dispose();
            wrongLoginData();
            return;
        }

        if (icalReturnCode == ICalHandler.NO_CONNECTION_ERROR) {
            loadingGui.dispose();
            if (!noInternetConnection(username, passwordText)) return;
        }

        if (!PasswordHandler.validatePassword(username, passwordText)) {
            loadingGui.dispose();
            wrongLoginData();
            return;
        }

        // set timetable
        Timetable.setLvsMap(iCalHandler.getLvs());

        final TimetableGui timetableGui = new TimetableGui(username, loadingGui);
        timetableGui.open();

        // save password
        PasswordHandler.saveHashedPassword(username, passwordText);

        if (icalReturnCode == ICalHandler.LOCAL_CALENDAR_BUILT) {
            timetableGui.setTitle(timetableGui.getTitle() + " lokal");

            if (!iCalHandler.loadAntragoTimetable()) System.exit(0);
            Timetable.setLvsMap(iCalHandler.getLvs());

            timetableGui.loadWeek(0);
            timetableGui.setTitle(timetableGui.getTitle().replaceAll("lokal", "aktualisiert"));
        }
    }

    /**
     * Die ausgelagerte Aktion, die ausgeführt wird, wenn der Login wegen falscher Anmeldedaten fehlschlägt.
     */
    private void wrongLoginData() {
        new LoginGui(true).open();

        JOptionPane.showMessageDialog(
            null,
            "Fehlerhafte Anmeldedaten.",
            "Login fehlgeschlagen",
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Die ausgelagerte Aktion, die ausgeführt wird, wenn keine Internetverbindung hergestellt werden kann.
     *
     * @param username     Der Benutzername des Nutzers.
     * @param passwordText Das Passwort des Nutzers.
     *
     * @return Wenn die Methode frühzeitig abgebrochen wurde {@code false} und bei vollständiger Ausführung
     *     {@code true}.
     */
    private boolean noInternetConnection(
        @NotNull final String username,
        @NotNull final String passwordText
    ) {
        if (!UserHandler.exists(username)) {
            new LoginGui(true).open();

            JOptionPane.showMessageDialog(
                null,
                "Es konnte keine Verbindung hergestellt werden.",
                "Login fehlgeschlagen",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
        }

        if (!PasswordHandler.validatePassword(username, passwordText)) {
            wrongLoginData();
            return false;
        }

        JOptionPane.showMessageDialog(
            null,
            "<html>Es konnte keine Verbindung hergestellt werden. <br>" +
                "Es wird eine bereits heruntergeladene Version des Stundenplans für den aktuellen Benutzer geladen.",
            "Keine Verbindung zum Server",
            JOptionPane.ERROR_MESSAGE
        );

        return true;
    }

    //<editor-fold desc="implementation">

    @Override
    public void draw(@NotNull final Graphics2D g) {
        g.setFont(Timetable.DEFAULT_FONT);
        g.drawImage(ImageType.HSPV_LOGO.getImage(), -10, 0, LOGO_WIDTH, LOGO_HEIGHT, null);

        g.drawString("Benutzername", WIDTH / 2 - TEXT_FIELD_WIDTH / 2, 190);
        g.drawString("Passwort", WIDTH / 2 - TEXT_FIELD_WIDTH / 2, 290);
    }

    @Override
    public void keyTyped(@NotNull final KeyEvent e) {

    }

    @Override
    public void keyPressed(@NotNull final KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER) return;

        login();
    }

    @Override
    public void keyReleased(@NotNull final KeyEvent e) {

    }
    //</editor-fold>
}
