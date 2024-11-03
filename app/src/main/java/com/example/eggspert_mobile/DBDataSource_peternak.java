package com.example.eggspert_mobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBDataSource {

    private SQLiteDatabase db;
    private DBHelper dbHelper;

    private final String[] allColumns = { DBHelper.COLUMN_ID, DBHelper.COLUMN_USN,
            DBHelper.COLUMN_PASS, DBHelper.COLUMN_NAMA,
            DBHelper.COLUMN_EMAIL, DBHelper.COLUMN_ALAMAT
    };

    public DBDataSource(Context context) {
        dbHelper =new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

}
