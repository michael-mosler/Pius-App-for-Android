package com.rmkrings.data.postings;

import com.rmkrings.data.MessageItem;
import com.rmkrings.helper.DateHelper;

import org.json.JSONObject;

import java.io.Serializable;

public class Posting extends MessageItem implements Serializable {
    public static final int postingItem = 0;

    // @serial
    private final String postingMessage;
    private final String timestamp;

    Posting(JSONObject data) throws Exception {
        postingMessage = data.getString("message");
        timestamp = DateHelper.convert(
                data.getString("timestamp").replace("Z", "+00:00"), "yyyy-MM-dd'T'HH:mm:ssz", "EEEE, d. MMMM yyyy, HH:mm 'Uhr'");
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
