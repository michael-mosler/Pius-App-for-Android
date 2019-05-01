package com.rmkrings.data.calendar;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Calendar {

    private ArrayList<MonthItem> monthItems;
    private String digest = null;

    public Calendar(JSONObject data) throws Exception {
        try {
            monthItems = new ArrayList<>();
            JSONArray jsonMonthItems = data.optJSONArray("monthItems");
            for (int i = 0; i < jsonMonthItems.length(); i++) {
                JSONObject jsonMonthItem = jsonMonthItems.getJSONObject(i);
                MonthItem monthItem = new MonthItem(jsonMonthItem);
                monthItems.add(monthItem);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new Exception("Failed to process calendar monthItems"));
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
}
