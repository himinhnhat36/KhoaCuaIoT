package com.example.firebase.TemporaryPassword;

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

import com.example.firebase.Log.LogAdapter;
import com.example.firebase.Log.LogSK;
import com.example.firebase.Log.MainLog;
import com.example.firebase.Main.MainActivity;
import com.example.firebase.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity_TemporaryPassword extends AppCompatActivity {
    private Button btAddTempo,btUpdateTempo,btBackTempo;
    private ListView listTempo;
    private ArrayList<Tempo> arrayList1;
    TempoAdapter adapter;
    DatabaseReference mReference;
    DatabaseTemporary databaseTemporary = new DatabaseTemporary(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_temporary_password);
        intView();
        mReference = FirebaseDatabase.getInstance().getReference();
        arrayList1= (ArrayList<Tempo>) databaseTemporary.getAll();
//        Collections.sort(Collections.unmodifiableList(arrayList1));
        adapter = new TempoAdapter(MainActivity_TemporaryPassword.this, R.layout.layout_temporary_passwords, arrayList1);
        listTempo.setAdapter(adapter);
        databaseTemporary.deleteItemsNotInFirebase();
        btBackTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_TemporaryPassword.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btAddTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity_TemporaryPassword.this,Add_Temporary_Passwords.class);
                startActivity(intent);
            }
        });
        btUpdateTempo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadListView();
            }
        });
        listTempo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String id1 = arrayList1.get(position).getUserTempo();
                AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity_TemporaryPassword.this);
                builder.setTitle("Thông báo xóa!!");
                builder.setIcon(R.drawable.img_6);
                builder.setMessage("Bạn có chắc muốn xóa phần tử này không?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseTemporary.deleteByIDTempo(id1);
                        arrayList1.remove(position);
                        adapter.notifyDataSetChanged();
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

    private void intView() {
        btAddTempo = findViewById(R.id.AddTempo);
        btUpdateTempo = findViewById(R.id.UpdateTempo);
        btBackTempo = findViewById(R.id.BackTempo);
        listTempo = findViewById(R.id.listTempoaryPasswords);
    }
    private void reloadListView() {
        arrayList1.clear();
        arrayList1.addAll(databaseTemporary.getAll());
        adapter.notifyDataSetChanged();
    }

}