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


        Iterator<String> shortcutNamesIterator = jsonStaffDictionary.keys();
        while (shortcutNamesIterator.hasNext()) {
            String shortcutName = shortcutNamesIterator.next();
            if (jsonStaffDictionary.get(shortcutName) instanceof JSONObject) {
                JSONObject jsonStaffMember = jsonStaffDictionary.getJSONObject(shortcutName);
                StaffMember staffMember = new StaffMember(jsonStaffMember);
                this.put(shortcutName, staffMember);
            }
        }
    }
}
