package com.toy.barterx;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.toy.barterx.adapter.ListingAdapter;
import com.toy.barterx.databinding.ActivityProfileBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class activity_profile extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private FirebaseFirestore db;
    private ListingAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                .permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        binding = ActivityProfileBinding.inflate(getLayoutInflater());
//        adapter = new ListingAdapter(this);
//        binding.myListingRecycler.setAdapter(adapter);
//        binding.myListingRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,true));
//        setContentView(binding.getRoot());
//        db = FirebaseFirestore.getInstance();
//        getProfileById(FirebaseAuth.getInstance().getCurrentUser().getEmail());
//        getMyListing(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        binding.editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(),"Edit Profile Button Clicked",Toast.LENGTH_LONG).show();
//            }
//        });
//
//        binding.listingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(activity_profile.this, Listings.class));
//            }
//        });
//        binding.btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO : edit 1
//                //startActivity(new Intent(activity_profile.this, Menu.class));
//            }
//        });
//
//        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(activity_profile.this, Login.class));
//            }
//        });
//    }
//
//    private void getProfileById(String collectionName){
//        DocumentReference documentReference = db.collection("users").document(collectionName);
//        documentReference.get().addOnCompleteListener( new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot document = task.getResult();
//                    if(document.exists()){
//                        Profile user = document.toObject(Profile.class);
//                        binding.clientName.setText(user.getFirstname() +" "+user.getLastname());
//                        binding.mail.setText(""+user.getEmail());
//                        binding.contactNumber.setText(""+user.getPhone());
//                        binding.location.setText("Lat: "+ user.getLatitude()+", Long:  "+user.getLogitude());
//                        try {
//                            binding.userImage.setImageBitmap(BitmapConverterFromUrl.drawImageToView(user.getImageUrl()));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                String message = e.getMessage();
//                Log.d("Tag",e.getMessage());
//            }
//        });
//    }
//
//    private void getMyListing(String userId){
//        db.collection("listing").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if(task.isSuccessful()){
//                            List<ListingDto> listingDtos = task.getResult().toObjects(ListingDto.class);
//                            adapter.clear();
//                            for (ListingDto dto: listingDtos){
//                                if(dto.getMerchantId().equals(userId)){
//                                    adapter.add(dto);
//                                }
//                            }
//                        }
//                    }
//                });
//    }
    }
}