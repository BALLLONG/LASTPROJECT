    package com.example.LastProject;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.LinearLayoutManager;
    import androidx.recyclerview.widget.RecyclerView;

    import android.content.Intent;
    import android.os.Bundle;
    import android.view.View;
    import android.widget.ImageView;
    import android.widget.Toast;

    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.util.ArrayList;

    public class Edit extends AppCompatActivity {
        RecyclerView recyclerView;
        ArrayList<Data> list;
        DatabaseReference databaseReference;
        MyAdapter adapter;
        ImageView btnBack;

        @Override
        public void onBackPressed() {
            super.onBackPressed();
            startActivity(new Intent(Edit.this, MainActivity.class));
            finish();
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit);
            btnBack = findViewById(R.id.btnBack);
            recyclerView = findViewById(R.id.recycleview);
            databaseReference = FirebaseDatabase.getInstance().getReference("Personal_Upload/Food_Allergies");
            list = new ArrayList<>();
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new MyAdapter(this, list, databaseReference);
            recyclerView.setAdapter(adapter);


            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Edit.this, Personal_info.class); // Correct Intent
                    startActivity(intent);
                    finish(); // Optionally finish this activity if you don't want to return to it
                }
            });
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String allergy = dataSnapshot.getValue(String.class); // อ่านข้อมูลเป็น String
                        String key = dataSnapshot.getKey(); // อ่านคีย์
                        Data data = new Data(allergy); // สร้าง DataClass ใหม่
                        data.setKey(key); // ตั้งค่าคีย์
                        list.add(data); // เพิ่มข้อมูลลงใน list
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Edit.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        private void deleteData(String key) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Personal_Upload/Food_Allergies");
            reference.child(key).removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Edit.this, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Edit.this, "Failed to delete data", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Edit.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }
