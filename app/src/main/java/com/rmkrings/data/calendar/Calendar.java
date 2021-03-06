package com.rmkrings.data.calendar;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Calendar implements Serializable {

    // @serial
    private final ArrayList<MonthItem> monthItems;

    // @serial
    private String digest;

    private Calendar() {
        monthItems = new ArrayList<>();
        digest = null;
    }

    public Calendar(JSONObject data) throws RuntimeException {
        try {
            monthItems = new ArrayList<>();
            JSONArray jsonMonthItems = data.optJSONArray("monthItems");
            for (int i = 0; i < Objects.requireNonNull(jsonMonthItems).length(); i++) {
                JSONObject jsonMonthItem = jsonMonthItems.getJSONObject(i);
                MonthItem monthItem = new MonthItem(jsonMonthItem);
                monthItems.add(monthItem);
            }
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

    public ArrayList<MonthItem> getMonthItems() {
        return monthItems;
    }

    @Nullable
    public String getDigest() {
        return digest;
    }

    @Nullable
    public MonthItem getMonthItem(String monthName) {
        for (MonthItem monthItem: getMonthItems()) {
            if (monthItem.getName().equals(monthName)) {
                return monthItem;
            }
        }

        return null;
    }

    public ArrayList<DayItem> getTodayEvents() {
        MonthItem monthItem = getMonthItems().get(0);
        return monthItem.getTodayEvents();
    }

    public Calendar filter(String s) {
        if (s.length() == 0) {
            return this;
        }

        Calendar c = new Calendar();
        for (MonthItem monthItem: getMonthItems()) {
            MonthItem m = monthItem.filter(s);

            if (m != null) {
                c.monthItems.add(m);
            }
        }

        return c;
    }
}
