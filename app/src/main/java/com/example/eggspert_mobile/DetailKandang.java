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

    BottomNavigationView navBar;
    String id, id_ras_ayam, id_pakan;

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
        String user = sharedPreferences.getString("nama", null);
        String farm = user + "'s Farm";

        nickname = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farm_name);

        nickname.setText(user);
        farmName.setText(farm);

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

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        String nama = response.getString("nama");
                        String jenis_kandang = response.getString("jenis_kandang");
                        String kapasitas = response.getString("kapasitas");
                        String jumlah_ayam = response.getString("jumlah_ayam");
                        id_ras_ayam = response.getString("id_ras_ayam");
                        id_pakan = response.getString("id_pakan");
                        String status_pakan = response.getString("status_pakan");
                        String status_kandang = response.getString("status_kandang");

                        getRas(id_ras_ayam);
                        getPakan(id_pakan);

                        heading.setText(nama);
                        namaKandang.setText(nama);
                        jenisKandang.setText(jenis_kandang);
                        kapasitasKandang.setText(kapasitas + " Ekor");
                        jumlahAyam.setText(jumlah_ayam + " Ekor");
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

        Eggspert.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void getRas(String idRas) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan, Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/rasayamku/" + idRas;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject rasAyamResponse = response.getJSONObject(0);
                        String namaRas = rasAyamResponse.getString("nama_ras_ayam");
                        rasAyam.setText(namaRas);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DetailKandang.this, "Gagal Mengambil Data! 1", Toast.LENGTH_SHORT).show();

                    }

                },
                error -> {
                    Log.e("API_ERROR", error.toString());
                    Toast.makeText(DetailKandang.this, "Gagal Mengambil Data! 2", Toast.LENGTH_SHORT).show();

                }
        ) {

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

    public void getPakan(String idPakan) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan, Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/pakanku/" + idPakan;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject pakanResponse = response.getJSONObject(0);
                        String jenis_pakan = pakanResponse.getString("jenis_pakan");
                        jenisPakan.setText(jenis_pakan);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }, error -> {
                    Log.e("API_ERROR", error.toString());
                    Toast.makeText(DetailKandang.this, "Gagal Mengambil Data! 2", Toast.LENGTH_SHORT).show();
                }

        ) {

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

}