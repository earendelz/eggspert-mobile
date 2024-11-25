package com.example.eggspert_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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

public class EditPassword extends AppCompatActivity {

    EditText oldPass, newPass, confPass;
    ImageButton back, save;

    BottomNavigationView navBar;
    Intent i; String user_id;
    ImageButton hiddenOld, hiddenNew, hiddenConf;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        user_id = sharedPreferences.getString("user_id",null);

        navBar = findViewById(R.id.bottom_navigation);
        navBar.setSelectedItemId(R.id.navigation_profile);
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

        oldPass = findViewById(R.id.old_pass);
        newPass = findViewById(R.id.new_pass);
        confPass = findViewById(R.id.conf_pass);

        hiddenOld = findViewById(R.id.hidden_old);
        hiddenNew = findViewById(R.id.hidden_new);
        hiddenConf = findViewById(R.id.hidden_conf);

        hiddenOld.setOnClickListener(view -> {
            if (isPasswordVisible) {
                oldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                oldPass.setSelection(oldPass.length());
                hiddenOld.setImageResource(R.drawable.hide_password);
            } else {
                oldPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                oldPass.setSelection(oldPass.length());
                hiddenOld.setImageResource(R.drawable.unhidden);

            }
            isPasswordVisible = !isPasswordVisible;

        });

        hiddenNew.setOnClickListener(view -> {
            if (isPasswordVisible) {
                newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                newPass.setSelection(newPass.length());
                hiddenNew.setImageResource(R.drawable.hide_password);

            } else {
                newPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                newPass.setSelection(newPass.length());
                hiddenNew.setImageResource(R.drawable.unhidden);

            }
            isPasswordVisible = !isPasswordVisible;

        });

        hiddenConf.setOnClickListener(view -> {
            if (isPasswordVisible) {
                confPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                confPass.setSelection(confPass.length());
                hiddenConf.setImageResource(R.drawable.hide_password);

            } else {
                confPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                confPass.setSelection(confPass.length());
                hiddenConf.setImageResource(R.drawable.unhidden);

            }

            isPasswordVisible = !isPasswordVisible;

        });

        save = findViewById(R.id.button_simpan);
        save.setOnClickListener(view -> {
            String old_pass = oldPass.getText().toString();
            String new_pass = newPass.getText().toString();
            String conf_pass = confPass.getText().toString();
            savePass(old_pass, new_pass, conf_pass);
            finish();
        });

        back = findViewById(R.id.button_kembali);
        back.setOnClickListener(view -> finish());

    }

    public void savePass(String oldPassword, String newPassword, String confPassword) {
        SharedPreferences sharedPreferences = getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(this, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;
        }

        if (oldPassword.isEmpty() || newPassword.isEmpty() || confPassword.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confPassword)) {
            Toast.makeText(this, "Password baru dan konfirmasi password tidak cocok.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = "http://10.0.2.2:8000/api/change-password";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("old_password", oldPassword);
            jsonObject.put("new_password", newPassword);
            jsonObject.put("confirm_password", confPassword);
        } catch (JSONException e) {
            e.printStackTrace();

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT, url, jsonObject,
                response -> {
                    try {
                        String message = response.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }

                }, error -> {
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        String errorMessage = new String(error.networkResponse.data);
                        try {
                            JSONObject errorJson = new JSONObject(errorMessage);
                            Toast.makeText(this, errorJson.getString("message"), Toast.LENGTH_SHORT).show();

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                    } else {
                        Toast.makeText(this, "Gagal menghubungi server.", Toast.LENGTH_SHORT).show();

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