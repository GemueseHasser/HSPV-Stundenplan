package de.gemuesehasser.hspv.handler;

import de.gemuesehasser.hspv.object.LVS;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.htmlunit.ElementNotFoundException;
import org.htmlunit.WebClient;
import org.htmlunit.WebResponse;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlPage;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Mithilfe dieses Handlers lassen sich alle Lehrveranstaltungen aus einer ICS-Datei laden und in Form einer
 * {@link LinkedHashMap} zurückgeben.
 */
public final class ICalHandler {

    //<editor-fold desc="CONSTANTS">
    /** Der Rückgabewert, wenn es beim Laden keinen Fehlern gab. */
    public static final int NO_ERROR = 0;
    /** Der Rückgabewert, wenn bei der Anmeldung ein Fehler aufgetreten ist. */
    public static final int WRONG_LOGIN = 1;
    /** Der Rückgabewert, wenn keine Verbindung zum Host (Antrago) aufgebaut werden kann. */
    public static final int NO_CONNECTION_ERROR = 2;
    //</editor-fold>


    //<editor-fold desc="LOCAL FIELDS">
    /** Der Benutzername des Nutzers auf der HSPV-Website. */
    private final String username;
    /** Das Passwort des Nutzers auf der HSPV-Website. */
    private final String password;
    /** Der Kalender, welche alle Einträge der ICS-Datei (Kalender-Datei) enthält. */
    private Calendar calendar;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue und vollständig unabhängige Instanz eines {@link ICalHandler}. Mithilfe dieses Handlers lassen
     * sich alle Lehrveranstaltungen aus einer ICS-Datei laden und in Form einer {@link LinkedHashMap} zurückgeben.
     *
     * @param username Der Benutzername des Nutzers auf der HSPV-Website.
     * @param password Das Passwort des Nutzers auf der HSPV-Website.
     */
    public ICalHandler(
        @NotNull final String username,
        @NotNull final String password
    ) {
        this.username = username;
        this.password = password;
    }
    //</editor-fold>


    /**
     * Lädt die Kalender Datei mit den Lehrveranstaltungen des jeweiligen Nutzers von der HSPV-Website.
     *
     * @return Es wird ein ganzzahliger Wert zurückgegeben, welcher den Ablauf des Prozesses repräsentiert.
     */
    public int loadICalFile() {
        try {
            final URL timetableUrlMember = new URL(
                "https://mvc.antrago.hspv.nrw.de/teilnehmerportal/Member/Stundenplan/ExportCalendar/download.ics?quelle=Veranstaltung&dataId=-1&year=2025&month=1"
            );

            final URL timetableUrlLecturer = new URL(
                "https://mvc.antrago.hspv.nrw.de/docentIcsDownloadLink"
            );

            if (!isConnectionPresent()) {
                if (UserHandler.exists(username)) {
                    final FileInputStream calenderInput = new FileInputStream(UserHandler.getTimetable(username));
                    final CalendarBuilder builder = new CalendarBuilder();
                    this.calendar = builder.build(calenderInput);
                    System.out.println("local calender built with local timetable.");
                }

                return NO_CONNECTION_ERROR;
            }

            try (final WebClient webClient = new WebClient()) {
                webClient.getOptions().setThrowExceptionOnScriptError(false);
                webClient.getOptions().setJavaScriptEnabled(false);
                webClient.getOptions().setRedirectEnabled(true);
                webClient.getOptions().setCssEnabled(false);
                webClient.getOptions().setUseInsecureSSL(true);
                webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                webClient.getCookieManager().setCookiesEnabled(true);

                HtmlPage loginPage = webClient.getPage("https://www.hspv.nrw.de/anmelden");
                System.out.println("HSPV-Login-Page opened.");

                HtmlForm form = loginPage.getForms().get(0);
                form.getInputByName("user").type(this.username);
                form.getInputByName("pass").type(this.password);

                form.getInputByName("submit").click();
                System.out.println("User " + username + " logged in.");

                final HtmlPage tools = webClient.getPage("https://www.hspv.nrw.de/webtools");
                System.out.println("webtools opened.");

                try {
                    final HtmlAnchor lvsAnchor = tools.getAnchorByText("Lehrveranstaltungsplan");
                    final HtmlPage antragoPage = lvsAnchor.click();

                    final WebResponse response = webClient.getPage(
                        antragoPage.getUrl().toString().contains("teilnehmerportal") ? timetableUrlMember : timetableUrlLecturer
                    ).getWebResponse();
                    response.getWebRequest().setDefaultResponseContentCharset(StandardCharsets.UTF_8);
                    System.out.println("get response from antrago calender");

                    UserHandler.saveTimetable(username, response.getContentAsString());
                    System.out.println("download completed from antrago.");
                } catch (@NotNull final ElementNotFoundException ignored) {
                    return WRONG_LOGIN;
                }
            }

            final FileInputStream calenderInput = new FileInputStream(UserHandler.getTimetable(username));
            final CalendarBuilder builder = new CalendarBuilder();
            this.calendar = builder.build(calenderInput);
            System.out.println("local calender built.");
        } catch (@NotNull final IOException | ParserException e) {
            throw new RuntimeException(e);
        }

        return NO_ERROR;
    }


    /**
     * Gibt eine {@link LinkedHashMap} mit jeweils der Startzeit der jeweiligen Lehrveranstaltung und des jeweiligen
     * Objekts der Veranstaltung zurück.
     *
     * @return Eine {@link LinkedHashMap} mit jeweils der Startzeit der jeweiligen Lehrveranstaltung und des jeweiligen
     *     Objekts der Veranstaltung.
     */
    @NotNull
    public LinkedHashMap<LocalDateTime, LVS> getLvs() {
        final LinkedHashMap<LocalDateTime, LVS> lvsMap = new LinkedHashMap<>();

        for (@NotNull final CalendarComponent component : calendar.getComponents()) {
            final Optional<Property> startProperty = Optional.ofNullable(component.getProperty("DTSTART"));
            final Optional<Property> endProperty = Optional.ofNullable(component.getProperty("DTEND"));
            final Optional<Property> descriptionProperty = Optional.ofNullable(component.getProperty("DESCRIPTION"));

            if (startProperty.isEmpty() || endProperty.isEmpty() || descriptionProperty.isEmpty()) continue;

            final LocalDateTime startDate = getDateFromString(startProperty.get().getValue());
            final LocalDateTime endDate = getDateFromString(endProperty.get().getValue());
            final String description = descriptionProperty.get().getValue();

            final LVS lvs = new LVS(startDate, endDate, description);
            lvsMap.put(startDate, lvs);
        }

        return lvsMap;
    }

    /**
     * Gibt ein {@link LocalDateTime Datum} auf der Grundlage eines Datums in Form eines {@link String Textes} zurück.
     *
     * @param date Das Datum in {@link String Textform}, welches als {@link LocalDateTime Datum} formatiert
     *             zurückgegeben werden soll.
     *
     * @return Ein {@link LocalDateTime Datum} auf der Grundlage eines Datums in Form eines {@link String Textes}.
     */
    @NotNull
    private LocalDateTime getDateFromString(@NotNull final String date) {
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        return LocalDateTime.parse(date, formatter);
    }

    //<editor-fold desc="utility">

    /**
     * Prüft anhand von verschiedener großer Webseiten, ob eine Verbindung zu einer dieser Webseiten hergestellt werden
     * kann und schließt daraus, ob eine Internetverbindung besteht oder nicht.
     *
     * @return Wenn eine Internetverbindung besteht {@code true}, ansonsten {@code false}.
     */
    private static boolean isConnectionPresent() {
        return isHostReachable("google.de") || isHostReachable("amazon.de")
            || isHostReachable("apple.com") || isHostReachable("github.com");
    }

    /**
     * Prüft, ob eine Verbindung zu einem bestimmten Host hergestellt werden kann.
     *
     * @param host Der Host, welcher überprüft werden soll.
     *
     * @return Wenn dieser Host erreichbar ist, {@code true}, ansonsten {@code false}.
     */
    private static boolean isHostReachable(final String host) {
        try (final Socket socket = new Socket()) {
            final InetSocketAddress address = new InetSocketAddress(host, 80);
            socket.connect(address, 1000);

            return true;
        } catch (@NotNull final IOException ignored) {
            return false;
        }
    }
    //</editor-fold>

}
