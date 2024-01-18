package com.toy.barterx;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.toy.barterx.model.Profile;

public class UserRegistrationStep2 extends AppCompatActivity {
    private FusedLocationProviderClient location;
    private EditText firstname;
    private EditText lastname;
    private EditText phone;
    private Button cont;
    private TextView canc;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private double lat, lon;

    private final double DEFAULT_LAT = 37.7749;
    private final double DEFAULT_LON = -122.4194;

    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration_step2);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        firstname = findViewById(R.id.txtFirstName);
        lastname = findViewById(R.id.txtLastName);
        phone = findViewById(R.id.txtPhone);
        cont = findViewById(R.id.btnContinue);
        canc = findViewById(R.id.cancel);
        location = LocationServices.getFusedLocationProviderClient(this);
        requestPermission();

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = mAuth.getCurrentUser(); // get instance of currently logged in user

                if (ActivityCompat.checkSelfPermission(UserRegistrationStep2.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(UserRegistrationStep2.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    location.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                lat = task.getResult().getLatitude();
                                lon = task.getResult().getLongitude();
                            } else {
                                // Use default location
                                lat = DEFAULT_LAT;
                                lon = DEFAULT_LON;
                                Toast.makeText(UserRegistrationStep2.this, "Using default location. Please update in profile settings.", Toast.LENGTH_LONG).show();
                            }
                            saveProfile(user, lat, lon);
                        }
                    });

                } else {
                    // No location permission, use default values
                    lat = DEFAULT_LAT;
                    lon = DEFAULT_LON;
                    Toast.makeText(UserRegistrationStep2.this, "Using default location. Please update in profile settings.", Toast.LENGTH_LONG).show();
                    saveProfile(user, lat, lon);
                }
            }
        });

        canc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Redirect user to the home page
            }
        });
    }

    private void saveProfile(FirebaseUser user, double lat, double lon) {
        profile = new Profile(user.getUid(),
                firstname.getText().toString(), lastname.getText().toString(),
                user.getEmail(),
                phone.getText().toString(),
                "", lat, lon);

        DocumentReference ref = db.collection("users").document(user.getUid());
        ref.set(profile, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "profile info saved", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(UserRegistrationStep2.this, UserRegistrationPart3.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "failed to save profile info", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void requestPermission() {
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                            } else {
                                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                            }
                        }
                );
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }
}