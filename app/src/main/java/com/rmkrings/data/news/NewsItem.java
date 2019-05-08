package com.rmkrings.data.news;

import org.json.JSONObject;

public class NewsItem {
    private String img;
    private String href;
    private String heading;
    private String text;

    public NewsItem(JSONObject data) throws Exception {
        try {
            img = data.optString("img");
            href = data.optString("href");
            heading = data.optString("heading");
            text = data.getString("text");
        }
        catch(Exception e) {
            e.printStackTrace();
            throw(new Exception(("Failed to process news item")));
        }
    }

    public String getImg() {
        return img;
    }

    public String getHref() {
        return href;
    }

    public String getHeading() {
        return heading;
    }

    public String getText() {
        return text;
    }
}
