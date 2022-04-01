package com.rmkrings.data.staff;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A staff dictionary is a typed hash map with a string key and a StaffMember value.
 * The key in fact is a staff member's shortcut name.
 */
public class StaffDictionary extends HashMap<String, StaffMember> {
    public StaffDictionary() {
        super();
    }

    public StaffDictionary(String data) throws JSONException {
        super();

        JSONObject jsonObject = new JSONObject(data);
        JSONObject jsonStaffDictionary = jsonObject.getJSONObject("staffDictionary");

        Iterator<String> shortNamesIterator = jsonStaffDictionary.keys();
        while (shortNamesIterator.hasNext()) {
            String shortName = shortNamesIterator.next();
            if (jsonStaffDictionary.get(shortName) instanceof JSONObject) {
                JSONObject jsonStaffMember = jsonStaffDictionary.getJSONObject(shortName);
                StaffMember staffMember = new StaffMember(shortName, jsonStaffMember);
                this.put(shortName, staffMember);
            }
        }
    }
}
