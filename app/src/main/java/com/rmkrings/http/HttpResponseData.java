package com.rmkrings.http;

import com.rmkrings.interfaces.HttpResponseCallback;

public class HttpResponseData {
    private String data;
    private final Integer httpStatusCode;
    private final boolean error;
    private final HttpResponseCallback callback;

    public HttpResponseData(Integer httpStatusCode, boolean error, String data, HttpResponseCallback callback) {
        this.httpStatusCode = httpStatusCode;
        this.error = error;
        this.data = data;
        this.callback = callback;
    }

    public HttpResponseData(Integer httpStatusCode, boolean error) {
        this.httpStatusCode = httpStatusCode;
        this.error = error;
        this.callback = null;
    }

    public Integer getHttpStatusCode() {
        return httpStatusCode;
    }

    public boolean isError() {
        return error;
    }

    public String getData() {
        return data;
    }

    HttpResponseCallback getCallback() {
        return callback;
    }
}
