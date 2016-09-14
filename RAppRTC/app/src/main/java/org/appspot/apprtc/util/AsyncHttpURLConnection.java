/*
 *  Copyright 2015 The WebRTC Project Authors. All rights reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.appspot.apprtc.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Scanner;

import cn.renyuzhuo.rlib.rlog;

/**
 * Asynchronous http requests implementation.
 */
public class AsyncHttpURLConnection {
    private static final int HTTP_TIMEOUT_MS = 8000;
    private static final String HTTP_ORIGIN = "https://appr.tc";
    private final String method;
    private final String url;
    private final String message;
    private final AsyncHttpEvents events;
    private String contentType;

    /**
     * Http requests callbacks.
     */
    public interface AsyncHttpEvents {
        void onHttpError(String errorMessage);

        void onHttpComplete(String response);
    }

    public AsyncHttpURLConnection(String method, String url, String message,
                                  AsyncHttpEvents events) {
        rlog.d("创建AsyncHttpURLConnection设置属性");
        this.method = method;
        this.url = url;
        this.message = message;
        this.events = events;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void send() {
        rlog.d("发出请求");
        Runnable runHttp = new Runnable() {
            public void run() {
                sendHttpMessage();
            }
        };
        new Thread(runHttp).start();
    }

    private void sendHttpMessage() {
        try {
            rlog.d("发送HttpMessage");
            HttpURLConnection connection =
                    (HttpURLConnection) new URL(url).openConnection();
            rlog.d("创建HttpURLConnection");
            byte[] postData = new byte[0];
            if (message != null) {
                postData = message.getBytes("UTF-8");
            }
            connection.setRequestMethod(method);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setConnectTimeout(HTTP_TIMEOUT_MS);
            connection.setReadTimeout(HTTP_TIMEOUT_MS);
            // TODO(glaznev) - query request origin from pref_room_server_url_key preferences.
            connection.addRequestProperty("origin", HTTP_ORIGIN);
            boolean doOutput = false;
            if (method.equals("POST")) {
                doOutput = true;
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(postData.length);
            }
            if (contentType == null) {
                connection.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            } else {
                connection.setRequestProperty("Content-Type", contentType);
            }
            rlog.d("设置请求参数等");

            // Send POST request.
            if (doOutput && postData.length > 0) {
                OutputStream outStream = connection.getOutputStream();
                outStream.write(postData);
                outStream.close();
            }
            rlog.d("发送请求");

            rlog.d("获取结果");
            // Get response.
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                rlog.d("获取结果出错");
                events.onHttpError("Non-200 response to " + method + " to URL: "
                        + url + " : " + connection.getHeaderField(null));
                connection.disconnect();
                return;
            }
            InputStream responseStream = connection.getInputStream();
            String response = drainStream(responseStream);
            responseStream.close();
            connection.disconnect();
            events.onHttpComplete(response);
            rlog.d("对返回值进行处理");
        } catch (SocketTimeoutException e) {
            events.onHttpError("HTTP " + method + " to " + url + " timeout");
            rlog.e("HTTP " + method + " to " + url + " timeout");
        } catch (IOException e) {
            events.onHttpError("HTTP " + method + " to " + url + " error: "
                    + e.getMessage());
            rlog.e("HTTP " + method + " to " + url + " error: " + e.getMessage());
        }
    }

    // Return the contents of an InputStream as a String.
    private static String drainStream(InputStream in) {
        rlog.d("inputStream -> String");
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
