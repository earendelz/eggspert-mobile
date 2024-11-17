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

        nickname = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farmName);

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
                startActivity(new Intent(this, HomePage.class));
                return true;
            } else if (itemId == R.id.navigation_profile)  {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.navigation_farm) {
                startActivity(new Intent(this, FarmActivity.class));
                return true;
            }
            return false;
        });

        i = getIntent();
        String user_id = i.getStringExtra("user_id");
        String nama = i.getStringExtra("name");

        nickname.setText(nama);
        farmName.setText(nama + "'s Farm");

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