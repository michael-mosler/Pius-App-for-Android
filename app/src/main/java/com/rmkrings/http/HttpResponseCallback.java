package com.rmkrings.http;

public interface HttpResponseCallback {
    void execute(boolean statusOk, boolean isError, HttpResponseData data);
}
