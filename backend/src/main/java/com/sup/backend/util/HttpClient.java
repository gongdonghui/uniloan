package com.sup.backend.util;

import com.google.common.net.HttpHeaders;
import org.asynchttpclient.*;
import org.asynchttpclient.proxy.ProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by 123 on 2016/4/20.
 */
public class HttpClient {

  private final static Logger logger = LoggerFactory.getLogger(HttpClient.class);
  final static DefaultAsyncHttpClientConfig.Builder builder = Dsl.config();
  static AsyncHttpClient client;

  static {
    builder.setConnectTimeout(10*1000);
    builder.setReadTimeout(30*1000);
    builder.setMaxConnections(6*1000);
    builder.setMaxConnectionsPerHost(1000);
    client = Dsl.asyncHttpClient(builder.build());
  }

  public static String httpGet(String url, String proxy_host, Integer proxy_port, int timeout) throws Exception {
    BoundRequestBuilder builder = client.prepareGet(url).setProxyServer(new ProxyServer.Builder(proxy_host, proxy_port)).setRequestTimeout(timeout);
    return builder.execute().get().getResponseBody();
  }

  public static String httpGet(String url, String proxy_host, Integer proxy_port, int timeout, Map<String, String> params) throws Exception {
    BoundRequestBuilder builder = client.prepareGet(url).setProxyServer(new ProxyServer.Builder(proxy_host, proxy_port)).setRequestTimeout(timeout);
    if (params != null) {
      params.forEach((k, v) -> {
        builder.addQueryParam(k, v);
      });
    }
    return builder.execute().get().getResponseBody();
  }

  public static String httpGetByAuth(String url, String user, String pwd) throws Exception {
    String sig = Base64.getEncoder().encodeToString((user + ":" + pwd).getBytes(StandardCharsets.UTF_8));
    BoundRequestBuilder builder = client.prepareGet(url).addHeader(HttpHeaders.AUTHORIZATION, "Basic " + sig);
    Future<Response> response_future = builder.execute();
    return response_future.get().getResponseBody();
  }

  public static <T> void httpGetByAuthAsync(String url, String user, String pwd, AsyncCompletionHandler<T> handler) throws Exception {
    String sig = Base64.getEncoder().encodeToString((user + ":" + pwd).getBytes(StandardCharsets.UTF_8));
    BoundRequestBuilder builder = client.prepareGet(url).addHeader(HttpHeaders.AUTHORIZATION, "Basic " + sig);
    builder.execute(handler);
  }

  public static String httpPutByAuth(String url, String user, String pwd, String body) throws Exception {
    String sig = Base64.getEncoder().encodeToString((user + ":" + pwd).getBytes(StandardCharsets.UTF_8));
    BoundRequestBuilder builder = client.preparePut(url).addHeader(HttpHeaders.AUTHORIZATION, "Basic " + sig).setBody(body);
    Future<Response> response_future = builder.execute();
    return response_future.get().getResponseBody();
  }


  public static String httpDeleteByAuth(String url, String user, String pwd) throws Exception {
    String sig = Base64.getEncoder().encodeToString((user + ":" + pwd).getBytes(StandardCharsets.UTF_8));
    BoundRequestBuilder builder = client.prepareDelete(url).addHeader(HttpHeaders.AUTHORIZATION, "Basic " + sig);
    Future<Response> response_future = builder.execute();
    return response_future.get().getResponseBody();
  }

  public static Response httpPostByAuth(String proxy_host, Integer proxy_port, String url, String user, String pwd, String data) throws Exception {
    String sig = Base64.getEncoder().encodeToString((user + ":" + pwd).getBytes(StandardCharsets.UTF_8));
    ProxyServer pxy = (new ProxyServer.Builder(proxy_host, proxy_port)).build();
    BoundRequestBuilder builder = client.preparePost(url).addHeader(HttpHeaders.AUTHORIZATION, "Basic " + sig).setBody(data).setProxyServer(pxy);
    Future<Response> response_future = builder.execute();
    return response_future.get();
  }

  public static Response httpPostByAuthDirect(String url, String user, String pwd, String data) throws Exception {
    String sig = Base64.getEncoder().encodeToString((user + ":" + pwd).getBytes(StandardCharsets.UTF_8));
    BoundRequestBuilder builder = client.preparePost(url).addHeader(HttpHeaders.AUTHORIZATION, "Basic " + sig).setBody(data);
    Future<Response> response_future = builder.execute();
    return response_future.get();
  }

  public static String httpGet(String url, Map<String, String> params) throws Exception {
    BoundRequestBuilder builder = client.prepareGet(url).addHeader("Content-Type", "application/json");
    if (params != null) {
      params.forEach((k, v) -> {
        builder.addQueryParam(k, v);
      });
    }
    //System.out.println("url: " + builder.toString());
    Future<Response> response_future = builder.execute();
    return response_future.get().getResponseBody();
  }

  public static String httpPost(String url, String post_body) throws ExecutionException, InterruptedException {
    BoundRequestBuilder builer = client.preparePost(url).addHeader("Content-Type", "application/json").setBody(post_body);
    Future<Response> response_future = builer.execute();
    return response_future.get().getResponseBody();
  }

  public static String httpPostWithParams(String url, Map<String, String> params) throws ExecutionException, InterruptedException {
    BoundRequestBuilder builer = client.preparePost(url).addHeader("Content-Type", "application/json");
    if (params != null) {
      params.forEach((k, v) -> {
        builer.addQueryParam(k, v);
      });
    }
    Future<Response> response_future = builer.execute();
    return response_future.get().getResponseBody();
  }

  public static <T>  void httpPostJsonAsync(String url, String post_body, AsyncCompletionHandler<T> handler) {
    BoundRequestBuilder builder = client.preparePost(url).addHeader("Content-Type", "application/json").setBody(post_body);
    builder.execute(handler);
  }

  public static <T> void HttpGetAsync(String url, Map<String, String> params, AsyncCompletionHandler<T> handler) {
    BoundRequestBuilder builer = client.prepareGet(url).addHeader("Content-Type", "application/json");
    if (params != null) {
      params.forEach((k, v) -> {
        builer.addQueryParam(k, v);
      });
    }
    builer.execute(handler);
  }

  public static <T> void HttpPostAsync(String url, Map<String, String> params, AsyncCompletionHandler<T> handler) {
    BoundRequestBuilder builer = client.preparePost(url).addHeader("Content-Type", "application/json");
    if (params != null) {
      params.forEach((k, v) -> {
        builer.addQueryParam(k, v);
      });
    }
    builer.execute(handler);
  }
}
