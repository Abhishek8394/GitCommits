package com.apbytes.gitcommits.networking;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class NetworkHelper {
    // Constants
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    private static final String TAG = "NetworkHelper";


    public static Uri addGetParams(String uriString, HashMap<String, String> getParams){
        Uri parsedUri = Uri.parse(uriString);
        return addGetParams(parsedUri, getParams);
    }

    /**
     * Adds get parameters provided as key value pairs to a url.
     * @param uri
     * @param getParams
     * @return
     */
    public static Uri addGetParams(Uri uri, HashMap<String, String> getParams){
        Uri.Builder uriBuilder = uri.buildUpon();
        if(getParams != null && getParams.size() > 0){
            for(String key: getParams.keySet()){
                uriBuilder.appendQueryParameter(key, getParams.get(key));
            }
        }
        uri = uriBuilder.build();
        return uri;
    }

    public static String makeRequest(String uri, String method, HashMap<String, String> getParams, String postParams){
        HttpsURLConnection connection = null;
        InputStream stream = null;
        String result = null;
        try {
            // add get parametrs to the query.
            uri = addGetParams(uri, getParams).toString();
            URL url = new URL(uri);
            // open connection
            connection = (HttpsURLConnection)url.openConnection();
            Log.d(TAG, "Making request to " + url.toString());
            // set connection params
            connection.setConnectTimeout(3000);
            connection.setRequestMethod(method);
            // Send if any request body is to be sent
            if(! method.toLowerCase().equals("get")){
                connection.setDoInput(true);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(postParams);
                writer.flush();
                writer.close();
            }
            // Read data
            stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer response = new StringBuffer();
            String line;
            while((line = reader.readLine()) != null){
                response.append(line);
            }
            result = response.toString();
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
