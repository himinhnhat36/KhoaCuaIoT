package com.example.firebase.TemporaryPassword;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.firebase.R;
import com.example.firebase.RFID.DatabaseRFID;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Add_Temporary_Passwords extends AppCompatActivity {
    private EditText edPassMo,edPassDong,edUser;
    private Button btAdd,btBack,btSetDate,btSetTime;
    TextView tvSetDate;
    TextView tvSetTime;

    DatabaseReference reference;
    DatabaseTemporary databaseTemporary = new DatabaseTemporary(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_temporary_passwords);
        intView();
        reference = FirebaseDatabase.getInstance().getReference().child("TemporaryPasswords");
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Add_Temporary_Passwords.this,MainActivity_TemporaryPassword.class);
                startActivity(intent);
            }
        });
        btSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();

                int year01 = calendar.get(Calendar.YEAR);
                int month01 = calendar.get(Calendar.MONTH);
                int day01 = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(Add_Temporary_Passwords.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Đặt ngày đã chọn cho TextView
                        tvSetDate.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year));
                    }
                }, year01, month01, day01);
                datePickerDialog.show();
            }
        });
        btSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(Add_Temporary_Passwords.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        tvSetTime.setText(String.format("%d:%d",hourOfDay,minute));
                    }
                },hour,minute,true);
                timePickerDialog.show();
            }
        });


        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edUser.getText().toString().trim();
                String passMo = edPassMo.getText().toString().trim();
                String passDong = edPassDong.getText().toString().trim();
                String date = tvSetDate.getText().toString().trim();
                String time = tvSetTime.getText().toString().trim();
                if (!user.isEmpty() && !passMo.isEmpty() && !passDong.isEmpty() && !date.isEmpty() && !time.isEmpty()) {
                    if (passMo.length() == 4 && passDong.length() == 4) {
                        Tempo tempo = new Tempo(user, Integer.parseInt(passMo), Integer.parseInt(passDong), date, time);
                        databaseTemporary.addTempo(tempo);
                        reference.push().setValue(tempo);
                        Intent intent = new Intent(Add_Temporary_Passwords.this,MainActivity_TemporaryPassword.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(Add_Temporary_Passwords.this, "Vui lòng nhập mật khẩu 4 ký tự.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Add_Temporary_Passwords.this, "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void intView() {
        edUser = findViewById(R.id.edUserTempo);
        edPassMo = findViewById(R.id.edPassTempo);
        edPassDong = findViewById(R.id.edPassTempo1);
        btAdd = findViewById(R.id.btAddTempo);
        btBack = findViewById(R.id.btBackTempo);
        btSetDate = findViewById(R.id.btSetDate);
        btSetTime = findViewById(R.id.btSetTime);
        tvSetDate = findViewById(R.id.tvSetDate);
        tvSetTime = findViewById(R.id.tvSetTime);
        reference = FirebaseDatabase.getInstance().getReference();
    }

}