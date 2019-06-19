package com.rmkrings.loader;

import android.util.Base64;

import com.rmkrings.helper.AppDefaults;

import java.net.URLConnection;

abstract class HttpAuthenticatedGetLoader extends HttpGet {
    static String getAndEncodeCredentials(String username, String password) {
        String realUsername;
        String realPassword;

        if (username == null && password == null) {
            realUsername = AppDefaults.getUsername();
            realPassword = AppDefaults.getPassword();
            //(realUsername, realPassword) = AppDefaults.credentials;
        } else {
            realUsername = username;
            realPassword = password;
        }

        String loginString = String.format("%s:%s", realUsername, realPassword);
        byte[] loginData = Base64.encode(loginString.getBytes(), Base64.DEFAULT);
        return new String(loginData);
    }

    @Override
    public URLConnection addRequestProperties(URLConnection connection) {
        super
                .addRequestProperties(connection)
                .addRequestProperty("Authorization", "Basic " + getAndEncodeCredentials(null, null));
        return connection;
    }
}
