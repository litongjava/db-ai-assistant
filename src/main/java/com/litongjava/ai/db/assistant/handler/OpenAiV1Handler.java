package com.litongjava.ai.db.assistant.handler;

import java.util.ArrayList;
import java.util.List;

import com.jfinal.kit.Kv;
import com.litongjava.tio.boot.http.TioControllerContext;
import com.litongjava.tio.http.common.HttpRequest;
import com.litongjava.tio.http.common.HttpResponse;
import com.litongjava.tio.http.server.model.HttpCors;
import com.litongjava.tio.http.server.util.HttpServerResponseUtils;

public class OpenAiV1Handler {

  public HttpResponse models(HttpRequest request) {
    /**
    {
      "object": "list",
      "data": [
        {
          "id": "dall-e-3",
          "object": "model",
          "created": 1698785189,
          "owned_by": "system"
        },
      ]
    ]
    */
    HttpResponse httpResponse = TioControllerContext.getResponse();
    HttpServerResponseUtils.enableCORS(httpResponse, new HttpCors());
    
    Kv model = Kv.create();
    model.set("id", "db-ai-assistant");
    model.set("object", "model");
    model.set("created", 1698785189);
    model.set("owned_by", "system");

    List<Kv> models = new ArrayList<>(1);
    models.add(model);

    Kv resp = Kv.create();
    resp.set("object", "list");
    resp.set("data", models);

    return httpResponse.setJson(resp);
  }
}
