package com.example.LastProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Personal_info extends AppCompatActivity {
    private Spinner spinnerAllergies;
    private ImageView btnBack;
    private Button btnWrite,btnEdit;
    private TextView textView;
    private int nextIndex = 0; // Starting index for new entries

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        getSupportActionBar().setTitle("Food Allergies App");

        spinnerAllergies = findViewById(R.id.spinnerAllergies);
        btnWrite = findViewById(R.id.btnWrite);
        btnBack  = findViewById(R.id.btnBack);
        textView = findViewById(R.id.textViewps);
        btnEdit = findViewById(R.id.btnEdit);

        // Setting up Spinner with allergy options
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.allergy_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAllergies.setAdapter(adapter);

        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });

        showData();
    }

    private void uploadData() {
        String selectedAllergy = spinnerAllergies.getSelectedItem().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Personal_Upload/Food_Allergies");

        // Create a new key (index) for the data
        String newKey = reference.push().getKey();

        // Increment the index for the next entry

        if(newKey != null) {
            reference.child(newKey).setValue(selectedAllergy)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Personal_info.this, "Saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Personal_info.this, "Failed to save: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Personal_info.this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(Personal_info.this, "Failed to generate a unique key", Toast.LENGTH_SHORT).show();
        }
    }

    private void showData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Personal_Upload/Food_Allergies");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder data = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String value = snapshot.getValue(String.class);
                    data.append(value).append("\n");
                }
                if (data.length() == 0) {
                    data.append("No data available");
                }
                textView.setText(data.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Personal_info.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Personal_info.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Personal_info.this, Edit.class));
            }
        });

    }
}
