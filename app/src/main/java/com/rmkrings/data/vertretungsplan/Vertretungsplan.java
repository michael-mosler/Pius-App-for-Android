package com.rmkrings.data.vertretungsplan;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Vertretungsplan {
    private String tickerText;
    private String additionalText;
    private String lastUpdate;
    private ArrayList<VertretungsplanForDate> vertretungsplaene;
    private String digest;

    public Vertretungsplan(JSONObject data) throws RuntimeException {
        try {
            tickerText = data.getString("tickerText");
        } catch (JSONException e) {
            e.printStackTrace();
            throw(new RuntimeException("Expected property tickerText not available in Vertetungsplan data"));
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
            throw(new RuntimeException("Expected property lastUpdate not available in Vertetungsplan data"));
        }

        try {
            vertretungsplaene = new ArrayList<>();
            JSONArray jsonDateItems = data.getJSONArray("dateItems");
            for (int i = 0; i < jsonDateItems.length(); i++) {
                JSONObject jsonDateItem = jsonDateItems.getJSONObject(i);
                VertretungsplanForDate vertretungsplanForDate = new VertretungsplanForDate(jsonDateItem);
                vertretungsplaene.add(vertretungsplanForDate);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException(("Failed to process date items from Vertretungsplan")));
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

    @Nullable
    public VertretungsplanForDate getTodaysSchedule() {
        final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        final String today = dateFormat.format(new Date());

        for (VertretungsplanForDate vertretungsplanForDate: vertretungsplaene) {
            String date = vertretungsplanForDate.getDate();
            if (date.substring(date.length() - 10).equals(today)) {
                return vertretungsplanForDate;
            }
        }

        return null;
    }
}
