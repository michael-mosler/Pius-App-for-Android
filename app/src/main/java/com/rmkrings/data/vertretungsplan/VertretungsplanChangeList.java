package com.rmkrings.data.vertretungsplan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VertretungsplanChangeList {
    private HashMap<String, ArrayList<VertretungsplanChangeDetailItem>> changes;

    public VertretungsplanChangeList(JSONArray jsonData) throws RuntimeException {
        changes = new HashMap<>();

        try {
            for (int i = 0; i < jsonData.length(); i++) {
                JSONObject jsonChangeItem = jsonData.getJSONObject(i);
                String date = jsonChangeItem.getString("date");

                ArrayList<VertretungsplanChangeDetailItem> a = changes.get(date);
                if (a == null) {
                    a = new ArrayList<>();
                }

                a.add(new VertretungsplanChangeDetailItem(jsonChangeItem));
                changes.put(date, a);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            throw(new RuntimeException("Failed to process substitution schedule change list item."));
        }
    }

    public HashMap<String, ArrayList<VertretungsplanChangeDetailItem>> getChanges() {
        return changes;
    }
}
