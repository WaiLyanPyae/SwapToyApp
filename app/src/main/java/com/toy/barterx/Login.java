package com.toy.barterx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

   private EditText username;
   private EditText password;
   private Button login;
   private TextView signup;

   private TextView forgotPassword;
   private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        username =  findViewById(R.id.txtUsername);
        password =  findViewById(R.id.txtPassword);
        login =     findViewById(R.id.btnLogIn);
        signup =    findViewById(R.id.signUpText);
        forgotPassword = findViewById(R.id.forgotPassword);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               login(username.getText().toString(), password.getText().toString());
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Redirect to the signup page
                Toast.makeText(getApplicationContext(), "Sign Up", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Login.this, SignUp.class)); //This will start the sign up activity
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPasswordIntent = new Intent(Login.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordIntent);
            }
        });
    }

    //takes in email and password, return the user corresponding to email if exist else return null
    private void login(String email, String password){
        if(email.isEmpty() && password.isEmpty()){
            Toast.makeText(getApplicationContext(),"Invalid login inputs can not be null",Toast.LENGTH_LONG).show();
            return;
        }
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email,password)//login with email and password
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser(); // get an instance of the currently logged user that just registered
                            Toast.makeText(getApplicationContext(), user.getEmail().toString() +" signed in", Toast.LENGTH_SHORT).show();
                            // TODO : edit 2
                            startActivity(new Intent(Login.this, Menu.class));
                        }else{
                            Toast.makeText(getApplicationContext(), "Auth failed please check login details", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
