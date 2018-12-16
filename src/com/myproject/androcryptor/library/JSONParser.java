package com.myproject.androcryptor.library;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

    static InputStream dAccIs = null;
    static JSONObject dAccObject = null;
    static String dAccJson = "";

    public JSONParser() {

    }

    public JSONObject dAccGetJSONFromUrl(String url, List<NameValuePair> params) {

    	//Creating HTTP request
        try {

            DefaultHttpClient dAccHttpClient = new DefaultHttpClient();
            HttpPost dAccHttpPost = new HttpPost(url);
            dAccHttpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse dAccHttpResponse = dAccHttpClient.execute(dAccHttpPost);
            HttpEntity dAccHttpEntity = dAccHttpResponse.getEntity();
            dAccIs = dAccHttpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader dAccReader = new BufferedReader(new InputStreamReader(
                    dAccIs, "iso-8859-1"), 8);
            StringBuilder dAccBuilder = new StringBuilder();
            String dAccLine = null;
            while ((dAccLine = dAccReader.readLine()) != null) {
            	dAccBuilder.append(dAccLine + "\n");
            }
            dAccIs.close();
            dAccJson = dAccBuilder.toString();
            Log.e("JSON", dAccJson);
        } catch (Exception e) {
            Log.e("Buffer Error", "Error when converting result " + e.toString());
        }


        try {
        	dAccObject = new JSONObject(dAccJson);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error when parsing the data " + e.toString());
        }

        return dAccObject;

    }
}
