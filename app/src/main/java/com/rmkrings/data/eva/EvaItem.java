package com.rmkrings.data.eva;

import org.json.JSONObject;

import java.io.Serializable;

public class EvaItem implements Serializable {
    private final String uuid;
    private final String course;
    private final String evaText;

    EvaItem(JSONObject data) throws RuntimeException {
        try {
            uuid = data.getString("uuid");
            course = data.getString("course");
            evaText = data.getString("evaText");
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to process EvaItem");
        }
    }

    public String getUuid() {
        return uuid;
    }

    public String getCourse() {
        return course;
    }

    public String getEvaText() {
        return evaText;
    }
}
