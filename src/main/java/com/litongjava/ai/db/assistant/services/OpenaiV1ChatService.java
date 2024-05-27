package com.litongjava.ai.db.assistant.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jfinal.kit.Kv;
import com.jfinal.template.Engine;
import com.jfinal.template.Template;
import com.litongjava.ai.db.assistant.constants.Fns;
import com.litongjava.ai.db.assistant.constants.Prompts;
import com.litongjava.jfinal.aop.Aop;
import com.litongjava.tio.utils.json.FastJson2Utils;
import com.litongjava.tio.utils.json.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpenaiV1ChatService {

  public JSONObject beforeCompletions(JSONObject openAiRequestVo) {
    Engine engine = Engine.use();
    Template template = engine.getTemplate("init_prompt.txt");
    Map<String, String> values = new HashMap<>();
    values.put("value", "postgresql");
    String renderToString = template.renderToString(values);
    JSONObject initPrompt = FastJson2Utils.parseObject(renderToString);

    JSONArray jsonArray = openAiRequestVo.getJSONArray("messages");
    String firstContent = jsonArray.getJSONObject(0).getString("content");
    log.info("content:{}", firstContent);
    if (firstContent.startsWith("init_prompt") || firstContent.startsWith(Prompts.phrase_sentence)) {
      return openAiRequestVo;
    } else {
      JSONObject jsonObject = initPrompt.getJSONArray("messages").getJSONObject(0);
      jsonArray.add(0, jsonObject);
    }

    JSONArray functions = openAiRequestVo.getJSONArray("functions");
    if (functions == null) {
      functions = initPrompt.getJSONArray("functions");
      openAiRequestVo.put("functions", functions);
    }

    return openAiRequestVo;
  }

  public String processLine(String line) {
//    log.info("line:{}", line);
    return line;
  }
  

  public String processData(String data) {
    log.info("data:{}",data);
    return data;
    
  }

  public void completionContent(StringBuffer completionContent) {

    log.info("completionContent:{}", completionContent);

  }

  public String functionCall(StringBuffer fnCallName, StringBuffer fnCallArgs) {
    log.info("fn:{},{}", fnCallName.toString(), fnCallArgs.toString());
    if (Fns.find.equals(fnCallName.toString())) {
      JSONObject parseObject = FastJson2Utils.parseObject(fnCallArgs.toString());
      String sql = parseObject.getString("sql");
      CoursesService coursesService = Aop.get(CoursesService.class);
      List<Kv> find = null;
      try {
        find = coursesService.find(sql);
      } catch (Exception e) {
        return e.getMessage();
      }

      return JsonUtils.toJson(find);

    }
    return null;

  }

}
