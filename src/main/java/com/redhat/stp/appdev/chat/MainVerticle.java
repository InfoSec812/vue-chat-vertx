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
    // Create an instance of Router
    Router router = Router.router(vertx);

    // Configure a CORS handler and attach it to the router
    CorsHandler corsHandler = CorsHandler.create(".*").allowedHeader("Access-Control-Request-Method")
      .allowedHeader("Access-Control-Allow-Origin")
      .allowedHeader("Access-Control-Allow-Headers").allowedHeader("Content-Type")
      .allowedMethod(HttpMethod.GET).allowedMethod(HttpMethod.POST)
      .allowedMethod(HttpMethod.HEAD).allowedMethod(HttpMethod.PUT)
      .allowedMethod(HttpMethod.OPTIONS).allowedMethod(HttpMethod.CONNECT);
    router.route().handler(corsHandler);

    // Configure a websocket to eventbus bridge
    BridgeOptions bridgeOpts = new BridgeOptions()
      .addInboundPermitted(new PermittedOptions().setAddress("messages"))
      .addOutboundPermitted(new PermittedOptions().setAddress("messages"));
    SockJSHandler sockJsBridge = SockJSHandler.create(vertx);
    sockJsBridge.bridge(bridgeOpts);

    // Create a simple health check endpoint
    router.get("/health").handler(ctx -> {
      ctx.response().setStatusCode(200).setStatusMessage("OK").end("{ \"status\": \"OK\" }");
    });

    // Create a StaticHandler to server the static HTML/CSS/JS site
    router.route().handler(StaticHandler.create("webroot"));

    // Attach the SockJS bridge to the router
    router.route("/eventbus/*").handler(sockJsBridge);

    return vertx.createHttpServer()               // Create HttpServer instance
                .requestHandler(router)           // Attach router to handle all requests
                .rxListen(8080, "0.0.0.0")        // Start the server listening on port 8080
                .doOnError(Completable::error)    // return an error if there is a problem
                .toFlowable()                     // Convert to a Flowable so that the Completable can be returned
                .ignoreElements();                // Return Completable on success
  }
}
