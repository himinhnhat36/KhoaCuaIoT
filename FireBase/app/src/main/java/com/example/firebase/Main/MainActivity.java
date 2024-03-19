package com.example.firebase.Main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.firebase.Log.MainLog;
import com.example.firebase.R;
import com.example.firebase.RFID.MainRFID;
import com.example.firebase.TemporaryPassword.MainActivity_TemporaryPassword;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    Switch aSwitch;
    TextView tvOnOffMain,tvNhietDo,tvDoAm;
    FirebaseDatabase database;
    DatabaseReference reference,dht;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intView();
        reference.child("Reset").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Khi dữ liệu trên Firebase thay đổi
                String firebaseValue = dataSnapshot.getValue(String.class);

                // Cập nhật giao diện người dùng dựa trên giá trị từ Firebase
                if ("On".equals(firebaseValue)) {
                    aSwitch.setChecked(true);
                    tvOnOffMain.setText("On");
                } else {
                    aSwitch.setChecked(false);
                    tvOnOffMain.setText("Off");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu từ Firebase
            }
        });
        dht.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Kiểm tra xem có dữ liệu trong DHT không
                if (dataSnapshot.exists()) {
                    // Lấy dữ liệu humidity và temperature từ dataSnapshot
                    String humidity = dataSnapshot.child("humidity").getValue(String.class);
                    String temperature = dataSnapshot.child("temperature").getValue(String.class);

                    // Hiển thị dữ liệu lên TextViews
                    tvNhietDo.setText(temperature);
                    tvDoAm.setText(humidity);
                } else {
                    // Nếu không có dữ liệu trong DHT, có thể xử lý tại đây
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
                Log.e("Firebase", "Error fetching DHT data", databaseError.toException());
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    tvOnOffMain.setText("On");
                    reference.child("Reset").setValue("On"); // Đã cập nhật dòng này
                } else {
                    tvOnOffMain.setText("Off");
                    reference.child("Reset").setValue("Off");
                }
            }
        });

    }

    private void intView() {
        aSwitch = findViewById(R.id.swonOffMain);
        tvOnOffMain = findViewById(R.id.tvOnOffMain);
        tvNhietDo = findViewById(R.id.tvnhietdo);
        tvDoAm = findViewById(R.id.tvDoAm);
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("/ESP32/OUTPUT/Digital");
        dht = database.getReference("DHT");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_item,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menuLog){
            Intent intent = new Intent(MainActivity.this, MainLog.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.menuRFID) {
            Intent intent = new Intent(MainActivity.this, MainRFID.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.menuTemporaryPassword) {
            Intent intent = new Intent(MainActivity.this, MainActivity_TemporaryPassword.class);
            startActivity(intent);
        }
        if(item.getItemId()==R.id.menuDangXuat) {
            Intent intent = new Intent(MainActivity.this, LoginMain.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}