package com.rmkrings.data.staff;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Defines properties of a staff member. In our case name
 * and list of subjects is all we need. Instances of this class are intended to be
 * stored in StaffDictionary. The dictionary associated shortcut names with more detailed
 * staff information.
 */
public class StaffMember {
    private final String name;
    private final ArrayList<String> subjects;
    private final String email;

    StaffMember(JSONObject fromJSON) throws JSONException {
        name = fromJSON.getString("name");
        boolean isTeacher = fromJSON.optBoolean("isTeacher");
        if (isTeacher) {
            email = fromJSON.getString("email");
        } else {
            email = null;
        }
        subjects = new ArrayList<>();
        JSONArray jsonSubjects = fromJSON.getJSONArray("subjects");
        for (int i = 0; i < jsonSubjects.length(); i++) {
            String subject = jsonSubjects.getString(i);
            subjects.add(subject);
        }
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    public String getEmail() {
        return email;
    }
}
