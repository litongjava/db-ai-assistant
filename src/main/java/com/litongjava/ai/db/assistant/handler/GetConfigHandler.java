package com.litongjava.ai.db.assistant.handler;

import com.litongjava.tio.boot.http.TioControllerContext;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.http.common.HttpResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetConfigHandler {

  public HttpResponse index(HttpRequest httpRequest) {
    httpRequest.channelContext.tioConfig.setUseQueueSend(false);
    log.info("useQueueSend:{}", httpRequest.channelContext.tioConfig.useQueueSend);

    return TioControllerContext.getResponse();
  }
}
