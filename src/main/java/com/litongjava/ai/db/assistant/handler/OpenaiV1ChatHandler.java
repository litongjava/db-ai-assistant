package com.litongjava.ai.db.assistant.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jfinal.kit.Kv;
import com.litongjava.ai.db.assistant.client.OpenAiClient;
import com.litongjava.ai.db.assistant.constants.OpenAiConstatns;
import com.litongjava.ai.db.assistant.services.OpenaiV1ChatService;
import com.litongjava.tio.boot.http.TioControllerContext;
import com.litongjava.tio.core.ChannelContext;
import com.litongjava.tio.core.Tio;
import com.litongjava.tio.http.common.HeaderName;
import com.litongjava.tio.http.common.HeaderValue;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.http.common.HttpResponse;
import com.litongjava.tio.http.common.encoder.ChunkEncoder;
import com.litongjava.tio.http.common.sse.SseBytesPacket;
import com.litongjava.tio.http.server.util.HttpServerResponseUtils;
import com.litongjava.tio.http.server.util.SseUtils;
import com.litongjava.tio.utils.environment.EnvUtils;
import com.litongjava.tio.utils.json.FastJson2Utils;
import com.litongjava.tio.utils.json.JsonUtils;
import com.litongjava.tio.utils.resp.RespVo;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Slf4j
public class OpenaiV1ChatHandler {

  private OpenaiV1ChatService openaiV1ChatService = new OpenaiV1ChatService();

  public HttpResponse completions(HttpRequest httpRequest) {
    long start = System.currentTimeMillis();
    HttpResponse httpResponse = TioControllerContext.getResponse();
    // HttpServerResponseUtils.enableCORS(httpResponse, new HttpCors());

    String requestURI = httpRequest.getRequestURI();

    Map<String, String> headers = httpRequest.getHeaders();
    String bodyString = httpRequest.getBodyString();
    log.info("requestURI:{},header:{},bodyString:{}", requestURI, headers, bodyString);

    // 替换基本的一些值
    String authorization = EnvUtils.get("OPENAI_API_KEY");
    headers.put("authorization", "Bearer " + authorization);
    headers.put("host", "api.openai.com");

    Boolean stream = true;
    JSONObject openAiRequestVo = null;
    if (bodyString != null) {
      openAiRequestVo = FastJson2Utils.parseObject(bodyString);
      stream = openAiRequestVo.getBoolean("stream");
      openAiRequestVo.put("model", OpenAiConstatns.gpt_4o_2024_05_13);
    }

    if (stream != null && stream) {
      if (openAiRequestVo != null) {
        // 告诉默认的处理器不要将消息体发送给客户端,因为后面会手动发送
        httpResponse.setSend(false);
        ChannelContext channelContext = httpRequest.getChannelContext();
        openAiRequestVo = openaiV1ChatService.beforeCompletions(openAiRequestVo);
        streamResponse(channelContext, httpResponse, headers, openAiRequestVo, start);
      } else {
        return httpResponse.setJson(RespVo.fail("empty body"));
      }
    } else {
      openAiRequestVo = openaiV1ChatService.beforeCompletions(openAiRequestVo);
      Response response = OpenAiClient.completions(headers, openAiRequestVo.toString());
      HttpServerResponseUtils.fromOkHttp(response, httpResponse);
      httpResponse.setHasGzipped(true);
      httpResponse.removeHeaders("Transfer-Encoding");
      httpResponse.removeHeaders("Server");
      httpResponse.removeHeaders("Date");
      httpResponse.setHeader("Connection", "close");
      httpResponse.removeHeaders("Set-Cookie");
      long end = System.currentTimeMillis();
      log.info("finish llm in {} (ms):", (end - start));
    }

    return httpResponse;
  }

  /**
   * 流式请求和响应
   *
   * @param channelContext
   * @param httpResponse
   * @param headers
   * @param start
   */
  public void streamResponse(ChannelContext channelContext, HttpResponse httpResponse, Map<String, String> headers,
      JSONObject requestBody, long start) {

    OpenAiClient.completions(headers, requestBody.toString(), new Callback() {

      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
        // 直接发送
        httpResponse.setSend(true);
        httpResponse.setJson(RespVo.fail(e.getMessage()));
        Tio.send(channelContext, httpResponse);

      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if (!response.isSuccessful()) {
          httpResponse.setSend(true);
          HttpServerResponseUtils.fromOkHttp(response, httpResponse);
          httpResponse.setHasGzipped(true);
          httpResponse.removeHeaders("Content-Length");
          // 响应
          Tio.send(channelContext, httpResponse);
          return;
        }
        // 设置sse请求头
        httpResponse.setServerSentEventsHeader();
        // 60秒后客户端关闭连接
        httpResponse.addHeader(HeaderName.Keep_Alive, HeaderValue.from("timeout=60"));
        httpResponse.addHeader(HeaderName.Transfer_Encoding, HeaderValue.from("chunked"));
        if (!httpResponse.isSend()) { // 不要让处理器发送,我来发送
          // 发送http 响应头,告诉客户端保持连接
          Tio.send(channelContext, httpResponse);
        }

        try (ResponseBody responseBody = response.body()) {
          if (responseBody == null) {
            String message = "response body is null";
            log.error(message);
            SseBytesPacket ssePacket = new SseBytesPacket(ChunkEncoder.encodeChunk(message.getBytes()));
            Tio.send(channelContext, ssePacket);
            closeSeeConnection(channelContext);
            return;
          }
          StringBuffer completionContent = new StringBuffer();
          StringBuffer fnCallName = new StringBuffer();
          StringBuffer fnCallArgs = new StringBuffer();

          StringBuffer toolFnCallId = new StringBuffer();
          StringBuffer toolFnCallName = new StringBuffer();
          StringBuffer toolFnCallArgs = new StringBuffer();

          String line;
          while ((line = responseBody.source().readUtf8Line()) != null) {
            // 必须添加一个回车符号
            byte[] bytes = (line + "\n\n").getBytes();
            if (line.length() < 1) {
              continue;
            }
            line = openaiV1ChatService.processLine(line);

            if (line.length() > 6) {
              int indexOf = line.indexOf(':');
              String data = line.substring(indexOf + 1, line.length());
              openaiV1ChatService.processData(data);
              if (data.endsWith("}")) {
                JSONObject parseObject = FastJson2Utils.parseObject(data);
                JSONArray choices = parseObject.getJSONArray("choices");
                if (choices.size() > 0) {
                  String content = choices.getJSONObject(0).getJSONObject("delta").getString("content");
                  // 只发送content信息
                  if (content != null) {
                    SseUtils.pushChunk(channelContext, bytes);
                  }
                  extraChoices(choices, completionContent, fnCallName, fnCallArgs, toolFnCallId, toolFnCallName,
                      toolFnCallArgs);
                }
              }
            }
          }
          openaiV1ChatService.completionContent(completionContent);

          if (fnCallName.length() > 0) {
            processFnCall(channelContext, httpResponse, headers, requestBody, start, fnCallName, fnCallArgs, "user",
                null);
          } else if (toolFnCallName.length() > 0) {
            processFnCall(channelContext, httpResponse, headers, requestBody, start, toolFnCallName, toolFnCallArgs,
                "tool", toolFnCallId.toString());

          } else {
            closeSeeConnection(channelContext);
          }
        }
      }
    });
  }

  public void processFnCall(ChannelContext channelContext, HttpResponse httpResponse, Map<String, String> headers,
      JSONObject requestBody, long start, StringBuffer fnCallName, StringBuffer fnCallArgs, String roleName,
      String fnCallId) {

    Kv functionCallResult = openaiV1ChatService.functionCall(channelContext, fnCallName, fnCallArgs);
    // 再次发送到大模型
    if (functionCallResult != null) {
      long newStart = System.currentTimeMillis();
      JSONArray messages = requestBody.getJSONArray("messages");

      Kv functionCall = Kv.by("name", fnCallName).set("arguments", fnCallArgs);

      // 查询结果
      Kv result = Kv.by("content", JsonUtils.toJson(functionCallResult));
      Kv lastMesage = Kv.by("role", "assistant");

      // assistantMessage.set("role", "s").set("content", null);

      if (fnCallId.length() > 0) {
        result.set("role", "tool").set("tool_call_id", fnCallId).set("name", fnCallName);

        List<Kv> toolCalls = new ArrayList<>(1);
        toolCalls.add(Kv.by("id", fnCallId).set("function", functionCall).set("type", "function"));
        lastMesage.set("tool_calls", toolCalls);
      } else {
        result.set("role", "system");
        lastMesage.set("function_call", functionCall);

      }
      messages.add(lastMesage);
      messages.add(result);
      // 防止重复发送响应头
      httpResponse.setSend(true);
      streamResponse(channelContext, httpResponse, headers, requestBody, newStart);

    } else {
      long end = System.currentTimeMillis();
      log.info("finish llm in {} (ms):", (end - start));
      closeSeeConnection(channelContext);
    }
  }

  /**
   * 发送一个大小为 0 的 chunk 以表示消息结束
   * @param channelContext
   */
  public void closeSeeConnection(ChannelContext channelContext) {
    // 关闭连接
    byte[] zeroChunk = ChunkEncoder.encodeChunk(new byte[0]);
    SseBytesPacket endPacket = new SseBytesPacket(zeroChunk);
    Tio.send(channelContext, endPacket);

    try {
      // 给客户端足够的时间接受消息
      Thread.sleep(1000);
      Tio.remove(channelContext, "remove");
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unused")
  private void test(ChannelContext channelContext) {
    for (int i = 0; i < 100; i++) {
      // String line = "data:鲁";
      String line = "data:{\"id\":\"chatcmpl-9P3fvvyk4IuCprCnvMytoKN8UtskC\",\"object\":\"chat.completion.chunk\",\"created\":1715759355,\"model\":\"gpt-3.5-turbo-0125\",\"system_fingerprint\":null,\"choices\":[{\"index\":0,\"delta\":{\"content\":\"鲁"
          + i + "\"},\"logprobs\":null,\"finish_reason\":null}]}";
      log.info("send:{}", line);

      byte[] bytes = (line + "\n\n").getBytes();

      // 将数据编码成chunked格式并返回,这样客户端的流式输出会更流程
      SseBytesPacket ssePacket = new SseBytesPacket(ChunkEncoder.encodeChunk(bytes));
      // 再次向客户端发送消息
      Tio.send(channelContext, ssePacket);
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public void extraChoices(JSONArray choices, StringBuffer complectionContent, StringBuffer fnCallName,
      StringBuffer fnCallArgs, StringBuffer tooFnCallId, StringBuffer tooFnCallName, StringBuffer tooFnCallArgs) {
    if (choices.size() > 0) {
      for (int i = 0; i < choices.size(); i++) {
        JSONObject delta = choices.getJSONObject(i).getJSONObject("delta");
        String part = delta.getString("content");
        if (part != null) {
          complectionContent.append(part);
        }
        String functionCallString = delta.getString("function_call");
        if (functionCallString != null) {
          JSONObject functionCall = FastJson2Utils.parseObject(functionCallString);
          String name = functionCall.getString("name");
          if (name != null) {
            fnCallName.append(name);
          }

          String arguments = functionCall.getString("arguments");
          if (arguments != null) {
            // System.out.println("arguments:" + arguments);
            fnCallArgs.append(arguments);
          }
        }
        String toolCallsString = delta.getString("tool_calls");
        if (toolCallsString != null) {
          // 不考虑执行多个的问题
          JSONArray parseArray = FastJson2Utils.parseArray(toolCallsString);
          JSONObject toolCall = parseArray.getJSONObject(0);
          String id = toolCall.getString("id");
          if (id != null) {
            tooFnCallId.append(id);
          }

          JSONObject funcation = toolCall.getJSONObject("function");
          String name = funcation.getString("name");
          if (name != null) {
            tooFnCallName.append(name);
          }
          String arguments = funcation.getString("arguments");
          if (arguments != null) {
            tooFnCallArgs.append(arguments);
          }
        }
      }
    }
  }
}