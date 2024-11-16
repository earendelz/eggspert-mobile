package com.example.eggspert_mobile;

public class RasAyam {
    private long id;
    private String nama_ras_ayam;
    private String jenis_ayam;
    private int id_peternak;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNama_ras_ayam() {
        return nama_ras_ayam;
    }

    public void setNama_ras_ayam(String nama_ras_ayam) {
        this.nama_ras_ayam = nama_ras_ayam;
    }

    public String getJenis_ayam() {
        return jenis_ayam;
    }

    public void setJenis_ayam(String jenis_ayam) {
        this.jenis_ayam = jenis_ayam;
    }

    public int getId_peternak() {
        return id_peternak;
    }

    public void setId_peternak(int id_peternak) {
        this.id_peternak = id_peternak;
    }

    public String toString() {
        return nama_ras_ayam + " - " + jenis_ayam;
    }
}
