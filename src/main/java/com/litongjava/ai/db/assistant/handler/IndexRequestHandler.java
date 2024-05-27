package com.litongjava.ai.db.assistant.handler;

import java.io.IOException;
import java.util.Map;

import com.litongjava.ai.db.assistant.constants.OpenAiConstatns;
import com.litongjava.ai.db.assistant.instance.OkHttpClientPool;
import com.litongjava.tio.boot.http.TioControllerContext;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.http.common.HttpResponse;
import com.litongjava.tio.http.common.RequestLine;
import com.litongjava.tio.http.server.util.HttpServerRequestUtils;
import com.litongjava.tio.http.server.util.HttpServerResponseUtils;
import com.litongjava.tio.utils.environment.EnvUtils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

//@Slf4j
public class IndexRequestHandler {

  public HttpResponse index(HttpRequest httpRequest) {
    HttpResponse httpResponse = TioControllerContext.getResponse();
    printRequest(httpRequest);
    // 修改授权信息
    String authorization = EnvUtils.get("OPENAI_API_KEY");
    httpRequest.addHeader("authorization", "Bearer "+authorization);

    // response.setBody(requestString.toString().getBytes());
    Request okHttpReqeust = HttpServerRequestUtils.toOkHttp(OpenAiConstatns.server_url, httpRequest);
    OkHttpClient httpClient = OkHttpClientPool.getHttpClient();

    try (Response okHttpResponse = httpClient.newCall(okHttpReqeust).execute()) {
      
      HttpServerResponseUtils.fromOkHttp(okHttpResponse, httpResponse);
      printResponse(okHttpResponse);
      
      httpResponse.setHasGzipped(true);
//      httpResponse.removeHeaders("Set-Cookie");
      httpResponse.removeHeaders("Transfer-Encoding");
      httpResponse.removeHeaders("Server");
      httpResponse.removeHeaders("Date");
      httpResponse.setHeader("Connection", "close");

    } catch (IOException e) {
      e.printStackTrace();
    }

    return httpResponse;
  }

  private void printResponse(Response okHttpResponse) {
    if (okHttpResponse.isSuccessful()) {
      try {
        System.out.println("response:\n" + okHttpResponse.body().string());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }

  private StringBuffer printRequest(HttpRequest httpRequest) {
    StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append("request:\n");
    RequestLine requestLine = httpRequest.getRequestLine();
    stringBuffer.append(requestLine.toString()).append("\n");
    Map<String, String> headers = httpRequest.getHeaders();
    for (Map.Entry<String, String> e : headers.entrySet()) {
      stringBuffer.append(e.getKey() + ":" + e.getValue()).append("\n");
    }

    // 请求体
    String contentType = httpRequest.getContentType();
    if (contentType != null) {
      if (contentType.startsWith("application/json")) {
        stringBuffer.append(httpRequest.getBodyString());

      } else if (contentType.startsWith("application/x-www-form-urlencoded")) {
        Map<String, Object[]> params = httpRequest.getParams();
        for (Map.Entry<String, Object[]> e : params.entrySet()) {
          stringBuffer.append(e.getKey() + ": " + e.getValue()[0]).append("\n");
        }

      } else if (contentType.startsWith("application/x-www-form-urlencoded")) {
        Map<String, Object[]> params = httpRequest.getParams();
        for (Map.Entry<String, Object[]> e : params.entrySet()) {
          Object value = e.getValue()[0];
          // 添加参数
          if (value instanceof String) {
            stringBuffer.append(e.getKey()).append(":").append(e.getValue()[0]).append("\n");
          } else {
            stringBuffer.append(e.getKey()).append(":").append("binary \n");
          }
        }
      }
    }

    System.out.println(stringBuffer.toString());
    return stringBuffer;

  }
}
