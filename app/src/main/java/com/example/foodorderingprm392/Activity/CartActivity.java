package com.example.foodorderingprm392.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderingprm392.Adapter.CartAdapter;
import com.example.foodorderingprm392.Helper.ChangeNumberItemsListener;
import com.example.foodorderingprm392.Helper.ManagementCart;
import com.example.foodorderingprm392.Helper.TinyDB;
import com.example.foodorderingprm392.R;
import com.example.foodorderingprm392.databinding.ActivityCartBinding;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    private ManagementCart managementCart;
    private ChangeNumberItemsListener changeNumberItemsListener;

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
        double delivery = 10;  // phí vận chuyển
        double tax = Math.round(managementCart.getTotalFee() * percentTax * 100.0) / 100;
        double total = Math.round((managementCart.getTotalFee() + tax + delivery) * 100) / 100;
        double itemTotal = Math.round(managementCart.getTotalFee() * 100) / 100;

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
            if (i == R.id.logout) {
                showLogoutDialog();
            }
        });

        binding.checkOutBtn.setOnClickListener(v -> showCheckoutDialog());
    }

    private void showCheckoutDialog() {
        // Hiển thị hộp thoại xác nhận thanh toán
        new AlertDialog.Builder(CartActivity.this)
                .setMessage("Do you want to proceed with the checkout?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Tính toán tổng số tiền (bao gồm phí vận chuyển)
                    double totalAmount = Math.round((managementCart.getTotalFee() + 10) * 100) / 100;  // Including delivery

                    // Hiển thị thông báo tổng số tiền
                    Toast.makeText(CartActivity.this, "Your total is: $" + totalAmount, Toast.LENGTH_LONG).show();

                    // Xóa tất cả các món trong giỏ hàng
                    for (int i = managementCart.getListCart().size() - 1; i >= 0; i--) {
                        managementCart.removeItem(managementCart.getListCart(), i, () -> {
                            // Cập nhật lại giao diện và các sự kiện
                            binding.cartView.getAdapter().notifyDataSetChanged();  // Thông báo cho adapter rằng dữ liệu đã thay đổi
                            if (changeNumberItemsListener != null) {
                                changeNumberItemsListener.change();  // Gọi để thay đổi số lượng
                            }
                        });
                    }

                    // Tính toán lại giỏ hàng và tổng số tiền
                    calculateCart();

                    // Quay về MainActivity
                    startActivity(new Intent(CartActivity.this, MainActivity.class));
                    finish();  // Đảm bảo không quay lại CartActivity khi bấm nút back
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
}
