package com.example.firebase.RFID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UpdateRFID extends AppCompatActivity {
    EditText edID, edUser, edEmail;
    Button btUpdateRFID, btBackRFID;
    DatabaseRFID databaseRFID;
    DatabaseReference databaseReference;
    String id01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_rfid);
        initView();

        // Lắng nghe sự thay đổi trên /RFID/ID
        databaseReference.child("/RFID/ID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getValue() != null) {
                    // Lấy giá trị từ Firebase
                    id01 = snapshot.getValue().toString().trim();
                    edID.setText(id01);

                    // Log giá trị để kiểm tra
                    Log.d("Debug", "id01: " + id01);

                    // Thực hiện so sánh và hiển thị dữ liệu
                    fetchDataAndCompare();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra
                Log.e("Error", "onCancelled: " + error.getMessage());
            }
        });

        btBackRFID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateRFID.this, MainRFID.class);
                startActivity(intent);
            }
        });
        btUpdateRFID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the updated user and email values
                String updatedUser = edUser.getText().toString().trim();
                String updatedEmail = edEmail.getText().toString().trim();

                // Update the local SQLite database
                updateLocalDatabase(updatedUser, updatedEmail);

                // Update the data in the Firebase Realtime Database
                updateFirebaseDatabase(updatedUser, updatedEmail);
                Intent intent = new Intent(UpdateRFID.this,MainRFID.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        edID = findViewById(R.id.edIDRFID1);
        edUser = findViewById(R.id.edNguoiDung1);
        edEmail = findViewById(R.id.edEmailRFID1);
        btUpdateRFID = findViewById(R.id.btUpdateRFID);
        btBackRFID = findViewById(R.id.btBackRFID1);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseRFID = new DatabaseRFID(this);
    }

    private void fetchDataAndCompare() {
        // Lấy dữ liệu từ /Data_RFID/MyIDRFID và so sánh với id01
        databaseReference.child("/Data_RFID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean matchFound = false;  // Cờ để kiểm tra xem có kết quả trùng khớp không

                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String myIdRFID = childSnapshot.child("MyIDRFID").getValue(String.class);
                    Log.d("Debug", "myIdRFID: " + myIdRFID);

                    // So sánh với id01
                    if (myIdRFID != null && myIdRFID.trim().equalsIgnoreCase(id01.trim())) {
                        String myUser = childSnapshot.child("MyUserRFID").getValue(String.class);
                        String myEmail = childSnapshot.child("MyEmailRFID").getValue(String.class);

                        // Hiển thị dữ liệu trong EditText
                        edUser.setText(myUser);
                        edEmail.setText(myEmail);

                        // Log giá trị để kiểm tra
                        Log.d("Debug", "myUser: " + myUser);
                        Log.d("Debug", "myEmail: " + myEmail);

                        Toast.makeText(UpdateRFID.this, "Dữ liệu đã được cập nhật", Toast.LENGTH_SHORT).show();

                        matchFound = true;  // Đặt cờ là true
                        break;  // Thoát khỏi vòng lặp ngay khi có kết quả trùng khớp
                    }
                }

                // Hiển thị thông báo nếu không tìm thấy trùng khớp
                if (!matchFound) {
                    Toast.makeText(UpdateRFID.this, "Không tìm thấy dữ liệu cho ID: " + id01, Toast.LENGTH_SHORT).show();
                    edUser.setText("");
                    edEmail.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Log.e("Error", "onCancelled: " + databaseError.getMessage());
                Toast.makeText(UpdateRFID.this, "Đã xảy ra lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateLocalDatabase(String updatedUser, String updatedEmail) {
        Log.d("Debug", "Updating local database...");
        // Assuming you have an RFID object representing the current data
        RFID updatedRFID = new RFID();
        updatedRFID.setMyIDRFID(id01);  // Assuming id01 is the current RFID ID
        updatedRFID.setMyUserRFID(updatedUser);
        updatedRFID.setMyEmailRFID(updatedEmail);

        // Update the record in the local SQLite database
        databaseRFID.update(updatedRFID); // Uncomment the update method in DatabaseRFID
    }

    private void updateFirebaseDatabase(String updatedUser, String updatedEmail) {
        // Update the data in the Firebase Realtime Database
        DatabaseReference dataRFIDRef = FirebaseDatabase.getInstance().getReference("/Data_RFID");
        dataRFIDRef.orderByChild("MyIDRFID").equalTo(id01).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    // Update the values in Firebase
                    childSnapshot.getRef().child("MyUserRFID").setValue(updatedUser);
                    childSnapshot.getRef().child("MyEmailRFID").setValue(updatedEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });

        Toast.makeText(UpdateRFID.this, "Dữ liệu đã được cập nhật", Toast.LENGTH_SHORT).show();
    }

}
