package com.recom3.snow3.mobilesdk.engageweb;

import android.net.Uri;
import android.util.Log;
import android.util.Pair;

import com.recom3.snow3.mobilesdk.ConcurrentAsyncTask;

import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

/**
 * Created by Recom3 on 24/01/2022.
 */

public class EngageWebClientRequest extends ConcurrentAsyncTask<EngageWebClient, Void, EngageWebResponse> {
    private static final int REQUEST_TIMEOUT_MS = 15000;

    public static String TAG = EngageWebClientRequest.class.getSimpleName();

    private static BasicHttpContext httpContext = new BasicHttpContext();

    private String authority;

    private EngageWebClient mOwner;

    private HTTP_METHOD method;

    private Map<String, String> params;

    private String uriPath;

    private HttpUriRequest uriRequest;

    static {
        httpContext.setAttribute("http.cookie-store", new BasicCookieStore());
    }

    EngageWebClientRequest(HTTP_METHOD paramHTTP_METHOD, String paramString1, String paramString2, boolean paramBoolean, Map<String, String> paramMap, String paramString3) {
        this.method = paramHTTP_METHOD;
        this.authority = paramString1;
        this.uriPath = paramString2;
        this.params = paramMap;
        if (paramString3 != null) {
            Log.w(TAG, "Authorization token specified, forcing HTTPS connection to " + paramString1);
            paramBoolean = true;
        }
        this.uriRequest = makeRequest(paramHTTP_METHOD, paramString1, paramString2, paramMap, paramBoolean);
        if (paramString3 != null && paramBoolean)
            this.uriRequest.addHeader("Authorization", paramString3);
    }

    private static HttpEntity createPOSTEntity(Map<String, String> paramMap) {
        ArrayList<BasicNameValuePair> arrayList = new ArrayList();
        if (paramMap != null)
            for (Map.Entry<String, String> entry : paramMap.entrySet())
                arrayList.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
        try {
            return (HttpEntity)new UrlEncodedFormEntity(arrayList);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new RuntimeException(unsupportedEncodingException);
        }
    }

    private static HttpEntity createPUTEntity(Map<String, String> paramMap) {

        if (paramMap != null && paramMap.containsKey("json") && paramMap.keySet().size() == 1)
            try {
                return (HttpEntity)new StringEntity(paramMap.get("json"));
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new RuntimeException(unsupportedEncodingException);
        }
        ArrayList<BasicNameValuePair> arrayList = new ArrayList();

        if (paramMap != null)
            for (Map.Entry entry : paramMap.entrySet())
                arrayList.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
        try {
            //UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(arrayList);
            return (HttpEntity)new UrlEncodedFormEntity(arrayList);
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new RuntimeException(unsupportedEncodingException);
        }
        //return (HttpEntity)unsupportedEncodingException;
    }

    private HttpClient createSSLHttpClient() {
        DefaultHttpClient defaultHttpClient;
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            EngageSSLSocketFactory engageSSLSocketFactory = new EngageSSLSocketFactory(keyStore);
            engageSSLSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            BasicHttpParams basicHttpParams = new BasicHttpParams();

            HttpProtocolParams.setVersion((HttpParams)basicHttpParams, (ProtocolVersion) HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset((HttpParams)basicHttpParams, "UTF-8");
            HttpConnectionParams.setConnectionTimeout((HttpParams)basicHttpParams, 15000);
            HttpConnectionParams.setSoTimeout((HttpParams)basicHttpParams, 15000);
            SchemeRegistry schemeRegistry = new SchemeRegistry();

            Scheme scheme = //new Scheme();
                new Scheme("http", (SocketFactory) PlainSocketFactory.getSocketFactory(), 80);
            schemeRegistry.register(scheme);
            scheme = //new Scheme();
                new Scheme("https", (SocketFactory)engageSSLSocketFactory, 443);
            schemeRegistry.register(scheme);
            ThreadSafeClientConnManager threadSafeClientConnManager = //new ThreadSafeClientConnManager();
                new ThreadSafeClientConnManager((HttpParams)basicHttpParams, schemeRegistry);
            defaultHttpClient = //new DefaultHttpClient();
                new DefaultHttpClient((ClientConnectionManager)threadSafeClientConnManager, (HttpParams)basicHttpParams);

        } catch (Exception exception) {
            defaultHttpClient = new DefaultHttpClient();
        }
        return (HttpClient)defaultHttpClient;
    }

    private static HttpUriRequest makeRequest(HTTP_METHOD paramHTTP_METHOD, String paramString1, String paramString2, Map<String, String> paramMap, boolean paramBoolean) {
        HttpPost httpPost;
        HttpPut httpPut1;
        HttpGet httpGet;
        HttpPut httpPut2;
        Uri.Builder builder = new Uri.Builder();
        if (paramBoolean) {
            builder.scheme("https");//was https
        } else {
            builder.scheme("http");
        }
        builder.encodedAuthority(paramString1);
        builder.path(paramString2);
        switch (paramHTTP_METHOD) {
            default:
                throw new UnsupportedOperationException("Unsupported HTTP method: " + paramHTTP_METHOD.toString());
            case POST:
                httpPost = new HttpPost(builder.build().toString());
                httpPost.setEntity(createPOSTEntity(paramMap));
                httpPost.addHeader("Accept", "application/json");
                return (HttpUriRequest)httpPost;
            case PUT:
                httpPut2 = new HttpPut(builder.build().toString());
                httpPut2.setEntity(createPUTEntity(paramMap));
                httpPut1 = httpPut2;
                if (paramMap != null) {
                    httpPut1 = httpPut2;
                    if (paramMap.containsKey("json")) {
                        httpPut2.addHeader("Content-type", "application/json");
                        httpPut1 = httpPut2;
                    }
                }
                httpPut1.addHeader("Accept", "application/json");
                return (HttpUriRequest)httpPut1;
            case GET:
                if (paramMap != null)
                    for (Map.Entry<String, String> entry : paramMap.entrySet())
                        builder.appendQueryParameter((String)entry.getKey(), (String)entry.getValue());
                httpGet = new HttpGet(builder.build().toString());
                httpGet.addHeader("Accept", "application/json");
                return (HttpUriRequest)httpGet;
            case DELETE:
                break;
        }
        HttpDelete httpDelete = new HttpDelete(builder.build().toString());
        httpDelete.addHeader("Accept", "application/json");
        return (HttpUriRequest)httpDelete;
    }

    private Pair<HttpResponse, Integer> sendRequest() {
        Pair<HttpResponse, Integer> pair;
        pair = null;
        try {
            HttpClient httpClient = createSSLHttpClient();
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            Log.i(str, stringBuilder.append("sendRequest requesting: ").append(this.uriRequest.getURI()).append(" ").append(getMethod()).toString());
            HttpResponse httpResponse = httpClient.execute(this.uriRequest, (HttpContext)httpContext);
            pair = new Pair(httpResponse, Integer.valueOf(200));
        } catch (ConnectTimeoutException connectTimeoutException) {
            Log.w(TAG, "Error sending request, connection timed out. message: ", (Throwable)connectTimeoutException);
            pair = new Pair(null, Integer.valueOf(408));
        } catch (SocketTimeoutException socketTimeoutException) {
            Log.w(TAG, "SocketTimeoutException " + socketTimeoutException);
            pair = new Pair(null, Integer.valueOf(408));
        } catch (Exception exception) {
            Log.e(TAG, "Error sending request", exception);
            pair = new Pair(null, Integer.valueOf(503));
        }
        return pair;
    }

    protected EngageWebResponse doInBackground(EngageWebClient... paramVarArgs) {
        Log.v(TAG, "initiated " + this.method + " request in the background");
        EngageWebResponse engageWebResponse = new EngageWebResponse(sendRequest(), this);
        this.mOwner = paramVarArgs[0];
        return engageWebResponse;
    }

    public String getAuthority() {
        return this.authority;
    }

    public String getMethod() {
        return this.method.name();
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public String getUriPath() {
        return this.uriPath;
    }

    protected void onCancelled() {
        Log.v(TAG, "Aborting HttpRequest: " + this.uriRequest.getURI());
        this.uriRequest.abort();
    }

    protected void onPostExecute(EngageWebResponse paramEngageWebResponse) {
        if (paramEngageWebResponse.mResponseCode != 200)
            Log.w(TAG, String.format("HTTP %d received: %s", new Object[] { Integer.valueOf(paramEngageWebResponse.mResponseCode), paramEngageWebResponse.mResponseString }));
        this.mOwner.handleResult(paramEngageWebResponse);
    }

    public enum HTTP_METHOD {
        DELETE, GET, POST, PUT;

        static {
            //DELETE = new HTTP_METHOD("DELETE", 3);
            //$VALUES = new HTTP_METHOD[] { GET, PUT, POST, DELETE };
        }
    }
}
