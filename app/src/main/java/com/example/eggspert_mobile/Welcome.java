package com.example.eggspert_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Welcome extends AppCompatActivity {

    TextView header, caption;
    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        header = findViewById(R.id.header);
        caption = findViewById(R.id.caption);
        start = findViewById(R.id.btn_start);

        Intent i = getIntent();
        String user_id = i.getStringExtra("user_id");
        String namaPeternak = i.getStringExtra("name");
        header.setText("Selamat Datang, " + namaPeternak + "!");
        caption.setText(" Selamat datang, " + namaPeternak + "! Perjalanan manajemen peternakan Anda dimulai di sini. Mari optimalkan peternakan unggas Anda bersama-sama dan capai tujuan dengan mudah.");

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HomePage.class);
                i.putExtra("user_id", user_id);
                i.putExtra("name", namaPeternak);
                startActivity(i);
            }
        });
    }

}