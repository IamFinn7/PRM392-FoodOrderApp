package com.example.foodorderingprm392.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodorderingprm392.R;
import com.example.foodorderingprm392.databinding.ActivityProfileBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class ProfileActivity extends BaseActivity {
    ActivityProfileBinding binding;

    private ImageView profileImageView, editAvatarIcon, editProfileNameIcon, editProfilePasswordIcon;
    private TextView profileName, profileEmail, profilePassword;
    private EditText editProfileName, editProfilePassword, editProfileConfirmPassword, editProfileOldPassword;
    private Button cancelProfileBtn, saveProfileBtn;
    private DatabaseReference userRef;
    private String userId;
    private Uri selectedImageUri;

    // Activity Result Launcher for selecting image from gallery
    private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    Glide.with(ProfileActivity.this)
                            .load(selectedImageUri)
                            .transform(new RoundedCorners(50)) // Bo góc ảnh
                            .into(profileImageView);  // Cập nhật ảnh vào ImageView
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Ánh xạ view
        profileImageView = findViewById(R.id.profileImageView);
        editAvatarIcon = findViewById(R.id.editAvatarIcon);
        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        profilePassword = findViewById(R.id.profilePassword);
        editProfileName = findViewById(R.id.editProfileName);
        editProfilePassword = findViewById(R.id.editProfilePassword);
        editProfileConfirmPassword = findViewById(R.id.editProfileConfirmPassword);
        editProfileOldPassword = findViewById(R.id.editProfileOldPassword);  // Thêm trường mật khẩu cũ
        editProfileNameIcon = findViewById(R.id.editProfileNameIcon);
        editProfilePasswordIcon = findViewById(R.id.editProfilePasswordIcon);
        saveProfileBtn = findViewById(R.id.saveProfileBtn);
        cancelProfileBtn = findViewById(R.id.cancelProfileBtn);

        // Lấy userId từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", "");
        if (TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "Lỗi: Không tìm thấy User ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Firebase
        userRef = database.getInstance().getReference("Users").child(userId);

        loadUserData();

        // Chỉnh sửa Avatar
        editAvatarIcon.setOnClickListener(v -> openGallery());

        // Chỉnh sửa tên
        editProfileNameIcon.setOnClickListener(v -> {
            profileName.setVisibility(View.GONE);
            editProfileName.setVisibility(View.VISIBLE);

            saveProfileBtn.setVisibility(View.VISIBLE);
            cancelProfileBtn.setVisibility(View.VISIBLE);
            editProfilePasswordIcon.setVisibility(View.GONE);
        });

        // Chỉnh sửa mật khẩu
        editProfilePasswordIcon.setOnClickListener(v -> {
            profilePassword.setVisibility(View.GONE);
            editProfileOldPassword.setVisibility(View.VISIBLE);
            editProfilePassword.setVisibility(View.VISIBLE);
            editProfileConfirmPassword.setVisibility(View.VISIBLE);

            saveProfileBtn.setVisibility(View.VISIBLE);
            cancelProfileBtn.setVisibility(View.VISIBLE);
            editProfileNameIcon.setVisibility(View.GONE);
        });

        // Lưu thay đổi
        saveProfileBtn.setOnClickListener(v -> saveProfileChanges());

        cancelProfileBtn.setOnClickListener(v -> cancelProfileChanges());

        setVariable();
    }

    private void setVariable() {
        binding.bottomMenu1.setItemSelected(R.id.profile, true);
        binding.bottomMenu1.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if(i == R.id.cart) {
                    startActivity(new Intent(ProfileActivity.this, CartActivity.class));
                }
                if(i == R.id.home) {
                    startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                }
                if (i == R.id.logout) {
                    showLogoutDialog();
                }
                if(i == R.id.orders) {
                    startActivity(new Intent(ProfileActivity.this, OrderActivity.class));
                }
            }
        });
    }

    private void loadUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String password = snapshot.child("password").getValue(String.class);
                    String avatarUrl = snapshot.child("imagePath").getValue(String.class);

                    // Cập nhật UI với dữ liệu Firebase
                    if (name != null) {
                        profileName.setText(name);
                        editProfileName.setText(name);
                    }

                    if (email != null) {
                        profileEmail.setText(email);
                    }

                    profilePassword.setText("********");  // Mật khẩu chỉ hiển thị dạng ẩn

                    if (avatarUrl != null) {
                        Glide.with(ProfileActivity.this)
                                .load(avatarUrl)
                                .transform(new RoundedCorners(50))  // Bo góc ảnh
                                .into(profileImageView);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Dữ liệu không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveProfileChanges() {
        String newName = editProfileName.getText().toString().trim();
        String newPassword = editProfilePassword.getText().toString().trim();
        String confirmPassword = editProfileConfirmPassword.getText().toString().trim();
        String oldPassword = editProfileOldPassword.getText().toString().trim();

        // Chỉnh sửa tên
        if (editProfileName.getVisibility() == View.VISIBLE) {
            // Cập nhật tên người dùng
            if (!TextUtils.isEmpty(newName)) {
                userRef.child("name").setValue(newName);
                profileName.setText(newName);
            }

            // Sau khi lưu tên, ẩn các EditText và quay lại TextView
            profileName.setVisibility(View.VISIBLE);
            editProfileName.setVisibility(View.GONE);
            saveProfileBtn.setVisibility(View.GONE);
            editProfilePasswordIcon.setVisibility(View.VISIBLE);
            cancelProfileBtn.setVisibility(View.GONE);

            Toast.makeText(ProfileActivity.this, "Name updated!", Toast.LENGTH_SHORT).show();
        }

        // Chỉnh sửa mật khẩu
        if (editProfilePassword.getVisibility() == View.VISIBLE) {
            // Kiểm tra mật khẩu cũ
            if (!TextUtils.isEmpty(oldPassword)) {
                userRef.child("password").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String storedPassword = task.getResult().getValue(String.class);
                        if (!storedPassword.equals(oldPassword)) {
                            Toast.makeText(ProfileActivity.this, "Mật khẩu cũ không đúng!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Cập nhật mật khẩu nếu khớp
                        if (!TextUtils.isEmpty(newPassword) && newPassword.equals(confirmPassword)) {
                            userRef.child("password").setValue(newPassword);
                            Toast.makeText(ProfileActivity.this, "Password updated!", Toast.LENGTH_SHORT).show();
                        } else if (!TextUtils.isEmpty(newPassword)) {
                            Toast.makeText(ProfileActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Sau khi lưu mật khẩu, ẩn các EditText và quay lại TextView
                        profilePassword.setVisibility(View.VISIBLE);
                        editProfilePassword.setVisibility(View.GONE);
                        editProfileConfirmPassword.setVisibility(View.GONE);
                        editProfileOldPassword.setVisibility(View.GONE);
                        saveProfileBtn.setVisibility(View.GONE);
                        cancelProfileBtn.setVisibility(View.GONE);
                        editProfileNameIcon.setVisibility(View.VISIBLE); // Show the name edit icon

                    } else {
                        Toast.makeText(ProfileActivity.this, "Lỗi kiểm tra mật khẩu cũ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        // Cập nhật avatar (nếu có)
        if (selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReferenceFromUrl("gs://pizzawebsite-6d7fa.appspot.com/foodorderapp/avatar")
                    .child(userId + ".jpg");

            storageRef.putFile(selectedImageUri).addOnSuccessListener(taskSnapshot -> {
                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Lưu đường dẫn ảnh vào Firebase
                    userRef.child("imagePath").setValue(uri.toString());
                    Glide.with(ProfileActivity.this)
                            .load(uri)
                            .transform(new RoundedCorners(50))
                            .into(profileImageView); // Cập nhật avatar
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void cancelProfileChanges() {
        // Reset the fields to the original data
        loadUserData();

        // Hide the "Cancel" button and "Save Changes" button
        saveProfileBtn.setVisibility(View.GONE);
        cancelProfileBtn.setVisibility(View.GONE);

        // Show the TextViews and hide the EditText fields
        profileName.setVisibility(View.VISIBLE);
        editProfileName.setVisibility(View.GONE);
        profilePassword.setVisibility(View.VISIBLE);
        editProfilePassword.setVisibility(View.GONE);
        editProfileConfirmPassword.setVisibility(View.GONE);
        editProfileOldPassword.setVisibility(View.GONE);

        // Make the EditIcon buttons visible again
        editProfileNameIcon.setVisibility(View.VISIBLE);
        editProfilePasswordIcon.setVisibility(View.VISIBLE);

        Toast.makeText(ProfileActivity.this, "Changes canceled", Toast.LENGTH_SHORT).show();
    }

    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(ProfileActivity.this)
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Clear the session data
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear(); // Clear all session data
                    editor.apply();

                    // Redirect to Login activity
                    Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish(); // Close the MainActivity to prevent going back to it
                })
                .setNegativeButton("No", null)
                .show();
    }
}
