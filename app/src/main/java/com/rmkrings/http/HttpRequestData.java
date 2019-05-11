package com.rmkrings.http;

import com.rmkrings.interfaces.HttpResponseCallback;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequestData {
    private HttpsURLConnection connection;
    private HttpResponseCallback callback;

    public HttpRequestData(HttpsURLConnection connection, HttpResponseCallback callback) {
        this.connection = connection;
        this.callback = callback;
    }

    HttpsURLConnection getConnection() {
        return connection;
    }

    HttpResponseCallback getCallback() {
        return callback;
    }
}
