package com.example.eggspert_mobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBDataSource_peternak {

    private SQLiteDatabase db;
    private final DBHelper_peternak dbHelper;

    private final String[] allColumns = { DBHelper_peternak.COLUMN_ID, DBHelper_peternak.COLUMN_NAMA,
            DBHelper_peternak.COLUMN_ALAMAT,DBHelper_peternak.COLUMN_EMAIL,
            DBHelper_peternak.COLUMN_USN, DBHelper_peternak.COLUMN_PASS
    };

    public DBDataSource_peternak(Context context) {
        dbHelper =new DBHelper_peternak(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Peternak register(String nama, String alamat,
                             String email,String usn, String pass) {

        ContentValues cv = new ContentValues();
        cv.put(DBHelper_peternak.COLUMN_NAMA, nama);
        cv.put(DBHelper_peternak.COLUMN_ALAMAT, alamat);
        cv.put(DBHelper_peternak.COLUMN_EMAIL, email);
        cv.put(DBHelper_peternak.COLUMN_USN, usn);
        cv.put(DBHelper_peternak.COLUMN_PASS, pass);

        long insertID = db.insert(DBHelper_peternak.TABLE_NAME, null, cv);
        Cursor cursor = db.query(DBHelper_peternak.TABLE_NAME, allColumns,
                DBHelper_peternak.COLUMN_ID + " = " + insertID,
                null, null, null, null);

        cursor.moveToFirst();

        Peternak peternak = cursorToPeternak(cursor);
        cursor.close();

        return peternak;

    }

    public Peternak cursorToPeternak(Cursor cursor) {
        Peternak peternak = new Peternak();
        peternak.setId(cursor.getLong(0));
        peternak.setNama(cursor.getString(1));
        peternak.setAlamat(cursor.getString(2));
        peternak.setEmail(cursor.getString(3));
        peternak.setUsername(cursor.getString(4));
        peternak.setPassword(cursor.getString(5));

        return peternak;
    }

    public boolean getPeternak(String usn, String pass) {
//        Peternak peternak = new Peternak();

        Cursor cursor = db.query(DBHelper_peternak.TABLE_NAME, allColumns,
                DBHelper_peternak.COLUMN_USN + " = ? AND "
                        + DBHelper_peternak.COLUMN_PASS + " = ? ",
                new String[]{usn, pass},null,null,null);

        boolean loginSuccess = false;

        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            loginSuccess = true;

        }

        cursor.close();
        return loginSuccess;

    }

    public Peternak getPeternakUser(String usn, String pass) {
        Peternak peternak = null;

        Cursor cursor = db.query(DBHelper_peternak.TABLE_NAME, allColumns,
                DBHelper_peternak.COLUMN_USN + " = ? AND "
                        + DBHelper_peternak.COLUMN_PASS + " = ? ",
                new String[]{usn, pass},null,null,null);

        if(cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            peternak = cursorToPeternak(cursor);

        }

        cursor.close();
        return peternak;

    }

}
