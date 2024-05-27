package com.litongjava.ai.db.assistant;

import com.litongjava.ai.db.assistant.config.AbAiAssistantAppConfig;
import com.litongjava.jfinal.aop.annotation.AComponentScan;
import com.litongjava.tio.boot.TioApplication;

@AComponentScan
public class AbAiAssistantApp {
  public static void main(String[] args) {
    long start = System.currentTimeMillis();
    TioApplication.run(AbAiAssistantApp.class, new AbAiAssistantAppConfig(), args);
    long end = System.currentTimeMillis();
    System.out.println((end - start) + "ms");
  }
}
  