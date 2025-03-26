package com.example.foodorderingprm392.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderingprm392.Domain.Order;
import com.example.foodorderingprm392.R;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private Context context;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.dateTxt.setText("Order: " + order.getOrderDate());
        holder.totalTxt.setText("Total: $" + order.getTotalAmount());

        // Kiểm tra danh sách món ăn trước khi truyền vào adapter
        if (order.getOrderedFoods() == null) {
            order.setOrderedFoods(new ArrayList<>());
        }

        Log.d("OrderAdapter", "Order ID: " + order.getOrderId() +
                ", Ordered Foods Count: " + order.getOrderedFoods().size());

        // Set LayoutManager trước khi đặt Adapter
        holder.orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        OrderedFoodAdapter orderedFoodAdapter = new OrderedFoodAdapter(order.getOrderedFoods(), context);
        holder.orderItemsRecyclerView.setAdapter(orderedFoodAdapter);

        holder.expandButton.setOnClickListener(v -> {
            holder.orderDetailsLayout.setVisibility(View.VISIBLE);
            holder.orderItemsRecyclerView.setVisibility(View.VISIBLE);
            holder.collapseButton.setVisibility(View.VISIBLE);
            holder.expandButton.setVisibility(View.GONE);  // Ẩn nút mở khi đã mở
        });

        holder.collapseButton.setOnClickListener(v -> {
            holder.orderDetailsLayout.setVisibility(View.GONE);
            holder.orderItemsRecyclerView.setVisibility(View.GONE);
            holder.collapseButton.setVisibility(View.GONE);
            holder.expandButton.setVisibility(View.VISIBLE);  // Hiện lại nút mở khi đóng
        });

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView totalTxt, dateTxt;
        ImageView expandButton, collapseButton;
        LinearLayout orderDetailsLayout;
        RecyclerView orderItemsRecyclerView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTxt = itemView.findViewById(R.id.dateTxt);
            totalTxt = itemView.findViewById(R.id.totalTxt);
            expandButton = itemView.findViewById(R.id.expandButton);
            collapseButton = itemView.findViewById(R.id.collapseButton);
            orderDetailsLayout = itemView.findViewById(R.id.orderDetailsLayout);
            orderItemsRecyclerView = itemView.findViewById(R.id.orderItemsRecyclerView);
        }
    }
}