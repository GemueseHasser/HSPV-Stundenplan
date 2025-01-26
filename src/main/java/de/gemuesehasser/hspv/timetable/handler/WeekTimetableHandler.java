package de.gemuesehasser.hspv.timetable.handler;

import de.gemuesehasser.hspv.timetable.HspvTimetable;
import de.gemuesehasser.hspv.timetable.object.LVS;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;

/**
 * Mithilfe dieses Handlers lassen sich die Lehrveranstaltungen einer bestimmten Woche aus allen Lehrveranstaltungen
 * herausfiltern und zurückgeben.
 */
public final class WeekTimetableHandler {

    /**
     * Filtert die Lehrveranstaltungen einer bestimmten Woche aus allen Lehrveranstaltungen heraus und gibt diese
     * zurück.
     *
     * @param weekAddition Die Anzahl an Wochen, die zu der aktuellen Woche dazuaddiert werden soll, um die Woche zu
     *                     ermitteln, für die die Lehrveranstaltungen herausgefiltert werden sollen (Negative
     *                     Wochenzahlen funktionieren auch).
     *
     * @return Die gefilterten Lehrveranstaltungen einer bestimmten Woche aus allen Lehrveranstaltungen.
     */
    public static LinkedList<LVS> getWeekLvs(@Range(from = 0, to = Integer.MAX_VALUE) final int weekAddition) {
        final LinkedList<LVS> lvsList = new LinkedList<>();

        final DayOfWeek currentDay = LocalDate.now().getDayOfWeek();
        final LocalDate begin = LocalDate.now()
            .minusDays(currentDay.getValue() - 1)
            .plusDays(weekAddition * 7L);
        final LocalDate end = begin.plusDays(5);

        for (@NotNull final Map.Entry<LocalDateTime, LVS> lvsEntry : HspvTimetable.getLvsMap().entrySet()) {
            final LocalDateTime lvsDate = lvsEntry.getKey();
            final LVS lvs = lvsEntry.getValue();

            if (lvsDate.isBefore(begin.atStartOfDay())) continue;
            if (lvsDate.isAfter(end.atStartOfDay())) continue;

            lvsList.add(lvs);
        }

        if (lvsList.isEmpty()) {
            lvsList.add(new LVS(begin.atStartOfDay(), null, null));
        }

        return lvsList;
    }


}
