package com.example.homelybites.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.homelybites.R;
import com.example.homelybites.databinding.ActivityKitchenDetailBinding;
import com.example.homelybites.adapters.DishAdapter;
import com.example.homelybites.models.Dish;
import com.example.homelybites.models.Kitchen;
import com.example.homelybites.models.Order;
import com.example.homelybites.utils.CartManager;
import com.example.homelybites.utils.FirebaseHelper;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class KitchenDetailActivity extends AppCompatActivity {

    private ActivityKitchenDetailBinding binding;
    private List<Dish> dishes = new ArrayList<>();
    private DishAdapter adapter;
    private String kitchenId, kitchenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKitchenDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        kitchenId = getIntent().getStringExtra("kitchenId");
        kitchenName = getIntent().getStringExtra("kitchenName");

        binding.tvTitle.setText(kitchenName != null ? kitchenName : "Kitchen");
        binding.btnBack.setOnClickListener(v -> finish());

        adapter = new DishAdapter(dishes, dish -> addToOrder(dish));
        binding.rvMenu.setLayoutManager(new LinearLayoutManager(this));
        binding.rvMenu.setAdapter(adapter);

        loadKitchenDetails();
        loadMenu();
        updateOrderBar();

        binding.btnOrder.setOnClickListener(v -> {
            startActivity(new Intent(this, CartActivity.class));
        });

        binding.btnFavorite.setOnClickListener(v -> {
            FirebaseHelper helper = FirebaseHelper.getInstance();
            if (helper.getCurrentUser() != null && kitchenId != null) {
                helper.getUser(helper.getCurrentUser().getUid(), task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        List<String> favs = (List<String>) task.getResult().get("favoriteKitchenIds");
                        if (favs == null) favs = new ArrayList<>();
                        if (!favs.contains(kitchenId)) {
                            favs.add(kitchenId);
                            helper.updateUserField(helper.getCurrentUser().getUid(), "favoriteKitchenIds", favs, t ->
                                    Toast.makeText(this, "Added to favorites!", Toast.LENGTH_SHORT).show());
                        } else {
                            Toast.makeText(this, "Already in favorites", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void loadKitchenDetails() {
        if (kitchenId == null) return;
        FirebaseHelper.getInstance().getKitchen(kitchenId, task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                Kitchen kitchen = task.getResult().toObject(Kitchen.class);
                if (kitchen != null) {
                    binding.tvKitchenName.setText(kitchen.getName());
                    binding.tvRating.setText(String.format("%.1f", kitchen.getRating()));
                    if (kitchen.getSpeciality() != null) binding.tvSpeciality.setText(kitchen.getSpeciality());
                    if (kitchen.getDescription() != null) binding.tvDescription.setText(kitchen.getDescription());
                }
            }
        });
    }

    private void loadMenu() {
        if (kitchenId == null) return;
        FirebaseHelper.getInstance().getDishesByKitchen(kitchenId, task -> {
            if (task.isSuccessful()) {
                dishes.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    Dish dish = doc.toObject(Dish.class);
                    if (dish != null) dishes.add(dish);
                }
                adapter.notifyDataSetChanged();
                binding.tvNoMenu.setVisibility(dishes.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void addToOrder(Dish dish) {
        Order.OrderItem item = new Order.OrderItem(dish.getDishId(), dish.getName(), dish.getPrice(), 1);
        CartManager.getInstance().addItem(kitchenId, kitchenName, item);
        updateOrderBar();
        Toast.makeText(this, dish.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
    }

    private void updateOrderBar() {
        CartManager cart = CartManager.getInstance();
        if (cart.getCartItems().isEmpty()) {
            binding.layoutOrderBar.setVisibility(View.GONE);
        } else {
            binding.layoutOrderBar.setVisibility(View.VISIBLE);
            binding.tvItemCount.setText(cart.getCartItems().size() + " item(s)");
            binding.tvTotalAmount.setText(String.format("₹%.0f", cart.getTotalAmount()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateOrderBar();
    }
}
