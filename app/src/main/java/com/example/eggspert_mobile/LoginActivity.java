package com.example.eggspert_mobile;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {

    EditText etUsn, etPass;
    TextView btnRegis; Button btnLogin;

    DBDataSource_peternak dataSource;

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

        dataSource = new DBDataSource_peternak(getApplicationContext());
        dataSource.open();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String usn = etUsn.getText().toString();
                String pass = etPass.getText().toString();

                if (usn.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Username atau Password tidak boleh kosong", Toast.LENGTH_LONG).show();
                    return;
                }

                boolean peternak = dataSource.getPeternak(usn, pass);
                if(peternak) {
                    Peternak p = dataSource.getPeternakUser(usn, pass);

                    Intent i = new Intent(getApplicationContext(), Welcome.class);
                    String namaPeternak = p.getNama();
                    i.putExtra("name", namaPeternak);

                    startActivity(i);

                } else {
                    Toast.makeText(getApplicationContext(), "Username Atau Password Salah",
                            Toast.LENGTH_LONG).show();
                    return;
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
}