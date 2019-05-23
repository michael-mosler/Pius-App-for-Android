package com.rmkrings.http;

import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

import com.rmkrings.helper.KitkatSocketFactory;
import com.rmkrings.loader.VertretungsplanLoader;

public class HttpGetRequest extends AsyncTask<HttpRequestData, Void, HttpResponseData> {

    private final static Logger logger = Logger.getLogger(VertretungsplanLoader.class.getName());

    @Override
    protected HttpResponseData doInBackground(HttpRequestData... params) {
        HttpRequestData data = params[0];
        HttpsURLConnection connection = data.getConnection();
        HttpResponseData response;
        String inputLine;

        try {
            connection.setReadTimeout(60000);
            connection.setConnectTimeout(10000);
            connection.setUseCaches(false);

            // On Kitkat we need to provide our own SSL Socket factory as by default TLS 1.1 and
            // TLS 1.2 are disabled. Backend on the other hand does not support TLS 1.0 any longer.
            // Actually nobody should use 1.0!
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            try {
                connection.setSSLSocketFactory(new KitkatSocketFactory());
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            connection.connect();

            // Ok: Read reply data.
            if (connection.getResponseCode() == 200) {
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                //Check if the line we are reading is not null
                while ((inputLine = reader.readLine()) != null) {
                    stringBuilder.append(inputLine);
                }

                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();

                //Set our result equal to our stringBuilder
                response = new HttpResponseData(connection.getResponseCode(), false, stringBuilder.toString(), data.getCallback());
            }
            // Failed with HTTP Status.
            else {
                response = new HttpResponseData(connection.getResponseCode(), false, null, data.getCallback());
            }
        }
        catch (java.io.IOException e) {
            logger.info(String.format("Failed to submit HTTP GET request to %s: %s", connection.getURL().toString(), e.getMessage()));
            response = new HttpResponseData(null, true, null, data.getCallback());
        }
        finally {
            connection.disconnect();
        }

        return response;
    }

    @Override
    protected void onPostExecute(HttpResponseData response) {
        response.getCallback().execute(response);
        super.onPostExecute(response);
    }
}
