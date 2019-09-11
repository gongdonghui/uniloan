package com.sup.paycenter.util;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: kouichi
 * @Date: 2018/11/6 17:33
 */
@Slf4j
public class OkBang {

    private static OkHttpClient client;
    private static OkHttpClient clientWithProxy;
    private static OkHttpClient.Builder builder = new OkHttpClient.Builder();

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final MediaType FILE = MediaType.parse("File/*");

    static {
        builder.connectTimeout(60, TimeUnit.SECONDS);
        builder.readTimeout(60, TimeUnit.SECONDS);
    }

    public static String get(String url) {
        return get(url, null, null);
    }

    public static String get(String url, Map<String, String> params) {
        return get(url, params, null, null);
    }

    public static String postJson(String url, String json) {
        return postJson(url, json, null, null);
    }

    public static String postFile(String url, File file) {
        return postFile(url, file, null, null);
    }

    public static String postForm(String url, Map<String, String> params) {
        return postForm(url, params, null, null);
    }

    public static String postMultiForm(String url, Map<String, String> params, Map<String, File> files) {
        return postMultiForm(url, params, files, null, null);
    }

    public static String get(String url, String proxyHost, Integer proxyPort) {
        return doHttp(url, null, proxyHost, proxyPort);
    }

    public static String get(String url, Map<String, String> params, String proxyHost, Integer proxyPort) {
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        params.forEach(((k, v) -> {
            sb.append(k);
            sb.append("=");
            sb.append(v);
            sb.append("&");
        }));
        sb.deleteCharAt(sb.length() - 1);
        return doHttp(url + sb.toString(), null, proxyHost, proxyPort);
    }

    public static String postJson(String url, String json, String proxyHost, Integer proxyPort) {
        RequestBody body = RequestBody.create(JSON, json);
        return doHttp(url, body, proxyHost, proxyPort);
    }

    public static String postFile(String url, File file, String proxyHost, Integer proxyPort) {
        RequestBody body = RequestBody.create(FILE, file);
        return doHttp(url, body, proxyHost, proxyPort);
    }

    public static String postForm(String url, Map<String, String> params, String proxyHost, Integer proxyPort) {
        FormBody.Builder builder = new FormBody.Builder();
        params.forEach((k, v) -> builder.add(k, v));
        return doHttp(url, builder.build(), proxyHost, proxyPort);
    }

    public static String postMultiForm(String url, Map<String, String> params, Map<String, File> files, String proxyHost, Integer proxyPort) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        if (null != params) {
            params.forEach((k, v) -> builder.addFormDataPart(k, v));
        }
        if (null != files) {
            files.forEach((k, v) -> builder.addFormDataPart(k, v.getName(), RequestBody.create(MediaType.parse("file/*"), v)));
        }
        return doHttp(url, builder.build(), proxyHost, proxyPort);
    }

    private static String doHttp(String url, RequestBody body, String proxyHost, Integer proxyPort) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        if (null != body) {
            builder.post(body);
        }
        return doHttp0(builder.build(), proxyHost, proxyPort);
    }

    private static String doHttp0(Request request, String proxyHost, Integer proxyPort) {
        try {
            Response response;
            if (!Strings.isNullOrEmpty(proxyHost) && null != proxyPort) {
                clientWithProxy = getClientWithProxy(proxyHost, proxyPort);
                response = clientWithProxy.newCall(request).execute();
            } else {
                client = getClient();
                response = client.newCall(request).execute();
            }
            return response.body().string();
        } catch (Exception e) {
            log.error("OkBang调用异常==>", e);
        }
        return "";
    }

    private static OkHttpClient getClientWithProxy(String proxyHost, Integer proxyPort) {
        if (null == clientWithProxy) {
            synchronized (OkHttpClient.class) {
                if (null == clientWithProxy) {
                    clientWithProxy = builder.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort))).build();
                }
            }
        }
        return clientWithProxy;
    }

    private static OkHttpClient getClient() {
        if (null == client) {
            synchronized (OkHttpClient.class) {
                if (null == client) {
                    client = builder.build();
                }
            }
        }
        return client;
    }

}
