package com.example.eggspert_mobile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditLaporanAyam extends AppCompatActivity {

    TextView farmName, nickname;
    EditText ettanggalPer, etJml ;

    Intent i; Button edit;
    ImageButton backButton;
    BottomNavigationView navBar;

    Spinner jenisLaporan;
    ArrayList<String> jenisLaporanData;

    String id, id_kandang, user_id, nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_laporan_ayam);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id",null);

        i = getIntent();
        id = i.getStringExtra("id");
        id_kandang = i.getStringExtra("id_kandang");

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

        getNamaUser(user_id);

        backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(view -> finish());

        ettanggalPer = findViewById(R.id.tanggal_peristiwa);
        etJml = findViewById(R.id.jml_ayam);

        jenisLaporan = findViewById(R.id.jenis_laporan);
        jenisLaporanData = new ArrayList<>();
        jenisLaporanData.add("Kematian");
        jenisLaporanData.add("Kelahiran");
        ArrayAdapter<String> adapterJLaporan = new ArrayAdapter<>(
                this, R.layout.custom_spinner_item, jenisLaporanData
        );
        adapterJLaporan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jenisLaporan.setAdapter(adapterJLaporan);
        jenisLaporan.setPopupBackgroundResource(R.drawable.custom_spinner_dropdown_background);

        ettanggalPer.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (DatePicker v, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        ettanggalPer.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();

        });

        pickData(id);

        edit = findViewById(R.id.btn_edit);
        edit.setOnClickListener(view -> {
            String tanggalPeristiwa = ettanggalPer.getText().toString();
            int jumlahAyam = Integer.parseInt(etJml.getText().toString());
            String jenis_laporan = jenisLaporan.getSelectedItem().toString();

            editData(jumlahAyam, jenis_laporan, tanggalPeristiwa, id_kandang, id);
            finish();
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

    public void pickData(String idLaporan) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/laporan-ayam/" + idLaporan;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);

                        String jumlah_ayam = jsonObject.getString("jumlah_ayam");
                        String jenis_laporan = jsonObject.getString("jenis_laporan");
                        String tanggal_peristiwa = jsonObject.getString("tanggal_peristiwa");

                        etJml.setText(jumlah_ayam);
                        ettanggalPer.setText(tanggal_peristiwa);

                        int jenisLaporanPos = jenisLaporanData.indexOf(jenis_laporan);
                        jenisLaporan.setSelection(jenisLaporanPos);

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

        Eggspert.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    public void editData(int jmlAyam, String jenis_laporan, String tanggal_peristiwa, String idKandang, String idLaporan) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/laporanayamku/" + idLaporan;
        Log.d("ID", "ID Vaksin: " + idLaporan + "ID Kandang: " + idKandang);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("jumlah_ayam", jmlAyam);
            jsonBody.put("jenis_laporan", jenis_laporan);
            jsonBody.put("tanggal_peristiwa", tanggal_peristiwa);
            jsonBody.put("id_kandang", idKandang);

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

                    if (errors.has("jumlah_ayam")) {
                        errorMessage.append(errors.getJSONArray("jumlah_ayam").getString(0)).append("\n");
                    }

                    if (errors.has("jenis_laporan")) {
                        errorMessage.append(errors.getJSONArray("jenis_laporan").getString(0)).append("\n");
                    }
                    if (errors.has("tanggal_peristiwa")) {
                        errorMessage.append(errors.getJSONArray("tanggal_peristiwa").getString(0)).append("\n");
                    }

                    if (errors.has("id_kandang")) {
                        errorMessage.append(errors.getJSONArray("id_kandang").getString(0)).append("\n");
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