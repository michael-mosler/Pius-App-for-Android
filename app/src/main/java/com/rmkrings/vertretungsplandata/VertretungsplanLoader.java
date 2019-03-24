package com.rmkrings.vertretungsplandata;

import android.util.Base64;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.http.HttpGetRequest;
import com.rmkrings.http.HttpRequestData;
import com.rmkrings.http.HttpResponseCallback;
import com.rmkrings.http.HttpResponseData;

public class VertretungsplanLoader {
    private Pattern matchEmptyCourse;
    private String forGrade;
    private String baseUrl = String.format("%s/v2/vertretungsplan", AppDefaults.getBaseUrl());
    private URL url;
    private Cache cache;
    private String cacheFileName;
    private String digestFileName;
    private String digest;

    private final static Logger logger = Logger.getLogger(VertretungsplanLoader.class.getName());

    public VertretungsplanLoader(String forGrade) {
        this.forGrade = forGrade;
/*
        String digest = null;
        String cacheFileName = (forGrade != null) ? String.format("%s.json", forGrade) : "vertretungsplan.json";
        String digestFileName = (forGrade != null) ? String.format("%s.md5", forGrade) : "md5";

        // If cache file exists we may use digest to detect changes in Vertretungsplan. Without cache file
        // we need to request data.
        cache = new Cache();

        if (cache.fileExists(cacheFileName)) {
            digest = cache.read(digestFileName).toString();
        } else {
            logger.info(String.format("Cache file %s does not exist. Not sending digest.", cacheFileName));
        }

        this.forGrade = forGrade;
        this.digest = digest;

        String urlString = baseUrl;
        if (forGrade != null || digest != null) {
            String separator = "/?";
            if (forGrade != null) {
                urlString += String.format("%sforGrade=%s", separator, forGrade);
                separator = "&";
            }

            if (digest != null) {
                urlString += String.format("%sdigest=%s", separator, digest);
            }
        }

        url = new URL(urlString);
        this.cacheFileName = cacheFileName;
        this.digestFileName = digestFileName;

        // This expression matches a missing course that is indicated by dashes or blanks.
        matchEmptyCourse = Pattern.compile("^[^A-Z]");
*/
    }

    private static String getAndEncodeCredentials(String username, String password) {
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

    private URL getVertretungsplanURL(String forGrade, String digest) throws java.net.MalformedURLException {
        String urlString = String.format("%s/v2/vertretungsplan", AppDefaults.getBaseUrl());;

        if (forGrade != null || digest != null) {
            String separator = "/?";
            if (forGrade != null) {
                urlString += String.format("%sforGrade=%s", separator, forGrade);
                separator = "&";
            }

            if (digest != null) {
                urlString += String.format("%sdigest=%s", separator, digest);
            }
        }

        return new URL(urlString);
    }

    public void load(HttpResponseCallback callback) {
        try {
            // @TODO Reachability check
            if (true) {
                URL url = getVertretungsplanURL(forGrade, digest);
                HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.addRequestProperty("Authorization", "Basic " + getAndEncodeCredentials(null, null));

                HttpGetRequest request = new HttpGetRequest();
                HttpRequestData data = new HttpRequestData(connection, callback);
                request.execute(data);
            } else {
                // null, true indicates that data could not be loaded due to connection
                // error. Callback will try to load data from cache and display an error
                // message.
                callback.execute(new HttpResponseData(null, true));
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.execute(new HttpResponseData(500, true));
        }
    }

    public static void validateLogin(String forUser, String withPassword, HttpResponseCallback callback) {
        try {
            URL url = new URL(String.format("%s/validateLogin", AppDefaults.getBaseUrl()));
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.addRequestProperty("Authorization", "Basic " + getAndEncodeCredentials(forUser, withPassword));

            HttpGetRequest request = new HttpGetRequest();
            HttpRequestData data = new HttpRequestData(connection, callback);
            request.execute(data);
        } catch (IOException e) {
            callback.execute(new HttpResponseData(500, true));
            e.printStackTrace();
        }
    }
}
