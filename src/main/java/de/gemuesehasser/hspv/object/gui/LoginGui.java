package de.gemuesehasser.hspv.object.gui;

import de.gemuesehasser.hspv.Timetable;
import de.gemuesehasser.hspv.constant.PropertyType;
import de.gemuesehasser.hspv.handler.ICalHandler;
import de.gemuesehasser.hspv.handler.PasswordHandler;
import de.gemuesehasser.hspv.handler.UserHandler;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
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
    private static final String LAST_LOGIN_PROPERTY = "lastLoginName";
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Das Textfeld, in welchem der Benutzername eingegeben werden soll. */
    private final JTextField usernameField = new JTextField();
    /** Das Textfeld, in welchem das Passwort eingegeben werden soll. */
    private final JPasswordField passwordField = new JPasswordField();
    /** Die {@link LoadingGui Lade-Animation}, die angezeigt wird, sobald die Benutzerdaten abgeschickt wurden. */
    private final LoadingGui loadingGui = new LoadingGui();
    /** Das HSPV-Logo, welches im Login-Bereich angezeigt wird. */
    private BufferedImage hspvLogo;
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

        try {
            hspvLogo = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/hspv.png")));
        } catch (@NotNull final IOException ignored) {
            hspvLogo = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D logoGraphics = (Graphics2D) hspvLogo.getGraphics();
            logoGraphics.setColor(Color.BLACK);
            logoGraphics.fillRect(0, 0, 30, 30);
            logoGraphics.dispose();
        }

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

        Executors.newScheduledThreadPool(1).schedule(this::loginAttemp, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Startet ausschließlich den Online-Loginversuch mit den eingegebenen Benutzerdaten.
     */
    private void loginAttemp() {
        final StringBuilder passwordBuilder = new StringBuilder();
        for (final char pwChar : passwordField.getPassword()) {
            passwordBuilder.append(pwChar);
        }

        final String username = usernameField.getText();
        final String passwordText = passwordBuilder.toString();

        final ICalHandler iCalHandler = new ICalHandler(username, passwordText);
        final int icalReturnCode = iCalHandler.loadICalFile();

        if (icalReturnCode == ICalHandler.WRONG_LOGIN) {
            loadingGui.dispose();
            wrongLoginData();
            return;
        }

        if (icalReturnCode == ICalHandler.NO_CONNECTION_ERROR) {
            loadingGui.dispose();
            if (!noInternetConnection(username, passwordText)) return;
        }

        // set timetable
        Timetable.setLvsMap(iCalHandler.getLvs());

        final TimetableGui timetableGui = new TimetableGui(username, loadingGui);
        timetableGui.open();

        // save password
        PasswordHandler.saveHashedPassword(username, passwordText);
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
    private boolean noInternetConnection(@NotNull final String username, @NotNull final String passwordText) {
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
    public void draw(@NotNull Graphics2D g) {
        g.drawImage(hspvLogo, -10, 0, LOGO_WIDTH, LOGO_HEIGHT, null);

        g.drawString("Benutzername", WIDTH / 2 - TEXT_FIELD_WIDTH / 2, 190);
        g.drawString("Passwort", WIDTH / 2 - TEXT_FIELD_WIDTH / 2, 290);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER) return;

        login();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
    //</editor-fold>
}
