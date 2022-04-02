package com.rmkrings.loader;

import android.annotation.SuppressLint;

import com.rmkrings.data.staff.StaffDictionary;
import com.rmkrings.helper.AppDefaults;
import com.rmkrings.helper.Cache;
import com.rmkrings.helper.Config;
import com.rmkrings.http.HttpResponseData;
import com.rmkrings.interfaces.HttpResponseCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Load staff data from backend and store in cache. No further processing
 * is initiated as cache is intended to be updated on app start only.
 * Whenever staff dictionary is needed loadFromCache() method must be
 * called.
 */
public class StaffLoader extends HttpGet implements HttpResponseCallback {

    private final String cacheFileName = Config.cacheFilename("staff");
    private final String digestFileName = Config.digestFilename(("staff"));
    private final Cache cache = new Cache();

    private HttpResponseCallback responseDelegate;

    private final static Logger logger = Logger.getLogger(CalendarLoader.class.getName());

    private void delegateResponse(HttpResponseData responseData) {
        if (responseDelegate != null) {
            responseDelegate.execute(responseData);
        }
    }

    @Override
    protected URL getURL(String digest) throws MalformedURLException {
        String urlString = String.format("%s/v2/staff", AppDefaults.getBaseUrl());

        if (digest != null) {
            urlString += String.format("?digest=%s", digest);
        }

        return new URL(urlString);
    }

    public void load() {
        String digest = null;

        if (cache.fileExists(digestFileName)) {
            digest = cache.read(digestFileName);
        }
        
        super.load(this, digest);
    }

    public void load(HttpResponseCallback responseDelegate) {
        this.responseDelegate = responseDelegate;
        load();
    }

    /**
     * Process backend response for staff load request. On success if staff dictionary has changed
     * this method simply updated local staff cache. No parsing or any other king of processing
     * is made. This method implements HttpResponseCallback interface.
     *
     * @param responseData Backend response data with HTTP status code information and data.
     */
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
     * Load staff dictionary from cache.
     *
     * @return Returns current staff dictionary. This dictionary might be empty.
     */
    public StaffDictionary loadFromCache() {
        if (!cache.fileExists(cacheFileName)) {
            return new StaffDictionary();
        }

        String data = cache.read(cacheFileName);
        if (data == null) {
            return new StaffDictionary();
        }

        try {
            return new StaffDictionary(data);
        } catch (JSONException e) {
            e.printStackTrace();
            return new StaffDictionary();
        }
    }

}
