package com.example.eggspert_mobile;

public class Kandang {
    private long id;
    private String nama;
    private String jenis_kandang;
    private int kapasitas;
    private int jumlah_ayam;
    private int id_ras_ayam;
    private int id_pakan;
    private String status_pakan;
    private int id_peternak;
    private String status_kandang;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getJenis_kandang() {
        return jenis_kandang;
    }

    public void setJenis_kandang(String jenis_kandang) {
        this.jenis_kandang = jenis_kandang;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }

    public int getJumlah_ayam() {
        return jumlah_ayam;
    }

    public void setJumlah_ayam(int jumlah_ayam) {
        this.jumlah_ayam = jumlah_ayam;
    }

    public int getId_ras_ayam() {
        return id_ras_ayam;
    }

    public void setId_ras_ayam(int id_ras_ayam) {
        this.id_ras_ayam = id_ras_ayam;
    }

    public int getId_pakan() {
        return id_pakan;
    }

    public void setId_pakan(int id_pakan) {
        this.id_pakan = id_pakan;
    }

    public String isStatus_pakan() {
        return status_pakan;
    }

    public void setStatus_pakan(String status_pakan) {
        this.status_pakan = status_pakan;
    }

    public String getStatus_pakan() {
        return status_pakan;
    }

    public String getStatus_kandang() {
        return status_kandang;
    }

    public void setStatus_kandang(String status_kandang) {
        this.status_kandang = status_kandang;
    }

    public int getId_peternak() {
        return id_peternak;
    }

    public void setId_peternak(int id_peternak) {
        this.id_peternak = id_peternak;
    }
}
