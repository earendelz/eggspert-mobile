package com.example.eggspert_mobile;

public class Users {

    private long id;
    private String username;
    private String password;
    private String nama;
    private String email;
    private String alamat;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public String getMerk_barang() {
        return merk_barang;
    }

    public void setMerk_barang(String merk_barang) {
        this.merk_barang = merk_barang;
    }

    public String getAsal_negara() {
        return asal_negara;
    }

    public void setAsal_negara(String asal_negara) {
        this.asal_negara = asal_negara;
    }

    public String toString() {
        return "Data : " + this.id + " - " + this.nama_barang + " (" + this.merk_barang + ")";
    }

}
