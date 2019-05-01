package com.rmkrings.data.calendar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MonthItem {

    private String name;
    private ArrayList<DayItem> dayItems;

    public MonthItem(JSONObject data) throws Exception {
        try {
            String fullName = data.getString("name");
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
}
