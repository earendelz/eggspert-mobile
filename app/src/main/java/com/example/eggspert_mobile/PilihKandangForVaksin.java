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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PilihKandangForVaksin extends AppCompatActivity {

    TextView farmName, nickname;

    RecyclerView rcvData;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layout;

    BottomNavigationView navBar;
    ImageButton backButton;
    Intent i; String user_id, nama;

    ArrayList IDLIst, namaList, jenisList, jmlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pilih_kandang_for_vaksin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id",null);

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

        i = getIntent();

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();

        getNamaUser(user_id);

        rcvData = findViewById(R.id.rcv_data);
        layout = new LinearLayoutManager(this);
        rcvData.setLayoutManager(layout);
        rcvData.setHasFixedSize(true);

        IDLIst = new ArrayList<>();
        namaList = new ArrayList<>();
        jenisList = new ArrayList<>();
        jmlList = new ArrayList<>();
        adapter = new DataAdapterVaksin(this, IDLIst, namaList, jenisList, jmlList);

        rcvData.setAdapter(adapter);

        showData();
    }

    public void showData() {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan, Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/kandangku";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API Response", response.toString());
                        IDLIst.clear();
                        namaList.clear();
                        jenisList.clear();
                        jmlList.clear();

                        for(int count = 0; count < response.length(); count++ ) {
                            JSONObject kandangObject = response.getJSONObject(count);
                            //Isi Array
                            long id = kandangObject.getLong("id");
                            String nama = kandangObject.getString("nama");
                            String jenisKandang = kandangObject.getString("jenis_kandang");
                            int jumlah_ayam = kandangObject.getInt("jumlah_ayam");

                            IDLIst.add(id);
                            namaList.add(nama);
                            jenisList.add(jenisKandang);
                            jmlList.add(jumlah_ayam);
                            Log.d("Count", "Count: " + count);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("API Error", "JSON Parsing Error: " +  e.getMessage());

                    }

                },
                error -> {
                    Log.e("API Error", "Error Response: " +  error.getMessage());
                    Toast.makeText(PilihKandangForVaksin.this, "Gagal Mengambil Data Kandang", Toast.LENGTH_SHORT).show();
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
                            nama = jsonObject.getString("nama");

                            nickname.setText(nama);
                            farmName.setText(nama + "'s Farm");

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