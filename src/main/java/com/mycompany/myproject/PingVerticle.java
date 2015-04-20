/*
 * Copyright 2013 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 *
 */
package com.mycompany.myproject;


import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

/*
 * This is a simple Java verticle which receives `ping` messages on the event bus and sends back `pong` replies
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class PingVerticle extends Verticle {

  @Override
  public void start(final Future<Void> startedResult) {
    final Logger logger = container.logger();

//    container.deployModule("com.campudus~session-manager~2.0.1-final", new JsonObject(), 1, new Handler<AsyncResult<String>>() {
//      @Override
//      public void handle(AsyncResult<String> event) {
//        container.deployModule("com.campudus~session-manager~2.0.1-final", new JsonObject(), 1, new Handler<AsyncResult<String>>() {
//          @Override
//          public void handle(AsyncResult<String> event) {
    container.deployModule("io.vertx~mod-mysql-postgresql_2.10~0.4.0-SNAPSHOT", new JsonObject(), 1, new Handler<AsyncResult<String>>() {
      @Override
      public void handle(AsyncResult<String> event) {
        if (event.succeeded()) {
          vertx.eventBus().sendWithTimeout("campudus.asyncdb", new JsonObject().putString("action", "raw").putString("command", "SELECT 0"), 500L,
            new Handler<AsyncResult<Message<JsonObject>>>() {
              @Override
              public void handle(AsyncResult<Message<JsonObject>> event) {
                if (event.succeeded()) {
                  startedResult.setResult(null);
                } else {
                  startedResult.setFailure(event.cause());
                }
              }
            });
        } else {
          startedResult.setFailure(event.cause());
        }
      }
    });
//          }
//        });
//      }
//    });

    vertx.eventBus().registerHandler("ping-address", new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> message) {
        message.reply("pong!");
        logger.info("Sent back pong");
      }
    });


    logger.info("PingVerticle started");

  }
}
