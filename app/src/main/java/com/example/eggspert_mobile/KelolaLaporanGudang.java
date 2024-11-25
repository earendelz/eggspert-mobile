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

public class KelolaLaporanGudang extends AppCompatActivity {

    TextView farmName, nickname;

    RecyclerView rcvData;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layout;

    BottomNavigationView navBar;
    ImageButton addData, backButton;
    Intent i;

    ArrayList IDLIst, jenisList, tanggalList, jumlahList, IDGudangList;
    String idGudang, nama_peternak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kelola_laporan_gudang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String user_id = sharedPreferences.getString("user_id",null);

        getNamaUser(user_id);

        i = getIntent();
        idGudang = i.getStringExtra("id");

        navBar = findViewById(R.id.bottom_navigation);
        navBar.setSelectedItemId(R.id.navigation_farm);
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

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());

        addData = findViewById(R.id.add_button);
        addData.setOnClickListener(view -> {
            i = new Intent(this, CreateLaporanGudang.class);
            i.putExtra("id_gudang", idGudang);
            startActivity(i);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        rcvData = findViewById(R.id.rcv_data);
        layout = new LinearLayoutManager(this);
        rcvData.setLayoutManager(layout);
        rcvData.setHasFixedSize(true);

        IDLIst = new ArrayList<>();
        jenisList = new ArrayList<>();
        tanggalList = new ArrayList<>();
        jumlahList = new ArrayList<>();
        IDGudangList = new ArrayList<>();
        adapter = new LaporanGudangAdapter(this, IDLIst, jenisList, tanggalList, jumlahList, IDGudangList);

        rcvData.setAdapter(adapter);

        showData(idGudang);

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
                            nama_peternak = jsonObject.getString("nama");

                            nickname.setText(nama_peternak);
                            farmName.setText(nama_peternak + "'s Farm");

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

    public void showData(String idGudang) {

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan, Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/laporangudangku/" + idGudang;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        IDLIst.clear();
                        jenisList.clear();
                        tanggalList.clear();
                        jumlahList.clear();
                        IDGudangList.clear();

                        for(int count = 0; count < response.length(); count++ ) {
                            JSONObject laporanAyamObject = response.getJSONObject(count);
                            //Isi Array
                            long id = laporanAyamObject.getLong("id");
                            String jenisLaporan = laporanAyamObject.getString("keterangan");
                            String tanggalPeristiwa = laporanAyamObject.getString("tanggal_laporan_gudang");
                            String jumlahTelur = laporanAyamObject.getString("jumlah_telur");
                            String id_gudang = laporanAyamObject.getString("id_gudang");

                            IDLIst.add(id);
                            jenisList.add(jenisLaporan);
                            tanggalList.add(tanggalPeristiwa);
                            jumlahList.add(jumlahTelur);
                            IDGudangList.add(id_gudang);
                            Log.d("Count", "Count: " + count);

                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("API Error", "JSON Parsing Error: " +  e.getMessage());

                    }

                }, error -> {
            Log.e("API Error", "Error Response: " +  error.getMessage());
            Toast.makeText(KelolaLaporanGudang.this, "Gagal Mengambil Data Kandang", Toast.LENGTH_SHORT).show();

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

}