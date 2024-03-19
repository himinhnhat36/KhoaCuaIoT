package com.example.firebase.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.firebase.Main.MainActivity;
import com.example.firebase.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainLog extends AppCompatActivity {
    SearchView searchView;
    ListView listView;
    Button btBack,btDelete;
    private ArrayList<LogSK> arrayList1;
    private DataBaseLog dataBaseLog = new DataBaseLog(this);;
    LogAdapter logAdapter;
    DatabaseReference mReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_log);
        intView();
        mReference = FirebaseDatabase.getInstance().getReference();
//        LogSK a = new LogSK("30/06/2002","12:03:04","Mở1 cửa");
//        mReference.child("LogSK").push().setValue(a);
//        add();
        arrayList1= (ArrayList<LogSK>) dataBaseLog.getAll();
//        Collections.sort(Collections.unmodifiableList(arrayList1));
        logAdapter = new LogAdapter(MainLog.this, R.layout.layout_log, arrayList1);
        listView.setAdapter(logAdapter);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainLog.this, MainActivity.class);
                startActivity(intent);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }

        });
        mReference.child("LogSK").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Lấy giá trị từ Firebase
                String date = snapshot.child("myDate").getValue(String.class);
                String time = snapshot.child("myTime").getValue(String.class);
                String description = snapshot.child("myTrangThai").getValue(String.class);

                // Kiểm tra và đặt giá trị null nếu giá trị từ Firebase không tồn tại
                date = (snapshot.child("myDate").exists()) ? date : "Lỗi gửi dữ liệu";
                time = (snapshot.child("myTime").exists()) ? time : "Lỗi gửi dữ liệu";
                description = (snapshot.child("myTrangThai").exists()) ? description : "Lỗi gửi dữ liệu";

                // Kiểm tra xem dữ liệu đã tồn tại trong cơ sở dữ liệu SQLite chưa
                if (date != null || time != null || description != null) {
                    if (!dataBaseLog.logExists(date, time, description)) {
                        // Thêm dữ liệu vào cơ sở dữ liệu SQLite và adapter
                        LogSK newLog = new LogSK(date, time, description);
                        dataBaseLog.addLog(newLog);
                        arrayList1.add(newLog);
                        logAdapter.notifyDataSetChanged();
                    }
                } else {
                    // Có thể thông báo hoặc xử lý trường hợp khi không có giá trị nào trong ID
                    Log.e("MainLog", "ID không chứa giá trị myDate, myTime, hoặc myTrangThai");
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String id1 = arrayList1.get(position).getMyTime();
                AlertDialog.Builder builder =new AlertDialog.Builder(MainLog.this);
                builder.setTitle("Thông báo xóa!!");
                builder.setIcon(R.drawable.img_6);
                builder.setMessage("Bạn có chắc muốn xóa phần tử này không?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataBaseLog.deleteByIdLogSK(id1);
                        arrayList1.remove(position);
                        logAdapter.notifyDataSetChanged();
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
        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder =new AlertDialog.Builder(MainLog.this);
                builder.setTitle("Thông báo xóa!!");
                builder.setIcon(R.drawable.img_6);
                builder.setMessage("Bạn có chắc muốn xóa tất cả phần");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dataBaseLog.deleteAllData();
                        arrayList1.clear();
                        logAdapter.notifyDataSetChanged();
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
    private void add(){
        dataBaseLog.addLog(new LogSK("15/12/2002","12:23:08","Sai mat khau"));
        dataBaseLog.addLog(new LogSK("26/12/2002","22:53:48","Mo cua"));
    }

    private void intView() {
        arrayList1 = new ArrayList<>();
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listViewLog);
        btBack = findViewById(R.id.btBackLog);
        btDelete = findViewById(R.id.btDeleTeLog);

    }
    private void filter(String newText) {
        ArrayList<LogSK> filter1 = new ArrayList<>();
        for (LogSK i: logAdapter.getGetBackUp()){
            if(i.getMyDate().toLowerCase().contains(newText.toLowerCase())){
                filter1.add(i);
            }
        }
        if(filter1.isEmpty()){
            Toast.makeText(this,"ko tim thay",Toast.LENGTH_LONG).show();
        }
        else {
            logAdapter.filterList(filter1);
        }
    }
}