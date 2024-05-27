package com.litongjava.ai.db.assistant.client;

import java.io.IOException;
import java.util.Map;

import com.litongjava.ai.db.assistant.constants.OpenAiConstatns;
import com.litongjava.ai.db.assistant.instance.OkHttpClientPool;

import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAiClient {

  public static Response completions(Map<String, String> requestHeaders, String bodyString) {

    OkHttpClient httpClient = OkHttpClientPool.getHttpClient();

    MediaType mediaType = MediaType.parse("application/json");

    RequestBody body = RequestBody.create(mediaType, bodyString);

    Headers headers = Headers.of(requestHeaders);

    Request request = new Request.Builder() //
        .url(OpenAiConstatns.server_url + "/v1/chat/completions") //
        .method("POST", body).headers(headers) //
        .build();
    try {
      return httpClient.newCall(request).execute();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void completions(Map<String, String> requestHeaders, String bodyString, Callback callback) {
    System.out.println(bodyString);
    OkHttpClient httpClient = OkHttpClientPool.getHttpClient();

    MediaType mediaType = MediaType.parse("application/json");

    RequestBody body = RequestBody.create(mediaType, bodyString);

    Headers headers = Headers.of(requestHeaders);

    Request request = new Request.Builder() //
        .url(OpenAiConstatns.server_url + "/v1/chat/completions") //
        .method("POST", body).headers(headers) //
        .build();
    httpClient.newCall(request).enqueue(callback);
  }

}