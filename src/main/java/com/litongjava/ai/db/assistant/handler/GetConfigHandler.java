package com.litongjava.ai.db.assistant.handler;

import com.litongjava.data.services.DbService;
import com.litongjava.jfinal.plugin.activerecord.Db;
import com.litongjava.tio.boot.http.TioControllerContext;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.http.common.HttpResponse;
import com.litongjava.tio.http.server.util.Resps;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GetConfigHandler {

  private DbService dbService = new DbService();

  public HttpResponse index(HttpRequest httpRequest) {
    httpRequest.channelContext.tioConfig.setUseQueueSend(false);
    log.info("useQueueSend:{}", httpRequest.channelContext.tioConfig.useQueueSend);
    return TioControllerContext.getResponse();
  }

  public HttpResponse getAllTableColumnsOfMarkdown(HttpRequest httpRequest) {
    HttpResponse httpResponse = TioControllerContext.getResponse();

    String markdown = dbService.getAllTableColumnsOfMarkdown(Db.use());

    return Resps.txt(httpResponse, markdown);

  }
  
  public HttpResponse getAllTableDataExamplesOfMarkdown(HttpRequest httpRequest) {
    HttpResponse httpResponse = TioControllerContext.getResponse();

    String markdown = dbService.getAllTableDataExamplesOfMarkdown(Db.use());

    return Resps.txt(httpResponse, markdown);

  }
}
