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

public class EditLaporanGudang extends AppCompatActivity {

    TextView farmName, nickname;
    EditText etKet, ettanggal, etJml ;

    Button edit; String user_id, nama, id;
    ImageButton backButton;
    BottomNavigationView navBar;

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_laporan_gudang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id",null);

        i = getIntent();
        id = i.getStringExtra("id");
        String idGudang = i.getStringExtra("id_gudang");

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

        getNamaUser(user_id);

        backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(view -> finish());

        etKet = findViewById(R.id.keterangan);;
        ettanggal = findViewById(R.id.tanggal_laporan);
        etJml = findViewById(R.id.jml_telur);

        ettanggal.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (DatePicker v, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        ettanggal.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();

        });

        pickData(id);

        edit = findViewById(R.id.btn_edit);
        edit.setOnClickListener(view -> {
            String keterangan = etKet.getText().toString();
            String tanggal = ettanggal.getText().toString();
            int jmlTelur = Integer.parseInt(etJml.getText().toString());

            editData(keterangan, tanggal, jmlTelur, idGudang, id);
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

    private void pickData(String idLaporan) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/laporan-gudang/" + idLaporan;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);

                        String jumlah_telur = jsonObject.getString("jumlah_telur");
                        String keterangan = jsonObject.getString("keterangan");
                        String tanggal = jsonObject.getString("tanggal_laporan_gudang");

                        etJml.setText(jumlah_telur);
                        ettanggal.setText(tanggal);
                        etKet.setText(keterangan);

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

    private void editData(String keterangan, String tanggalLaporan, int jmlTelur, String idGudang, String idLaporan) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/laporangudangku/" + idLaporan;
        Log.d("ID", "ID Vaksin: " + idLaporan + "ID Gudang: " + idGudang);

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("jumlah_telur", jmlTelur);
            jsonBody.put("keterangan", keterangan);
            jsonBody.put("tanggal_laporan_gudang", tanggalLaporan);
            jsonBody.put("id_gudang", idGudang);

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

                    if (errors.has("jumlah_telur")) {
                        errorMessage.append(errors.getJSONArray("jumlah_telur").getString(0)).append("\n");
                    }

                    if (errors.has("keterangan")) {
                        errorMessage.append(errors.getJSONArray("keterangan").getString(0)).append("\n");
                    }
                    if (errors.has("tanggal_laporan_gudang")) {
                        errorMessage.append(errors.getJSONArray("tanggal_laporan_gudang").getString(0)).append("\n");
                    }

                    if (errors.has("id_gudang")) {
                        errorMessage.append(errors.getJSONArray("id_gudang").getString(0)).append("\n");
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