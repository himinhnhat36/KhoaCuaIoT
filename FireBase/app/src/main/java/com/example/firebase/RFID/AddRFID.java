package com.example.firebase.RFID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddRFID extends AppCompatActivity {
    EditText edID,edUser,edEmail;
    Button btAddRFID,btBackRFID;

    DatabaseReference reference;
    DatabaseRFID databaseRFID = new DatabaseRFID(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rfid);
        intView();
        reference.child("/RFID/ID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists() && snapshot.getValue() != null) {
                    edID.setText(snapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btBackRFID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddRFID.this, MainRFID.class);
                startActivity(intent);
            }
        });
        btAddRFID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String rfidId = edID.getText().toString().trim();
                final String userName = edUser.getText().toString().trim();
                final String userEmail = edEmail.getText().toString().trim();

                // Check if RFID ID is "nocard"
                if ("nocard".equals(rfidId.toLowerCase())) {
                    // ID is "nocard", show a message or handle accordingly
                    Toast.makeText(AddRFID.this, "Hãy đưa thẻ ID vào", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if RFID ID already exists in SQLite
                    if (databaseRFID.isRFIDIdExists(rfidId)) {
                        // ID already exists in SQLite, show a message or handle accordingly
                        Toast.makeText(AddRFID.this, "ID đã tồn tại trong SQLite!", Toast.LENGTH_SHORT).show();
                    } else {
                        // Check if RFID ID already exists in Firebase
                        reference.child("Data_RFID").orderByChild("MyIDRFID").equalTo(rfidId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // ID already exists in Firebase, show a message or handle accordingly
                                            Toast.makeText(AddRFID.this, "ID đã tồn tại trong Firebase!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // ID does not exist in either SQLite or Firebase, add the RFID data to both
                                            databaseRFID.addRFID(new RFID(rfidId, userName, userEmail));
//                                            reference.child("Data_RFID").child(rfidId).setValue(new RFID(rfidId, userName, userEmail));
                                            Intent intent = new Intent(AddRFID.this, MainRFID.class);
                                            startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // Handle the error if needed
                                        Toast.makeText(AddRFID.this, "Lỗi kiểm tra sự tồn tại của ID trong Firebase", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            }
        });

    }

    private void intView() {
        edID = findViewById(R.id.edIDRFID);
        edUser = findViewById(R.id.edNguoiDung);
        edEmail = findViewById(R.id.edEmailRFID);
        btAddRFID = findViewById(R.id.btAddRFID);
        btBackRFID = findViewById(R.id.btBackRFID);
        reference = FirebaseDatabase.getInstance().getReference();
    }
}