package com.example.LastProject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    ImageView btnBack;
    Button btnWrite;
    TextView textView;
    EditText wrtext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        getSupportActionBar().setTitle("Food Allergies App");
        btnBack = findViewById(R.id.btnBack);
        textView = findViewById(R.id.textViewps);
        btnWrite = findViewById(R.id.btnWrite);
        wrtext = findViewById(R.id.WRtext);
        //setListener();
        //readText();
        showData();
    }

    private void showData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Personal_Upload");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                StringBuilder data = new StringBuilder();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DataClass dataClass = snapshot.getValue(DataClass.class);
                    if (dataClass != null) {
                        data.append(dataClass.getAllergies()).append("\n");
                    }
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
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();
            }
        });
    }


    private void uploadData() {

        String text = wrtext.getText().toString();

        DataClass dataClass = new DataClass(text);

        FirebaseDatabase.getInstance().getReference("Personal_Upload").child("Food_Allergies")
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Personal_info.this, "Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Personal_info.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}


    /*private void readText() {
        String read = "";
        try {
            InputStream inputStream = openFileInput("Personal_Info.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String reciving_text = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((reciving_text = bufferedReader.readLine()) != null){
                stringBuilder.append("\n").append(reciving_text);
            }
            inputStream.close();
            read = stringBuilder.toString();
            textView.setText(read);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setListener() {
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeText(wrtext.getText().toString());
            }
        });

    private void writeText(String text) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("Personal_Info.txt",MODE_PRIVATE));
            outputStreamWriter.write(text);
            outputStreamWriter.close();

            wrtext.setText(null);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Toast.makeText(this,"Data is save",Toast.LENGTH_SHORT).show();
        readText();
    }

}*/