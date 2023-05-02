package com.erlanggariansyah.be_mahasiswamanagement_vertx;

import com.erlanggariansyah.be_mahasiswamanagement_vertx.controller.MahasiswaController;
import com.erlanggariansyah.be_mahasiswamanagement_vertx.exception.throwable.NotFoundException;
import com.erlanggariansyah.be_mahasiswamanagement_vertx.repository.MahasiswaRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MainVerticle extends AbstractVerticle {
  private static final Logger LOGGER = Logger.getLogger(MainVerticle.class.getName());

  private PgPool pgPool() {
    PgConnectOptions pgConnectOptions = new PgConnectOptions()
      .setPort(5432)
      .setHost("localhost")
      .setDatabase("mahasiswamanagement_vertx")
      .setUser("postgres")
      .setPassword("xxx");

    PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

    return PgPool.pool(vertx, pgConnectOptions, poolOptions);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOGGER.log(Level.INFO, "Starting HTTP server...");

    VertxOptions options = new VertxOptions();
    options.setEventLoopPoolSize(2);
    LOGGER.info("Event Loop: " + options.getEventLoopPoolSize());

    MahasiswaRepository mahasiswaRepository = MahasiswaRepository.create(pgPool());
    MahasiswaController mahasiswaController = MahasiswaController.create(mahasiswaRepository);

    vertx.createHttpServer().requestHandler(routes(mahasiswaController)).listen(9002, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 9002");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }

  private Router routes(MahasiswaController mahasiswaController) {
    Router router = Router.router(vertx);
    router.get("/mahasiswa").produces("application/json").handler(mahasiswaController::all);
    router.post("/mahasiswa").consumes("application/json").handler(BodyHandler.create()).handler(mahasiswaController::save);
    router.get("/mahasiswa/:id").produces("application/json").handler(mahasiswaController::get).failureHandler(
      failureContext -> {
        Throwable failure = failureContext.failure();
        if (failure instanceof NotFoundException) {
          failureContext.response().setStatusCode(404).end();
        }

        failureContext.response().setStatusCode(500).setStatusMessage(failure.getMessage()).end();
      }
    );
    router.put("/mahasiswa/:id").consumes("application/json").handler(BodyHandler.create()).handler(mahasiswaController::update);
    router.delete("/mahasiswa/:id").handler(mahasiswaController::delete);

    return router;
  }
}
