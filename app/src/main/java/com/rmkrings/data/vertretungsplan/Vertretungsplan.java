package com.rmkrings.data.vertretungsplan;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Vertretungsplan {
    private String tickerText = null;
    private String additionalText = null;
    private String lastUpdate = null;
    private ArrayList<VertretungsplanForDate> vertretungsplaene;
    private String digest = null;

    public Vertretungsplan(JSONObject data) throws Exception {
        try {
            tickerText = data.getString("tickerText");
        } catch (JSONException e) {
            e.printStackTrace();
            throw(new Exception("Expected property tickerText not available in Vertetungsplan data"));
        }

        try {
            additionalText = data.getString("_additionalText");
        } catch (JSONException e) {
            e.printStackTrace();
            additionalText = null;
        }

        try {
            lastUpdate = data.getString("lastUpdate");
        } catch (JSONException e) {
            e.printStackTrace();
            throw(new Exception("Expected property lastUpdate not available in Vertetungsplan data"));
        }

        try {
            vertretungsplaene = new ArrayList<VertretungsplanForDate>();
            JSONArray jsonDateItems = data.getJSONArray("dateItems");
            for (int i = 0; i < jsonDateItems.length(); i++) {
                JSONObject jsonDateItem = jsonDateItems.getJSONObject(i);
                VertretungsplanForDate vertretungsplanForDate = new VertretungsplanForDate(jsonDateItem);
                vertretungsplaene.add(vertretungsplanForDate);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new Exception(("Failed to process date items from Vertretungsplan")));
        }

        try {
            digest = data.getString("_digest");
        } catch (JSONException e) {
            e.printStackTrace();
            digest = null;
        }
    }

    public String getTickerText() {
        return tickerText;
    }

    public String getAdditionalText() {
        return additionalText;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public ArrayList<VertretungsplanForDate> getVertretungsplaene() {
        return vertretungsplaene;
    }

    @Nullable
    public String getDigest() {
        return digest;
    }
}
