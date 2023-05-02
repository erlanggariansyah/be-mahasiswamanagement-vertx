package com.erlanggariansyah.be_mahasiswamanagement_vertx.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Mahasiswa {
  private UUID id;
  private String nama;
  private String jurusan;
  private LocalDateTime createdAt;

  public static Mahasiswa of(String nama, String jurusan) {
    return of(null, nama, jurusan, null);
  }

  public static Mahasiswa of(UUID id, String nama, String jurusan, LocalDateTime createdAt) {
    Mahasiswa mahasiswa = new Mahasiswa();
    mahasiswa.setId(id);
    mahasiswa.setNama(nama);
    mahasiswa.setJurusan(jurusan);
    mahasiswa.setCreatedAt(createdAt);

    return mahasiswa;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getNama() {
    return nama;
  }

  public void setNama(String nama) {
    this.nama = nama;
  }

  public String getJurusan() {
    return jurusan;
  }

  public void setJurusan(String jurusan) {
    this.jurusan = jurusan;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
