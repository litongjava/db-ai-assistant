package com.litongjava.ai.db.assistant.client;

import org.junit.Test;

import com.jfinal.kit.Kv;
import com.litongjava.tio.utils.json.JsonUtils;

public class StreamModelUtilsTest {

  @Test
  public void test() {
    Kv kv = StreamModelUtils.buildMessage("system", "hi");
    String json = JsonUtils.toJson(kv);
    System.out.println(json);
  }

}
