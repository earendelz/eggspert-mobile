package com.example.eggspert_mobile;

import android.content.Intent;
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

public class CreateKandang extends AppCompatActivity {

    Button add;
    EditText etNama, etJenis, etKap, etJml;
    TextView nick, farmName;
    ImageButton back;

    DBHelper_kandang config;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_kandang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String user_id = getIntent().getStringExtra("user_id");

        etNama = findViewById(R.id.nama_kandang);
        etJenis = findViewById(R.id.jenis_kandang);
        etKap = findViewById(R.id.kapasitas);
        etJml = findViewById(R.id.jml_ayam);

        nick = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farm_name);

        back = findViewById(R.id.btn_back);

        String nama =  getIntent().getStringExtra("name");

        nick.setText(nama);
        farmName.setText(nama + "'s Farm");

        add = findViewById(R.id.btn_add);

        config = new DBHelper_kandang(this);

        add.setOnClickListener(view -> createData(user_id));

        back.setOnClickListener(view -> {
            finish();
        });

    }

    public void createData(String user_id) {
        String nama = etNama.getText().toString();
        String jenis = etJenis.getText().toString();
        String kapasitas = etKap.getText().toString();
        String jumlah = etJml.getText().toString();
        String id_peternak = user_id;

        if (nama.isEmpty() || jenis.isEmpty() || kapasitas.isEmpty() || jumlah.isEmpty()) {
            Toast.makeText(this, "Data Harus Terisi Lengkap", Toast.LENGTH_LONG).show();

        } else {
            db = config.getReadableDatabase();
            db.execSQL("INSERT INTO kandang (nama_kandang, jenis_kandang, kapasitas, jumlah_ayam, id_peternak) VALUES ('" +
                    nama + "', '" + jenis + "', '" + kapasitas + "', '" + jumlah + "', '" + id_peternak + "')" );
            Toast.makeText(this, "Kandang Berhasil DItambah", Toast.LENGTH_LONG).show();
            finish();

        }

    }

}