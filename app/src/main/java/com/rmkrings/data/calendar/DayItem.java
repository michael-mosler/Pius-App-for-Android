package com.rmkrings.data.calendar;

import android.graphics.Point;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A day item defines a single calendar event. It provides a matcher which also build a
 * search result list with matches being highlighted.
 */
public class DayItem extends CalendarListItem {
    // @serial
    private final String day;

    // @serial
    private final String event;

    private final ArrayList<Point> searchMatches;

    DayItem(JSONObject data) throws Exception {
        JSONArray jsonDetailItems = data.getJSONArray("detailItems");
        this.day = jsonDetailItems.getString(0);
        this.event = jsonDetailItems.getString(1);
        this.searchMatches = new ArrayList<>();
    }

    /**
     * Gets day name.
     * @return Day name as DD.MM.
     */
    public String getDay() {
        return day;
    }

    /**
     * Gets event title.
     * @return Event title
     */
    public String getEvent() {
        return event;
    }

    /**
     * Gets current search matches, aka result from last search for this day item. For a day item
     * there may be more than one match, obviously which is why an array is returned.
     * @return Search matches from last search
     */
    public ArrayList<Point> getSearchMatches() {
        return searchMatches;
    }

    @Override
    public int getType() {
        return dayItem;
    }

    /**
     * Checks of day item matches the given search criteria. If so true is returned and
     * the matching substring is highlighted and added to searchMatches. Call getSearchMatches()
     * to get latest matching result.
     * @param s Search term
     * @return true if item matches search term.
     */
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

    /**
     * When search ends this method should be called to clear the search result.
     */
    public void resetSearchMatches() {
        searchMatches.clear();
    }
}
