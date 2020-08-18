package com.rmkrings.data.calendar;

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

public class MonthItem implements Serializable {

    // @serial
    private String name;

    // @serial
    private ArrayList<DayItem> dayItems;

    private  MonthItem(MonthItem monthItem) {
        name = monthItem.getName();
        dayItems = new ArrayList<>();
    }

    MonthItem(JSONObject data) throws RuntimeException {
        try {
            String fullName = StringHelper.replaceHtmlEntities(data.getString("name"));
            name = fullName.substring(0, 3) + " " + fullName.substring(fullName.length() - 2);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException("Expected property name not found in month item"));
        }

        try {
            dayItems = new ArrayList<>();
            JSONArray jsonDayItems = data.optJSONArray("dayItems");
            for (int i = 0; i < Objects.requireNonNull(jsonDayItems).length(); i++) {
                JSONObject jsonDayItem = jsonDayItems.getJSONObject(i);
                DayItem dayItem = new DayItem(jsonDayItem);
                dayItems.add(dayItem);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException("Failed to process day items for calendar month " + name));
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<DayItem> getDayItems() {
        return dayItems;
    }

    ArrayList<DayItem> getTodayEvents() {
        ArrayList<DayItem> l = new ArrayList<>();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.GERMANY);
        String today = dateFormat.format(new Date());
        String year = getName().substring(getName().length() - 2);

        for (DayItem dayItem: getDayItems()) {
            String dateString = dayItem.getDay().substring(dayItem.getDay().length() - 6) + year;

            if (dateString.equals(today)) {
                l.add(dayItem);
            }
        }

        return l;
    }

    // @nullable
    MonthItem filter(String s) {
        if (s.length() == 0) {
            return this;
        }

        MonthItem m = new MonthItem(this);
        for (DayItem dayItem: getDayItems()) {
            if (dayItem.matches(s)) {
                m.dayItems.add(dayItem);
            }
        }

        return (m.dayItems.size() > 0) ? m : null;
    }
}
