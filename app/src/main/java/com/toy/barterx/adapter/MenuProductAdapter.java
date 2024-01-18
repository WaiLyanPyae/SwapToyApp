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
import java.util.List;

public class MenuProductAdapter extends RecyclerView.Adapter<MenuProductAdapter.ViewHolder> {

    private Context context;
    private List<ListingDto> products;

    public MenuProductAdapter(Context context, List<ListingDto> dtos) {
        this.context =context;
        this.products = dtos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_grid_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.prodName.setText(products.get(position).getTitle());
        holder.prodDistance.setText(products.get(position).getDistance()+"m");
        try {
            holder.prodImage.setImageBitmap(BitmapConverterFromUrl.drawImageToView(products.get(position).getListingImages().get(0)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListingView.class);
                intent.putExtra("key",products.get(position).getProductId());
                intent.putExtra("toggle",true);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

       private TextView prodName;
       private TextView prodDistance;
       private ImageView prodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prodName = itemView.findViewById(R.id.txtMenuProductName);
            prodDistance = itemView.findViewById(R.id.txtMenuProductDistance);
            prodImage = itemView.findViewById(R.id.imgMenuProductImage);
        }
    }
}
