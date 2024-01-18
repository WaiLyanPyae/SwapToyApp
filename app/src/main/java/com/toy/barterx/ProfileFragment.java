package com.toy.barterx;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.toy.barterx.adapter.ListingAdapter;
import com.toy.barterx.algorithms.BitmapConverterFromUrl;
import com.toy.barterx.model.ListingDto;
import com.toy.barterx.model.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private FirebaseFirestore db;
    private ListingAdapter adapter;
    private ImageView imageView;
    private TextView  name,email,number;
    private RecyclerView mRecyclerView;
    private LinearLayout btnEdit, btnAdd,btnView;
    private ProgressBar progressBar;
    private ImageButton btnLogOut;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mRecyclerView = view.findViewById(R.id.myListingRecycler);
        imageView = view.findViewById(R.id.userImage);
        name = view.findViewById(R.id.client_name);
        email = view.findViewById(R.id.mail);
        number = view.findViewById(R.id.contactNumber);
        btnAdd = view.findViewById(R.id.btnAddListing);
        btnEdit = view.findViewById(R.id.btnEditProfile);
        btnView = view.findViewById(R.id.btnViewListing);
        progressBar = view.findViewById(R.id.userProgressBar);
        adapter = new ListingAdapter(getContext());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,true));
        db = FirebaseFirestore.getInstance();
        getProfileByEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        getMyListing(FirebaseAuth.getInstance().getCurrentUser().getUid());

        btnLogOut = view.findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               FirebaseAuth.getInstance().signOut();
               startActivity(new Intent(getContext(),Login.class));
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), EditProfile.class);
                startActivity(intent);
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(new Intent(getContext(),Listings.class));
            }
        });

    }

    private void getProfileByEmail(String userEmail) {
        progressBar.setVisibility(View.VISIBLE);
        db.collection("users")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            Profile user = document.toObject(Profile.class);
                            name.setText(user.getFirstname() + " " + user.getLastname());
                            email.setText(userEmail);
                            number.setText(user.getPhone());
                            try {
                                imageView.setImageBitmap(BitmapConverterFromUrl.drawImageToView(user.getImageUrl()));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Tag", e.getMessage());
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private void getMyListing(String userId){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("listing").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<ListingDto> listingDtos = task.getResult().toObjects(ListingDto.class);
                            adapter.clear();
                            for (ListingDto dto: listingDtos){
                                if(dto.getMerchantId().equals(userId)){
                                    adapter.add(dto);
                                }
                            }
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
    @Override
    public void onResume() {
        super.onResume();
        getProfileByEmail(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

}