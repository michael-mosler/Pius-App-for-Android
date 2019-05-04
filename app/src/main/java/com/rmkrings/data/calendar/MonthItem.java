package com.rmkrings.data.calendar;

import com.rmkrings.helper.StringHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class MonthItem implements Serializable {

    // @serial
    private String name;

    // @serial
    private ArrayList<DayItem> dayItems;

    private  MonthItem(MonthItem monthItem) {
        name = monthItem.getName();
        dayItems = new ArrayList<>();
    }

    MonthItem(JSONObject data) throws Exception {
        try {
            String fullName = StringHelper.replaceHtmlEntities(data.getString("name"));
            name = fullName.substring(0, 3) + " " + fullName.substring(fullName.length() - 2);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new Exception("Expected property name not found in month item"));
        }

        try {
            dayItems = new ArrayList<>();
            JSONArray jsonDayItems = data.optJSONArray("dayItems");
            for (int i = 0; i < jsonDayItems.length();i++) {
                JSONObject jsonDayItem = jsonDayItems.getJSONObject(i);
                DayItem dayItem = new DayItem(jsonDayItem);
                dayItems.add(dayItem);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new Exception("Failed to process day items for calendar month " + name));
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<DayItem> getDayItems() {
        return dayItems;
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
