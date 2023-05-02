package com.erlanggariansyah.be_mahasiswamanagement_vertx.controller;

import com.erlanggariansyah.be_mahasiswamanagement_vertx.dto.MahasiswaData;
import com.erlanggariansyah.be_mahasiswamanagement_vertx.model.Mahasiswa;
import com.erlanggariansyah.be_mahasiswamanagement_vertx.repository.MahasiswaRepository;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class MahasiswaController {
  private static final Logger LOGGER = Logger.getLogger(MahasiswaController.class.getName());
  private final MahasiswaRepository mahasiswaRepository;

  private MahasiswaController(MahasiswaRepository mahasiswaRepository) {
    this.mahasiswaRepository = mahasiswaRepository;
  }

  public static MahasiswaController create(MahasiswaRepository mahasiswaRepository) {
    return new MahasiswaController(mahasiswaRepository);
  }

  public void all(RoutingContext routingContext) {
    this.mahasiswaRepository.findAll()
      .onSuccess(data -> routingContext.response().end(Json.encode(data))
      .onFailure(
        throwable -> routingContext.fail(500, throwable)
      ));
  }

  public void get(RoutingContext routingContext) {
    Map<String, String> pathParams = routingContext.pathParams();
    UUID id = UUID.fromString(pathParams.get("id"));

    this.mahasiswaRepository.findById(id)
      .onSuccess(mahasiswa -> routingContext.response().end(Json.encode(mahasiswa)))
      .onFailure(throwable -> routingContext.fail(404, throwable));
  }

  public void save(RoutingContext routingContext) {
    JsonObject body = routingContext.getBodyAsJson();
    MahasiswaData mahasiswaData = body.mapTo(MahasiswaData.class);

    this.mahasiswaRepository
      .save(Mahasiswa.of(mahasiswaData.getNama(), mahasiswaData.getJurusan()))
      .onSuccess(savedId -> routingContext
        .response()
        .putHeader("Location", "/mahasiswa/" +savedId)
        .setStatusCode(201)
        .end())
      .onFailure(
        throwable -> routingContext.fail(500, throwable)
      );
  }

  public void update(RoutingContext routingContext) {
    Map<String, String> params = routingContext.pathParams();
    UUID id = UUID.fromString(params.get("id"));
    JsonObject body = routingContext.getBodyAsJson();
    MahasiswaData mahasiswaData = body.mapTo(MahasiswaData.class);

    this.mahasiswaRepository.findById(id).compose(
      mahasiswa -> {
        mahasiswa.setNama(mahasiswaData.getNama());
        mahasiswa.setJurusan(mahasiswaData.getJurusan());

        return this.mahasiswaRepository.update(mahasiswa);
      }
    ).onSuccess(
      status -> {
        routingContext.response().setStatusCode(204).end();
      }
    ).onFailure(
      throwable -> {
        routingContext.fail(404, throwable);
      }
    );
  }

  public void delete(RoutingContext routingContext) {
    Map<String, String> params = routingContext.pathParams();
    UUID id = UUID.fromString(params.get("id"));

    this.mahasiswaRepository.findById(id).compose(
      mahasiswa -> this.mahasiswaRepository.deleteById(id)
    ).onSuccess(
      data -> routingContext.response().setStatusCode(204).end()
    ).onFailure(
      throwable -> routingContext.fail(404, throwable)
    );
  }
}
