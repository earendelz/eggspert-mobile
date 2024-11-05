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

public class RegisterActivity extends AppCompatActivity {

    EditText etNama, etAlamat, etEmail, etUsn, etPass;
    CheckBox cbOK;
    Button daftar;
    TextView login;
    ImageButton hidden;

    DBDataSource_peternak dataSource;

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

        dataSource = new DBDataSource_peternak(getApplicationContext());
        dataSource.open();

        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = null;
                String alamat = null;
                String email = null;
                String usn = null;
                String pass = null;

                Peternak peternak = null;
                if( !etNama.getText().toString().isEmpty() &&
                        !etAlamat.getText().toString().isEmpty() &&
                        !etEmail.getText().toString().isEmpty() &&
                        !etUsn.getText().toString().isEmpty() &&
                        !etPass.getText().toString().isEmpty()) {

                    boolean isChecked = cbOK.isChecked();

                    if (isChecked) {
                        nama = etNama.getText().toString();
                        alamat = etAlamat.getText().toString();
                        email = etEmail.getText().toString();
                        usn = etUsn.getText().toString();
                        pass = etPass.getText().toString();

                        peternak = dataSource.register(nama, alamat, email, usn, pass);

                        Toast.makeText(getApplicationContext(), "User Berhasil Dibuat", Toast.LENGTH_LONG).show();

                        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(i);

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
}