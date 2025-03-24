package com.example.foodorderingprm392.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.foodorderingprm392.Domain.User;
import com.example.foodorderingprm392.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends BaseActivity {  // ⚡ Kế thừa từ BaseActivity

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView btnRegister;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Ánh xạ view
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        databaseReference = database.getReference("Users"); // ⚡ Lấy dữ liệu từ Firebase

        // Xử lý đăng nhập
        btnLogin.setOnClickListener(v -> {
            String emailInput = edtEmail.getText().toString().trim();
            String passwordInput = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(emailInput) || TextUtils.isEmpty(passwordInput)) {
                Toast.makeText(LoginActivity.this, "Please enter complete information!", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(emailInput, passwordInput);  // ⚡ Gọi hàm với tham số chính xác
            }
        });

        // Chuyển sang màn hình đăng ký
        btnRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void loginUser(String emailInput, String passwordInput) {
        DatabaseReference usersRef = database.getInstance().getReference("Users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isUserFound = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    String password = userSnapshot.child("password").getValue(String.class);

                    if (email != null && email.equals(emailInput) && password != null && password.equals(passwordInput)) {
                        isUserFound = true;

                        // Lấy userId từ Firebase key
                        String userId = userSnapshot.getKey();
                        String name = userSnapshot.child("name").getValue(String.class);

                        Log.d("UserLogin", "Login Success - UserID: " + userId);

                        // Lưu vào SharedPreferences
                        saveUserSession(userId, name);

                        // Chuyển đến màn hình chính
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        break;
                    }
                }

                if (!isUserFound) {
                    Toast.makeText(LoginActivity.this, "Incorrect email or password!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Connection error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Sửa lại lưu thông tin user
    private void saveUserSession(String userId, String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userId", userId);
        editor.putString("name", name);
        editor.apply();
        Log.d("UserSession", "Saved User ID: " + userId);
    }
}