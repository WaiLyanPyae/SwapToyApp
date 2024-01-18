package com.toy.barterx;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ProgressBar;

import com.toy.barterx.adapter.MenuProductAdapter;
import com.toy.barterx.algorithms.HaversineDistanceAlgorithm;
import com.toy.barterx.model.ListingDto;
import com.toy.barterx.model.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private RecyclerView dataList;
    private MenuProductAdapter adapter;
    private List<ListingDto> products = new ArrayList<>();
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private String productId;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        dataList = view.findViewById(R.id.dataList);
        progressBar = view.findViewById(R.id.gridProgressBar);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
        getListing(gridLayoutManager);
    }

    private void getListing(GridLayoutManager gridLayoutManager){
        progressBar.setVisibility(View.VISIBLE);
        database.collection("listing").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            // Get all listings that aren't by the current user
                            products = task.getResult().toObjects(ListingDto.class).stream()
                                    .filter(o -> !o.getMerchantId().equals(user.getUid()))
                                    .collect(Collectors.toList());

                            // Retrieve the user's profile using their UID
                            database.collection("users").document(user.getUid()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@androidx.annotation.NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                Profile profile = task.getResult().toObject(Profile.class);
                                                if (profile != null) {
                                                    // Filter listings based on distance
                                                    HaversineDistanceAlgorithm algorithm = new HaversineDistanceAlgorithm(products, profile);
                                                    List<ListingDto> nearbyListings = algorithm.FindNearestUsers(50000);
                                                    // Set the filtered listings to the adapter
                                                    adapter = new MenuProductAdapter(getContext(), nearbyListings);
                                                    dataList.setLayoutManager(gridLayoutManager);
                                                    dataList.setAdapter(adapter);
                                                }
                                            }
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }
                                    });
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }
}