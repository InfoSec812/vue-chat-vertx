package com.redhat.stp.appdev.chat;

import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgPool;
import io.reactiverse.pgclient.PgPoolOptions;
import io.reactiverse.pgclient.PgRowSet;
import io.reactivex.Completable;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.AbstractVerticle;

public class MessagePersistence extends AbstractVerticle {

  @Override
  public Completable rxStart() {
    PgPoolOptions options = new PgPoolOptions()
      .setPort(5432)
      .setHost("chatdb")
      .setDatabase("chatdb")
      .setUser("chatdb")
      .setPassword("chatdb")
      .setMaxSize(5);
    PgPool client = PgClient.pool(options);

    vertx.eventBus().consumer("messages", msg -> {
      JsonObject body = (JsonObject)msg.body();
      client.query("INSERT INTO messages (message, msgtime, avatar) VALUES ('" + body.getString("message") + "', '" + body.getString("timestamp") + "'::TIMESTAMPTZ, '" + body.getString("avatar") + "')", ar -> {
        if (ar.failed()) {
          msg.reply(new JsonObject().put("error", ar.cause().getLocalizedMessage()));
        }
      });
    });

    vertx.eventBus().consumer("history", msg -> {
      JsonObject body = (JsonObject)msg.body();
      String msgtime = body.getString("msgtime");
      String directionOperator = ">";
      String orderOperator = "asc";
      if (body.getString("direction").contentEquals("earlier")) {
        directionOperator = "<";
        orderOperator = "desc";
      }
      client.query("SELECT * FROM messages WHERE msgtime " + directionOperator + " '" + msgtime + "'::TIMESTAMPTZ ORDER BY msgtime " + orderOperator, ar -> {
        if (ar.succeeded()) {
          JsonArray responseData = new JsonArray();
          ar.result().forEach(row -> {
            JsonObject rowData = new JsonObject();
            rowData.put("msg", row.getString("message"));
            rowData.put("timestamp", row.getOffsetDateTime("msgtime").toInstant().toString());
            responseData.add(rowData);
          });
          msg.reply(new JsonObject().put("status", "OK").put("array", responseData));
        } else {
          msg.reply(new JsonObject().put("err", ar.cause().getLocalizedMessage()));
        }
      });
    });
    return Completable.complete();
  }
}
