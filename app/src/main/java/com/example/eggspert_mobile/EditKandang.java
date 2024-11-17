package com.example.eggspert_mobile;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditKandang extends AppCompatActivity {

    TextView nick, farmName;

    EditText etNama, etKap, etJml;
    Spinner jenisKandang, rasAyam, jenisPakan;
    RadioGroup rg_sPakan; RadioButton rbManual, rbOtomatis, rb_sPakan;
    ToggleButton tg_sKandang;

    ArrayList<String> jenisKandangData;
    List<RasAyam> rasData; List<Pakan> pakanData;
    ArrayAdapter<RasAyam> adapterRas;
    ArrayAdapter<Pakan> adapterPakan;

    BottomNavigationView navBar;

    Button edit;
    ImageButton back;

    Pakan selectedPakan; RasAyam selectedRas;

    String nama_kandang, jenis_kandang, kapasitas, jumlah_ayam, status_pakan, status_kandang, id_ras_ayam, id_pakan;
    String val_namaKandang, val_jenis_kandang, val_kapasitas, val_jumlah_ayam, idRas, idPakan, statusPakan, statusKandang;

    int kapasitasKandang, jumlahAyam, rasId, pakanId;

    Intent i;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_kandang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String user = sharedPreferences.getString("nama", null);
        String user_id = sharedPreferences.getString("nama", null);

        navBar = findViewById(R.id.bottom_navigation);
        navBar.setSelectedItemId(R.id.navigation_home);
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                i = new Intent(this, HomePage.class);
                i.putExtra("name", user);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_profile)  {
                i = new Intent(this, ProfileActivity.class);
                i.putExtra("name", user);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_farm) {
                i = new Intent(this, FarmActivity.class);
                i.putExtra("name", user);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            }
            return false;
        });

        //User's Bio
        nick = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farm_name);
        nick.setText(user);
        String farm_name = user + "'s Farm";
        farmName.setText(farm_name);

        //All Edit Text
        etNama = findViewById(R.id.nama_kandang);
        etKap = findViewById(R.id.kapasitas);
        etJml = findViewById(R.id.jml_ayam);

        //Toggle Status Kandang
        tg_sKandang = findViewById(R.id.tg_status_kandang);

        //Spinner Jenis Kandang
        jenisKandang = findViewById(R.id.jenis_kandang);
        jenisKandangData = new ArrayList<>();
        jenisKandangData.add("Petelur");
        jenisKandangData.add("Pedaging");
        ArrayAdapter<String> adapterJKandang = new ArrayAdapter<>(
                this, R.layout.custom_spinner_item, jenisKandangData
        );
        adapterJKandang.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jenisKandang.setAdapter(adapterJKandang);
        jenisKandang.setPopupBackgroundResource(R.drawable.custom_spinner_dropdown_background);

        // Spinner Ras Ayam
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

        // Spinner Jenis Pakan
        jenisPakan = findViewById(R.id.jenis_pakan);
        pakanData = new ArrayList<>();
        pickPakan();

        adapterPakan = new ArrayAdapter<Pakan>(
                this, R.layout.custom_spinner_item, pakanData) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                Pakan pakanAyam = getItem(position);
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setText(pakanAyam.getJenis_pakan());
                return textView;

            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                Pakan pakanAyam = getItem(position);
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                textView.setText(pakanAyam.getJenis_pakan());

                return textView;

            }

        };
        adapterPakan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterPakan.notifyDataSetChanged();
        jenisPakan.setAdapter(adapterPakan);
        jenisPakan.setPopupBackgroundResource(R.drawable.custom_spinner_dropdown_background);
        jenisPakan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedPakan = (Pakan) adapterView.getItemAtPosition(i);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Radio Group
        rg_sPakan = findViewById(R.id.rg_status);
        rbManual = findViewById(R.id.rb_manual);
        rbOtomatis = findViewById(R.id.rb_otomatis);

        i = getIntent();
        id = i.getStringExtra("id");
        pickData(id);

        edit = findViewById(R.id.btn_edit);
        edit.setOnClickListener(view -> {
            val_namaKandang = etNama.getText().toString();

            val_jenis_kandang = jenisKandang.getSelectedItem().toString();

            val_kapasitas = etKap.getText().toString();
            kapasitasKandang = Integer.parseInt(val_kapasitas);

            val_jumlah_ayam = etJml.getText().toString();
            jumlahAyam = Integer.parseInt(val_jumlah_ayam);

            idRas = String.valueOf(selectedRas.getId());
            rasId = Integer.parseInt(idRas);

            idPakan = String.valueOf(selectedPakan.getId());
            pakanId = Integer.parseInt(idPakan);

            int checkedRGID = rg_sPakan.getCheckedRadioButtonId();
            rb_sPakan = findViewById(checkedRGID);
            statusPakan = rb_sPakan.getText().toString();

            statusKandang = "tidak tersedia";
            tg_sKandang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        statusKandang = "tersedia";
                    } else {
                        statusKandang = "tidak tersedia";
                    }
                    Log.d("Status Kandang", statusKandang);

                }

            });

            editKandang(val_namaKandang, val_jenis_kandang, kapasitasKandang, jumlahAyam,
                    rasId, pakanId, statusPakan, statusKandang, id);

            finish();
        });

        back = findViewById(R.id.btn_back);
        back.setOnClickListener(view -> finish());

    }

    private void pickData(String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/kandangku/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        nama_kandang = response.getString("nama");
                        jenis_kandang = response.getString("jenis_kandang");
                        kapasitas = response.getString("kapasitas");
                        jumlah_ayam = response.getString("jumlah_ayam");
                        id_ras_ayam = response.getString("id_ras_ayam");
                        id_pakan = response.getString("id_pakan");
                        status_pakan = response.getString("status_pakan");
                        status_kandang = response.getString("status_kandang");

                        etNama.setText(nama_kandang);
                        etKap.setText(kapasitas);
                        etJml.setText(jumlah_ayam);

                        int jenisKandangPos = jenisKandangData.indexOf(jenis_kandang);
                        jenisKandang.setSelection(jenisKandangPos);

                        setRas(id_ras_ayam);
                        setPakan(id_pakan);

                        if ("Manual".equals(status_pakan)) {
                            rbManual.setChecked(true);

                        } else if ("Otomatis".equals(status_pakan)) {
                            rbOtomatis.setChecked(true);

                        }

                        if ("tersedia".equals(status_kandang)) {
                            tg_sKandang.setChecked(true);  // Toggle ON (Tersedia)
                        } else {
                            tg_sKandang.setChecked(false);  // Toggle OFF (Tidak Tersedia)
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

    private void pickRas() {
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

                            RasAyam ras_Ayam = new RasAyam();
                            ras_Ayam.setId(id);
                            ras_Ayam.setNama_ras_ayam(namaRasAyam);

                            rasData.add(ras_Ayam);

                        }

                        adapterRas.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("API Error", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(EditKandang.this, "Gagal Mengambil Data Ras Ayam", Toast.LENGTH_SHORT).show();
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

    private void pickPakan() {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/pakanku";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API RESPONSE", response.toString());

                        pakanData.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject pakanObject = response.getJSONObject(i);

                            long id = pakanObject.getLong("id");
                            String jenis_pakan = pakanObject.getString("jenis_pakan");

                            Pakan pakan = new Pakan();
                            pakan.setId(id);
                            pakan.setJenis_pakan(jenis_pakan);

                            pakanData.add(pakan);

                        }

                        adapterPakan.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("API Error", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(EditKandang.this, "Gagal Mengambil Data Pakan", Toast.LENGTH_SHORT).show();

                    }

                },
                error -> {
                    Log.e("API Error", "Error Response: " + error.getMessage());

                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                Log.d("Token", "Bearer " + token);
                return headers;

            }

        };

        Eggspert.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private void setRas(String RasID) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if ( token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/rasayamku/" + RasID;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject rasObject = response.getJSONObject(0);

                        long id_ras = rasObject.getLong("id");
                        int selectedPos = -1;
                        for (int i = 0; i < rasData.size(); i++) {
                            if (rasData.get(i).getId() == id_ras) {
                                selectedPos = i;
                                break;
                            }
                        }

                        if (selectedPos != -1) {
                            rasAyam.setSelection(selectedPos);
                        }

                    } catch (JSONException e) {
                        Log.e("API Error", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(EditKandang.this, "Gagal Mengambil Data Ras Ayam", Toast.LENGTH_SHORT).show();
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

            }

        };

        Eggspert.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private void setPakan(String PakanID) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if ( token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali!", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/pakanku/" + PakanID;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject pakanObject = response.getJSONObject(0);

                        long id_pakan = pakanObject.getLong("id");
                        int selectedPos = -1;
                        for (int i = 0; i < pakanData.size(); i++) {
                            if (pakanData.get(i).getId() == id_pakan) {
                                selectedPos = i;
                                break;
                            }
                        }

                        if (selectedPos != -1) {
                            jenisPakan.setSelection(selectedPos);
                        }

                    } catch (JSONException e) {
                        Log.e("API Error", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(EditKandang.this, "Gagal Mengambil Data Ras Ayam", Toast.LENGTH_SHORT).show();
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

        Eggspert.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private void editKandang(String nama_kandang, String jenis_kandang, int kapasitas, int jumlah_ayam,
                             int rasId, int pakanId, String status_pakan, String status_kandang, String id) {

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/kandangku/" + id;

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("nama", nama_kandang);
            jsonBody.put("jenis_kandang", jenis_kandang);
            jsonBody.put("kapasitas", kapasitas);
            jsonBody.put("jumlah_ayam", jumlah_ayam);
            jsonBody.put("id_ras_ayam", rasId);
            jsonBody.put("id_pakan", pakanId);
            jsonBody.put("status_pakan", status_pakan);
            jsonBody.put("status_kandang", status_kandang);


        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT, url, jsonBody,
                response -> {
                    Log.d("API Response", response.toString());
                    Toast.makeText(getApplicationContext(), "Ubah Data Berhasil!", Toast.LENGTH_SHORT).show();

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
                            if (errors.has("jenis_kandang")) {
                                errorMessage.append(errors.getJSONArray("jenis_kandang").getString(0)).append("\n");
                            }

                            if (errors.has("kapasitas")) {
                                errorMessage.append(errors.getJSONArray("kapasitas").getString(0)).append("\n");
                            }
                            if (errors.has("jumlah_ayam")) {
                                errorMessage.append(errors.getJSONArray("jumlah_ayam").getString(0)).append("\n");
                            }

                            if (errors.has("id_ras_ayam")) {
                                errorMessage.append(errors.getJSONArray("id_ras_ayam").getString(0)).append("\n");
                            }
                            if (errors.has("id_pakan")) {
                                errorMessage.append(errors.getJSONArray("id_pakan").getString(0)).append("\n");
                            }

                            if (errors.has("status_pakan")) {
                                errorMessage.append(errors.getJSONArray("status_pakan").getString(0)).append("\n");
                            }
                            if (errors.has("status_kandang")) {
                                errorMessage.append(errors.getJSONArray("status_kandang").getString(0)).append("\n");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Log.e("Error", error.getMessage());
                        Toast.makeText(getApplicationContext(), "Ubah Data Gagal! Silahkan Coba Lagi", Toast.LENGTH_SHORT).show();

                    }

                })
        {

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