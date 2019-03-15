package com.rmkrings.http;

public class HttpResponseData {
    private String data;
    private Integer httpStatusCode;
    private boolean error;
    private HttpResponseCallback callback;

    HttpResponseData(Integer httpStatusCode, boolean error, String data, HttpResponseCallback callback) {
        this.httpStatusCode = httpStatusCode;
        this.error = error;
        this.data = data;
        this.callback = callback;
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

    public HttpResponseCallback getCallback() {
        return callback;
    }
}
