package com.recom3.snow3.mobilesdk.engageweb;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.cookie.DateParseException;
import org.apache.http.impl.cookie.DateUtils;

/**
 * Created by Recom3 on 24/01/2022.
 */

public class EngageWebResponse {
    public static final String TAG = "EngageWebResponse";

    public EngageWebClientRequest mRequest;

    public HttpResponse mResponse;

    public int mResponseCode;

    public String mResponseString;

    public int mResponseTime;

    public EngageWebResponse(Pair<HttpResponse, Integer> paramPair, EngageWebClientRequest paramEngageWebClientRequest) {
        HttpResponse httpResponse = (HttpResponse)paramPair.first;
        this.mRequest = paramEngageWebClientRequest;

        /*
        String json;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            json = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        if (httpResponse != null) {
            this.mResponse = httpResponse;
            this.mResponseString = parseEntity(httpResponse.getEntity());
            this.mResponseCode = httpResponse.getStatusLine().getStatusCode();
            int i = 0;
            try {
                long l = DateUtils.parseDate(httpResponse.getFirstHeader("Date").getValue()).getTime() / 1000L;
                i = (int)l;
            } catch (DateParseException dateParseException) {
                dateParseException.printStackTrace();
            }
            this.mResponseTime = i;
            Log.i("EngageWebResponse", "time of response: " + i);
            return;
        }
        this.mResponse = null;
        this.mResponseString = null;
        //this.mResponseCode = ((Integer)((Pair)dateParseException).second).intValue();
    }

    private static String parseEntity(HttpEntity paramHttpEntity) {
        try {
            InputStream inputStream = paramHttpEntity.getContent();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String str = bufferedReader.readLine();
                if (str != null) {
                    StringBuilder stringBuilder1 = new StringBuilder();
                    stringBuilder.append(stringBuilder1.append(str).append("\n").toString());
                    continue;
                }
                inputStream.close();
                return stringBuilder.toString();
            }
        } catch (IOException iOException) {

        }
        return null;
    }
}
