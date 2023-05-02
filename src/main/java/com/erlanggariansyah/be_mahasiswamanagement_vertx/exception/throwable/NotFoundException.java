package com.erlanggariansyah.be_mahasiswamanagement_vertx.exception.throwable;

import java.util.UUID;

public class NotFoundException extends RuntimeException {
  public NotFoundException(UUID id) {
    super("Mahasiswa with this id is not found.");
  }
}
