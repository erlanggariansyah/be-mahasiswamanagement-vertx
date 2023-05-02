package com.erlanggariansyah.be_mahasiswamanagement_vertx.repository;

import com.erlanggariansyah.be_mahasiswamanagement_vertx.exception.throwable.NotFoundException;
import com.erlanggariansyah.be_mahasiswamanagement_vertx.model.Mahasiswa;
import io.vertx.core.Future;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MahasiswaRepository {
  private final PgPool client;
  private static final Logger LOGGER = Logger.getLogger(MahasiswaRepository.class.getName());
  private static Function<Row, Mahasiswa> MAPPER = (row) -> Mahasiswa.of(
    row.getUUID("id"),
    row.getString("nama"),
    row.getString("jurusan"),
    row.getLocalDateTime("createdat")
  );

  private MahasiswaRepository(PgPool pgPool) {
    this.client = pgPool;
  }

  public static MahasiswaRepository create(PgPool pgPool) {
    return new MahasiswaRepository(pgPool);
  }

  public Future<List<Mahasiswa>> findAll() {
    try {
      Future<List<Mahasiswa>> futureListMahasiswa = client
        .query("SELECT * FROM mahasiswa ORDER BY createdat DESC")
        .execute()
        .map(rows -> StreamSupport
          .stream(rows.spliterator(), false)
          .map(MAPPER)
          .collect(Collectors.toList())
        );

      return futureListMahasiswa;
    } catch (Exception exception) {
      System.out.println("EXCEPTION CAUGHT: " + exception.getMessage());
      exception.printStackTrace();

      return null;
    }
  }

  public Future<Mahasiswa> findById(UUID id) {
    Objects.requireNonNull(id, "id field is mandatory.");

    return client.preparedQuery("SELECT * FROM mahasiswa WHERE id = $1")
      .execute(Tuple.of(id))
      .map(RowSet::iterator)
      .map(rowRowIterator -> rowRowIterator.hasNext() ? MAPPER.apply(rowRowIterator.next()) : null)
      .map(Optional::ofNullable)
      .map(p -> p.orElseThrow(() -> new NotFoundException(id)));
  }

  public Future<UUID> save(Mahasiswa mahasiswa) {
    try {
      Future<UUID> futureUUID = client.preparedQuery("INSERT INTO mahasiswa(id,nama,jurusan,createdAt) VALUES($1,$2,$3,$4) RETURNING (id)")
        .execute(Tuple.of(UUID.randomUUID(), mahasiswa.getNama(), mahasiswa.getJurusan(), LocalDateTime.now()))
        .map(rows -> rows.iterator().next().getUUID("id"));

      return futureUUID;
    } catch (Exception exception) {
      System.out.println("EXCEPTION CAUGHT: " + exception.getMessage());
      exception.printStackTrace();

      return null;
    }
  }

  public Future<Integer> saveAll(List<Mahasiswa> mahasiswas) {
    List<Tuple> mahasiswaTuple = mahasiswas.stream()
      .map(mahasiswa -> Tuple.of(mahasiswa.getNama(), mahasiswa.getJurusan()))
      .collect(Collectors.toList());

    return client.preparedQuery("INSERT INTO mahasiswa(nama,jurusan) VALUES($1,$2)")
      .executeBatch(mahasiswaTuple)
      .map(SqlResult::rowCount);
  }

  public Future<Integer> update(Mahasiswa mahasiswa) {
    return client.preparedQuery("UPDATE mahasiswa SET nama=$1, jurusan=$2 WHERE id=$3")
      .execute(Tuple.of(mahasiswa.getNama(), mahasiswa.getJurusan(), mahasiswa.getId()))
      .map(SqlResult::rowCount);
  }

  public Future<Integer> deleteAll() {
    return client.query("DELETE FROM mahasiswa")
      .execute()
      .map(SqlResult::rowCount);
  }

  public Future<Integer> deleteById(UUID id) {
    Objects.requireNonNull(id, "id field is mandatory.");

    return client.preparedQuery("DELETE FROM mahasiswa WHERE id=$1")
      .execute(Tuple.of(id))
      .map(SqlResult::rowCount);
  }
}
