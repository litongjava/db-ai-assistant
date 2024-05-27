package com.litongjava.ai.db.assistant.config;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.alibaba.fastjson2.JSONObject;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.litongjava.tio.utils.json.FastJson2Utils;

public class EnjoyEngineConfigTest {

  @BeforeClass
  public static void beforeClass() {
    new EnjoyEngineConfig().config();
  }

  @Test
  public void test() {
    Engine engine = Engine.use();
    Template template = engine.getTemplate("init_prompt.txt");
    Map<String, String> values = new HashMap<>();
    values.put("value", "postgresql");
    String renderToString = template.renderToString(values);
    System.out.println(renderToString);
    
    JSONObject parseObject = FastJson2Utils.parseObject(renderToString);
    System.out.println(parseObject.toString());
    
  }
}
