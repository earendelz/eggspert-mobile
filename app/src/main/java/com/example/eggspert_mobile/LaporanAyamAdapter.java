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

public class LaporanAyamAdapter extends RecyclerView.Adapter<LaporanAyamAdapter.ViewHolder> {

    private ArrayList IDList, jenisList, tanggalList, jumlahList, IDKandangList;
    private Context context; private Intent i;

    public LaporanAyamAdapter(Context context, ArrayList IDList, ArrayList jenisList, ArrayList tanggalList,  ArrayList jumlahList, ArrayList IDKandangList) {
        this.context = context;
        this.IDList = IDList;
        this.jenisList = jenisList;
        this.tanggalList = tanggalList;
        this.jumlahList = jumlahList;
        this.IDKandangList = IDKandangList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_laporan_ayam, parent, false);
        return new ViewHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String id = IDList.get(position).toString();
        final String jenis_laporan = jenisList.get(position).toString();
        final String tanggal_peristiwa = tanggalList.get(position).toString();
        final String jumlah_ayam = jumlahList.get(position).toString();
        final String id_kandang = IDKandangList.get(position).toString();

        holder.txtid.setText(id);
        holder.txtJenis.setText(jenis_laporan);
        holder.txtTanggal.setText(tanggal_peristiwa);
        holder.txtJumlah.setText(jumlah_ayam);

        holder.option.setOnClickListener(view -> {

            BottomSheetDialog bsdOption = new BottomSheetDialog(context);
            bsdOption.setContentView(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.bottom_sheet_dialog, null));

            bsdOption.findViewById(R.id.edit_opt).setOnClickListener(view1 -> {
                i = new Intent(context, EditLaporanAyam.class);
                i.putExtra("id", id);
                i.putExtra("id_kandang", id_kandang);
                context.startActivity(i);
                bsdOption.dismiss();

            });

            bsdOption.findViewById(R.id.delete_opt).setOnClickListener(view1 -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Apakah Anda Yakin Ingin Menghapus Data?")
                        .setItems(new CharSequence[]{"Ya, Hapus", "Tidak"}, (dialog, which) -> {

                            switch (which) {
                                case 0 :
                                    deleteData(id, id_kandang);
                                    bsdOption.dismiss();
                                    break;

                                case 1 :
                                    dialog.dismiss();
                                    break;
                            }

                        });

                builder.show();

            });

            bsdOption.show();

        });
    }

    @Override
    public int getItemCount() {return IDList.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtid, txtJenis, txtTanggal, txtJumlah;
        private CardView cardView;
        private ImageButton option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtid = itemView.findViewById(R.id.txt_id);
            txtJenis = itemView.findViewById(R.id.txt_jenis_laporan);
            txtTanggal = itemView.findViewById(R.id.tanggal_peristiwa);
            txtJumlah = itemView.findViewById(R.id.txt_jumlah);

            option = itemView.findViewById(R.id.option);

            cardView = itemView.findViewById(R.id.card_view);

        }

    }

    public void deleteData(String idLaporan, String idGudang) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(context, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;

        }

        String url = "http://10.0.2.2:8000/api/laporanayamku/" + idLaporan;

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(context, "Data Berhasil Dihapus!", Toast.LENGTH_SHORT).show();
                    ((KelolaLaporanGudang)context).showData(idGudang);

                }, error -> {
            if (error.networkResponse != null) {
                int errorCode = error.networkResponse.statusCode;
                Log.e("API Error", "Error Code: " + errorCode + " Message: " + new String(error.networkResponse.data));

                if ( errorCode == 204) {
                    Toast.makeText(context, "Data Berhasil Dihapus!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, "Gagal Menghapus Data! Silahkan Coba Lagi (1)", Toast.LENGTH_SHORT).show();

                }

            } else {
                Log.e("API Error", "Error: " + error.getMessage());
                Toast.makeText(context, "Gagal Menghapus Data! Silahkan Coba Lagi (2)", Toast.LENGTH_SHORT).show();
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

        Eggspert.getInstance().addToRequestQueue(stringRequest);

    }

}
