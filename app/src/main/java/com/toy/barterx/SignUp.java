package com.toy.barterx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUp extends AppCompatActivity {

   private EditText email;
   private EditText email2;
   private EditText password;
   private EditText password2;
   private Button signIn;
   private TextView login;
   private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.txtEmail);
        email2 = findViewById(R.id.txtEmail2);
        password = findViewById(R.id.txtPassword);
        password2 = findViewById(R.id.txtPassword2);
        signIn = findViewById(R.id.btnLogIn);
        login = findViewById(R.id.goBackToLogin);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateEmail(email.getText().toString(), email2.getText().toString()) &&
                        validatePassword(password.getText().toString(), password2.getText().toString()))
                {
                   register(email.getText().toString(), password.getText().toString());
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, Login.class));
            }
        });
    }

    private boolean validateEmail(String mail, String mail2)
    {
        String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mail);
        if(matcher.matches())
        {
            if(mail.equals(mail2))
                return true;
            Toast.makeText(getApplicationContext(), "Emails do not match", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
        }
        return false; //Invalid email address, or email is not valid
    }

    private boolean validatePassword(String pass, String pass2){

        if(!pass.equals(pass2))
        {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(pass.length() >= 8))
        {
            Toast.makeText(getApplicationContext(), "Password must have 8 or more characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        // TODO: Add password validation
        //Do an in depth password analysis using the following rules
            /*
                A password must have:
                    - At least 8 characters
                    - At least 1 special character
                    - At least 1 upper case character
                    - At least 1 lower case character
                    - At least 1 number
                A password must not include the email address of the user
             */
        return true;
    }

    //register new user with email and password if successful redirect to profile activity
    private void register(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                storeUserProfile(user.getUid(), email); // Store the user's UID and email in Firestore
                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Verification email has been sent to " + user.getEmail(), Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(SignUp.this, VerificationActivity.class));
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to send verification email. Please try again.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Exception e = task.getException();
                            if (e != null) {
                                Toast.makeText(getApplicationContext(), "Registration Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Registration Failed!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void storeUserProfile(String uid, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user profile document with the UID as the document ID
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("id", uid); // Store the UID as "id" field
        profileData.put("email", email); // Store the email

        db.collection("users").document(uid)
                .set(profileData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Profile stored successfully
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to store user profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
