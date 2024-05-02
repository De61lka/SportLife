package com.example.sportlife;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int PERMISSION_REQUEST_STORAGE = 2;

    private ImageView imageViewProfile;
    private TextView textViewUsername;
    private TextView textViewUserHeight;
    private TextView textViewUserWeight;
    private ImageButton btnEditProfile;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageViewProfile = findViewById(R.id.imageViewProfile);
        textViewUsername = findViewById(R.id.username);
        textViewUserHeight = findViewById(R.id.user_height);
        textViewUserWeight = findViewById(R.id.user_weight);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        TextView btnLogout = findViewById(R.id.btn_logout);

        loadUserProfileData();

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        imageViewProfile.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_STORAGE);
            }
        });

        btnLogout.setOnClickListener(v -> {
            logoutUser();
        });

        setupBottomNavigationView();
    }

    private void loadUserProfileData() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String height = prefs.getString("userHeight", "Not set");
        String weight = prefs.getString("userWeight", "Not set");
        TextView usernameTextView = findViewById(R.id.username);
        String username = UserManager.getInstance().getUsername(); // Получаем имя пользователя
        usernameTextView.setText(username);


        textViewUserHeight.setText(String.format("Height: %s cm", height));
        textViewUserWeight.setText(String.format("Weight: %s kg", weight));

        String imageUriString = prefs.getString("profile_image_uri", "");
        if (!imageUriString.isEmpty()) {
            Uri imageUri = Uri.parse(imageUriString);
            imageViewProfile.setImageURI(imageUri);
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            imageViewProfile.setImageURI(selectedImageUri);

            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("profile_image_uri", selectedImageUri.toString());
            editor.apply();
        }
    }

    private void logoutUser() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // This will clear all data stored in SharedPreferences
        editor.apply();

        startActivity(new Intent(this, Login.class)); // Close this activity
    }

    private void setupBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);  // Устанавливаем выбранный элемент – профиль

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navigation_home) {
                startActivity(new Intent(this, Home.class));
                return true;
            } else if (itemId == R.id.navigation_analytics) {
                startActivity(new Intent(this, AnalyticsActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Пользователь уже находится на странице профиля, поэтому ничего не делаем
                return true;
            } else {
                return false;
            }
        });

        ImageButton button = findViewById(R.id.polit_konf);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPrivacyPolicyDialog();
            }
        });

    }

    private void showPrivacyPolicyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Политика конфиденциальности")
                .setMessage(R.string.privacy_policy)
                .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}