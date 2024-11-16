package com.example.eggspert_mobile;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailKandang extends AppCompatActivity {

    TextView nickname, farmName, nama, jenis, kapasitas, jumlah;
    ImageButton back;


    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_kandang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nickname = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farm_name);
        nama = findViewById(R.id.nama_kandang);
        jenis = findViewById(R.id.jenis_kandang);
        kapasitas = findViewById(R.id.kapasitas);
        jumlah = findViewById(R.id.jml_ayam);

        back = findViewById(R.id.btn_back);

        Intent i = getIntent();
        String user = i.getStringExtra("name");
        String farm = user + "'s Farm";

        nickname.setText(user);
        farmName.setText(farm);

        id = i.getStringExtra("id");

        detail();

        back.setOnClickListener(view -> finish());

    }

    private void detail() {}

}