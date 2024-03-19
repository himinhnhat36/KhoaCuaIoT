package com.example.firebase.Main;

import static android.widget.Toast.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.firebase.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginMain extends AppCompatActivity {

    EditText edUser,edPassword;
    Button btShow,btSign,btRegister;
    CheckBox ckRemember;
    FirebaseAuth firebaseAuth;

    private static final String PREF_EMAIL_KEY = "email";
    private static final String PREF_PASSWORD_KEY = "password";
    private static final String PREF_REMEMBER_KEY = "remember";
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        intView();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean remember = preferences.getBoolean(PREF_REMEMBER_KEY, false);
        if (remember) {
            String savedEmail = preferences.getString(PREF_EMAIL_KEY, "");
            String savedPassword = preferences.getString(PREF_PASSWORD_KEY, "");

            edUser.setText(savedEmail);
            edPassword.setText(savedPassword);
            ckRemember.setChecked(true);
        }

        btSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email1,password1;
                email1 = edUser.getText().toString();
                password1 = edPassword.getText().toString();
                if(TextUtils.isEmpty(email1)){
                    Toast.makeText(LoginMain.this,"Vui lòng nhập email!! ", LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password1)){
                    Toast.makeText(LoginMain.this,"Vui lòng nhập password!! ", LENGTH_SHORT).show();
                    return;
                }
                firebaseAuth.signInWithEmailAndPassword(email1,password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getApplicationContext(),"Đăng nhập thành công", LENGTH_SHORT).show();
                            saveLoginInfo(email1, password1);
                            Intent intent = new Intent(LoginMain.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Đăng nhập không thành công", LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginMain.this, RegisterAnAccount.class);
                startActivity(intent);
            }
        });
        btShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra trạng thái hiện tại của EditText
                if (edPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    // Nếu đang hiển thị, ẩn ký tự
                    edPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btShow.setText("Show");
                } else {
                    // Nếu đang ẩn, hiển thị ký tự
                    edPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btShow.setText("Hide");
                }

                // Di chuyển con trỏ về cuối chuỗi
                edPassword.setSelection(edPassword.getText().length());
            }
        });
        ckRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Lưu trạng thái của ckRemember vào SharedPreferences
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(PREF_REMEMBER_KEY, isChecked);
                editor.apply();
            }
        });
    }

    private void saveLoginInfo(String email, String password) {
        // Lưu thông tin đăng nhập vào SharedPreferences
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_EMAIL_KEY, email);
        editor.putString(PREF_PASSWORD_KEY, password);
        editor.apply();
    }
    private void intView() {
        firebaseAuth = FirebaseAuth.getInstance();
        edUser = findViewById(R.id.editUser);
        edPassword = findViewById(R.id.editPassword);
        btShow = findViewById(R.id.btShow);
        btSign = findViewById(R.id.btSignIn);
        btRegister = findViewById(R.id.register);
        ckRemember = findViewById(R.id.ckRemember);
    }
}