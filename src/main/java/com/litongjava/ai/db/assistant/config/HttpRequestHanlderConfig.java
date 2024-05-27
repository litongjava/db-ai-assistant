package com.litongjava.ai.db.assistant.config;

import com.litongjava.ai.db.assistant.handler.GetConfigHandler;
import com.litongjava.ai.db.assistant.handler.IndexRequestHandler;
import com.litongjava.ai.db.assistant.handler.OpenAiV1Handler;
import com.litongjava.ai.db.assistant.handler.OpenaiV1ChatHandler;
import com.litongjava.tio.boot.server.TioBootServer;
import com.litongjava.tio.http.server.router.HttpReqeustSimpleHandlerRoute;

public class HttpRequestHanlderConfig {

  public void config() {
    // 获取router
    HttpReqeustSimpleHandlerRoute r = TioBootServer.me().getHttpReqeustSimpleHandlerRoute();

    // 创建handler
    //IndexRequestHandler indexRequestHandler = new IndexRequestHandler();
    // 添加action
    //r.add("/*", indexRequestHandler::index);
    
    OpenAiV1Handler openAiV1Handler = new OpenAiV1Handler();
    r.add("/openai/v1/models", openAiV1Handler::models);
    
    OpenaiV1ChatHandler openaiV1ChatHandler = new OpenaiV1ChatHandler();
    
    r.add("/v1/chat/completions", openaiV1ChatHandler::completions);
    r.add("/openai/v1/chat/completions", openaiV1ChatHandler::completions);
    
    
    GetConfigHandler getConfigHandler = new GetConfigHandler();
    r.add("/get/config", getConfigHandler::index);
	
	r.add("/***/models", openAiV1Handler::models);
	r.add("/***/chat/completions", openaiV1ChatHandler::completions);
  }
}
