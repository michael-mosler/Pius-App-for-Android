package com.rmkrings.loader;

import android.annotation.SuppressLint;

import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.Config;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.logging.Logger;

public class CalendarLoader extends HttpGet implements HttpResponseCallback {

    private final String digestFileName = Config.digestFilename("calendar");
    private final String cacheFileName = Config.cacheFilename("calendar");
    private final Cache cache = new Cache();
    private HttpResponseCallback responseDelegate;

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());

    @Override
    protected URL getURL(String digest) throws java.net.MalformedURLException {
        String urlString = String.format("%s/calendar", AppDefaults.getBaseUrl());

        if (digest != null) {
            urlString += String.format("?digest=%s", digest);
        }

        return new URL(urlString);
    }

    public void load(HttpResponseCallback responseDelegate) {
        String digest = null;

        if (cache.fileExists(digestFileName)) {
            digest = cache.read(digestFileName);
        }

        this.responseDelegate = responseDelegate;
        super.load(this, digest);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void execute(HttpResponseData responseData) {
        if (responseData.getHttpStatusCode() != null && responseData.getHttpStatusCode() != 200 && responseData.getHttpStatusCode() != 304) {
            logger.severe(String.format("Failed to load data for Staff dictionary. HTTP Status code %d.", responseData.getHttpStatusCode()));
            delegateResponse(responseData);
            return;
        }

        String data = responseData.getData();
        if (data != null) {
            // When data has changed we need the new digest as it must be updated.
            try {
                String digest = (new JSONObject(data)).getString("_digest");
                cache.store(cacheFileName, data);
                cache.store(digestFileName, digest);
                delegateResponse(responseData);
            } catch (JSONException e) {
                onInternalError(e);
            }
        } else {
            data = cache.read(cacheFileName);
            delegateResponse(new HttpResponseData(200, false, data, responseDelegate));
        }
    }

    @Override
    public void onInternalError(Exception e) {
        if (responseDelegate != null) {
            responseDelegate.onInternalError(e);
        } else {
            e.printStackTrace();
        }
    }

    /**
     * If delegate is set then response data processing is sent to it.
     * @param responseData Response data from last server response.
     */
    private void delegateResponse(HttpResponseData responseData) {
        if (responseDelegate != null) {
            responseDelegate.execute(responseData);
        }
    }
}
