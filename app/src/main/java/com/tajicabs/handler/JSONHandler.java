package com.tajicabs.handler;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tajicabs.configuration.TajiCabs;
import com.tajicabs.constants.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JSONHandler {
    private static final String TAG = JSONHandler.class.getName();

    private static InputStream inputStream = null;
    private static JSONObject jsonObject = null;
    private static JSONArray jsonArray = null;
    private static String jsonString = "";
    private static String error = "";

    public JSONHandler(){}

    public JSONArray locationPoolRequest(String url, String method, MultipartEntity multipartEntity, String latitude, String longitude) {
        try{
            if(method.equalsIgnoreCase("POST")){
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(multipartEntity);
                HttpResponse httpResponse = TajiCabs.APP_HTTP_CLIENT.execute(httpPost);

                Log.e(TAG, "" + httpResponse.getStatusLine().getStatusCode());
                error = String.valueOf(httpResponse.getStatusLine().getStatusCode());

                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "ERROR: " + e.getMessage());
        }

        try{
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line + "\n");
            }

            inputStream.close();
            jsonString = stringBuilder.toString();
            Log.e(TAG, "" + jsonString);
        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }

        try {
            if (jsonString != null){
                jsonArray = new JSONArray(jsonString);
            }

            TajiCabs.ERROR_CODE = error;
            Log.e(TAG, "ERROR_CODE" + TajiCabs.ERROR_CODE);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        return jsonArray;
    }
}
