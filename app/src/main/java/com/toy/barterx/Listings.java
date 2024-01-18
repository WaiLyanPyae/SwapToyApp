package com.toy.barterx;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.toy.barterx.databinding.ActivityListingsBinding;
import com.toy.barterx.model.ListingDto;
import com.toy.barterx.model.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Listings extends AppCompatActivity {

    private ActivityListingsBinding activityListingsBinding;
    private final int GALLERY_REQ_CODE = 1000;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private FirebaseUser user;
    private StorageReference storageReference;
    private List<String> listingImages = new ArrayList<>();
    private Bitmap imageToClassify;

    private int[] images = {
            R.drawable.a, R.drawable.b, R.drawable.c,
            R.drawable.d, R.drawable.e, R.drawable.f
    };
    private Uri[] Images = new Uri[3];
    ImageSwitcher imageSwitcher;
    Button next, prev, addNew, cont;
    int index = 0;

    String[] conditionItems = {
            "New", "Used - Like New", "Used - Partly Damaged", "Used - Damaged", "Used - Not Recoverable"
    };
    AutoCompleteTextView condition;
    ArrayAdapter<String> conditionAdapterItems;


    String[] categoryItems = {
            "Puzzle", "Barbie", "Stuffed toy", "Doll", "LEGO", "Play-Doh",
            "Education toy", "Video Games", "Nerf", "Musical toys", "Yo-yo",
            "Balls", "Vehicles", "Electronic toy"
    };
    AutoCompleteTextView category;
    ArrayAdapter<String> categoryAdapterItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityListingsBinding = ActivityListingsBinding.inflate(getLayoutInflater());
        setContentView(activityListingsBinding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("imageDir");


        next = findViewById(R.id.buttonNext);
        prev = findViewById(R.id.buttonPrev);
        addNew = findViewById(R.id.buttonAddNewImage);
        imageSwitcher = findViewById(R.id.imageSwitcher);

        /**
         * Drop Down list stuff
         */

        cont = findViewById(R.id.buttonContinue);
        condition = findViewById(R.id.txtCondition);
        conditionAdapterItems = new ArrayAdapter<>(this, R.layout.catergory_list_items, conditionItems);
        condition.setAdapter(conditionAdapterItems);
        condition.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(), "Item : " + item, Toast.LENGTH_SHORT).show();
            }
        });

        category = findViewById(R.id.txtCategory);
        categoryAdapterItems = new ArrayAdapter<>(this, R.layout.catergory_list_items, categoryItems);
        category.setAdapter(categoryAdapterItems);
        category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(getApplicationContext(), "Item : " + item, Toast.LENGTH_SHORT).show();
            }
        });

        /***
         * ImageSwitcher Stuff
         */

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Images != null) index++;
                if (index == Images.length)
                    index = 0;
                imageSwitcher.setImageURI(Images[index]);
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Images != null) index--;
                if (index < 0)
                    index = Images.length - 1;
                imageSwitcher.setImageURI(Images[index]);
            }
        });

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setMaxWidth(250);
                imageView.setMaxHeight(250);
                return imageView;
            }
        });

        if (Images != null) imageSwitcher.setImageURI(Images[index]);

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();
            }
        });
    }

    private void pickImage() {
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_REQ_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                imageUri = data.getData();
                if (imageUri != null)
                    imageSwitcher.setImageURI(imageUri);
                Images[index] = imageUri;
            }
        }

    }



    private void upload() {
        activityListingsBinding.simpleProgressBar.setVisibility(View.VISIBLE);
        for (Uri imgUri : Images) {
            if (imgUri != null) {
                final String imageKey = UUID.randomUUID().toString();
                StorageReference fileRef = storageReference.child("listing/" + imageKey);
                fileRef.putFile(imgUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                fileRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String imgUrl = task.getResult().toString();
                                        listingImages.add(imgUrl);
                                        if (listingImages.size() == Images.length) {
                                            uploadMetaData();
                                        }
                                    }
                                });
                                activityListingsBinding.simpleProgressBar.setVisibility(View.INVISIBLE);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                activityListingsBinding.simpleProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Image Upload Failed", Toast.LENGTH_LONG).show();
                            }
                        });
            } else {
                Log.e("Upload error", "imgUri was null");
            }
        }
    }

    private void uploadMetaData() {
        activityListingsBinding.simpleProgressBar.setVisibility(View.VISIBLE);
        DocumentReference documentReference = database.collection("users").document(user.getUid()); // Use UID instead of email
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Profile usr = document.toObject(Profile.class);
                        ListingDto dto = new ListingDto(
                                activityListingsBinding.txtProductName.getText().toString(),
                                activityListingsBinding.txtCategory.getText().toString(),
                                activityListingsBinding.txtCondition.getText().toString(),
                                activityListingsBinding.txtDescription.getText().toString()
                        );
                        dto.setLatitude(usr.getLatitude());
                        dto.setLongitude(usr.getLongitude());
                        dto.setMerchantId(user.getUid());
                        dto.setListingImages(listingImages);
                        dto.setProductId(UUID.randomUUID().toString());
                        database.collection("listing").add(dto)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Listing Completed", Toast.LENGTH_LONG).show();
                                            clear();
                                            activityListingsBinding.simpleProgressBar.setVisibility(View.INVISIBLE);
                                            startActivity(new Intent(Listings.this, Menu.class));
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void clear() {
        // Use the binding to clear UI elements
        activityListingsBinding.txtCategory.setText("");
        activityListingsBinding.txtCondition.setText("");
        activityListingsBinding.txtDescription.setText("");
        activityListingsBinding.txtProductName.setText("");
        listingImages.clear();
        Images = new Uri[3]; // Reset the Images array
    }


}