package com.example.foodorderingprm392.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodorderingprm392.Domain.Foods;
import com.example.foodorderingprm392.Helper.ManagementCart;
import com.example.foodorderingprm392.databinding.ActivityDetailBinding;

public class DetailActivity extends AppCompatActivity {
    ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;
    private ManagementCart managementCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        managementCart = new ManagementCart(this);

        binding.backBtn.setOnClickListener(v -> finish());

        Glide.with(this)
                .load(object.getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(20))
                .into(binding.pic);

        binding.priceTxt.setText("$" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getStar() + " Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText("$ " + (num * object.getPrice()));

        binding.plusBtn.setOnClickListener(v -> {
            num = num + 1;
            binding.numTxt.setText(num + "");
            binding.totalTxt.setText("$ " + (num * object.getPrice()));
        });

        binding.minusBtn.setOnClickListener(v -> {
            if(num>1) {
                num = num - 1;
                binding.numTxt.setText(num + "");
                binding.totalTxt.setText("$ " + (num * object.getPrice()));
            }
        });

        binding.addBtn.setOnClickListener(v -> {
          object.setNumberInCart(num);
          managementCart.insertFood(object);
        });
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}