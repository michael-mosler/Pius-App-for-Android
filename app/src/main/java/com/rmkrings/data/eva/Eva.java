package com.rmkrings.data.eva;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Eva implements Serializable {

    // @serial
    private final ArrayList<String> dates;
    // @serial
    private final HashMap<String, ArrayList<EvaItem>> evaData;

    // @serial
    private String digest;

    public Eva(JSONObject data) throws RuntimeException {
        try {
            dates = new ArrayList<>();
            evaData = new HashMap<>();
            JSONArray jsonEvaDataItems = data.optJSONArray("evaData");
            for (int i = 0; i < Objects.requireNonNull(jsonEvaDataItems).length(); i++) {
                JSONObject jsonEvaDataItem = jsonEvaDataItems.getJSONObject(i);
                String date = jsonEvaDataItem.getString("date");
                dates.add(date);

                JSONArray jsonEvaItems = jsonEvaDataItem.optJSONArray("evaItems");
                ArrayList<EvaItem> evaItems = new ArrayList<>();
                for (int j = 0; j < Objects.requireNonNull(jsonEvaItems).length(); j++) {
                    EvaItem evaItem = new EvaItem(jsonEvaItems.getJSONObject(j));
                    evaItems.add(evaItem);
                }

                evaData.put(date, evaItems);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process EVA items");
        }

        try {
            digest = data.getString("_digest");
        } catch (JSONException e) {
            e.printStackTrace();
            digest = null;
        }
    }

    public ArrayList<String> getDates() {
        return dates;
    }

    public HashMap<String, ArrayList<EvaItem>> getEvaData() {
        return evaData;
    }

    public String getDigest() {
        return digest;
    }
}
