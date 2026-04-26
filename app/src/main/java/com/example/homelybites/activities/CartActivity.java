package com.example.homelybites.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homelybites.R;
import com.example.homelybites.adapters.CartAdapter;
import com.example.homelybites.models.Order;
import com.example.homelybites.models.User;
import com.example.homelybites.utils.CartManager;
import com.example.homelybites.utils.FirebaseHelper;

import java.util.ArrayList;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCartItems;
    private TextView tvTotalAmount;
    private View btnPlaceOrder;
    private ImageView btnBack;
    private CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        rvCartItems = findViewById(R.id.rvCartItems);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        setupCartList();
        updateUI();

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void setupCartList() {
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartAdapter(CartManager.getInstance().getCartItems(), position -> {
            CartManager.getInstance().removeItem(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
            updateUI();
        });
        rvCartItems.setAdapter(adapter);
    }

    private void updateUI() {
        double total = CartManager.getInstance().getTotalAmount();
        tvTotalAmount.setText(String.format("₹%.2f", total));
        
        if (CartManager.getInstance().getCartItems().isEmpty()) {
            btnPlaceOrder.setEnabled(false);
            btnPlaceOrder.setAlpha(0.5f);
        } else {
            btnPlaceOrder.setEnabled(true);
            btnPlaceOrder.setAlpha(1.0f);
        }
    }

    private void placeOrder() {
        FirebaseHelper helper = FirebaseHelper.getInstance();
        CartManager cart = CartManager.getInstance();

        if (!helper.isLoggedIn()) {
            Toast.makeText(this, "Please login to place order", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cart.getCartItems().isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        btnPlaceOrder.setEnabled(false);
        String orderId = helper.generateOrderId();
        String customerId = helper.getCurrentUser().getUid();

        // Create Order Object
        Order order = new Order();
        order.setOrderId(orderId);
        order.setCustomerId(customerId);
        order.setKitchenId(cart.getCurrentKitchenId());
        order.setKitchenName(cart.getCurrentKitchenName());
        order.setItems(new ArrayList<>(cart.getCartItems()));
        order.setTotalAmount(cart.getTotalAmount());
        order.setStatus("waiting");
        order.setTimestamp(System.currentTimeMillis());

        // Save to Firebase
        helper.saveOrder(order, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                cart.clearCart();
                finish();
            } else {
                Toast.makeText(this, "Failed to place order: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                btnPlaceOrder.setEnabled(true);
            }
        });
    }
}
