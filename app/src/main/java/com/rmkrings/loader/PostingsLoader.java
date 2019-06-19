package com.rmkrings.loader;

import com.rmkrings.helper.AppDefaults;

import java.net.URL;

public class PostingsLoader extends HttpGet {

    @Override
    protected URL getURL(String digest) throws java.net.MalformedURLException {
        String urlString = String.format("%s/v2/postings", AppDefaults.getBaseUrl());

        if (digest != null) {
            urlString += String.format("?digest=%s", digest);
        }

        return new URL(urlString);
    }
}
