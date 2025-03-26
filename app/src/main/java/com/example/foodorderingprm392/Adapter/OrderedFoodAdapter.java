package com.example.foodorderingprm392.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderingprm392.Domain.OrderedFood;
import com.example.foodorderingprm392.R;

import java.util.List;

public class OrderedFoodAdapter extends RecyclerView.Adapter<OrderedFoodAdapter.FoodViewHolder> {
    private List<OrderedFood> foodList;
    private Context context;

    public OrderedFoodAdapter(List<OrderedFood> foodList, Context context) {
        this.foodList = foodList;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_detail, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        OrderedFood food = foodList.get(position);
        holder.itemDetailNameTxt.setText(food.getName());
        holder.itemDetailQuantityTxt.setText("x" + food.getQuantity());
        holder.itemDetailTotalTxt.setText("$" + food.getPrice() * food.getQuantity());

        // Load ảnh món ăn
        Glide.with(context).load(food.getImageUrl()).into(holder.itemDetailImg);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView itemDetailNameTxt, itemDetailQuantityTxt, itemDetailTotalTxt;
        ImageView itemDetailImg;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            itemDetailNameTxt = itemView.findViewById(R.id.itemDetailNameTxt);
            itemDetailQuantityTxt = itemView.findViewById(R.id.itemDetailQuantityTxt);
            itemDetailTotalTxt = itemView.findViewById(R.id.itemDetailTotalTxt);
            itemDetailImg = itemView.findViewById(R.id.itemDetailImg);
        }
    }
}