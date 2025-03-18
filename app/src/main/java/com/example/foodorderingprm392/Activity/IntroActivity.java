package com.example.foodorderingprm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderingprm392.R;
import com.example.foodorderingprm392.databinding.ActivityIntroBinding;

public class IntroActivity extends AppCompatActivity {

    ActivityIntroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = com.example.foodorderingprm392.databinding.ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.goBtn.setOnClickListener(v -> startActivity(new Intent(IntroActivity.this, MainActivity.class)));
    }
}