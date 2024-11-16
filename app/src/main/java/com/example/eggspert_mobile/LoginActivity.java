package com.example.eggspert_mobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText etUsn, etPass;
    TextView btnRegis; Button btnLogin;

//    DBHelper_peternak config;
//    DBDataSource_peternak dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsn = findViewById(R.id.usn_login);
        etPass = findViewById(R.id.pass_login);
        btnRegis = findViewById(R.id.btn_regis);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = etUsn.getText().toString().trim();
                String password = etPass.getText().toString().trim();

                if (!username.isEmpty() && !password.isEmpty()) {

                    login(username, password);

//                    dataSource = new DBDataSource_peternak(getApplicationContext());
//                    dataSource.open();
//
//                    boolean peternak = dataSource.getPeternak(username, password);
//                    if(peternak) {
//                        Peternak p = dataSource.getPeternakUser(username, password);
//
//                        Intent i = new Intent(getApplicationContext(), Welcome.class);
//                        String id = String.valueOf(p.getId());
//                        String namaPeternak = p.getNama();
//                        i.putExtra("user_id", id);
//                        i.putExtra("name", namaPeternak);
//
//                        startActivity(i);
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Username Atau Password Salah",
//                                Toast.LENGTH_LONG).show();
//                        return;
//                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
            }
        });

    }

    public void login(String username, String password) {
        String url = "http://10.0.2.2:8000/api/login";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("username", username);
            jsonBody.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        JSONObject data = response.getJSONObject("data");
                        String token = data.getString("token");
                        JSONObject user = data.getJSONObject("user");

                        String userID = user.getString("id");
                        String nama = user.getString("nama");

                        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("token", token);
                        editor.putString("user_id", userID);
                        editor.putString("nama", nama);
                        editor.apply();

                        Toast.makeText(getApplicationContext(), "Login Berhasil", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(LoginActivity.this, Welcome.class);
                        i.putExtra("user_id", userID);
                        i.putExtra("name", nama);
                        startActivity(i);
                        finish();

                    } catch (JSONException e){
                        e.printStackTrace();

                    }
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Login Gagal! Periksa Kembali", Toast.LENGTH_SHORT).show();

                }
        );

        Eggspert.getInstance().addToRequestQueue(jsonObjectRequest);

    }

}