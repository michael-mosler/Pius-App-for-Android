package com.rmkrings.http;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequestData {
    private HttpsURLConnection connection;
    private HttpResponseCallback callback;

    public HttpRequestData(HttpsURLConnection connection, HttpResponseCallback callback) {
        this.connection = connection;
        this.callback = callback;
    }

    public HttpsURLConnection getConnection() {
        return connection;
    }

    public HttpResponseCallback getCallback() {
        return callback;
    }
}
