package com.toy.barterx;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.toy.barterx.R;
import com.toy.barterx.adapter.ListingAdapter;
import com.toy.barterx.model.ListingDto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListingsFragment extends Fragment {
    private RecyclerView dataList;
    private ListingAdapter adapter;
    private List<ListingDto> products = new ArrayList<>();
    private FirebaseFirestore database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private String productId;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ListingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListingsFragment newInstance(String param1, String param2) {
        ListingsFragment fragment = new ListingsFragment();
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
        return inflater.inflate(R.layout.fragment_listings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        dataList = view.findViewById(R.id.myListingRecycler);
        progressBar = view.findViewById(R.id.miProgressBar);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        user = mAuth.getCurrentUser();
        getListing();
    }

    private void getListing(){
        progressBar.setVisibility(View.VISIBLE);
        database.collection("listing").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            products = task.getResult().toObjects(ListingDto.class).stream().filter(o->o.getMerchantId().equals(user.getUid())).collect(Collectors.toList());
                            adapter = new ListingAdapter(getContext());
                            dataList.setLayoutManager(new LinearLayoutManager(getContext()));
                            dataList.setAdapter(adapter);
                            for (ListingDto prod:products){
                                adapter.add(prod);
                            }
                            if(products.size() ==0){
                                Toast.makeText(getContext(),"You Have No Listing At the Moment",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
        progressBar.setVisibility(View.INVISIBLE);
    }
}