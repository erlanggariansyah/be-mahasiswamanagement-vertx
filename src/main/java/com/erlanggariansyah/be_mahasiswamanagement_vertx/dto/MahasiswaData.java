package com.erlanggariansyah.be_mahasiswamanagement_vertx.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public class MahasiswaData {
  private String nama;
  private String jurusan;

  public MahasiswaData() {}

  public MahasiswaData(String nama, String jurusan) {
    this.nama = nama;
    this.jurusan = jurusan;
  }

  public static MahasiswaData of(String nama, String jurusan) {
    return new MahasiswaData(nama, jurusan);
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
}
