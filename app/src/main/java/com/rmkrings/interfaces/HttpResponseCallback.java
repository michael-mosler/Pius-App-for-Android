package com.rmkrings.interfaces;

import com.rmkrings.http.HttpResponseData;

public interface HttpResponseCallback {
    void execute(HttpResponseData data);
}
