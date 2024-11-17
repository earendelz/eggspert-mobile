package com.example.eggspert_mobile;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataAdapterVaksin extends RecyclerView.Adapter<DataAdapterVaksin.ViewHolder> {

    private ArrayList IDList, namaList, jenisList, jmlList;
    private Context context;
    private Intent i;

    public DataAdapterVaksin(Context context, ArrayList IDList, ArrayList namaList, ArrayList jenisList, ArrayList jmlList) {
        this.context = context;
        this.IDList = IDList;
        this.namaList = namaList;
        this.jenisList = jenisList;
        this.jmlList = jmlList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_uneditable, parent, false);
        return new ViewHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String id = IDList.get(position).toString();
        final String nama = namaList.get(position).toString();
        final String jenis = jenisList.get(position).toString();
        final String jumlah_ayam = jmlList.get(position).toString();

        holder.txtid.setText(id);
        holder.txtNama.setText(nama);
        holder.txtJenis.setText(jenis);
        holder.txtJumlah.setText(jumlah_ayam);

        holder.cardView.setOnClickListener(view -> {
            i = new Intent(context, KelolaVaksin.class);
            i.putExtra("id", id);
            context.startActivity(i);

        });

    }

    @Override
    public int getItemCount() { return IDList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtid, txtNama, txtJenis, txtJumlah;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtid = itemView.findViewById(R.id.txt_id);
            txtNama = itemView.findViewById(R.id.txt_nama_kandang);
            txtJenis = itemView.findViewById(R.id.txt_jenis_kandang);
            txtJumlah = itemView.findViewById(R.id.txt_jumlah);

            cardView = itemView.findViewById(R.id.card_view);

        }

    }

}
