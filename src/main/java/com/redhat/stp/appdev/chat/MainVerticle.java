package com.redhat.stp.appdev.chat;

import io.reactivex.Completable;
import io.vertx.core.Future;
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
  public void start(Future<Void> startFuture) throws Exception {
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
    router.get("/health").handler(ctx -> {
      ctx.response().setStatusCode(200).setStatusMessage("OK").end("{ \"status\": \"OK\" }");
    });
    router.route("/eventbus/*").handler(sockJsBridge);
    router.route().handler(StaticHandler.create("webroot"));
    vertx.createHttpServer()
      .requestHandler(router::accept)
      .rxListen(8080, "0.0.0.0")
      .doOnError(startFuture::fail)
      .subscribe(server -> startFuture.complete());
  }

}
