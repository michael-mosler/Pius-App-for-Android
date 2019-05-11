package com.rmkrings.data.postings;

import com.rmkrings.data.MessageItem;

import org.json.JSONObject;

import java.io.Serializable;

public class Posting extends MessageItem implements Serializable {
    public static final int postingItem = 0;

    // @serial
    private String postingMessage;
    private String timestamp;

    public Posting(String postingMessage, String timestamp) {
        this.postingMessage = postingMessage;
        this.timestamp = timestamp;
    }

    Posting(JSONObject data) throws Exception {
        postingMessage = data.getString("message");
        timestamp = data.getString("timestamp");
    }

    public String getPostingMessage() {
        return postingMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getType() {
        return postingItem;
    }
}
