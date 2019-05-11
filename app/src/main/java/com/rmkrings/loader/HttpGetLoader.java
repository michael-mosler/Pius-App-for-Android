package com.rmkrings.loader;

import com.rmkrings.http.HttpGetRequest;
import com.rmkrings.http.HttpRequestData;
import com.rmkrings.interfaces.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

abstract class HttpGetLoader {
    protected abstract URL getURL(String digest) throws java.net.MalformedURLException;

    public void load(HttpResponseCallback callback, String digest) {
        try {
            // @TODO Reachability check
            if (true) {
                URL url = getURL(digest);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setRequestMethod("GET");

                HttpGetRequest request = new HttpGetRequest();
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
