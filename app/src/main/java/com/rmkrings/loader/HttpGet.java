package com.rmkrings.loader;

import com.rmkrings.helper.Reachability;
import com.rmkrings.http.HttpRequest;
import com.rmkrings.http.HttpRequestData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

abstract class HttpGet {
    protected abstract URL getURL(String digest) throws java.net.MalformedURLException;

    URLConnection addRequestProperties(URLConnection connection) {
        return connection;
    }

    public void load(HttpResponseCallback callback, String digest) {
        try {
            if (Reachability.isReachable()) {
                URL url = getURL(digest);
                URLConnection connection = url.openConnection();

                ((HttpURLConnection)connection).setRequestMethod("GET");
                connection = addRequestProperties(connection);

                HttpRequest request = new HttpRequest();
                HttpRequestData data = new HttpRequestData(connection, callback);
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
        }
    }
}
