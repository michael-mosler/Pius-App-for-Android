package com.rmkrings.data.vertretungsplan;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VertretungsplanChangeList {
    private final HashMap<String, ArrayList<VertretungsplanChangeDetailItem>> changes;

    public VertretungsplanChangeList(JSONArray jsonData) {
        changes = new HashMap<>();

        for (int i = 0; i < jsonData.length(); i++) {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public HashMap<String, ArrayList<VertretungsplanChangeDetailItem>> getChanges() {
        return changes;
    }
}
