package com.example.homelybites.utils;

import com.example.homelybites.models.Dish;
import com.example.homelybites.models.Kitchen;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class DummyData {

    public static void generateDummyData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        // 1. Create Dummy Kitchens
        List<Kitchen> kitchens = new ArrayList<>();
        
        Kitchen k1 = new Kitchen("k1", "Mom's Magic Kitchen", "cook1");
        k1.setCookName("Mrs. Sharma");
        k1.setSpeciality("North Indian, Thali");
        k1.setRating(4.8f);
        k1.setAddress("Sector 15, Chandigarh");
        k1.setDescription("Authentic home-cooked North Indian meals made with love.");
        k1.setImageUrl("https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500");
        kitchens.add(k1);

        Kitchen k2 = new Kitchen("k2", "The Tiffin Box", "cook2");
        k2.setCookName("Aunty's Kitchen");
        k2.setSpeciality("South Indian, Dosa");
        k2.setRating(4.5f);
        k2.setAddress("Phase 7, Mohali");
        k2.setDescription("Healthy and hygienic South Indian breakfast and lunch boxes.");
        k2.setImageUrl("https://images.unsplash.com/photo-1589302168068-964664d93dc0?w=500");
        kitchens.add(k2);

        Kitchen k3 = new Kitchen("k3", "Healthy Home Meals", "cook3");
        k3.setCookName("Chef Rahul");
        k3.setSpeciality("Salads, Low Carb");
        k3.setRating(4.2f);
        k3.setAddress("Zirakpur, Punjab");
        k3.setDescription("Guilt-free home-cooked meals for fitness enthusiasts.");
        k3.setImageUrl("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500");
        kitchens.add(k3);

        for (Kitchen k : kitchens) {
            batch.set(db.collection("kitchens").document(k.getKitchenId()), k);
        }

        // 2. Create Dummy Dishes
        List<Dish> dishes = new ArrayList<>();

        // Dishes for Mom's Magic Kitchen
        dishes.add(new Dish("d1", "k1", "Paneer Butter Masala Thali", 150.0, "Monday", 10));
        dishes.add(new Dish("d2", "k1", "Dal Makhani & Jeera Rice", 120.0, "Tuesday", 15));
        dishes.add(new Dish("d3", "k1", "Aloo Gobhi & Paratha", 100.0, "Wednesday", 12));

        // Dishes for The Tiffin Box
        dishes.add(new Dish("d4", "k2", "Masala Dosa Combo", 80.0, "Monday", 20));
        dishes.add(new Dish("d5", "k2", "Idli Sambar Plate", 60.0, "Tuesday", 25));
        dishes.add(new Dish("d6", "k2", "Lemon Rice with Chutney", 90.0, "Wednesday", 18));

        // Dishes for Healthy Home Meals
        dishes.add(new Dish("d7", "k3", "Grilled Chicken Salad", 180.0, "Monday", 8));
        dishes.add(new Dish("d8", "k3", "Quinoa Veggie Bowl", 160.0, "Tuesday", 10));
        dishes.add(new Dish("d9", "k3", "Boiled Eggs & Sprouts", 70.0, "Wednesday", 30));

        for (Dish d : dishes) {
            d.setImageUrl("https://images.unsplash.com/photo-1540189549336-e6e99c3679fe?w=500");
            d.setDescription("Freshly prepared home-cooked " + d.getName());
            batch.set(db.collection("dishes").document(d.getDishId()), d);
        }

        // Commit the batch
        batch.commit().addOnSuccessListener(aVoid -> {
            System.out.println("Dummy data generated successfully!");
        }).addOnFailureListener(e -> {
            System.err.println("Error generating dummy data: " + e.getMessage());
        });
    }
}
