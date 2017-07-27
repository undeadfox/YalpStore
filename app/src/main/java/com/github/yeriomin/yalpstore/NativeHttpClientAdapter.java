package com.github.yeriomin.yalpstore;

import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.github.yeriomin.playstoreapi.AuthException;
import com.github.yeriomin.playstoreapi.GooglePlayAPI;
import com.github.yeriomin.playstoreapi.GooglePlayException;
import com.github.yeriomin.playstoreapi.HttpClientAdapter;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class NativeHttpClientAdapter extends HttpClientAdapter {

    static private final int TIMEOUT = 15000;

    static {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    @Override
    public byte[] get(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(buildUrl(url, params)).openConnection();
        return request(connection, null, headers);
    }

    @Override
    public byte[] postWithoutBody(String url, Map<String, String> urlParams, Map<String, String> headers) throws IOException {
        return post(buildUrl(url, urlParams), new HashMap<String, String>(), headers);
    }

    @Override
    public byte[] post(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        return post(url, buildFormBody(params).getBytes(), headers);
    }

    @Override
    public byte[] post(String url, byte[] body, Map<String, String> headers) throws IOException {
        if (!headers.containsKey("Content-Type")) {
            headers.put("Content-Type", "application/x-protobuf");
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        return request(connection, body, headers);
    }

    @Override
    public String buildUrl(String url, Map<String, String> params) {
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (String key: params.keySet()) {
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }

    protected byte[] request(HttpURLConnection connection, byte[] body, Map<String, String> headers) throws IOException {
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        connection.setRequestProperty("Accept-Encoding", "gzip");
        for (String headerName: headers.keySet()) {
            connection.addRequestProperty(headerName, headers.get(headerName));
        }
        addBody(connection, body);

        byte[] content;
        Log.i(getClass().getName(), "Requesting " + connection.getURL().toString());
        connection.connect();

        int code = connection.getResponseCode();
        Log.i(getClass().getName(), "HTTP result code " + code);
        boolean isGzip = connection.getContentEncoding().contains("gzip");
        try {
            content = readFully(connection.getInputStream(), isGzip);
        } catch (IOException e) {
            content = readFully(connection.getErrorStream(), isGzip);
            Log.e(getClass().getName(), "Exception " + e.getClass().getName() + " " + e.getMessage());
            if (code < 400) {
                throw e;
            }
        } finally {
            connection.disconnect();
        }
        processHttpErrorCode(code, content);
        return content;
    }

    static public String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // Unlikely
        }
        return null;
    }

    static private void addBody(HttpURLConnection connection, byte[] body) throws IOException {
        if (null == body) {
            body = new byte[0];
        }
        connection.addRequestProperty("Content-Length", Integer.toString(body.length));
        if (body.length > 0) {
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(body);
            outputStream.close();
        } else {
            connection.setDoOutput(false);
        }
    }

    static private void processHttpErrorCode(int code, byte[] content) throws GooglePlayException {
        if (code == 401 || code == 403) {
            AuthException e = new AuthException("Auth error", code);
            Map<String, String> authResponse = GooglePlayAPI.parseResponse(new String(content));
            if (authResponse.containsKey("Error") && authResponse.get("Error").equals("NeedsBrowser")) {
                e.setTwoFactorUrl(authResponse.get("Url"));
            }
            throw e;
        } else if (code >= 500) {
            throw new GooglePlayException("Server error", code);
        } else if (code >= 400) {
            throw new GooglePlayException("Malformed request", code);
        }
    }

    static private byte[] readFully(InputStream inputStream, boolean gzipped) throws IOException {
        if (gzipped) {
            inputStream = new GZIPInputStream(inputStream);
        }
        InputStream bufferedInputStream = new BufferedInputStream(inputStream);
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        while ((bytesRead = bufferedInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] result = outputStream.toByteArray();
        Util.closeSilently(bufferedInputStream);
        Util.closeSilently(outputStream);
        return result;
    }

    static private String buildFormBody(Map<String, String> params) {
        List<String> keyValuePairs = new ArrayList<>();
        for (String key: params.keySet()) {
            keyValuePairs.add(urlEncode(key) + "=" + urlEncode(params.get(key)));
        }
        return TextUtils.join("&", keyValuePairs);
    }
}
