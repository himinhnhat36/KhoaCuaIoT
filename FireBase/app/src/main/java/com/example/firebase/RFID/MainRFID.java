package com.example.firebase.RFID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.firebase.Main.MainActivity;
import com.example.firebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainRFID extends AppCompatActivity {
    ListView listView;
    Button btADD,btBack,btUpdate;

    DatabaseRFID databaseRFID = new DatabaseRFID(this);
    private ArrayList<RFID> arrayList1;
    RFIDAdapter rfidAdapter;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference rfidRef = firebaseDatabase.getReference("Data_RFID");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_rfid);
        intView();
//        add1();
        add();
        arrayList1= (ArrayList<RFID>) databaseRFID.getAll();
//        Collections.sort(Collections.unmodifiableList(arrayList1));
        rfidAdapter = new RFIDAdapter(MainRFID.this, R.layout.layout_rfid, arrayList1);
        listView.setAdapter(rfidAdapter);
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainRFID.this, UpdateRFID.class);
                startActivity(intent);
            }
        });
        btADD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainRFID.this, AddRFID.class);
                startActivity(intent);
            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainRFID.this, MainActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String id1 = arrayList1.get(position).getMyIDRFID();
                AlertDialog.Builder builder =new AlertDialog.Builder(MainRFID.this);
                builder.setTitle("Thông báo xóa!!");
                builder.setIcon(R.drawable.img_6);
                builder.setMessage("Bạn có chắc muốn xóa phần tử này không?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseRFID.deleteById(id1);
                        arrayList1.remove(position);
                        rfidAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog =builder.create();
                dialog.show();
            }
        });
    }

    private void add1(){
        databaseRFID.addRFID(new RFID("a2 4b 5f 8h","Nguyễn Minh Nhật","nhatsieunhan@gmail.com"));
    }
    private void add() {
        ArrayList<RFID> localData = (ArrayList<RFID>) databaseRFID.getAll();

        for (RFID rfid : localData) {
            // Giả sử MyIDRFID, MyUserRFID, MyEmailRFID là các phương thức getter trong lớp RFID
            String id = rfid.getMyIDRFID();
            String user = rfid.getMyUserRFID();
            String email = rfid.getMyEmailRFID();

            // Đẩy dữ liệu lên Firebase
            pushToFirebase(id, user, email);
        }
    }
    private void pushToFirebase(String id, String user, String email) {
        // Kiểm tra xem ID đã tồn tại trên Firebase chưa
        rfidRef.orderByChild("MyIDRFID").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    // Nếu ID không tồn tại, thì mới đẩy dữ liệu lên Firebase
                    String key = rfidRef.push().getKey();

                    Map<String, Object> rfidData = new HashMap<>();
                    rfidData.put("MyIDRFID", id);
                    rfidData.put("MyUserRFID", user);
                    rfidData.put("MyEmailRFID", email);

                    rfidRef.child(key).setValue(rfidData);
                } else {
                    // ID đã tồn tại, bạn có thể xử lý tùy ý (ví dụ: thông báo lỗi)
                    Toast.makeText(MainRFID.this, "ID đã tồn tại trên Firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Toast.makeText(MainRFID.this, "Đã xảy ra lỗi: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void intView() {
        listView = findViewById(R.id.ListViewRFID);
        btADD = findViewById(R.id.ADD);
        btUpdate = findViewById(R.id.Update1);
        btBack = findViewById(R.id.BackRfid);
        arrayList1 = new ArrayList<>();
    }
}