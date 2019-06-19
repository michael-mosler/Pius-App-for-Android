package com.rmkrings.http;

import com.rmkrings.interfaces.HttpResponseCallback;

import java.net.URLConnection;

public class HttpRequestData {
    private final URLConnection connection;
    private final HttpResponseCallback callback;
    private final String body;

    public HttpRequestData(URLConnection connection, HttpResponseCallback callback) {
        this.connection = connection;
        this.callback = callback;
        this.body = null;
    }

    public HttpRequestData(URLConnection connection, HttpResponseCallback callback, String body) {
        this.connection = connection;
        this.callback = callback;
        this.body = body;
    }

    URLConnection getConnection() {
        return connection;
    }

    HttpResponseCallback getCallback() {
        return callback;
    }

    String getBody() {
        return body;
    }
}
