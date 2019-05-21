package com.rmkrings.data.eva;

import org.json.JSONObject;

import java.io.Serializable;

public class EvaItem implements Serializable {
    private String uuid;
    private String course;
    private String evaText;

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

    @SuppressWarnings("unused")
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
