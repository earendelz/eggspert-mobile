package com.example.eggspert_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    EditText etNama, etEmail, etAlamat, etUsn;
    ImageButton back, save;

    BottomNavigationView navBar;
    String userid; Intent i;
    String usn, nama, email, alamat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        userid = sharedPreferences.getString("user_id", null);

        navBar = findViewById(R.id.bottom_navigation);
        navBar.setSelectedItemId(R.id.navigation_profile);
        navBar.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                i = new Intent(this, HomePage.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", userid);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_profile)  {
                i = new Intent(this, ProfileActivity.class);
                startActivity(i);
                return true;
            } else if (itemId == R.id.navigation_farm) {
                i = new Intent(this, FarmActivity.class);
                i.putExtra("name", nama);
                i.putExtra("user_id", userid);
                startActivity(i);
                return true;
            }
            return false;
        });

        pickData(userid);

        save = findViewById(R.id.button_simpan);
        save.setOnClickListener(view -> {
            String userNama = etNama.getText().toString();
            String userEmail = etEmail.getText().toString();
            String userAlamat = etAlamat.getText().toString();
            String userName = etUsn.getText().toString();
            saveData(userNama, userEmail, userAlamat, userName, userid);
            finish();
        });

        back = findViewById(R.id.button_kembali);
        back.setOnClickListener(view -> finish());

    }

    private void pickData(String userID) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        etNama = findViewById(R.id.editTextNama);
        etEmail = findViewById(R.id.editTextEmail);
        etAlamat = findViewById(R.id.editTextAddress);
        etUsn = findViewById(R.id.editTextUsn);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/users/" + userID;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");

                        if (success) {
                            JSONObject jsonObject = response.getJSONObject("data");

                            nama = jsonObject.getString("nama");
                            email = jsonObject.getString("email");
                            alamat = jsonObject.getString("alamat");
                            usn = jsonObject.getString("username");

                            etNama.setText(nama);
                            etEmail.setText(email);
                            etAlamat.setText(alamat);
                            etUsn.setText(usn);

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

    private void saveData(String Nama, String Email, String Alamat, String Usn, String userID) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/users/" + userID;

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("username", Usn);
            jsonBody.put("nama", Nama);
            jsonBody.put("email", Email);
            jsonBody.put("alamat", Alamat);


        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT, url, jsonBody,
                response -> {
                    Log.d("API Response", response.toString());
                    Toast.makeText(getApplicationContext(), "Ubah Data Berhasil!", Toast.LENGTH_SHORT).show();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("username", Usn);
                    editor.putString("nama", Nama);
                    editor.putString("email", Email);
                    editor.putString("alamat", Alamat);
                    editor.apply();

                },
                error -> {
                    if (error.networkResponse != null && error.networkResponse.statusCode == 422) {
                        try {
                            String errorResponse = new String(error.networkResponse.data, "UTF-8");
                            JSONObject jsonObject = new JSONObject(errorResponse);
                            JSONObject errors = jsonObject.getJSONObject("errors");

                            StringBuilder errorMessage = new StringBuilder();

                            if (errors.has("username")) {
                                errorMessage.append(errors.getJSONArray("username").getString(0)).append("\n");
                            }
                            if (errors.has("nama")) {
                                errorMessage.append(errors.getJSONArray("nama").getString(0)).append("\n");
                            }

                            if (errors.has("email")) {
                                errorMessage.append(errors.getJSONArray("email").getString(0)).append("\n");
                            }
                            if (errors.has("password")) {
                                errorMessage.append(errors.getJSONArray("password").getString(0)).append("\n");
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