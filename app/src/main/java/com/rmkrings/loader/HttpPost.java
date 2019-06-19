package com.rmkrings.loader;

import com.rmkrings.helper.Reachability;
import com.rmkrings.http.HttpRequest;
import com.rmkrings.http.HttpRequestData;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;

import org.json.JSONException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

abstract class HttpPost {
    protected abstract URL getURL() throws java.net.MalformedURLException;
    protected abstract String getBody() throws JSONException;

    @SuppressWarnings("WeakerAccess")
    URLConnection addRequestProperties(URLConnection connection) {
        return connection;
    }

    public void load(HttpResponseCallback callback) {
        try {
            if (Reachability.isReachable()) {
                URL url = getURL();
                URLConnection connection = url.openConnection();

                ((HttpURLConnection)connection).setRequestMethod("POST");
                connection = addRequestProperties(connection);

                HttpRequest request = new HttpRequest();
                HttpRequestData data = new HttpRequestData(connection, callback, getBody());
                request.execute(data);
            } else {
                // null, true indicates that data could not be loaded due to connection
                // error. Callback will try to load data from cache and display an error
                // message.
                callback.execute(new HttpResponseData(null, true));
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.execute(new HttpResponseData(500, true));
        } catch (JSONException e) {
            e.printStackTrace();
            callback.execute(new HttpResponseData(500, true));
        }
    }
}
