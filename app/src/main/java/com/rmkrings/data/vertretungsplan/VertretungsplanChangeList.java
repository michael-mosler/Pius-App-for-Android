package com.rmkrings.data.vertretungsplan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VertretungsplanChangeList {
    private final HashMap<String, ArrayList<VertretungsplanChangeDetailItem>> changes = new HashMap<>();

    public VertretungsplanChangeList(JSONArray jsonData) throws RuntimeException {
        try {
            for (int i = 0; i < jsonData.length(); i++) {
                if (jsonData.isNull(i)) {
                    continue;
                }

                JSONObject jsonChangeItem = jsonData.getJSONObject(i);
                if (jsonChangeItem.isNull("date")) {
                    continue;
                }

                String date = jsonChangeItem.getString("date");
                ArrayList<VertretungsplanChangeDetailItem> a = changes.get(date);
                if (a == null) {
                    a = new ArrayList<>();
                }

                a.add(new VertretungsplanChangeDetailItem(jsonChangeItem));
                changes.put(date, a);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException("Failed to process substitution schedule change list item."));
        }
    }

    public HashMap<String, ArrayList<VertretungsplanChangeDetailItem>> getChanges() {
        return changes;
    }
}
