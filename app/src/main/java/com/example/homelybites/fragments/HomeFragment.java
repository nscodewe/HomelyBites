package com.example.homelybites.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homelybites.R;
import com.example.homelybites.activities.CustomerHomeActivity;
import com.example.homelybites.activities.KitchenDetailActivity;
import com.example.homelybites.adapters.CategoryAdapter;
import com.example.homelybites.adapters.KitchenAdapter;
import com.example.homelybites.models.Kitchen;
import com.example.homelybites.utils.FirebaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories, rvKitchens;
    private TextView tvGreeting;
    private LinearLayout searchBar;
    private List<Kitchen> kitchenList = new ArrayList<>();
    private KitchenAdapter kitchenAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvGreeting = view.findViewById(R.id.tvGreeting);
        rvCategories = view.findViewById(R.id.rvCategories);
        rvKitchens = view.findViewById(R.id.rvKitchens);
        searchBar = view.findViewById(R.id.searchBar);

        setupGreeting();
        setupCategories();
        setupKitchens();
        loadKitchens();

        // 1. Make Search Bar functional (Navigates to Search Tab)
        searchBar.setOnClickListener(v -> navigateToSearch());
    }

    private void navigateToSearch() {
        if (getActivity() instanceof CustomerHomeActivity) {
            BottomNavigationView nav = getActivity().findViewById(R.id.bottomNav);
            if (nav != null) {
                nav.setSelectedItemId(R.id.nav_search);
            }
        }
    }

    private void setupGreeting() {
        FirebaseHelper helper = FirebaseHelper.getInstance();
        if (helper.getCurrentUser() != null) {
            helper.getUser(helper.getCurrentUser().getUid(), task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String name = task.getResult().getString("name");
                    if (name != null && isAdded()) {
                        tvGreeting.setText(getString(R.string.hello_greeting, name));
                    }
                }
            });
        }
    }

    private void setupCategories() {
        List<CategoryAdapter.Category> categories = Arrays.asList(
                new CategoryAdapter.Category("North Indian", "🍛"),
                new CategoryAdapter.Category("South Indian", "🥘"),
                new CategoryAdapter.Category("Chinese", "🥡"),
                new CategoryAdapter.Category("Desserts", "🍰"),
                new CategoryAdapter.Category("Snacks", "🍿"),
                new CategoryAdapter.Category("Beverages", "🥤")
        );

        CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
            // When a category is clicked, we can also go to search and filter (optional improvement)
            navigateToSearch();
        });

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(adapter);
    }

    private void setupKitchens() {
        kitchenAdapter = new KitchenAdapter(kitchenList, kitchen -> {
            Intent intent = new Intent(getActivity(), KitchenDetailActivity.class);
            intent.putExtra("kitchenId", kitchen.getKitchenId());
            intent.putExtra("kitchenName", kitchen.getName());
            startActivity(intent);
        });

        // Using a LinearLayoutManager and nestedScrollingEnabled=false allows the
        // RecyclerView to expand inside the ScrollView so the whole page scrolls.
        rvKitchens.setLayoutManager(new LinearLayoutManager(getContext()));
        rvKitchens.setAdapter(kitchenAdapter);
        rvKitchens.setNestedScrollingEnabled(false);
    }

    private void loadKitchens() {
        FirebaseHelper.getInstance().getAllKitchens(task -> {
            if (task.isSuccessful() && isAdded()) {
                kitchenList.clear();
                for (DocumentSnapshot doc : task.getResult()) {
                    Kitchen kitchen = doc.toObject(Kitchen.class);
                    if (kitchen != null) {
                        kitchenList.add(kitchen);
                    }
                }
                kitchenAdapter.notifyDataSetChanged();
            }
        });
    }
}
