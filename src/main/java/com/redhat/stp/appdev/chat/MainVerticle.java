package com.redhat.stp.appdev.chat;

import io.reactivex.Completable;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.CorsHandler;
import io.vertx.reactivex.ext.web.handler.StaticHandler;
import io.vertx.reactivex.ext.web.handler.sockjs.SockJSHandler;

public class MainVerticle extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    Router router = Router.router(vertx);
    CorsHandler corsHandler = CorsHandler.create(".*").allowedHeader("Access-Control-Request-Method")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Headers").allowedHeader("Content-Type")
      .allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST)
      .allowedMethod(HttpMethod.HEAD).allowedMethod(HttpMethod.PUT)
      .allowedMethod(HttpMethod.OPTIONS).allowedMethod(HttpMethod.CONNECT);
    router.route().handler(corsHandler);
    BridgeOptions bridgeOpts = new BridgeOptions()
      .addInboundPermitted(new PermittedOptions().setAddress("messages"))
      .addOutboundPermitted(new PermittedOptions().setAddress("messages"));
    SockJSHandler sockJsBridge = SockJSHandler.create(vertx);
    sockJsBridge.bridge(bridgeOpts);
    router.route().handler(StaticHandler.create("webroot"));
    router.route("/eventbus/*").handler(sockJsBridge);
    return vertx.createHttpServer()
                .requestHandler(router)
                .rxListen(8080, "0.0.0.0")
                .doOnError(Completable::error)
                .toFlowable()
                .ignoreElements();
  }
}
