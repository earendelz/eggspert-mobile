package com.example.eggspert_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KelolaKandang extends AppCompatActivity {
    TextView farmName, nick;

    RecyclerView rcvData;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layout;
    private String user_id;

    ImageButton addData, backButton;
    Intent i;

    ArrayList<Kandang> kandangList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kelola_kandang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nick = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farm_name);

        i = getIntent();
        String nama_peternak = i.getStringExtra("name");

        nick.setText(nama_peternak);
        farmName.setText(nama_peternak + "'s Farm");

        addData = findViewById(R.id.add_button);

        addData.setOnClickListener(view -> {
            i = new Intent(this, CreateKandang.class);
            startActivity(i);
        });

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> finish());

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", null);

        if ( user_id != null) {

            rcvData = findViewById(R.id.rcv_data);
            layout = new LinearLayoutManager(this);
            rcvData.setLayoutManager(layout);
            rcvData.setHasFixedSize(true);

            kandangList = new ArrayList<>();
            adapter = new DataAdapter(this, kandangList);

            rcvData.setAdapter(adapter);

            kandangList.clear();

            showData();

        }
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

                        for(int count = 0; count < response.length(); count++ ) {
                            JSONObject kandangObject = response.getJSONObject(count);
                            //Isi Array
                            long id = kandangObject.getLong("id");
                            String nama = kandangObject.getString("nama");
                            String jenisKandang = kandangObject.getString("jenis_kandang");
                            int kapasitas = kandangObject.getInt("kapasitas");

                            Kandang kandang = new Kandang();
                            kandang.setId(id);
                            kandang.setNama(nama);
                            kandang.setJenis_kandang(jenisKandang);
                            kandang.setKapasitas(kapasitas);

                            kandangList.add(kandang);
                            Log.d("Count", "Count: " + count);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("API Error", "JSON Parsing Error: " +  e.getMessage());

                    }

                },
                error -> {
                    Log.e("API Error", "Error Response: " +  error.getMessage());
                    Toast.makeText(KelolaKandang.this, "Gagal Mengambil Data Kandang", Toast.LENGTH_SHORT).show();
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