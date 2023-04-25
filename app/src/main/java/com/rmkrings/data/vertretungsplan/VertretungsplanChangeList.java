package com.rmkrings.data.vertretungsplan;

import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.loader.HttpAppErrorReporter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class VertretungsplanChangeList implements HttpResponseCallback {
    private final HashMap<String, ArrayList<VertretungsplanChangeDetailItem>> changes;

    public VertretungsplanChangeList(JSONArray jsonData) {
        changes = new HashMap<>();

        for (int i = 0; i < jsonData.length(); i++) {
            try {
                if (jsonData.isNull(i)) {
                    continue;
                }

                JSONObject jsonChangeItem = jsonData.getJSONObject(i);
                if (jsonChangeItem.isNull("date")) {
                    continue;
                }

                String date = jsonChangeItem.getString("date");
                ArrayList<VertretungsplanChangeDetailItem> a = changes.get(date);
                if (a == null) {
                    a = new ArrayList<>();
                }

                a.add(new VertretungsplanChangeDetailItem(jsonChangeItem));
                changes.put(date, a);
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                String message = e.getMessage() + ": " + sw;

                HttpAppErrorReporter httpAppErrorReporter = new HttpAppErrorReporter(message);
                httpAppErrorReporter.load(this);
            }
        }
    }

    public HashMap<String, ArrayList<VertretungsplanChangeDetailItem>> getChanges() {
        return changes;
    }

    @Override
    public void execute(HttpResponseData data) { }

    @Override
    public void onInternalError(Exception e) { }
}
