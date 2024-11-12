package com.example.eggspert_mobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelper_kandang extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "kandang";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAMA = "nama_kandang";
    public static final String COLUMN_JENIS = "jenis_kandang";
    public static final String COLUMN_CAP = "kapasitas";
    public static final String COLUMN_JML = "jumlah_ayam";
    public static final String COLUMN_IDUSER = "id_peternak";

    public static final String DB_NAME = "eggspert.db";
    public static final int DB_VERSION = 2 ;

    private static final String TBL_CREATE = "CREATE table IF NOT EXISTS " +
            TABLE_NAME + " ( " +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAMA + " varchar(50) not null, " +
            COLUMN_JENIS + " varchar(50) not null, " +
            COLUMN_CAP + " integer not null, " +
            COLUMN_JML + " integer not null, " +
            COLUMN_IDUSER + " integer not null);";

    public DBHelper_kandang(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("DBHelper", "Creating table kandang if it does not exist.");
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TBL_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int lama, int baru) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
