package com.example.eggspert_mobile;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EditKandang extends AppCompatActivity {

    TextView nick, farm;
    EditText etNamaKandang, etJenisKandang, etKapasistas, etJumlahAyam;
    Button edit; ImageButton back;

    DBHelper_kandang config;
    SQLiteDatabase db;
    String id, username; int user_id;
    Intent i; Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_kandang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nick = findViewById(R.id.nickname);
        farm = findViewById(R.id.farm_name);

        etNamaKandang = findViewById(R.id.nama_kandang);
        etJenisKandang = findViewById(R.id.jenis_kandang);
        etKapasistas = findViewById(R.id.kapasitas);
        etJumlahAyam = findViewById(R.id.jml_ayam);

        edit = findViewById(R.id.btn_edit);
        back = findViewById(R.id.btn_back);

        i = getIntent();

        id = i.getStringExtra("id");
        username = i.getStringExtra("name");
        String userID = i.getStringExtra("user_id");
        user_id = Integer.parseInt(userID);

        nick.setText(username);
        String farmName = username + "'s Farm";
        farm.setText(farmName);

        config = new DBHelper_kandang(this);

        showData();

        edit.setOnClickListener(view -> editKandang());

        back.setOnClickListener(view -> finish());

    }

    private void showData() {

        db = config.getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM kandang WHERE id = '" + id + "'", null);
        cursor.moveToFirst();

        etNamaKandang.setText(cursor.getString(1));
        etJenisKandang.setText(cursor.getString(2));
        etKapasistas.setText(cursor.getString(3));
        etJumlahAyam.setText(cursor.getString(4));

    }

    private void editKandang() {

        String nama_kandang = etNamaKandang.getText().toString();
        String jenis_kandang = etJenisKandang.getText().toString();
        String kapasitas = etKapasistas.getText().toString();
        String jumlah_ayam = etJumlahAyam.getText().toString();

        if (nama_kandang.isEmpty() || jenis_kandang.isEmpty() || kapasitas.isEmpty() || jumlah_ayam.isEmpty()) {
            Toast.makeText(this, "Data Harus Diisi Seluruhnya!", Toast.LENGTH_SHORT).show();
        } else {

            db = config.getReadableDatabase();
            db.execSQL("UPDATE kandang SET nama_kandang  = '" + nama_kandang + "', jenis_kandang = '" + jenis_kandang
                    + "', kapasitas = '" + kapasitas + "', jumlah_ayam = '" + jumlah_ayam + "', id_peternak = '" + user_id
                    + "' WHERE id = '" + id + "'");
            Toast.makeText(this, "Kandang Berhasil Diubah", Toast.LENGTH_SHORT).show();
            finish();

        }

    }

}