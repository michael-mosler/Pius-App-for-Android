package com.rmkrings.http;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.net.ssl.HttpsURLConnection;

public class HttpGetRequest extends AsyncTask<HttpRequestData, Void, HttpResponseData> {

    @Override
    protected HttpResponseData doInBackground(HttpRequestData... params) {
        HttpRequestData data = params[0];
        HttpsURLConnection connection = data.getConnection();
        HttpResponseData response = null;
        String inputLine;

        try {
            connection.setReadTimeout(60000);
            connection.setConnectTimeout(10000);
            connection.setUseCaches(false);

            connection.connect();
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }

            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();

            //Set our result equal to our stringBuilder
            response = new HttpResponseData(connection.getResponseCode(), false, stringBuilder.toString(), data.getCallback());
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
            response = new HttpResponseData(500, true, null, data.getCallback());
        }
        finally {
            connection.disconnect();
        }

        return response;
    }

    @Override
    protected void onPostExecute(HttpResponseData response) {
        response.getCallback().execute(response.getHttpStatusCode() == 200, response.isError(), null);
        super.onPostExecute(response);
    }
}
