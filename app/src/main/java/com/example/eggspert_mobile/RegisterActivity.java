package com.example.eggspert_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText etNama, etAlamat, etEmail, etUsn, etPass;
    CheckBox cbOK;
    Button daftar;
    TextView login;
    ImageButton hidden;

//    DBDataSource_peternak dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNama = findViewById(R.id.nama_regis);
        etAlamat = findViewById(R.id.alamat_regis);
        etEmail = findViewById(R.id.email_regis);
        etUsn = findViewById(R.id.usn_regis);
        etPass = findViewById(R.id.pass_regis);

        cbOK = findViewById(R.id.cb);

        daftar = findViewById(R.id.btn_regis);

        login = findViewById(R.id.btn_login);

        hidden = findViewById(R.id.hidden);

//        dataSource = new DBDataSource_peternak(getApplicationContext());
//        dataSource.open();

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = etNama.getText().toString();
                String alamat = etAlamat.getText().toString();
                String email = etEmail.getText().toString();
                String usn = etUsn.getText().toString();
                String pass = etPass.getText().toString();

                Peternak peternak = null;
                if( !nama.isEmpty() && !alamat.isEmpty() && !email.isEmpty() && !usn.isEmpty() && !pass.isEmpty()) {

                    boolean isChecked = cbOK.isChecked();

                    if (isChecked) {

                        register(nama, alamat, email, usn, pass);

//                        peternak = dataSource.register(nama, alamat, email, usn, pass);
//
//                        Toast.makeText(getApplicationContext(), "User Berhasil Dibuat", Toast.LENGTH_LONG).show();
//
//                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
//                        startActivity(i);

                    } else {
                        Toast.makeText(getApplicationContext(), "Harap Menyetujui Syarat Dan Ketentuan", Toast.LENGTH_SHORT).show();
                        return;

                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Semua Data Harus Terisi Dengan Benar", Toast.LENGTH_SHORT).show();
                    return;

                }

            }

        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

    }

    public void register(String nama, String alamat, String email, String username, String password) {
        String url = "http://10.0.2.2:8000/api/register";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nama", nama);
            jsonBody.put("alamat", alamat);
            jsonBody.put("email", email);
            jsonBody.put("username", username);
            jsonBody.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    Toast.makeText(getApplicationContext(), "Registrasi Berhasil", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
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
                            if (errors.has("alamat")) {
                                errorMessage.append(errors.getJSONArray("alamat").getString(0)).append("\n");
                            }
                            if (errors.has("email")) {
                                errorMessage.append(errors.getJSONArray("email").getString(0)).append("\n");
                            }
                            if (errors.has("username")) {
                                errorMessage.append(errors.getJSONArray("username").getString(0)).append("\n");
                            }
                            if (errors.has("password")) {
                                errorMessage.append(errors.getJSONArray("password").getString(0)).append("\n");
                            }

                            Toast.makeText(RegisterActivity.this, errorMessage.toString().trim(), Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Registrasi gagal, coba lagi", Toast.LENGTH_SHORT).show();

                    }

                }

        );

        Eggspert.getInstance().addToRequestQueue(jsonObjectRequest);

    }

}