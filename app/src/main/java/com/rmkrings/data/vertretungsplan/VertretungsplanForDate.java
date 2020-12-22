package com.rmkrings.data.vertretungsplan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class VertretungsplanForDate {
    private final String date;
    private final ArrayList<GradeItem> gradeItems;

    VertretungsplanForDate(JSONObject data) throws RuntimeException {
        try {
            date = data.getString("title");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException("Expected property title not present in date item"));
        }

        try {
            gradeItems = new ArrayList<>();
            JSONArray jsonGradeItems = data.getJSONArray("gradeItems");
            for (int i = 0; i< jsonGradeItems.length(); i++) {
                JSONObject jsonGradeItem = jsonGradeItems.getJSONObject(i);
                GradeItem gradeItem = new GradeItem(jsonGradeItem);
                gradeItems.add(gradeItem);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException("Failed to process grade items for date " + date));
        }
    }

    VertretungsplanForDate(VertretungsplanForDate from, GradeItem gradeItem) {
        date = from.getDate();
        gradeItems = new ArrayList<>();
        gradeItems.add(gradeItem);
    }

    public String getDate() {
        return date;
    }

    public ArrayList<GradeItem> getGradeItems() {
        return gradeItems;
    }
}
