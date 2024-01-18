package com.toy.barterx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        mAuth = FirebaseAuth.getInstance();

        Button btnCheckVerification = findViewById(R.id.btnCheckVerification);

        btnCheckVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVerification();
            }
        });
    }

    private void checkVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    if (user.isEmailVerified()) {
                        Toast.makeText(getApplicationContext(), "Email verified successfully!", Toast.LENGTH_LONG).show();
                        // Redirect to next activity here
                        startActivity(new Intent(VerificationActivity.this, UserRegistrationStep2.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Email not verified yet. Please check your inbox.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
