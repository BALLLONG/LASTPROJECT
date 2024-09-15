package com.example.LastProject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<Data> list;
    DatabaseReference databaseReference;

    public MyAdapter(Context context, ArrayList<Data> list, DatabaseReference databaseReference) {
        this.context = context;
        this.list = list;
        this.databaseReference = databaseReference;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.editentry,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (position >= 0 && position < list.size()) { // ตรวจสอบให้แน่ใจว่าตำแหน่งที่เข้าถึงนั้นถูกต้อง
            Data data = list.get(position);
            holder.name.setText(data.getAllergies());

            holder.deleteButton.setOnClickListener(v -> {
                // ลบข้อมูลจาก Firebase โดยใช้ key
                databaseReference.child(data.getKey()).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            if (position >= 0 && position < list.size()) { // ตรวจสอบตำแหน่งอีกครั้ง
                                list.remove(position); // ลบข้อมูลออกจาก list
                                notifyItemRemoved(position); // อัปเดต adapter
                                notifyItemRangeChanged(position, list.size()); // อัปเดตตำแหน่งที่เหลือ
                            } else {
                                //Toast.makeText(context, "Item not found", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to delete data", Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            Toast.makeText(context, "Item not found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        Button deleteButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textname);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

}
