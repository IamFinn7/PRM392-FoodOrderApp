package com.example.foodorderingprm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodorderingprm392.Domain.User;
import com.example.foodorderingprm392.R;
import com.google.firebase.database.DatabaseReference;

public class RegisterActivity extends BaseActivity {  // ⚡ Kế thừa từ BaseActivity

    private EditText edtName, edtEmail, edtPassword;
    private Button btnRegister;
    private TextView btnLogin;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Ánh xạ view
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        databaseReference = database.getReference("Users"); // ⚡ Sử dụng Firebase từ BaseActivity

        // Sự kiện khi nhấn nút Đăng Ký
        btnRegister.setOnClickListener(v -> registerUser());

        // Chuyển sang màn hình đăng nhập
        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String imgUrl = "https://firebasestorage.googleapis.com/v0/b/pizzawebsite-6d7fa.appspot.com/o/foodorderapp%2Favatar%2Favatar-mac-dinh-12-1724862391.jpg?alt=media&token=661d281b-ebe7-44c5-a24c-7cb73f0415ad";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter complete information!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = databaseReference.push().getKey();
        User user = new User(userId, name, email, password, imgUrl);

        // Lưu thông tin người dùng vào Firebase
        databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Registration failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}