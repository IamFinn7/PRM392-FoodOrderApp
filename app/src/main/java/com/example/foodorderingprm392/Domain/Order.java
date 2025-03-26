package com.example.foodorderingprm392.Domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Order {
    private Long orderId;
    private String userId;
    private List<OrderedFood> orderedFoods;
    private double totalAmount;
    private String orderDate;  // Thêm trường ngày đặt hàng

    public Order(Long orderId, String userId, List<OrderedFood> orderedFoods, double totalAmount, String orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderedFoods = orderedFoods;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;  // Gán giá trị ngày đặt hàng
    }

    public Order() {
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<OrderedFood> getOrderedFoods() {
        return orderedFoods;
    }

    public void setOrderedFoods(List<OrderedFood> orderedFoods) {
        this.orderedFoods = orderedFoods;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
