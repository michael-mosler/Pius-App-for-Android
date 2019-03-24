package com.rmkrings.data.vertretungsplan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GradeItem {
    private String grade;
    private ArrayList<String[]> vertretungsplanItems;

    public GradeItem(JSONObject data) throws Exception {
        try {
            grade = data.getString("grade");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new Exception("Expected property grade not found in grade item"));
        }

        try {
            vertretungsplanItems = new ArrayList<String[]>();
            JSONArray jsonVertretungsplanItems = data.optJSONArray("vertretungsplanItems");
            for (int i = 0; i < jsonVertretungsplanItems.length(); i++) {
                JSONObject jsonVertretungsplanItem = jsonVertretungsplanItems.getJSONObject(i);
                JSONArray jsonDetailItems = jsonVertretungsplanItem.getJSONArray("detailItems");

                String[] detailItems = new String[jsonDetailItems.length()];
                for (int j = 0; j < jsonDetailItems.length(); j++) {
                    detailItems[j] = jsonDetailItems.getString(j);
                }
                vertretungsplanItems.add(detailItems);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new Exception("Failed to process details items for grade " + grade));
        }
    }

    public String getGrade() {
        return grade;
    }

    public ArrayList<String[]> getVertretungsplanItems() {
        return vertretungsplanItems;
    }
}
