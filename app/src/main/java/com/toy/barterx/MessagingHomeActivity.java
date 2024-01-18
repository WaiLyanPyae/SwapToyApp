package com.toy.barterx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.toy.barterx.adapter.MessagingUserAdapter;
import com.toy.barterx.databinding.ActivityMessagingHomeBinding;
import com.toy.barterx.model.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MessagingHomeActivity extends AppCompatActivity {
    private ActivityMessagingHomeBinding binding;
    private FirebaseFirestore databaseReference;
    private MessagingUserAdapter adapter;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    String currentMerchantId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessagingHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        adapter = new MessagingUserAdapter(this);
        binding.userRecyclerView.setAdapter(adapter);
        binding.userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentMerchantId = extras.getString("currentMerchantId");
        }
        getUserProfile();
    }


    private void getUserProfile() {
        if (currentMerchantId != null) {
            databaseReference.collection("users").document(currentMerchantId)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Profile profile = documentSnapshot.toObject(Profile.class);
                            if (profile != null) {
                                profile.setId(documentSnapshot.getId());  // Set the ID manually
                                adapter.add(profile);
                            } else {
                                Toast.makeText(getApplicationContext(), "No user found with the given merchant ID", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Error fetching user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "No merchant ID provided", Toast.LENGTH_SHORT).show();
        }
    }
}