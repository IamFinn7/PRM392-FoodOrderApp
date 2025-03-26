package com.example.foodorderingprm392.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderingprm392.Adapter.CartAdapter;
import com.example.foodorderingprm392.Domain.Foods;
import com.example.foodorderingprm392.Domain.Order;
import com.example.foodorderingprm392.Domain.OrderedFood;
import com.example.foodorderingprm392.Helper.ChangeNumberItemsListener;
import com.example.foodorderingprm392.Helper.ManagementCart;
import com.example.foodorderingprm392.R;
import com.example.foodorderingprm392.databinding.ActivityCartBinding;
import com.google.firebase.database.DatabaseReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    private ManagementCart managementCart;
    private ChangeNumberItemsListener changeNumberItemsListener;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managementCart = new ManagementCart(this);

        setVariable();
        calculateCart();
        initCartList();
    }

    private void initCartList() {
        if (managementCart.getListCart().isEmpty()) {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(managementCart.getListCart(), managementCart, () -> calculateCart()));
    }

    private void calculateCart() {
        double percentTax = 0;
        double delivery = 10;  // Phí vận chuyển

        BigDecimal itemTotal = BigDecimal.valueOf(managementCart.getTotalFee())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal tax = itemTotal.multiply(BigDecimal.valueOf(percentTax))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = itemTotal.add(tax).add(BigDecimal.valueOf(delivery))
                .setScale(2, RoundingMode.HALF_UP);

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    private void setVariable() {
        binding.bottomMenu.setItemSelected(R.id.cart, true);
        binding.bottomMenu.setOnItemSelectedListener(i -> {
            if (i == R.id.home) {
                startActivity(new Intent(CartActivity.this, MainActivity.class));
            }
            if (i == R.id.profile) {
                startActivity(new Intent(CartActivity.this, ProfileActivity.class));
            }
            if (i == R.id.orders) {
                startActivity(new Intent(CartActivity.this, OrderActivity.class));
            }
            if (i == R.id.logout) {
                showLogoutDialog();
            }
        });

        binding.checkOutBtn.setOnClickListener(v -> showCheckoutDialog());
    }

    private void showCheckoutDialog() {
        new AlertDialog.Builder(CartActivity.this)
                .setMessage("Do you want to proceed with the checkout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    BigDecimal totalAmount = BigDecimal.valueOf(managementCart.getTotalFee())
                            .add(BigDecimal.valueOf(10)) // Thêm phí vận chuyển
                            .setScale(2, RoundingMode.HALF_UP); // Làm tròn chính xác

                    Toast.makeText(CartActivity.this, "Your total is: $" + totalAmount, Toast.LENGTH_LONG).show();

                    saveOrderToFirebase(totalAmount);
                })
                .setNegativeButton("No", null)
                .show();
    }


    private void showLogoutDialog() {
        new android.app.AlertDialog.Builder(CartActivity.this)
                .setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Xóa dữ liệu phiên đăng nhập
                    SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();  // Xóa tất cả dữ liệu phiên
                    editor.apply();

                    // Chuyển đến LoginActivity
                    Intent loginIntent = new Intent(CartActivity.this, LoginActivity.class);
                    startActivity(loginIntent);
                    finish();  // Đảm bảo không quay lại MainActivity
                })
                .setNegativeButton("No", null)
                .show();
    }
    private void saveOrderToFirebase(BigDecimal totalAmount) {
        databaseReference = database.getInstance().getReference("Orders");

        databaseReference.child("lastOrderId").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Long lastOrderId = task.getResult().getValue(Long.class);
                long newOrderId = (lastOrderId != null) ? lastOrderId + 1 : 1;

                SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
                String userId = sharedPreferences.getString("userId", "");

                if (userId.isEmpty()) {
                    Toast.makeText(CartActivity.this, "User ID is missing!", Toast.LENGTH_LONG).show();
                    return;
                }

                List<OrderedFood> orderedFoods = new ArrayList<>();
                for (Foods food : managementCart.getListCart()) {
                    orderedFoods.add(new OrderedFood(food.getId(), food.getTitle(), food.getNumberInCart(), food.getPrice(), food.getImagePath()));
                }

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
                String orderDate = sdf.format(new Date());

                // Chuyển BigDecimal thành String để lưu vào Firebase
                Order newOrder = new Order(newOrderId, userId, orderedFoods, totalAmount.doubleValue(), orderDate);
                databaseReference.child(String.valueOf(newOrderId)).setValue(newOrder)
                        .addOnSuccessListener(aVoid -> {
                            databaseReference.child("lastOrderId").setValue(newOrderId);
                            Toast.makeText(CartActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();

                            // Xóa giỏ hàng
                            for (int i = managementCart.getListCart().size() - 1; i >= 0; i--) {
                                managementCart.removeItem(managementCart.getListCart(), i, () -> {
                                    binding.cartView.getAdapter().notifyDataSetChanged();
                                    if (changeNumberItemsListener != null) {
                                        changeNumberItemsListener.change();
                                    }
                                });
                            }

                            calculateCart();

                            startActivity(new Intent(CartActivity.this, MainActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(CartActivity.this, "Failed to place order.", Toast.LENGTH_LONG).show()
                        );
            }
        });
    }
}
