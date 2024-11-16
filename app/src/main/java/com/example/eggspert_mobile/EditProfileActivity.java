package com.example.eggspert_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView userPhotoProfile; // Reference to the ImageView
    private ImageButton simpanButton, kembaliButton; // Reference to the Simpan and Kembali buttons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        // Apply window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Reference the ImageView and buttons
        userPhotoProfile = findViewById(R.id.userPhotoProfile);
        simpanButton = findViewById(R.id.button_simpan);
        kembaliButton = findViewById(R.id.button_kembali);  // Reference to the back button

        // Open gallery to select an image when the profile picture is clicked
        userPhotoProfile.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Handle "Simpan" button click to save the image
        simpanButton.setOnClickListener(view -> {
            // Save the image URI to SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // Save the URI of the selected image
            if (userPhotoProfile.getDrawable() != null) {
                Uri imageUri = Uri.parse(userPhotoProfile.getTag().toString()); // Tag stores the URI
                editor.putString("profileImageUri", imageUri.toString());
                editor.apply();

                // Optionally, show a toast to confirm saving
                Toast.makeText(EditProfileActivity.this, "Profile Picture Saved", Toast.LENGTH_SHORT).show();

                // Optionally, navigate back to ProfileActivity
                Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            } else {
                // Show a message if no image is selected
                Toast.makeText(EditProfileActivity.this, "Please select a profile picture", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle "Kembali" button click to go back to ProfileActivity
        kembaliButton.setOnClickListener(view -> {
            Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish(); // Optionally finish this activity after navigation
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();

            try {
                // Set the selected image URI to the ImageView tag
                userPhotoProfile.setTag(imageUri.toString());

                // Optionally, display the selected image in the ImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                userPhotoProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
