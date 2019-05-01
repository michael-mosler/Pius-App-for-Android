package com.rmkrings.data.calendar;

import org.json.JSONArray;
import org.json.JSONObject;

public class DayItem {
    private String day;
    private String event;

    public DayItem(JSONObject data) throws Exception {
        JSONArray jsonDetailItems = data.getJSONArray("detailItems");
        this.day = jsonDetailItems.getString(0);
        this.event = jsonDetailItems.getString(1);
    }

    public String getDay() {
        return day;
    }

    public String getEvent() {
        return event;
    }
}
