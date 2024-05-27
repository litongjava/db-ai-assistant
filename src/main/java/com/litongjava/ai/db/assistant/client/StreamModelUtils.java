package com.litongjava.ai.db.assistant.client;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.Kv;

public class StreamModelUtils {

  // {"id":"chatcmpl-9P3fvvyk4IuCprCnvMytoKN8UtskC","object":"chat.completion.chunk","created":1715759355,"model":"gpt-3.5-turbo-0125",
  // "system_fingerprint":null,"choices":[{"index":0,"delta":{"role":"assistant","content":""},"logprobs":null,"finish_reason":null}]}
  public static Kv buildMessage(String role, String content) {
    // 获取当前时间的 Instant 对象
    Instant now = Instant.now();

    // 将 Instant 对象转换为 Unix 时间戳（以秒为单位）
    long unixTimestamp = now.getEpochSecond();

    Kv delta = Kv.create();
    delta.set("role", role);
    delta.set("content", content);

    Kv message = Kv.create();
    message.set("index", 0);
    message.set("delta", delta);

    List<Kv> choices = new ArrayList<>();
    choices.add(message);

    Kv kv = Kv.create();
    kv.set("id", "chatcmpl-9P3fvvyk4IuCprCnvMytoKN8UtskC");
    kv.set("object", "chat.completion.chunk");
    kv.set("created", unixTimestamp);
    kv.set("model", "litongjava");
    kv.set("choices", choices);
    return kv;
  }

}
