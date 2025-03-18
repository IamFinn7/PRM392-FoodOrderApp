package com.example.foodorderingprm392.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingprm392.Adapter.FoodListAdapter;
import com.example.foodorderingprm392.Domain.Foods;
import com.example.foodorderingprm392.R;
import com.example.foodorderingprm392.databinding.ActivityListFoodBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListFoodActivity extends BaseActivity {
    ActivityListFoodBinding binding;
    private int categoryId;
    private String categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        initList();
    }

    private void initList() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);

        ArrayList<Foods> list = new ArrayList<>();
        Query query = myRef.orderByChild("CategoryId").equalTo(categoryId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DEBUG", "DataSnapshot: " + snapshot.toString());

                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Foods.class));
                    }
                }

                binding.foodListView.setLayoutManager(new LinearLayoutManager(ListFoodActivity.this, LinearLayoutManager.VERTICAL, false));
                binding.foodListView.setAdapter(new FoodListAdapter(list));

                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ERROR", "Database Error: " + error.getMessage());
            }
        });
    }

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");

        binding.titleTxt.setText(categoryName);
        binding.backBtn.setOnClickListener(v -> finish());
    }
}