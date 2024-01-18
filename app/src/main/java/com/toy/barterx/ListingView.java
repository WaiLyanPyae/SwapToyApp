package com.toy.barterx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.toy.barterx.adapter.SliderAdapter;
import com.toy.barterx.model.ListingDto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ListingView extends AppCompatActivity {
    private List<ListingDto> listOfItems;
    private ViewPager2 viewPager2;
    private FirebaseAuth mAuth;
    private FirebaseFirestore database;
    private FirebaseUser user;
    private String productId;
    private ListingDto listing = new ListingDto();
    private  boolean toggle = false;
    private  Button btnChat;

    private TextView txtProductName, txtCategory,txtCondition,txtDescription;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_view);
        viewPager2 = findViewById(R.id.pageImageSlider);
        txtProductName = findViewById(R.id.txtProdViewName);
        txtCategory = findViewById(R.id.txtProdViewCategoryDetail);
        txtCondition = findViewById(R.id.txtProdViewConditionDetail);
        txtDescription = findViewById(R.id.txtProdViewDescriptionDetail);
        progressBar = findViewById(R.id.detailProgressBar);
        btnChat = findViewById(R.id.chatButton);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            productId= extras.getString("key");
            toggle = extras.getBoolean("toggle");
        }
        if(toggle){
            btnChat.setVisibility(View.VISIBLE);

            btnChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MessagingHomeActivity.class);
                    intent.putExtra("chatId", user.getUid());
                    intent.putExtra("chatIdCheck", true);
                    intent.putExtra("currentMerchantId", listing.getMerchantId());  // Pass the current merchant ID
                    startActivity(intent);
                }
            });

        }
        if (productId != null) {
            downloadData(productId);
        } else {
            Toast.makeText(this, "Product ID is missing!", Toast.LENGTH_SHORT).show();
        }
    }
    private void downloadData(String userId){
        progressBar.setVisibility(View.VISIBLE);
        database.collection("listing").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<ListingDto>  dtos = task.getResult().toObjects(ListingDto.class);
                            for (ListingDto d:dtos){
                                if(d.getProductId().equals(productId)){
                                    listing = d;
                                    break;
                                }
                            }
                            viewPager2.setAdapter(new SliderAdapter(listing.getListingImages(),viewPager2));
                            viewPager2.setClipToPadding(false);
                            viewPager2.setClipChildren(false);
                            viewPager2.setOffscreenPageLimit(3);
                            viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

                            CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                            compositePageTransformer.addTransformer(new MarginPageTransformer(40));
                            compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                                @Override
                                public void transformPage(@NonNull View page, float position) {
                                    float r = 1 - Math.abs(position);
                                    page.setScaleY(0.85f+r*0.15f);
                                }
                            });
                            viewPager2.setPageTransformer(compositePageTransformer);
                            txtProductName.setText(listing.getTitle());
                            txtCategory.setText(listing.getCategory());
                            txtCondition.setText(listing.getCondition());
                            txtDescription.setText(listing.getDescription());
                            progressBar.setVisibility(View.INVISIBLE);
                        }else {
                            Toast.makeText(ListingView.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
}