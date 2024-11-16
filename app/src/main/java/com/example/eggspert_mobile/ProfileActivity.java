package com.example.eggspert_mobile;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    private ImageView userPhotoProfile; // Reference to the ImageView where the profile picture will be displayed

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Apply window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Reference the ImageView where the profile picture will be shown
        userPhotoProfile = findViewById(R.id.userPhotoProfile);

        // Retrieve the saved profile image URI from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        String profileImageUriString = sharedPreferences.getString("profileImageUri", "");

        if (!profileImageUriString.isEmpty()) {
            Uri imageUri = Uri.parse(profileImageUriString);

            try {
                // Convert URI to Bitmap and set it to the ImageView
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                userPhotoProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Find the ImageButton by its ID to navigate to EditProfileActivity
        ImageButton arrowUbahProfil = findViewById(R.id.arrowUbahProfil);

        // Set OnClickListener to navigate to EditProfileActivity
        arrowUbahProfil.setOnClickListener(view -> {
            // Navigate to EditProfileActivity
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }
}
