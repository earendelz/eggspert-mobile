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

public class VaksinAdapter extends RecyclerView.Adapter<VaksinAdapter.ViewHolder> {

    private ArrayList IDList, jenisList, tanggalList, IDKandangList;
    private Context context;
    private Intent i;

    public VaksinAdapter(Context context, ArrayList IDList, ArrayList jenisList, ArrayList tanggalList, ArrayList IDKandangList) {
        this.context = context;
        this.IDList = IDList;
        this.jenisList = jenisList;
        this.tanggalList = tanggalList;
        this.IDKandangList = IDKandangList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_vaksin, parent, false);
        return new ViewHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String id = IDList.get(position).toString();
        final String jenis_vaksin = jenisList.get(position).toString();
        final String tanggal_vaksin = tanggalList.get(position).toString();
        final String id_kandang = IDKandangList.get(position).toString();

        holder.txtid.setText(id);
        holder.txtJenis.setText(jenis_vaksin);
        holder.txtTanggal.setText(tanggal_vaksin);

        holder.option.setOnClickListener(view -> {

            BottomSheetDialog bsdOption = new BottomSheetDialog(context);
            bsdOption.setContentView(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.bottom_sheet_dialog, null));

            bsdOption.findViewById(R.id.edit_opt).setOnClickListener(view1 -> {
                i = new Intent(context, EditVaksin.class);
                i.putExtra("id", id);
                i.putExtra("id_product", id_kandang);
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
        private TextView txtid, txtJenis, txtTanggal;
        private CardView cardView;
        private ImageButton option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtid = itemView.findViewById(R.id.txt_id);
            txtJenis = itemView.findViewById(R.id.txt_jenis_vaksin);
            txtTanggal = itemView.findViewById(R.id.txt_tanggal);

            option = itemView.findViewById(R.id.option);

            cardView = itemView.findViewById(R.id.card_view);

        }

    }

    public void deleteData(String idVaksin, String idKandang) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("EggspertPrefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("SharedPreferences", "Token: " + token);

        if (token == null) {
            Toast.makeText(context, "Token Tidak Ditemukan! Silahkan Login Kembali", Toast.LENGTH_SHORT).show();
            return;

        }

        String url = "http://10.0.2.2:8000/api/vaksinasiku/" + idVaksin;

        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE, url,
                response -> {
                    Toast.makeText(context, "Data Berhasil Dihapus!", Toast.LENGTH_SHORT).show();
                    ((KelolaVaksin)context).showData(idKandang);

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
