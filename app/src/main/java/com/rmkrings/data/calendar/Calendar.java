package com.rmkrings.data.calendar;

import androidx.annotation.Nullable;

import com.kizitonwose.calendarview.model.CalendarDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Observable;
import java.util.stream.Collectors;

/**
 * Holds backend calendar as Java object.
 */
public class Calendar extends Observable implements Serializable {

    // @serial
    private final ArrayList<MonthItem> monthItems;

    // @serial
    private String digest;

    /**
     * Instantiates an empty calendar.
     */
    public Calendar() {
        monthItems = new ArrayList<>();
        digest = null;
    }

    /**
     *
     * Instantiate calendar from JSON object.
     * @param data JSON calendar data
     * @throws RuntimeException Indicates that JSON data processing failed.
     */
    public void load(JSONObject data) throws RuntimeException {
        monthItems.clear();
        try {
            JSONArray jsonMonthItems = data.optJSONArray("monthItems");
            for (int i = 0; i < Objects.requireNonNull(jsonMonthItems).length(); i++) {
                JSONObject jsonMonthItem = jsonMonthItems.getJSONObject(i);
                MonthItem monthItem = new MonthItem(jsonMonthItem);
                monthItems.add(monthItem);
            }

            setChanged();
            notifyObservers();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException("Failed to process calendar monthItems"));
        }

        try {
            digest = data.getString("_digest");
        } catch (JSONException e) {
            e.printStackTrace();
            digest = null;
        }
    }

    /**
     * Gets calendar digest.
     * @return Calendar digest, null when there is none.
     */
    @Nullable
    public String getDigest() {
        return digest;
    }

    /**
     * Get all month items.
     * @return List of all month items.
     */
    public ArrayList<MonthItem> getMonthItems() {
        return monthItems;
    }

    /**
     * Gets month item for month name.
     * @param monthName Month name to get item for.
     * @return Month item instance
     */
    @Nullable
    public MonthItem getMonthItem(String monthName) {
        for (MonthItem monthItem: getMonthItems()) {
            if (monthItem.getName().equals(monthName)) {
                return monthItem;
            }
        }

        return null;
    }

    /**
     * Gets all events for today.
     * @return List of today's events.
     */
    public ArrayList<DayItem> getTodayEvents() {
        MonthItem monthItem = getMonthItems().get(0);
        return monthItem.getTodayEvents();
    }

    /**
     * Filters calendar by given string and returns a new calendar.
     * @param s Filter string
     * @return Filtered calendar
     */
    public Calendar filter(String s) {
        Calendar c = new Calendar();
        c.monthItems.addAll(
                getMonthItems()
                        .stream()
                        .map(item -> item.filter(s))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toCollection(ArrayList<MonthItem>::new))
        );

        return c;
    }

    /**
     * Checks if calendar contains the given date. Returns the number of occurences.
     * @param calendarDay The day to check for
     * @return Number of occurences of calendarDay
     */
    public ArrayList<DayItem> filterBy(CalendarDay calendarDay) {
        final ArrayList<DayItem> dateList = new ArrayList<>();

        final String dayName = Calendar.getDayName(calendarDay);
        final String shortMonthName = Calendar.getShortMonthName(calendarDay);
        final String year = Calendar.getShortYear(calendarDay);
        final String monthName = String.format("%s %s", shortMonthName, year);

        MonthItem monthItem = getMonthItem(monthName);

        if (monthItem != null) {
            ArrayList<DayItem> dayItems = monthItem.getDayItems()
                    .stream()
                    .filter(dayItem -> dayItem.getDay().equals(dayName))
                    .collect(Collectors.toCollection(ArrayList::new));
            dateList.addAll(dayItems);
        }

        return dateList;
    }

    /**
     * Gets tha last month present in calendar.
     * @return Last month in calendar.
     */
    public YearMonth getLastMonth() {
        if (monthItems.size() == 0) {
            return YearMonth.now();
        }

        final MonthItem monthItem = monthItems.get(monthItems.size() - 1);
        final TemporalAccessor ta = DateTimeFormatter
                .ofPattern("MMMM yyyy")
                .withLocale(Locale.GERMAN)
                .parse(monthItem.getFullName());
        return YearMonth.from(ta);
    }

    /**
     * Gets calendar specific day name from calendar day.
     * @param calendarDay Calendar day to get day name from.
     * @return Day in format, e.g., Mo 24.11.
     */
    public static String getDayName(CalendarDay calendarDay) {
        return String.format(
                Locale.GERMAN,
                "%s %02d.%02d.",
                calendarDay
                        .getDate()
                        .getDayOfWeek()
                        .getDisplayName(TextStyle.SHORT, Locale.GERMAN)
                        .substring(0, 2),
                calendarDay
                        .getDay(),
                calendarDay
                        .getDate()
                        .getMonthValue()
        );
    }

    /**
     * Gets month from calendar day.
     * @param calendarDay Calendar day to get month from.
     * @return Month in format MON, e.g. DEZ for December
     */
    public static String getShortMonthName(CalendarDay calendarDay) {
        return String.format(
                "%s",
                calendarDay
                        .getDate()
                        .getMonth()
                        .getDisplayName(TextStyle.SHORT, Locale.GERMAN)
                        .substring(0, 3)
        );
    }

    /**
     * Gets short year from input date.
     * @param calendarDay Input date
     * @return Year in form YY, e.g. 21 for 2021.
     */
    public static String getShortYear(CalendarDay calendarDay) {
        return String.format(
                Locale.GERMAN,
                "%d",
                calendarDay
                        .getDate()
                        .getYear()
        ).substring(2);
    }
}
