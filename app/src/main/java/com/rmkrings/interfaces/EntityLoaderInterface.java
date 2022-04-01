package com.rmkrings.interfaces;

public interface EntityLoaderInterface {

    String getCacheFileName();
    String getDigestFileName();
    void load(HttpResponseCallback responseDelegate, String digest);
}
