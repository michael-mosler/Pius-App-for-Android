package com.rmkrings.data.news;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NewsItems {
    private final ArrayList<NewsItem> newsItems;
    private String digest;

    public NewsItems(JSONObject data) throws RuntimeException{
        try {
            newsItems = new ArrayList<>();
            JSONArray jsonNewsItems = data.getJSONArray("newsItems");
            for (int i = 0; i < jsonNewsItems.length(); i++) {
                JSONObject jsonNewsItem = jsonNewsItems.getJSONObject(i);
                NewsItem newsItem = new NewsItem(jsonNewsItem);
                newsItems.add(newsItem);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException("Failed to process news items"));
        }

        try {
            digest = data.getString("_digest");
        } catch (JSONException e) {
            e.printStackTrace();
            digest = null;
        }
    }

    public ArrayList<NewsItem> getNewsItems() {
        return newsItems;
    }

    @Nullable
    public String getDigest() {
        return digest;
    }
}
