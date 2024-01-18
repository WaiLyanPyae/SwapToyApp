package com.toy.barterx.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toy.barterx.ListingView;
import com.toy.barterx.R;
import com.toy.barterx.algorithms.BitmapConverterFromUrl;
import com.toy.barterx.model.ListingDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingHolder> {
    private List<ListingDto> listingItem;
    private Context context;

    public ListingAdapter(Context context) {
        this.context = context;
        listingItem = new ArrayList<>();
    }

    public  void add(ListingDto dto){
        listingItem.add(dto);
        notifyDataSetChanged();
    }

    public void clear(){
        listingItem.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ListingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listing_row_item, parent,false);
        return new ListingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListingHolder holder, @SuppressLint("RecyclerView") int position) {
     holder.tvTitle.setText(listingItem.get(position).getTitle());
        holder.tvCategory.setText(listingItem.get(position).getCategory());
        holder.tvCondition.setText(listingItem.get(position).getCondition());
        holder.tvDescription.setText(listingItem.get(position).getDescription());
        try {
            holder.imageView.setImageBitmap(BitmapConverterFromUrl.drawImageToView(listingItem.get(position).getListingImages().get(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListingView.class);
                intent.putExtra("key",listingItem.get(position).getProductId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listingItem.size();
    }

    public class ListingHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tvTitle,tvCategory,tvCondition,tvDescription;
        public ListingHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.idListingImage);
            tvTitle = itemView.findViewById(R.id.idTitile);
            tvCategory = itemView.findViewById(R.id.idCategory);
            tvCondition = itemView.findViewById(R.id.idCondition);
            tvDescription = itemView.findViewById(R.id.idDescription);
        }
    }


}
