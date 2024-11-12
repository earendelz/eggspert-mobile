package com.example.eggspert_mobile;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KelolaKandang extends AppCompatActivity {

    DBHelper_kandang config;
    SQLiteDatabase db;
    Cursor cursor;

    TextView farmName, nick;

    RecyclerView rcvData;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layout;

    ImageButton addData, backButton;
    Intent i;

    ArrayList IDList, nameList, typeList, capacityList, ownerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_kelola_kandang);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        config = new DBHelper_kandang(this);

        addData = findViewById(R.id.add_data);
        nick = findViewById(R.id.nickname);
        farmName = findViewById(R.id.farm_name);
        backButton = findViewById(R.id.back_button);

        i = getIntent();
        String nama_peternak = i.getStringExtra("name");
        String id = i.getStringExtra("user_id");
        int userID = Integer.parseInt(id);

        nick.setText(nama_peternak);
        farmName.setText(nama_peternak + "'s Farm");

        addData.setOnClickListener(view -> {
            i = new Intent(this, CreateKandang.class);
            i.putExtra("name", nama_peternak);
            i.putExtra("user_id", id);
            startActivity(i);
        });

        backButton.setOnClickListener(view -> {
            finish();
        });

        showData(userID);

    }

    @Override
    protected void onResume() {
        i = getIntent();
        String id = i.getStringExtra("user_id");
        int userID = Integer.parseInt(id);

        showData(userID);
        super.onResume();
    }

    public void showData(int userID) {
        IDList = new ArrayList<>();
        nameList = new ArrayList<>();
        typeList = new ArrayList<>();
        capacityList = new ArrayList<>();
        ownerList = new ArrayList<>();

        String nama_peternak = i.getStringExtra("name");

        layout = new LinearLayoutManager(this);

        adapter = new DataAdapter(this, IDList, nameList, typeList, capacityList, ownerList, nama_peternak);

        rcvData = findViewById(R.id.rcv_data);

        rcvData.setLayoutManager(layout);
        rcvData.setHasFixedSize(true);
        rcvData.setAdapter(adapter);

        db = config.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM kandang WHERE id_peternak = '" + userID + "'", null);
        cursor.moveToFirst();
        Log.d("KelolaKandang", "Jumlah data yang diambil: " + cursor.getCount());

        for(int count = 0; count < cursor.getCount(); count++ ) {
            cursor.moveToPosition(count);
            //Isi Array
            IDList.add(cursor.getString(0));
            nameList.add(cursor.getString(1));
            typeList.add(cursor.getString(2));
            capacityList.add(cursor.getString(3));
            ownerList.add(cursor.getString(5));
        }

        adapter.notifyDataSetChanged();

    }

}