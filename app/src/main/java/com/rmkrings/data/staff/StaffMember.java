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
    private final String shortName;
    private final String name;
    private final ArrayList<String> subjects;
    private final String email;
    private final boolean isTeacher;

    StaffMember(String shortName, JSONObject fromJSON) throws JSONException {
        this.shortName = shortName;
        this.name = fromJSON.getString("name");
        this.isTeacher = fromJSON.optBoolean("isTeacher");
        this.email = isTeacher ? fromJSON.getString("email") : null;
        this.subjects = new ArrayList<>();
        JSONArray jsonSubjects = fromJSON.getJSONArray("subjects");
        for (int i = 0; i < jsonSubjects.length(); i++) {
            String subject = jsonSubjects.getString(i);
            this.subjects.add(subject);
        }
    }

    public String getShortName() {
        return shortName;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getSubjects() {
        return subjects;
    }

    /**
     * Gets subject list as comma separated string.
     * @return Comma separated subject list.
     */
    public String getSubjectsAsString() {
        if (subjects == null) {
            return "";
        }

        return subjects
                .stream()
                .reduce("", (acc, subject) -> acc.equals("") ? subject : acc.concat(", ").concat(subject));
    }

    public String getEmail() {
        return email;
    }

    public boolean getIsTeacher() {
        return isTeacher;
    }

    public boolean matches(String searchTerm) {
        if (searchTerm == null) {
            return true;
        }

        String lcSearchTerm = searchTerm.toLowerCase();
        return getShortName() != null && getShortName().toLowerCase().contains(lcSearchTerm)
                || getName() != null && getName().toLowerCase().contains(lcSearchTerm)
                || getSubjectsAsString() !=  null && getSubjectsAsString().toLowerCase().contains(lcSearchTerm)
                || getEmail() != null && getEmail().toLowerCase().contains(lcSearchTerm);
    }
}
