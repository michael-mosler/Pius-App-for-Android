package com.rmkrings.data.vertretungsplan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public final class GradeItem implements Serializable {
    // @serial
    private final String grade;

    // @serial
    private final ArrayList<String[]> vertretungsplanItems;

    GradeItem(JSONObject data) throws RuntimeException {
        try {
            grade = data.getString("grade");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException("Expected property grade not found in grade item"));
        }

        try {
            vertretungsplanItems = new ArrayList<>();
            JSONArray jsonVertretungsplanItems = data.optJSONArray("vertretungsplanItems");
            for (int i = 0; i < Objects.requireNonNull(jsonVertretungsplanItems).length(); i++) {
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
            throw(new RuntimeException("Failed to process details items for grade " + grade));
        }
    }

    GradeItem(GradeItem from, String[] vertretungsplanItem) {
        grade = from.grade;
        vertretungsplanItems = new ArrayList<>();
        vertretungsplanItems.add(vertretungsplanItem);
    }

    public String getGrade() {
        return grade;
    }

    public ArrayList<String[]> getVertretungsplanItems() {
        return vertretungsplanItems;
    }
}
