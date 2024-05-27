package com.litongjava.open.chat.services;

import org.junit.Test;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.litongjava.ai.db.assistant.services.OpenaiV1ChatService;
import com.litongjava.jfinal.aop.Aop;

public class OpenaiV1ChatServiceTest {

  @Test
  public void test01() {

    // 创建messages数组
    JSONArray messagesArray = new JSONArray();

    // 添加第一个消息
    JSONObject userMessage = new JSONObject();
    userMessage.put("role", "user");
    userMessage.put("content", "Fall 2024 LAW 101");
    messagesArray.add(userMessage);

    JSONObject openAiRequestVo = new JSONObject();
    openAiRequestVo.put("messages", messagesArray);

    OpenaiV1ChatService openaiV1ChatService = Aop.get(OpenaiV1ChatService.class);
    System.out.println(openAiRequestVo.toString());
    JSONObject beforeCompletions = openaiV1ChatService.beforeCompletions(openAiRequestVo);
    System.out.println(beforeCompletions.toString());
  }

  @Test
  public void test02() {

    // 创建messages数组
    JSONArray messagesArray = new JSONArray();

    // 添加第一个消息
    JSONObject userMessage = new JSONObject();
    userMessage.put("role", "user");
    userMessage.put("content", "Fall 2024 LAW 101");
    messagesArray.add(userMessage);

    JSONObject openAiRequestVo = new JSONObject();
    openAiRequestVo.put("messages", messagesArray);

    OpenaiV1ChatService openaiV1ChatService = Aop.get(OpenaiV1ChatService.class);
    System.out.println(openAiRequestVo.toString());

    openAiRequestVo = openaiV1ChatService.beforeCompletions(openAiRequestVo);
    System.out.println(openAiRequestVo.toString());

    openAiRequestVo = openaiV1ChatService.beforeCompletions(openAiRequestVo);
    System.out.println(openAiRequestVo.toString());
  }

}
