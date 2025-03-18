package com.example.foodorderingprm392.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderingprm392.Adapter.CartAdapter;
import com.example.foodorderingprm392.Helper.ChangeNumberItemsListener;
import com.example.foodorderingprm392.Helper.ManagementCart;
import com.example.foodorderingprm392.R;
import com.example.foodorderingprm392.databinding.ActivityCartBinding;

public class CartActivity extends AppCompatActivity {
    ActivityCartBinding binding;
    private ManagementCart managementCart;

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
        if(managementCart.getListCart().isEmpty())
        {
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        } else
        {
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }

        binding.cartView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.cartView.setAdapter(new CartAdapter(managementCart.getListCart(), managementCart, () -> calculateCart()));
    }

    private void calculateCart() {
        double percentTax = 0;
        double delivery = 10;
        double tax = Math.round(managementCart.getTotalFee() * percentTax*100.0) / 100;
        double total = Math.round((managementCart.getTotalFee() + tax + delivery)*100)/100;
        double itemTotal = Math.round(managementCart.getTotalFee()*100)/100;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(view -> startActivity(new Intent(CartActivity.this, MainActivity.class)));
    }
}