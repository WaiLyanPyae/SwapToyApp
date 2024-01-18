package com.toy.barterx;

import com.bumptech.glide.Glide;
import com.toy.barterx.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.toy.barterx.model.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.Manifest;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
public class EditProfile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri; // Store the selected image URI
    private EditText editFirstName, editLastName, editPhone, editImageUrl;
    private ImageView editProfileImage;
    private TextView textEmail, textLatitude, textLongitude;
    private ProgressBar editProgressBar;
    private FirebaseFirestore db;

    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editImageUrl = findViewById(R.id.editImageUrl);
        editProfileImage = findViewById(R.id.editProfileImage);
        editProfileImage.setOnClickListener(v -> openFileChooser()); // Click image to choose a new one
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editPhone = findViewById(R.id.editPhone);
        textEmail = findViewById(R.id.textEmail);
        textLatitude = findViewById(R.id.textLatitude);
        textLongitude = findViewById(R.id.textLongitude);
        editProgressBar = findViewById(R.id.editProgressBar);
        db = FirebaseFirestore.getInstance();
        // Initialize the FusedLocationProviderClient
        // fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getProfileByEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        Button btnSaveProfile = findViewById(R.id.btnSaveProfile);
        btnSaveProfile.setOnClickListener(v -> {
            editProgressBar.setVisibility(View.VISIBLE);

            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            String firstName = editFirstName.getText().toString().trim();
            String lastName = editLastName.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String imageUrl = editImageUrl.getText().toString().trim();
            double latitude = Double.parseDouble(textLatitude.getText().toString().trim());
            double longitude = Double.parseDouble(textLongitude.getText().toString().trim());

            Profile profile = new Profile(userId, firstName, lastName, email, phone, imageUrl, latitude, longitude);

            updateProfile(profile);
            uploadProfileImageAndSave();
        });
    }

    private void uploadProfileImageAndSave() {
        editProgressBar.setVisibility(View.VISIBLE);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_images");
        StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            double latitude = Double.parseDouble(textLatitude.getText().toString().trim());
                            double longitude = Double.parseDouble(textLongitude.getText().toString().trim());
                            Profile profile = new Profile(userId, firstName, lastName, email, phone, imageUrl, latitude, longitude);
                            updateProfile(profile);
                        }))
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfile.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    editProgressBar.setVisibility(View.INVISIBLE);
                });
    }

    private void updateProfile(Profile profile) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .set(profile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfile.this, "Error updating profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    editProgressBar.setVisibility(View.INVISIBLE);
                });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(editProfileImage);
        }
    }

    private void getProfileByEmail(String email) {
        editProgressBar.setVisibility(View.VISIBLE);
        db.collection("users")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        Profile user = document.toObject(Profile.class);
                        editFirstName.setText(user.getFirstname());
                        editLastName.setText(user.getLastname());
                        editPhone.setText(user.getPhone());
                        editImageUrl.setText(user.getImageUrl()); // Set the image URL here
                        textEmail.setText(user.getEmail());
                        textLatitude.setText(String.valueOf(user.getLatitude()));
                        textLongitude.setText(String.valueOf(user.getLongitude()));

                        String imageUrl = user.getImageUrl();
                        editImageUrl.setText(imageUrl); // Set the image URL in EditText
                        Glide.with(EditProfile.this)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_baseline_person_24) // Optional placeholder
                                .into(editProfileImage); // Load the image into ImageView
                        editProgressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Tag", e.getMessage());
                    editProgressBar.setVisibility(View.INVISIBLE);
                });
    }
}
