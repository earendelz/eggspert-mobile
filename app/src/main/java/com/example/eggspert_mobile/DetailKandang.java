package com.example.eggspert_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetailKandang extends AppCompatActivity {

    TextView nickname, farmName, heading;
    TextView namaKandang, jenisKandang, kapasitasKandang, jumlahAyam, rasAyam, jenisPakan, statusPakan, statusKandang;
    ImageButton back;

    Intent i;

    BottomNavigationView navBar;
    String id, id_ras_ayam, id_pakan, user_id, user;

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

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", null);

        nickname = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farm_name);

        getNamaUser(user_id);

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

        heading = findViewById(R.id.heading_nama);
        namaKandang = findViewById(R.id.nama_kandang);
        jenisKandang = findViewById(R.id.jenis_kandang);
        kapasitasKandang = findViewById(R.id.kapasitas);
        jumlahAyam = findViewById(R.id.jml_ayam);
        rasAyam = findViewById(R.id.ras_ayam);
        jenisPakan = findViewById(R.id.jenis_pakan);
        statusPakan = findViewById(R.id.status_pakan);
        statusKandang = findViewById(R.id.status_kandang);

        back = findViewById(R.id.btn_back);

        Intent i = getIntent();
        id = i.getStringExtra("id");
        detail(id);

        back.setOnClickListener(view -> finish());

    }

    private void detail(String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan, Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/kandangku/" + id;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);
                        JSONObject rasObject = jsonObject.getJSONObject("ras_ayam");
                        JSONObject pakanObject = jsonObject.getJSONObject("pakan");

                        String nama = jsonObject.getString("nama");
                        String jenis_kandang = jsonObject.getString("jenis_kandang");
                        String kapasitas = jsonObject.getString("kapasitas");
                        String jumlah_ayam = jsonObject.getString("jumlah_ayam");
                        String nama_ras_ayam = rasObject.getString("nama_ras_ayam");
                        String jenis_pakan = pakanObject.getString("jenis_pakan");
                        String status_pakan = jsonObject.getString("status_pakan");
                        String status_kandang = jsonObject.getString("status_kandang");

                        heading.setText(nama);
                        namaKandang.setText(nama);
                        jenisKandang.setText(jenis_kandang);
                        kapasitasKandang.setText(kapasitas + " Ekor");
                        jumlahAyam.setText(jumlah_ayam + " Ekor");
                        rasAyam.setText(nama_ras_ayam);
                        jenisPakan.setText(jenis_pakan);
                        statusPakan.setText(status_pakan);
                        statusKandang.setText(status_kandang);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Kesalahan Parsing Data!", Toast.LENGTH_SHORT).show();

                    }

                }, error -> {
                    if (error.networkResponse != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        Log.e("API Error", errorMessage);
                        Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();

                    } else {
                        Log.e("API Error", "Tidak dapat terhubung ke server.");
                        Toast.makeText(getApplicationContext(), "Tidak dapat terhubung ke server.", Toast.LENGTH_SHORT).show();
                    }

                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                Log.d("Token", "Bearer " + token);
                return headers;

            }

        };

        Eggspert.getInstance().addToRequestQueue(jsonArrayRequest);

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
                            user = jsonObject.getString("nama");

                            nickname.setText(user);
                            farmName.setText(user + "'s Farm");

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