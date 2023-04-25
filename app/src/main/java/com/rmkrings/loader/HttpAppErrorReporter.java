package com.rmkrings.loader;

import android.content.pm.PackageManager;

import com.rmkrings.helper.AppDefaults;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpAppErrorReporter extends HttpPost {
    final private String message;

    public HttpAppErrorReporter(String message) {
        this.message = message;
    }

    @Override
    protected URL getURL() throws MalformedURLException {
        return new URL(String.format("%s/v2/errorReport", AppDefaults.getBaseUrl()));
    }

    @Override
    protected String getBody() throws JSONException {
        try {
            final String apiKey = AppDefaults.getApplicationParameter("apiKey");
            JSONObject jsonData = new JSONObject()
                    .put("apiKey", apiKey)
                    .put("message", message);
            return jsonData.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
