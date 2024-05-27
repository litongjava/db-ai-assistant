package com.litongjava.ai.db.assistant.config;

import com.litongjava.tio.boot.context.TioBootConfiguration;

public class AbAiAssistantAppConfig implements TioBootConfiguration {

  @Override
  public void config() {
    new ExecutorServiceConfig().config();
    new HttpRequestHanlderConfig().config();
    new EnjoyEngineConfig().config();
    new TableToJsonConfig().activeRecordPlugin();
  }
}
