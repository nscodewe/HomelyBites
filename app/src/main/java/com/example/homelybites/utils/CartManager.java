package com.example.homelybites.utils;

import com.example.homelybites.models.Order;
import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private List<Order.OrderItem> cartItems;
    private String currentKitchenId;
    private String currentKitchenName;

    private CartManager() {
        cartItems = new ArrayList<>();
    }

    public static synchronized CartManager getInstance() {
        if (instance == null) {
            instance = new CartManager();
        }
        return instance;
    }

    public void addItem(String kitchenId, String kitchenName, Order.OrderItem item) {
        // Simple logic: If adding from a different kitchen, clear previous cart (standard food app behavior)
        if (currentKitchenId != null && !currentKitchenId.equals(kitchenId)) {
            cartItems.clear();
        }
        
        currentKitchenId = kitchenId;
        currentKitchenName = kitchenName;

        // Check if item already exists, then just increase quantity
        boolean exists = false;
        for (Order.OrderItem existingItem : cartItems) {
            if (existingItem.getDishId().equals(item.getDishId())) {
                existingItem.setQuantity(existingItem.getQuantity() + 1);
                exists = true;
                break;
            }
        }

        if (!exists) {
            cartItems.add(item);
        }
    }

    public List<Order.OrderItem> getCartItems() {
        return cartItems;
    }

    public double getTotalAmount() {
        double total = 0;
        for (Order.OrderItem item : cartItems) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
        currentKitchenId = null;
        currentKitchenName = null;
    }

    public String getCurrentKitchenName() {
        return currentKitchenName;
    }
    
    public String getCurrentKitchenId() {
        return currentKitchenId;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
        }
    }
}
