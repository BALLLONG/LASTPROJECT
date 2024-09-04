package com.example.LastProject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ImageView clear, getImage, copy, personalinfo;
    private EditText recgText;
    private Uri imageUri;
    private TextRecognizer textRecognizer;
    private static final String FILE_NAME = "Personal_Info.txt";
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://allergies-eb2fa-default-rtdb.asia-southeast1.firebasedatabase.app");
    DatabaseReference reference = database.getReference("Food_Allergies");
    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textViewps3);
        showData();
        initializeViews();
        configureFirebase();
        setOnClickListeners();
    }

    public void showData() {
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
                Toast.makeText(MainActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeViews() {
        clear = findViewById(R.id.clear);
        getImage = findViewById(R.id.getImage);
        copy = findViewById(R.id.copy);
        recgText = findViewById(R.id.recgText);
        personalinfo = findViewById(R.id.Personal);
        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    private void configureFirebase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://allergies-eb2fa-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference myRef = database.getReference("Personal_Upload");
    }

    private void setOnClickListeners() {
        personalinfo.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Personal_info.class);
            startActivity(intent);
        });

        getImage.setOnClickListener(v -> {
            ImagePicker.with(MainActivity.this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        });

        copy.setOnClickListener(v -> {
            String text = recgText.getText().toString();
            if (text.isEmpty()) {
                Toast.makeText(MainActivity.this, "There is no text to copy", Toast.LENGTH_SHORT).show();
            } else {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Data", text);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(MainActivity.this, "Text copied to Clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        clear.setOnClickListener(v -> {
            if (recgText.getText().toString().isEmpty()) {
                Toast.makeText(MainActivity.this, "There is no text to clear", Toast.LENGTH_SHORT).show();
            } else {
                recgText.setText("");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Image selected", Toast.LENGTH_SHORT).show();
            recognizeText();
        } else {
            Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
        }
    }

    /*private void checkTextInFirebase(String text) {
        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean foundAllergy = false;
                DataSnapshot snapshot = task.getResult();
                if (snapshot != null && snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String allergy = dataSnapshot.child("allergies").getValue(String.class);
                        if (allergy != null && text.contains(allergy)) {
                            showAlert("Allergy Alert", "Potential allergy found: " + allergy);
                            foundAllergy = true;
                            break; // Exit loop when allergy is found
                        }
                    }
                }
                if (!foundAllergy) {
                    showAlert("No Allergy Found", "No allergens detected in the text.");
                }
            } else {
                String errorMsg = "Failed to check data: " + task.getException().getMessage();
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.TransparentDialogTheme);
        View customLayout = getLayoutInflater().inflate(R.layout.alert, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button successDoneButton = customLayout.findViewById(R.id.successDone);
        if (successDoneButton != null) {
            successDoneButton.setOnClickListener(v -> dialog.dismiss());
        }
    }*/
    private void recognizeText() {
        if (imageUri != null) {
            try {
                InputImage inputImage = InputImage.fromFilePath(this, imageUri);
                textRecognizer.process(inputImage)
                        .addOnSuccessListener(text -> {
                            String recognizedText = text.getText();
                            recgText.setText(recognizedText);

                            // เรียกใช้ฟังก์ชันตรวจสอบข้อความกับ Firebase
                            checkTextInFirebase(recognizedText);
                        })
                        .addOnFailureListener(e -> {
                            String errorMsg = "Failed to recognize text: " + e.getMessage();
                            Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        });
            } catch (IOException e) {
                String errorMsg = "Error loading image: " + e.getMessage();
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkTextInFirebase(String text) {
        DatabaseReference ref = FirebaseDatabase.getInstance("https://allergies-eb2fa-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("Personal_Upload/Food_Allergies/allergies");

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    // ดึงค่าจาก Firebase และแปลงเป็นตัวพิมพ์เล็ก
                    String allergiesText = task.getResult().getValue(String.class);
                    String textLowerCase = text.toLowerCase(); // ข้อความที่รับจากการรู้จำแปลงเป็นตัวพิมพ์เล็ก
                    if (allergiesText != null && text.contains(allergiesText)) {
                        showAlert1("","");
                        //showAlert("Allergy Alert", "Potential allergy found: " + allergiesText);
                    } else {
                        showAlert("No Allergy Found", "No allergens detected in the text.");
                    }
                } else {
                    showAlert("No Data Found", "No data found at the specified path.");
                }
            } else {
                String errorMsg = "Error checking data in Firebase: " + task.getException().getMessage();
                Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlert(String title, String message) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void showAlert1(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.TransparentDialogTheme);
        View customLayout = getLayoutInflater().inflate(R.layout.alert, null);
        builder.setView(customLayout);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button successDoneButton = customLayout.findViewById(R.id.successDone);
        if (successDoneButton != null) {
            successDoneButton.setOnClickListener(v -> dialog.dismiss());
        }
    }
}

