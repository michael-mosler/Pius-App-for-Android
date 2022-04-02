package com.rmkrings.data.calendar;

import androidx.annotation.Nullable;

import com.rmkrings.helper.StringHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A single month item holds all events for a given month. It allows filtering of all
 * event by a given search term.
 */
public class MonthItem implements Serializable {

    // @serial
    private final String name;

    // @serial
    private final ArrayList<DayItem> dayItems;

    private MonthItem(MonthItem monthItem) {
        name = monthItem.getName();
        dayItems = new ArrayList<>();
    }

    MonthItem(JSONObject data) throws RuntimeException {
        try {
            String fullName = StringHelper.replaceHtmlEntities(data.getString("name"));
            name = fullName.substring(0, 3) + " " + fullName.substring(fullName.length() - 2);
        } catch (Exception e) {
            e.printStackTrace();
            throw (new RuntimeException("Expected property name not found in month item"));
        }

        try {
            dayItems = new ArrayList<>();
            JSONArray jsonDayItems = data.optJSONArray("dayItems");
            for (int i = 0; i < Objects.requireNonNull(jsonDayItems).length(); i++) {
                JSONObject jsonDayItem = jsonDayItems.getJSONObject(i);
                DayItem dayItem = new DayItem(jsonDayItem);
                dayItems.add(dayItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw (new RuntimeException("Failed to process day items for calendar month " + name));
        }
    }

    /**
     * Gets name of month.
     *
     * @return Name of month
     */
    public String getName() {
        return name;
    }

    /**
     * Gets all day items for month.
     *
     * @return Day items of calendar.
     */
    public ArrayList<DayItem> getDayItems() {
        return dayItems;
    }

    /**
     * Gets all event for "today's" date.
     *
     * @return List of today's events.
     */
    ArrayList<DayItem> getTodayEvents() {
        ArrayList<DayItem> l = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
        String today = dateFormat.format(new Date());
        String year = getName().substring(getName().length() - 2);

        for (DayItem dayItem : getDayItems()) {
            String dateString = dayItem.getDay().substring(dayItem.getDay().length() - 6) + year;

            if (dateString.equals(today)) {
                l.add(dayItem);
            }
        }

        return l;
    }

    /**
     * Filters month item by the given search term. If result is empty then null is returned.
     * If search term is null then monnt item is returned as is.
     *
     * @param s Search term
     * @return Calendar with filtered month items.
     */
    @Nullable
    MonthItem filter(String s) {
        if (s.length() == 0) {
            getDayItems()
                    .forEach(DayItem::resetSearchMatches);
            return this;
        }

        MonthItem m = new MonthItem(this);
        m.dayItems.addAll(
                getDayItems()
                        .stream()
                        .filter(item -> item.matches(s))
                        .collect(Collectors.toCollection(ArrayList<DayItem>::new))
        );

        return (m.dayItems.size() > 0) ? m : null;
    }
}
