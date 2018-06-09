package com.apbytes.gitcommits.networking;

import android.net.Uri;

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
    public static String makeRequest(String uri, String method, String postParams){
        HttpsURLConnection connection = null;
        InputStream stream = null;
        String result = null;
        try {
            URL url = new URL(uri);
            // open connection
            connection = (HttpsURLConnection)url.openConnection();
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
