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
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KelolaVaksin extends AppCompatActivity {

    TextView farmName, nick;

    RecyclerView rcvData;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layout;

    BottomNavigationView navBar;
    ImageButton addData, backButton;
    Intent i;

    ArrayList IDLIst, jenisList, tanggalList, IDKandangList;
    String idKandang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kelola_vaksin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String nama_peternak = sharedPreferences.getString("nama",null);
        String user_id = sharedPreferences.getString("user_id",null);

        i = getIntent();
        idKandang = i.getStringExtra("id");

        navBar = findViewById(R.id.bottom_navigation);
        navBar.setSelectedItemId(R.id.navigation_home);
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                i = new Intent(this, HomePage.class);
                i.putExtra("name", nama_peternak);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_profile)  {
                i = new Intent(this, ProfileActivity.class);
                i.putExtra("name", nama_peternak);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_farm) {
                i = new Intent(this, FarmActivity.class);
                i.putExtra("name", nama_peternak);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            }
            return false;
        });

        nick = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farm_name);

        nick.setText(nama_peternak);
        farmName.setText(nama_peternak + "'s Farm");

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());

        addData = findViewById(R.id.add_button);

        addData.setOnClickListener(view -> {
            i = new Intent(this, CreateVaksin.class);
            i.putExtra("id_kandang", idKandang);
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
        IDKandangList = new ArrayList<>();
        adapter = new VaksinAdapter(this, IDLIst, jenisList, tanggalList, IDKandangList);

        rcvData.setAdapter(adapter);

        showData(idKandang);
    }

    public void showData(String idKandang) {

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan, Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/vaksin/" + idKandang;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        IDLIst.clear();
                        jenisList.clear();
                        tanggalList.clear();
                        IDKandangList.clear();

                        for(int count = 0; count < response.length(); count++ ) {
                            JSONObject vaksinObject = response.getJSONObject(count);
                            //Isi Array
                            long id = vaksinObject.getLong("id");
                            String jenisVaksin = vaksinObject.getString("jenis_vaksin");
                            String tanggalVaksin = vaksinObject.getString("tanggal_vaksinasi");
                            String id_product = vaksinObject.getString("id_product");

                            IDLIst.add(id);
                            jenisList.add(jenisVaksin);
                            tanggalList.add(tanggalVaksin);
                            IDKandangList.add(id_product);
                            Log.d("Count", "Count: " + count);

                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("API Error", "JSON Parsing Error: " +  e.getMessage());

                    }

                }, error -> {
                    Log.e("API Error", "Error Response: " +  error.getMessage());
                    Toast.makeText(KelolaVaksin.this, "Gagal Mengambil Data Kandang", Toast.LENGTH_SHORT).show();

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