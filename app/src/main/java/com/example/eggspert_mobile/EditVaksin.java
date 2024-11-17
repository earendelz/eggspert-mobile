package com.example.eggspert_mobile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditVaksin extends AppCompatActivity {

    TextView farmName, nick;
    EditText etjenisVaksin, ettanggalVaksin;

    Button edit;
    ImageButton backButton;
    BottomNavigationView navBar;

    Intent i;
    String id, id_kandang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_vaksin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String nama_peternak = sharedPreferences.getString("nama",null);
        String user_id = sharedPreferences.getString("user_id",null);

        i = getIntent();
        id = i.getStringExtra("id");
        id_kandang = i.getStringExtra("id_kandang");

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

        backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(view -> finish());

        etjenisVaksin = findViewById(R.id.jenis_vaksin);
        ettanggalVaksin = findViewById(R.id.tanggal_vaksinasi);

        ettanggalVaksin.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (DatePicker v, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        ettanggalVaksin.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();

        });

        pickData(id);

        edit = findViewById(R.id.btn_edit);
        edit.setOnClickListener(view -> {
            String jenisVaksin = etjenisVaksin.getText().toString();
            String tanggalVaksin = ettanggalVaksin.getText().toString();
            editData(jenisVaksin, tanggalVaksin, id_kandang, id);
            finish();
        });

    }

    public void pickData(String idVaksin) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/vaksinasiku/" + idVaksin;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        String jenis_vaksin = response.getString("jenis_vaksin");
                        String tanggal_vaksin = response.getString("tanggal_vaksinasi");

                        etjenisVaksin.setText(jenis_vaksin);
                        ettanggalVaksin.setText(tanggal_vaksin);
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }},

                error -> {
                    Log.e("API Error", "Error Response: " + error.getMessage());

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                Log.d("Token", "Bearer " + token);
                return headers;

            }};

        Eggspert.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void editData(String jenis_vaksin, String tanggal_vaksin, String idKandang, String idVaksin) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/vaksinasiku/" + idVaksin;
        Log.d("ID", "ID Vaksin: " + idVaksin + "ID Kandang: " + idKandang);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("jenis_vaksin", jenis_vaksin);
            jsonBody.put("tanggal_vaksinasi", tanggal_vaksin);

        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT, url, jsonBody,
                response -> {
                    Log.d("API Response", response.toString());
                    Toast.makeText(getApplicationContext(), "Ubah Data Berhasil!", Toast.LENGTH_SHORT).show();

                }, error -> {
            if (error.networkResponse != null && error.networkResponse.statusCode == 422) {
                try {
                    String errorResponse = new String(error.networkResponse.data, "UTF-8");
                    JSONObject jsonObject = new JSONObject(errorResponse);
                    JSONObject errors = jsonObject.getJSONObject("errors");

                    StringBuilder errorMessage = new StringBuilder();

                    if (errors.has("jenis_vaksin")) {
                        errorMessage.append(errors.getJSONArray("jenis_vaksin").getString(0)).append("\n");
                    }
                    if (errors.has("tanggal_vaksinasi")) {
                        errorMessage.append(errors.getJSONArray("tanggal_vaksinasi").getString(0)).append("\n");
                    }

                    if (errors.has("id_product")) {
                        errorMessage.append(errors.getJSONArray("id_product").getString(0)).append("\n");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show();

                }

            } else {
                Log.e("Error", error.getMessage());
                Toast.makeText(getApplicationContext(), "Ubah Data Gagal! Silahkan Coba Lagi", Toast.LENGTH_SHORT).show();

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

}