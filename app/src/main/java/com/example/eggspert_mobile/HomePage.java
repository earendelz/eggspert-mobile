package com.example.eggspert_mobile;

import android.content.Intent;
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
    LinearLayout kelolaKandang, kelolaLaporan, kelolaVaksin, kelolaRas, kelolaPakan;
    BottomNavigationView navBar;

    Intent i;

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

    }

    @Override
    protected void onResume() {
        super.onResume();
        nickname = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farmName);

        i = getIntent();
        String user_id = i.getStringExtra("user_id");
        String nama = i.getStringExtra("name");

        nickname.setText(nama);
        farmName.setText(nama + "'s Farm");

        kelolaKandang = findViewById(R.id.kelola_kandang);
        kelolaLaporan = findViewById(R.id.kelola_laporan);
        kelolaVaksin = findViewById(R.id.kelola_vaksin);
        kelolaRas = findViewById(R.id.kelola_ras);
        kelolaPakan = findViewById(R.id.kelola_pakan);

        navBar = findViewById(R.id.bottom_navigation);
        navBar.setSelectedItemId(R.id.navigation_home);
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                i = new Intent(this, HomePage.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_profile)  {
                i = new Intent(this, ProfileActivity.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_farm) {
                i = new Intent(this, FarmActivity.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            }
            return false;
        });

        kelolaKandang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(getApplicationContext(), KelolaKandang.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", user_id);
                startActivity(i);
            }
        });

        kelolaLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(getApplicationContext(), PilihKandangForLaporan.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", user_id);
                startActivity(i);
            }
        });

        kelolaVaksin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(getApplicationContext(), PilihKandangForVaksin.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", user_id);
                startActivity(i);
            }
        });

        kelolaRas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(getApplicationContext(), KelolaRasAyam.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", user_id);
                startActivity(i);
            }
        });

        kelolaPakan.setOnClickListener(view -> {
            i = new Intent(getApplicationContext(), KelolaPakan.class);
            startActivity(i);
        });

    }
}