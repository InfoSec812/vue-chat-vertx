package com.redhat.stp.appdev.chat.services;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

@ProxyGen
@VertxGen
public interface ReactiveChat {

  static ReactiveChat create(Vertx vertx) {
    return null;
  }

  static ReactiveChat createProxy(Vertx vertx, String address) {
    return null;
  }

  void send(JsonObject message, Handler<AsyncResult<JsonObject>> handler);
}
