package com.example.eggspert_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomePage extends AppCompatActivity {

    TextView nickname, farmName;
    LinearLayout kelolaKandang, kelolaLaporan, kelolaVaksin, kelolaRas, kelolaPakan;
    BottomNavigationView navBar;

    Intent i;
    String namaUser, user_id;

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

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id",null);
        getNamaUser(user_id);

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
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_profile)  {
                i = new Intent(this, ProfileActivity.class);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_farm) {
                i = new Intent(this, FarmActivity.class);
                startActivity(i);
                return true;
            }
            return false;
        });

        kelolaKandang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(getApplicationContext(), KelolaKandang.class);
                startActivity(i);
            }
        });

        kelolaLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(getApplicationContext(), PilihKandangForLaporan.class);
                startActivity(i);
            }
        });

        kelolaVaksin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(getApplicationContext(), PilihKandangForVaksin.class);
                startActivity(i);
            }
        });

        kelolaRas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i = new Intent(getApplicationContext(), KelolaRasAyam.class);
                startActivity(i);
            }
        });

        kelolaPakan.setOnClickListener(view -> {
            i = new Intent(getApplicationContext(), KelolaPakan.class);
            startActivity(i);
        });

    }

    private void getNamaUser(String userID) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        nickname = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farm_name);

        String url = "http://10.0.2.2:8000/api/users/" + userID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONObject jsonObject = response.getJSONObject("data");
                            namaUser = jsonObject.getString("nama");

                            nickname.setText(namaUser);
                            farmName.setText(namaUser + "'s Farm");

                        } else {
                            String errorMessage = response.getString("message");
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                },

                error -> {
                    Log.e("API Error", "Error Response: " + error.getMessage());

                })

        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                Log.d("Token", "Bearer " + token);
                return headers;

            }};

        Eggspert.getInstance().addToRequestQueue(jsonObjectRequest);
    }

}