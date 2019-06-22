package com.rmkrings.data.vertretungsplan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VertretungsplanChangeDetailItem {
    private String changeType;
    private String course;
    private String lesson;
    private VertretungsplanDetailItem newDetail;
    private VertretungsplanDetailItem oldDetail;
    private String remark;
    private String evaText;

    VertretungsplanChangeDetailItem(JSONObject jsonData) throws RuntimeException {
        try {
            changeType = jsonData.getString("type");
            
            JSONArray jsonDetailsNew = null;
            JSONArray jsonDetailsOld = null;
            if (changeType.equals("ADDED") || changeType.equals("CHANGED")) {
                jsonDetailsNew = jsonData.getJSONArray("detailsNew");
                newDetail = new VertretungsplanDetailItem(jsonDetailsNew.getString(1), jsonDetailsNew.getString(3), jsonDetailsNew.getString(4));
            }
            
            if (changeType.equals("CHANGED") || changeType.equals("DELETED")) {
                jsonDetailsOld = jsonData.getJSONArray("detailsOld");
                oldDetail = new VertretungsplanDetailItem(jsonDetailsOld.getString(1), jsonDetailsOld.getString(3), jsonDetailsOld.getString(4), true);
            }

            JSONArray jsonDetails = (jsonDetailsNew != null) ? jsonDetailsNew : jsonDetailsOld;
            course = jsonDetails != null ? jsonDetails.getString(2) : "";
            lesson = jsonDetails != null ? jsonDetails.getString(0) : "";
            remark = jsonDetails != null ? jsonDetails.getString(6) : "";
            evaText = ((jsonDetails != null ? jsonDetails.length() : 0) > 7) ? jsonDetails.getString(7) : null;
        }
        catch (JSONException e) {
            e.printStackTrace();
            throw(new RuntimeException("Failed to process subsitution schedule change detail item"));
        }
    }

    public String getChangeType() {
        return changeType;
    }

    public String getCourse() {
        return course;
    }

    public String getLesson() {
        return lesson;
    }

    public VertretungsplanDetailItem getDetailNew() {
        return newDetail;
    }

    public VertretungsplanDetailItem getDetailOld() {
        return oldDetail;
    }

    public String getRemark() {
        return remark;
    }

    public String getEvaText() {
        return evaText;
    }
}
