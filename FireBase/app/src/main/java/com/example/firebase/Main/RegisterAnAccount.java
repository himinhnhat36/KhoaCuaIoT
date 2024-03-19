package com.example.firebase.Main;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterAnAccount extends AppCompatActivity {
    EditText edUser,edPass;
    Button btShow,btRegister,btQuayLai;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_an_account);
        intView();
        btShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra trạng thái hiện tại của EditText
                if (edPass.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Nếu đang hiển thị, ẩn ký tự
                    edPass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btShow.setText("Show");
                } else {
                    // Nếu đang ẩn, hiển thị ký tự
                    edPass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btShow.setText("Hide");
                }

                // Di chuyển con trỏ về cuối chuỗi
                edPass.setSelection(edPass.getText().length());
            }
        });
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email1,password1;
                email1 = edUser.getText().toString();
                password1 = edPass.getText().toString();
                if(TextUtils.isEmpty(email1)){
                    Toast.makeText(RegisterAnAccount.this,"Vui lòng nhập email!! ", LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password1)){
                    Toast.makeText(RegisterAnAccount.this,"Vui lòng nhập password!! ", LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.createUserWithEmailAndPassword(email1,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Đăng ký thành công", LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterAnAccount.this, LoginMain.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Đăng ký không thành công", LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        btQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterAnAccount.this,LoginMain.class);
                startActivity(intent);
            }
        });
    }

    private void intView() {
        firebaseAuth = FirebaseAuth.getInstance();
        edUser = findViewById(R.id.editUser1);
        edPass = findViewById(R.id.editPassword1);
        btShow = findViewById(R.id.btShow1);
        btRegister = findViewById(R.id.register1);
        btQuayLai= findViewById(R.id.quayLai);
    }
}