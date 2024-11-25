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

public class KelolaPakan extends AppCompatActivity {

    TextView nickname, farmName;
    ImageButton add, back;
    ListView lvPakan; ArrayList<Pakan> arrayPakan;
    ArrayAdapter<Pakan> pakanArrayAdapter;

    BottomNavigationView navBar;

    String user_id, nama;
    EditText etJenis;
    Button submit;

    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kelola_pakan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id", null);

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
                return true;
            } else if (itemId == R.id.navigation_farm) {
                i = new Intent(this, FarmActivity.class);
                startActivity(i);
                return true;
            }
            return false;
        });

        getNamaUser(user_id);

        add = findViewById(R.id.add_data);
        back = findViewById(R.id.back_button);

        add.setOnClickListener(view -> {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.pakan_dialog);
            dialog.setTitle("Tambah Data Pakan");
            dialog.show();

            etJenis = dialog.findViewById(R.id.add_jenis);
            submit = dialog.findViewById(R.id.btn_add);

            submit.setOnClickListener(view1 -> {
                String jenis_pakan = etJenis.getText().toString();
                createPakan(jenis_pakan);
                dialog.dismiss();
            });
        });

        back.setOnClickListener(view -> finish());

    }

    @Override
    protected void onResume() {
        super.onResume();

        lvPakan = findViewById(R.id.lv_data);
        arrayPakan = new ArrayList<>();
        pakanArrayAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_list_item_1, arrayPakan);

        lvPakan.setAdapter(pakanArrayAdapter);

        showData();

        lvPakan.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                BottomSheetDialog bsdOption = new BottomSheetDialog(KelolaPakan.this);
                bsdOption.setContentView(LayoutInflater.from(KelolaPakan.this).inflate(R.layout.bottom_sheet_dialog, null));

                TextView edit_opt, delete_opt;
                edit_opt = bsdOption.findViewById(R.id.edit_opt);
                delete_opt = bsdOption.findViewById(R.id.delete_opt);

                final Pakan selected = (Pakan) lvPakan.getAdapter().getItem(i);
                String id = String.valueOf(selected.getId());

                edit_opt.setOnClickListener(view1 -> {
                    final Dialog dialog = new Dialog(KelolaPakan.this);
                    dialog.setContentView(R.layout.pakan_dialog);
                    dialog.setTitle("Tambah Data Pakan");
                    dialog.show();

                    etJenis = dialog.findViewById(R.id.add_jenis);
                    submit = dialog.findViewById(R.id.btn_add);

                    submit.setText("Ubah");

                    pickData(id);

                    submit.setOnClickListener(view2 -> {
                        String jenis_pakan = etJenis.getText().toString();
                        editPakan(jenis_pakan, id);
                        dialog.dismiss();
                        bsdOption.dismiss();
                    });
                });

                delete_opt.setOnClickListener(view1 -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(KelolaPakan.this);
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

        if (token == null) {
            Toast.makeText(KelolaPakan.this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/pakanku";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        Log.d("API RESPONSE", response.toString());

                        arrayPakan.clear();


                        for (int i = 0; i < response.length(); i++) {
                            JSONObject pakanObject = response.getJSONObject(i);

                            long id = pakanObject.getLong("id");
                            String jenis_pakan = pakanObject.getString("jenis_pakan");
                            int id_peternak = pakanObject.getInt("id_peternak");

                            Pakan pakan = new Pakan();
                            pakan.setId(id);
                            pakan.setJenis_pakan(jenis_pakan);
                            pakan.setId_peternak(id_peternak);

                            arrayPakan.add(pakan);

                        }

                        pakanArrayAdapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        Log.e("API Error", "JSON Parsing Error: " + e.getMessage());
                        Toast.makeText(KelolaPakan.this, "Gagal Mengambil Data Pakan", Toast.LENGTH_SHORT).show();

                    }

                },
                error -> {
                    Log.e("API Error", "Error Response: " + error.getMessage());

                }) {

            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                Log.d("Token", "Bearer " + token);
                return headers;

            }

        };

        Eggspert.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    public void createPakan(String jenis_pakan) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token == null) {
            Toast.makeText(KelolaPakan.this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/pakanku";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("jenis_pakan", jenis_pakan);

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

                            if (errors.has("jenis_pakan")) {
                                errorMessage.append(errors.getJSONArray("jenis_pakan").getString(0)).append("\n");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan!", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Penambahan Data Gagal! Silahkan Coba Lagi", Toast.LENGTH_SHORT).show();

                    }

                }) {

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

        String url = "http://10.0.2.2:8000/api/pakanku/" + id;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONObject rasAyamObject = response.getJSONObject(0);

                        String jenis = rasAyamObject.getString("jenis_pakan");

                        etJenis.setText(jenis);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(KelolaPakan.this, "Gagal Mengambil Data! 1", Toast.LENGTH_SHORT).show();

                    }

                    },
                error -> {
                    Log.e("API_ERROR", error.toString());
                    Toast.makeText(KelolaPakan.this, "Gagal Mengambil Data! 2", Toast.LENGTH_SHORT).show();

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

    public void editPakan(String jenis_pakan, String id) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;

        }

        String url = "http://10.0.2.2:8000/api/pakanku/" + id;

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("jenis_pakan", jenis_pakan);
            Log.d("Input Values", "Jenis: " + jenis_pakan);


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

                            if (errors.has("jenis_pakan")) {
                                errorMessage.append(errors.getJSONArray("jenis_pakan").getString(0)).append("\n");
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

        if (token == null) {
            Toast.makeText(KelolaPakan.this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/pakanku/" + id;

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