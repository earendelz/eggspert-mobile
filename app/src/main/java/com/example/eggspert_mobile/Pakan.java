package com.example.eggspert_mobile;

public class Pakan {
    private long id;
    private String jenis_pakan;
    private int id_peternak;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJenis_pakan() {
        return jenis_pakan;
    }

    public void setJenis_pakan(String jenis_pakan) {
        this.jenis_pakan = jenis_pakan;
    }

    public int getId_peternak() {
        return id_peternak;
    }

    public void setId_peternak(int id_peternak) {
        this.id_peternak = id_peternak;
    }

    public String toString() {
        return jenis_pakan;
    }
}
