package com.example.eggspert_mobile;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DataAdapterLaporanGudang extends RecyclerView.Adapter<DataAdapterLaporanGudang.ViewHolder> {
    private ArrayList IDList, namaList, jenisList, jmlList;
    private Context context;
    private Intent i;

    public DataAdapterLaporanGudang(Context context, ArrayList IDList, ArrayList namaList, ArrayList jenisList, ArrayList jmlList) {
        this.context = context;
        this.IDList = IDList;
        this.namaList = namaList;
        this.jenisList = jenisList;
        this.jmlList = jmlList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_gudang_uneditable, parent, false);
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
            i = new Intent(context, KelolaLaporanGudang.class);
            i.putExtra("id", id);
            context.startActivity(i);

        });
    }

    @Override
    public int getItemCount() {return IDList.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtid, txtNama, txtJenis, txtJumlah;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtid = itemView.findViewById(R.id.txt_id);
            txtNama = itemView.findViewById(R.id.txt_nama_gudang);
            txtJenis = itemView.findViewById(R.id.txt_jenis_gudang);
            txtJumlah = itemView.findViewById(R.id.txt_jumlah);

            cardView = itemView.findViewById(R.id.card_view);

        }

    }

}
