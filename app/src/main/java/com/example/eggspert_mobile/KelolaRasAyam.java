package com.example.eggspert_mobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
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
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KelolaRasAyam extends AppCompatActivity {

    ImageButton add, back;
    TextView nickname, farmName;
    ListView lvRas; ArrayList<RasAyam> arrayRas;
    ArrayAdapter<RasAyam> rasAyamArrayAdapter;

    BottomNavigationView navBar;

    String user_id, nama;
    EditText etNama, etJenis;
    Button submit; Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kelola_ras_ayam);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", null);
        nama = sharedPreferences.getString("nama", null);

        navBar = findViewById(R.id.bottom_navigation);
        navBar.setSelectedItemId(R.id.navigation_home);
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                i = new Intent(this, HomePage.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_profile)  {
                i = new Intent(this, ProfileActivity.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_farm) {
                i = new Intent(this, FarmActivity.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", user_id);
                startActivity(i);
                return true;
            }
            return false;
        });

        nickname = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farm_name);

        nickname.setText(nama);
        farmName.setText(nama + "'s Farm");

        add = findViewById(R.id.add_data);
        back = findViewById(R.id.back_button);

        add.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.ras_dialog);
            dialog.setTitle("Tambah Data Ras");
            dialog.show();

            etNama = dialog.findViewById(R.id.add_nama);
            etJenis = dialog.findViewById(R.id.add_jenis);
            submit = dialog.findViewById(R.id.btn_add);

            submit.setOnClickListener(view1 -> {
                String nama = etNama.getText().toString();
                String jenis = etJenis.getText().toString();
                createRas(nama, jenis);
                dialog.dismiss();

            });

        });

        back.setOnClickListener(view -> finish());

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", null);
        nama = sharedPreferences.getString("nama", null);

        lvRas = findViewById(R.id.lv_data);
        arrayRas = new ArrayList<>();
        rasAyamArrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, arrayRas);

        lvRas.setAdapter(rasAyamArrayAdapter);

        showData();

        lvRas.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                BottomSheetDialog bsdOption = new BottomSheetDialog(KelolaRasAyam.this);
                bsdOption.setContentView(LayoutInflater.from(KelolaRasAyam.this).inflate( R.layout.bottom_sheet_dialog, null));

                TextView edit_opt, delete_opt;
                edit_opt = bsdOption.findViewById(R.id.edit_opt);
                delete_opt = bsdOption.findViewById(R.id.delete_opt);

                final RasAyam selected = (RasAyam) lvRas.getAdapter().getItem(i);
                String id = String.valueOf(selected.getId());

                edit_opt.setOnClickListener(view1 -> {
                    final Dialog dialog = new Dialog(KelolaRasAyam.this);
                    dialog.setContentView(R.layout.ras_dialog);
                    dialog.setTitle("Edit Kandang");
                    dialog.show();

                    etNama = dialog.findViewById(R.id.add_nama);
                    etJenis = dialog.findViewById(R.id.add_jenis);
                    submit = dialog.findViewById(R.id.btn_add);

                    submit.setText("Ubah");

                    pickData(id);

                    submit.setOnClickListener(view2 -> {
                        String nama = etNama.getText().toString();
                        String jenis = etJenis.getText().toString();
                        editRas(nama, jenis, id);
                        dialog.dismiss();
                        bsdOption.dismiss();
                    });
                });

                delete_opt.setOnClickListener(view1 -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(KelolaRasAyam.this);
                    builder.setTitle("Apakah Anda Yakin Ingin Menghapus Data Ini?")
                            .setItems(new CharSequence[]{"Ya, Hapus", "Tidak"}, (dialog, which) -> {

                                switch (which) {

                                    case 0:
                                        deleteData(id);
                                        bsdOption.dismiss();
                                        break;

                                    case 1:
                                        dialog.dismiss();
                                        break;
                                }

                            });

                    builder.create().show();

                });

                bsdOption.show();

                return false;
            }

        });

    }

    public void showData() {
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

                        arrayRas.clear();

                        for (int count = 0; count < response.length(); count++) {
                            JSONObject rasAyamObject = response.getJSONObject(count);

                            long id = rasAyamObject.getLong("id");
                            String namaRasAyam = rasAyamObject.getString("nama_ras_ayam");
                            String jenisAyam = rasAyamObject.getString("jenis_ayam");
                            int id_peternak = rasAyamObject.getInt("id_peternak");

                            RasAyam rasAyam = new RasAyam();
                            rasAyam.setId(id);
                            rasAyam.setNama_ras_ayam(namaRasAyam);
                            rasAyam.setJenis_ayam(jenisAyam);
                            rasAyam.setId_peternak(id_peternak);

                            arrayRas.add(rasAyam);

                        }

                        rasAyamArrayAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("API Error", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(KelolaRasAyam.this, "Gagal Mengambil Data Ras Ayam", Toast.LENGTH_SHORT).show();
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

    public void createRas(String nama, String jenis) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        String user_id = sharedPreferences.getString("user_id", null);
        Log.d("SharedPreferences", "Token: " + token);
        Log.d("SharedPreferences", "User ID: " + user_id);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/rasayamku";

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("nama_ras_ayam", nama);
            jsonBody.put("jenis_ayam", jenis);
            Log.d("Input Values", "Nama: " + nama + ", Jenis: " + jenis);


        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    Log.d("API Response", response.toString());
                    Toast.makeText(getApplicationContext(), "Tambah Data Berhasil!", Toast.LENGTH_SHORT).show();

                    showData();

                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 422) {
                        try {
                            String errorResponse = new String(error.networkResponse.data, "UTF-8");
                            JSONObject jsonObject = new JSONObject(errorResponse);
                            JSONObject errors = jsonObject.getJSONObject("errors");

                            StringBuilder errorMessage = new StringBuilder();

                            if (errors.has("nama_ras_ayam")) {
                                errorMessage.append(errors.getJSONArray("nama_ras_ayam").getString(0)).append("\n");
                            }
                            if (errors.has("jenis_ayam")) {
                                errorMessage.append(errors.getJSONArray("jenis_ayam").getString(0)).append("\n");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show();

                        }

                    } else {
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

    public void pickData(String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        String user_id = sharedPreferences.getString("user_id", null);
        Log.d("SharedPreferences", "Token: " + token);
        Log.d("SharedPreferences", "User ID: " + user_id);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/rasayamku/" + id;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null, response -> {
                    try {
                        JSONObject rasAyamObject = response.getJSONObject(0);

                        String namaRas = rasAyamObject.getString("nama_ras_ayam");
                        String jenis = rasAyamObject.getString("jenis_ayam");

                        etNama.setText(namaRas);
                        etJenis.setText(jenis);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(KelolaRasAyam.this, "Gagal Mengambil Data! 1", Toast.LENGTH_SHORT).show();

                    }

                },
                error -> {
                    Log.e("API_ERROR", error.toString());
                    Toast.makeText(KelolaRasAyam.this, "Gagal Mengambil Data! 2", Toast.LENGTH_SHORT).show();

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

    };

    public void editRas(String nama, String jenis, String id){
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;

        }

        String url = "http://10.0.2.2:8000/api/rasayamku/" + id;

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("nama_ras_ayam", nama);
            jsonBody.put("jenis_ayam", jenis);
            Log.d("Input Values", "Nama: " + nama + ", Jenis: " + jenis);


        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT, url, jsonBody,
                response -> {
                    Log.d("API Response", response.toString());
                    Toast.makeText(getApplicationContext(), "Ubah Data Berhasil!", Toast.LENGTH_SHORT).show();

                    showData();

                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 422) {
                        try {
                            String errorResponse = new String(error.networkResponse.data, "UTF-8");
                            JSONObject jsonObject = new JSONObject(errorResponse);
                            JSONObject errors = jsonObject.getJSONObject("errors");

                            StringBuilder errorMessage = new StringBuilder();

                            if (errors.has("nama_ras_ayam")) {
                                errorMessage.append(errors.getJSONArray("nama_ras_ayam").getString(0)).append("\n");
                            }
                            if (errors.has("jenis_ayam")) {
                                errorMessage.append(errors.getJSONArray("jenis_ayam").getString(0)).append("\n");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Penambahan Data Gagal! Silahkan Coba Lagi", Toast.LENGTH_SHORT).show();

                    }

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

        Eggspert.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void deleteData(String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;

        }

        String url = "http://10.0.2.2:8000/api/rasayamku/" + id;

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(getApplicationContext(), "Data Berhasil Dihapus!", Toast.LENGTH_SHORT).show();
                    showData();

                },
                error -> {
                    if (error.networkResponse != null) {
                        int errorCode = error.networkResponse.statusCode;
                        Log.e("API Error", "Error Code: " + errorCode + " Message: " + new String(error.networkResponse.data));

                        if ( errorCode == 204) {
                            Toast.makeText(getApplicationContext(), "Data Berhasil Dihapus!", Toast.LENGTH_SHORT).show();
                            showData();

                        } else {
                            Toast.makeText(getApplicationContext(), "Gagal Menghapus Data! Silahkan Coba Lagi (1)", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Log.e("API Error", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Gagal Menghapus Data! Silahkan Coba Lagi (2)", Toast.LENGTH_SHORT).show();

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

        Eggspert.getInstance().addToRequestQueue(stringRequest);

    }

}