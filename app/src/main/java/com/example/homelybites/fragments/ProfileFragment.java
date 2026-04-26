package com.example.homelybites.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.homelybites.R;
import com.example.homelybites.activities.BecomeCookActivity;
import com.example.homelybites.activities.ChangeAddressActivity;
import com.example.homelybites.activities.CustomerLoginActivity;
import com.example.homelybites.activities.HelpCenterActivity;
import com.example.homelybites.activities.OrderHistoryActivity;
import com.example.homelybites.activities.CustomerHomeActivity;
import com.example.homelybites.utils.FirebaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvOrdersCount, tvFavCount, tvRating;
    private View cardMyOrders, cardHelpCenter, cardChangeAddress, cardBecomeCook, btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Views
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvOrdersCount = view.findViewById(R.id.tvOrdersCount);
        tvFavCount = view.findViewById(R.id.tvFavCount);
        tvRating = view.findViewById(R.id.tvRating);

        cardMyOrders = view.findViewById(R.id.cardMyOrders);
        cardHelpCenter = view.findViewById(R.id.cardHelpCenter);
        cardChangeAddress = view.findViewById(R.id.cardChangeAddress);
        cardBecomeCook = view.findViewById(R.id.cardBecomeCook);
        btnLogout = view.findViewById(R.id.btnLogout);

        loadUserData();
        setupClickListeners(view);
    }

    private void loadUserData() {
        FirebaseHelper helper = FirebaseHelper.getInstance();
        if (helper.getCurrentUser() != null) {
            String uid = helper.getCurrentUser().getUid();
            helper.getUser(uid, task -> {
                if (task.isSuccessful() && task.getResult().exists() && isAdded()) {
                    String name = task.getResult().getString("name");
                    String email = task.getResult().getString("email");
                    List<String> favorites = (List<String>) task.getResult().get("favoriteKitchenIds");
                    
                    tvName.setText(name != null ? name : "User Name");
                    tvEmail.setText(email != null ? email : helper.getCurrentUser().getEmail());
                    
                    if (favorites != null) {
                        tvFavCount.setText(String.valueOf(favorites.size()));
                    }

                    // Load orders count
                    helper.getOrdersByCustomer(uid, orderTask -> {
                        if (orderTask.isSuccessful() && isAdded()) {
                            tvOrdersCount.setText(String.valueOf(orderTask.getResult().size()));
                        }
                    });
                }
            });
        }
    }

    private void setupClickListeners(View view) {
        cardMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), OrderHistoryActivity.class));
        });

        cardHelpCenter.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), HelpCenterActivity.class));
        });

        cardChangeAddress.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChangeAddressActivity.class));
        });

        cardBecomeCook.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), BecomeCookActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            FirebaseHelper.getInstance().signOut();
            Intent intent = new Intent(getActivity(), CustomerLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Make the "Favorites" count clickable to go to favorites tab
        // Fixed: Cast getParent() to View to set OnClickListener
        ((View) tvFavCount.getParent()).setOnClickListener(v -> {
            if (getActivity() instanceof CustomerHomeActivity) {
                BottomNavigationView nav = getActivity().findViewById(R.id.bottomNav);
                if (nav != null) {
                    nav.setSelectedItemId(R.id.nav_favorites);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserData(); // Refresh data when returning to profile
    }
}
