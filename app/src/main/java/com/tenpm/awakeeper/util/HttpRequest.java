package com.tenpm.awakeeper.util;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ChaosControl on 2016. 8. 22..
 */
public class HttpRequest {
    private static final String BASE_URL = "https://10pm.pythonanywhere.com/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public String getJSON() {
        String ret = "";
        client.get("http://10pm.pythonanywhere.com/", new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String resBody = new String(responseBody);
                        Log.i("test", "jsonData: " + resBody);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    }
                });

        return ret;
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
