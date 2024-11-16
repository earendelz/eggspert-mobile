package com.example.eggspert_mobile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private ArrayList<Kandang> kandangList;
    private Context context;
    private Intent i;

    public DataAdapter(Context context, ArrayList<Kandang> kandangList) {
        this.context = context;
        this.kandangList = kandangList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View listItemView  = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view, parent, false);
        return new ViewHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Kandang kandang = (Kandang) kandangList.get(position);

        holder.txtid.setText(String.valueOf(kandang.getId()));
        holder.txtNama.setText(kandang.getNama());
        holder.txtJenis.setText(kandang.getJenis_kandang());
        holder.txtKapasitas.setText(String.valueOf(kandang.getKapasitas()));

        holder.cardView.setOnClickListener(view -> {
            i = new Intent(context, DetailKandang.class);
            String id = String.valueOf(kandang.getId());
            i.putExtra("id", id);
            context.startActivity(i);

        });

//        holder.option.setOnClickListener(view -> {
//
//            BottomSheetDialog bsdOption = new BottomSheetDialog(context);
//            bsdOption.setContentView(LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.bottom_sheet_dialog, null));
//
//            bsdOption.findViewById(R.id.edit_opt).setOnClickListener(view1 -> {
//                i = new Intent(context, EditKandang.class);
//                String id = String.valueOf(kandang.getId());
//                i.putExtra("id", id);
//                context.startActivity(i);
//                bsdOption.dismiss();
//
//            });

//            bsdOption.findViewById(R.id.delete_opt).setOnClickListener(view1 -> {
//                deleteData(id, Integer.parseInt(owner));
//                bsdOption.dismiss();
//
//            });

//            bsdOption.show();

//        });

    }

    @Override
    public int getItemCount() { return kandangList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtid, txtNama, txtJenis, txtKapasitas;
        private CardView cardView;
        private ImageButton option;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtid = itemView.findViewById(R.id.txt_id);
            txtNama = itemView.findViewById(R.id.txt_nama_kandang);
            txtJenis = itemView.findViewById(R.id.txt_jenis_kandang);
            txtKapasitas = itemView.findViewById(R.id.txt_kapasitas);

            option = itemView.findViewById(R.id.option);

            cardView = itemView.findViewById(R.id.card_view);

        }

    }

//    private void deleteData(String id, int owner) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Apakah Anda Yakin Ingin Menghapus Data?")
//                .setItems(new CharSequence[]{"Ya, Hapus", "Tidak"}, (dialog, which) -> {
//
//                    switch (which) {
//                        case 0 :
//
//                            db = config.getReadableDatabase();
//                            db.execSQL("DELETE FROM kandang WHERE id = '" + id + "'");
//                            Toast.makeText(context, "Data Berhasil Dihapus", Toast.LENGTH_SHORT).show();
//                            ((KelolaKandang)context).showData(owner);
//                            break;
//
//                    }
//
//                });
//
//        builder.show();
//
//    }


}
