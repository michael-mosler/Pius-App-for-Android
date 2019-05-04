package com.rmkrings.data.calendar;

import android.graphics.Point;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DayItem extends CalendarListItem {
    // @serial
    private String day;

    // @serial
    private String event;

    private ArrayList<Point> searchMatches;

    DayItem(JSONObject data) throws Exception {
        JSONArray jsonDetailItems = data.getJSONArray("detailItems");
        this.day = jsonDetailItems.getString(0);
        this.event = jsonDetailItems.getString(1);
        this.searchMatches = new ArrayList<>();
    }

    public String getDay() {
        return day;
    }

    public String getEvent() {
        return event;
    }

    public ArrayList<Point> getSearchMatches() {
        return searchMatches;
    }

    @Override
    public int getType() {
        return dayItem;
    }

    boolean matches(String s) {
        if (s.length() == 0) {
            return true;
        }

        searchMatches.clear();
        String sLc = s.toLowerCase();
        int i = event.toLowerCase().indexOf(sLc);
        while (i != -1) {
            searchMatches.add(new Point(i, i + s.length()));
            i = event.toLowerCase().indexOf(sLc, i + s.length());
        }

        return (searchMatches.size() != 0);
    }
}
