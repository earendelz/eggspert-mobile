package com.example.eggspert_mobile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.List;
import java.util.Map;

public class CreateGudang extends AppCompatActivity {

    TextView nickname, farmName;

    EditText etNama, etTanggal, etJml; Spinner rasAyam;
    ImageButton back; Button add;

    List<RasAyam> rasData; RasAyam selectedRas;
    ArrayAdapter<RasAyam> adapterRas;

    BottomNavigationView navBar;
    Intent i; String namaUser, user_id;

    String nama_gudang, tanggal_pembuatan, jml_telur, id_ras;
    int jumlah_telur, rasId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_gudang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id",null);

        getNamaUser(user_id);

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

        etNama = findViewById(R.id.nama_gudang);
        etTanggal = findViewById(R.id.tanggal_pembuatan);
        etJml = findViewById(R.id.jml_telur);

        etTanggal.setOnClickListener(view -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (DatePicker v, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
                        etTanggal.setText(selectedDate);
                    }, year, month, day);

            datePickerDialog.show();

        });

        rasAyam = findViewById(R.id.ras_ayam);
        rasData = new ArrayList<>();
        pickRas();
        adapterRas = new ArrayAdapter<RasAyam>(
                this, R.layout.custom_spinner_item, rasData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                RasAyam rasAyam = getItem(position);

                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setText(rasAyam.getNama_ras_ayam());

                return textView;

            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                RasAyam rasAyam = getItem(position);

                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setText(rasAyam.getNama_ras_ayam());

                return textView;

            }

        };

        adapterRas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterRas.notifyDataSetChanged();
        rasAyam.setAdapter(adapterRas);
        rasAyam.setPopupBackgroundResource(R.drawable.custom_spinner_dropdown_background);

        rasAyam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRas = (RasAyam) adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        add = findViewById(R.id.btn_add);

        add.setOnClickListener(view -> {
            nama_gudang = etNama.getText().toString();
            tanggal_pembuatan = etTanggal.getText().toString();

            jml_telur = etJml.getText().toString();
            jumlah_telur = Integer.parseInt(jml_telur);

            id_ras = String.valueOf(selectedRas.getId());
            rasId = Integer.parseInt(id_ras);

            createData(nama_gudang, jumlah_telur, tanggal_pembuatan, rasId);

            finish();
        });

        back = findViewById(R.id.back_button);
        back.setOnClickListener(view -> finish());
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

    public void pickRas() {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if ( token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/rasayamku";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API Response", response.toString());

                        rasData.clear();

                        for (int count = 0; count < response.length(); count++) {
                            JSONObject rasAyamObject = response.getJSONObject(count);

                            long id = rasAyamObject.getLong("id");
                            String namaRasAyam = rasAyamObject.getString("nama_ras_ayam");

                            RasAyam rasAyamSpinner = new RasAyam();
                            rasAyamSpinner.setId(id);
                            rasAyamSpinner.setNama_ras_ayam(namaRasAyam);

                            rasData.add(rasAyamSpinner);

                        }

                        adapterRas.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("API Error", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(CreateGudang.this, "Gagal Mengambil Data Ras Ayam", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    Log.e("API Error", "Error Response: " + error.getMessage());
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

    public void createData(String nama_gudang, int jumlah_telur, String tanggalPembuatan, int rasId) {

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/gudangku";

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("nama", nama_gudang);
            jsonBody.put("tanggal_pembuatan", tanggalPembuatan);
            jsonBody.put("jumlah_telur", jumlah_telur);
            jsonBody.put("id_ras_ayam", rasId);


        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    Log.d("API Response", response.toString());
                    Toast.makeText(getApplicationContext(), "Tambah Data Berhasil!", Toast.LENGTH_SHORT).show();

                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 422) {
                        try {
                            String errorResponse = new String(error.networkResponse.data, "UTF-8");
                            JSONObject jsonObject = new JSONObject(errorResponse);
                            JSONObject errors = jsonObject.getJSONObject("errors");

                            StringBuilder errorMessage = new StringBuilder();

                            if (errors.has("nama")) {
                                errorMessage.append(errors.getJSONArray("nama").getString(0)).append("\n");
                            }
                            if (errors.has("tanggal_pembuatan")) {
                                errorMessage.append(errors.getJSONArray("tanggal_pembuatan").getString(0)).append("\n");
                            }

                            if (errors.has("jumlah_telur")) {
                                errorMessage.append(errors.getJSONArray("jumlah_telur").getString(0)).append("\n");
                            }
                            if (errors.has("id_ras_ayam")) {
                                errorMessage.append(errors.getJSONArray("id_ras_ayam").getString(0)).append("\n");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Log.e("Error", error.getMessage());
                        Toast.makeText(getApplicationContext(), "Penambahan Data Gagal! Silahkan Coba Lagi", Toast.LENGTH_SHORT).show();

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