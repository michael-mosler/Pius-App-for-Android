package com.rmkrings.data.postings;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Postings implements Serializable {

    // @serial
    private ArrayList<Posting> postings;

    // @serial
    private String digest;

    public Postings(JSONObject data) throws RuntimeException {
        try {
            postings = new ArrayList<>();
            JSONArray jsonPostings = data.optJSONArray("messages");
            for (int i = 0; i < jsonPostings.length(); i++) {
                JSONObject jsonPosting = jsonPostings.getJSONObject(i);
                Posting posting = new Posting(jsonPosting);
                postings.add(posting);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            throw(new RuntimeException("Failed to process posting messages"));
        }

        try {
            digest = data.getString("_digest");
        } catch (JSONException e) {
            e.printStackTrace();
            digest = null;
        }
    }

    @Nullable
    public ArrayList<Posting> getPostings() {
        return postings;
    }

    @Nullable
    public String getDigest() {
        return digest;
    }
}
