package com.example.eggspert_mobile;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomePage extends AppCompatActivity {

    TextView nickname, farmName;
    LinearLayout kelolaKandang;
    BottomNavigationView navBar;
    SQLiteDatabase db;

    DBHelper_kandang config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nickname = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farmName);
        kelolaKandang = findViewById(R.id.kelola_kandang);

        navBar = findViewById(R.id.bottom_navigation);
        navBar.setSelectedItemId(R.id.navigation_home);

        Intent i = getIntent();
        String id = i.getStringExtra("user_id");
        String nama = i.getStringExtra("name");

        nickname.setText(nama);
        farmName.setText(nama + "'s Farm");

        kelolaKandang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), KelolaKandang.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", id);
                startActivity(i);
            }
        });
    }
}