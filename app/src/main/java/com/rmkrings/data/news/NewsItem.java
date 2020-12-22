package com.rmkrings.data.news;

import org.json.JSONObject;

public class NewsItem {
    private final String img;
    private final String href;
    private final String heading;
    private final String text;

    NewsItem(JSONObject data) throws RuntimeException {
        try {
            img = data.optString("img");
            href = data.optString("href");
            heading = data.optString("heading");
            text = data.getString("text");
        }
        catch(Exception e) {
            e.printStackTrace();
            throw(new RuntimeException(("Failed to process news item")));
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
