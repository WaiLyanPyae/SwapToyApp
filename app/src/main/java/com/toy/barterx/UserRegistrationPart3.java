package com.toy.barterx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserRegistrationPart3 extends AppCompatActivity {

    private Button getImage;
    private ImageView image;
    private Button next;
    private final int GALLERY_REQ_CODE = 1000;
    private StorageReference storageReference;
    private  Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Map<String, String> images = new HashMap<>();
    private  TextView cancel;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration_part3);
        storageReference = FirebaseStorage.getInstance().getReference().child("imageDir");
        getImage = findViewById(R.id.btnGetImageFromUser);
        image = findViewById(R.id.myImageView);
        next = findViewById(R.id.btnToNext);
        cancel = findViewById(R.id.cancelImageReg);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.simpleProgressBar);

        getImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri == null){
                    Toast.makeText(getApplicationContext(),"Please select image",Toast.LENGTH_LONG).show();
                    return;
                }
                upload();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: redirect user to home page and do not capture image details
                // TODO : edit 5
                //startActivity(new Intent(UserRegistrationPart3.this, Dashboard.class));
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == GALLERY_REQ_CODE){
                imageUri = data.getData();
                if(imageUri != null)
                    image.setImageURI(imageUri);
            }
        }

    }
    private void pickImage(){
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQ_CODE);
    }

    private  void  upload(){
        progressBar.setVisibility(View.VISIBLE);
        final String imageKey = UUID.randomUUID().toString();
        StorageReference fileRef = storageReference.child("image/"+imageKey);
        fileRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        uploadMetaData(fileRef);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Image Upload Failed", Toast.LENGTH_LONG).show();
                    }
                });
       }
    private  void uploadMetaData(StorageReference ref){
        FirebaseUser user = mAuth.getCurrentUser();
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                String imgUrl = task.getResult().toString();
                db.collection("users").document(user.getUid()) // Use UID instead of email
                        .update("imageUrl", imgUrl)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(),"Image Uploaded plus image url set",Toast.LENGTH_LONG).show();
                                startActivity(new Intent(UserRegistrationPart3.this, Menu.class));
                            }
                        });
            }
        });
    }
    }
