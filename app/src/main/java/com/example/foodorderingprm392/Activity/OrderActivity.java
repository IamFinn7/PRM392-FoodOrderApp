package com.example.foodorderingprm392.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingprm392.Adapter.OrderAdapter;
import com.example.foodorderingprm392.Domain.Order;
import com.example.foodorderingprm392.R;
import com.example.foodorderingprm392.databinding.ActivityMainBinding;
import com.example.foodorderingprm392.databinding.ActivityOrderBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends BaseActivity {
    ActivityOrderBinding binding;
    private RecyclerView orderRecyclerView;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private DatabaseReference databaseReference;
    private TextView emptyTxt;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOrderBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        orderRecyclerView = findViewById(R.id.orderRecyclerView);
        emptyTxt = findViewById(R.id.emptyTxt);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, this);
        orderRecyclerView.setAdapter(orderAdapter);

        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        userId = sharedPreferences.getString("userId", null);

        if (userId != null) {
            databaseReference = database.getInstance().getReference("Orders");
            loadOrders();
        } else {
            emptyTxt.setText("Please log in to view your orders.");
            emptyTxt.setVisibility(View.VISIBLE);
            orderRecyclerView.setVisibility(View.GONE);
        }

        setVariable();
    }

    private void loadOrders() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    try {
                        // Lấy orderId trước khi chuyển đổi toàn bộ object
                        Long orderId = dataSnapshot.child("orderId").getValue(Long.class);

                        // Lấy dữ liệu order từ Firebase
                        Order order = dataSnapshot.getValue(Order.class);

                        if (order != null) {
                            order.setOrderId(orderId); // Gán orderId theo kiểu Long

                            // Đảm bảo danh sách orderedFoods không bị null
                            if (order.getOrderedFoods() == null) {
                                order.setOrderedFoods(new ArrayList<>());
                            }

                            // Kiểm tra xem order có thuộc về user hiện tại không
                            if (userId.equals(order.getUserId())) {
                                orderList.add(order);
                            }

                            Log.d("OrderActivity", "Order ID: " + order.getOrderId() +
                                    ", Foods Count: " + order.getOrderedFoods().size());
                        }
                    } catch (Exception e) {
                        Log.e("OrderActivity", "Error parsing order: " + e.getMessage());
                    }
                }

                // Cập nhật giao diện hiển thị
                if (orderList.isEmpty()) {
                    emptyTxt.setVisibility(View.VISIBLE);
                    emptyTxt.setText("You have no orders.");
                    orderRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyTxt.setVisibility(View.GONE);
                    orderRecyclerView.setVisibility(View.VISIBLE);
                    orderAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OrderActivity", "Error fetching orders: " + error.getMessage());
            }
        });
    }

    private void setVariable() {
        binding.bottomMenu.setItemSelected(R.id.orders, true);
        binding.bottomMenu.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                if (i == R.id.cart) {
                    startActivity(new Intent(OrderActivity.this, CartActivity.class));
                }
                if (i == R.id.home) {
                    startActivity(new Intent(OrderActivity.this, MainActivity.class));
                }
                if (i == R.id.profile) {
                    startActivity(new Intent(OrderActivity.this, ProfileActivity.class));
                }
                if (i == R.id.logout) {
                    showLogoutDialog();
                }
            }
        });
    }

    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(OrderActivity.this)
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Clear the session data
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear(); // Clear all session data
                    editor.apply();

                    // Redirect to Login activity
                    Intent loginIntent = new Intent(OrderActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish(); // Close the MainActivity to prevent going back to it
                })
                .setNegativeButton("No", null)
                .show();
    }
}
