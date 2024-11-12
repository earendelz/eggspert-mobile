package com.example.eggspert_mobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper_peternak extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "peternak";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAMA = "nama";
    public static final String COLUMN_ALAMAT = "alamat";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_USN = "username";
    public static final String COLUMN_PASS = "password";

    public static final String DB_NAME = "eggspert.db";
    public static final int DB_VERSION = 2 ;

    private static final String TBL_CREATE = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME + " ( " +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAMA + " varchar(50) not null, " +
            COLUMN_ALAMAT + " varchar(50) not null, " +
            COLUMN_EMAIL + " varchar(50) not null unique, " +
            COLUMN_USN + " varchar(18) not null unique, " +
            COLUMN_PASS + " varchar(18) not null);";

    public DBHelper_peternak(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TBL_CREATE);
    }

    public void onUpgrade(SQLiteDatabase db, int lama, int baru) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
