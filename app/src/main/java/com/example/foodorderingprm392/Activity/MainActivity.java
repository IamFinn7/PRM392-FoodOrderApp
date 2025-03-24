package com.example.foodorderingprm392.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.foodorderingprm392.Adapter.CategoryAdapter;
import com.example.foodorderingprm392.Adapter.SliderAdapter;
import com.example.foodorderingprm392.Domain.Category;
import com.example.foodorderingprm392.Domain.SliderItems;
import com.example.foodorderingprm392.R;
import com.example.foodorderingprm392.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadUserInfo();
        initCategory();
        initBanner();
        setVariable();
    }

    private void initBanner() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Banners");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> items = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        SliderItems item = data.getValue(SliderItems.class);
                        if (item != null) {
                            items.add(item);
                        }
                    }
                    banners(items);
                }
                binding.progressBarBanner.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void banners(ArrayList<SliderItems> items) {
        binding.viewpager2.setAdapter(new SliderAdapter(items, binding.viewpager2));
        binding.viewpager2.setClipChildren(false);
        binding.viewpager2.setClipToPadding(false);
        binding.viewpager2.setOffscreenPageLimit(3);
        binding.viewpager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewpager2.setPageTransformer(compositePageTransformer);
    }

    private void setVariable() {
        binding.bottomMenu.setItemSelected(R.id.home, true);
        binding.bottomMenu.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if(i == R.id.cart) {
                    startActivity(new Intent(MainActivity.this, CartActivity.class));
                }
                if(i == R.id.profile) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                }
            }
        });
    }

    private void initCategory() {
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }

                    if (!list.isEmpty()) {
                        binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 3));
                        binding.categoryView.setAdapter(new CategoryAdapter(list));
                    }
                }
                binding.progressBarCategory.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadUserInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String profileImageUrl = snapshot.child("imagePath").getValue(String.class);

                        binding.textView2.setText(name != null ? name : "No Name");

                        if (profileImageUrl != null) {
                            Glide.with(MainActivity.this)
                                    .load(profileImageUrl)
                                    .transform(new RoundedCorners(50)) // Bo góc ảnh
                                    .into(binding.imageView5);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}
