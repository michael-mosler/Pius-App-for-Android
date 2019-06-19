package com.rmkrings.http;

import android.os.AsyncTask;
import android.os.Build;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

import com.rmkrings.helper.KitkatSocketFactory;
import com.rmkrings.loader.VertretungsplanLoader;

public class HttpRequest extends AsyncTask<HttpRequestData, Void, HttpResponseData> {

    private final static Logger logger = Logger.getLogger(VertretungsplanLoader.class.getName());

    @Override
    protected HttpResponseData doInBackground(HttpRequestData... params) {
        HttpRequestData data = params[0];
        URLConnection connection = data.getConnection();
        DataOutputStream dos = null;
        byte[] bodyData = {};
        HttpResponseData response;
        String inputLine;

        try {
            connection.setReadTimeout(60000);
            connection.setConnectTimeout(10000);
            connection.setUseCaches(false);

            // On Kitkat we need to provide our own SSL Socket factory as by default TLS 1.1 and
            // TLS 1.2 are disabled. Backend on the other hand does not support TLS 1.0 any longer.
            // Actually nobody should use 1.0!
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && connection instanceof HttpsURLConnection)
            try {
                ((HttpsURLConnection)connection).setSSLSocketFactory(new KitkatSocketFactory());
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            String body = data.getBody();
            if (body != null) {
                bodyData = body.getBytes(StandardCharsets.UTF_8);
                int bytes = bodyData.length;

                ((HttpURLConnection)connection).setFixedLengthStreamingMode(bytes);
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            }

            connection.connect();

            if (bodyData.length > 0) {
                dos = new DataOutputStream(connection.getOutputStream());
                dos.write(bodyData);
            }

            // Ok: Read reply data.
            int responseCode = ((HttpURLConnection)connection).getResponseCode();

            if (responseCode == 200) {
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
                response = new HttpResponseData(responseCode, false, stringBuilder.toString(), data.getCallback());
            }
            // Failed with HTTP Status.
            else {
                response = new HttpResponseData(responseCode, false, null, data.getCallback());
            }
        }
        catch (java.io.IOException e) {
            logger.info(String.format("Failed to submit HTTP GET request to %s: %s", connection.getURL().toString(), e.getMessage()));
            response = new HttpResponseData(null, true, null, data.getCallback());
        }
        finally {
            ((HttpURLConnection)connection).disconnect();

            if (dos != null) {
                try { dos.close(); } catch (IOException e) { e.printStackTrace(); }
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(HttpResponseData response) {
        super.onPostExecute(response);
        response.getCallback().execute(response);
    }
}
