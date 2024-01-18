package com.toy.barterx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.toy.barterx.R;
import com.toy.barterx.algorithms.BitmapConverterFromUrl;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.IOException;
import java.util.List;

public class SliderAdapter extends  RecyclerView.Adapter<SliderAdapter.SliderViewModel> {

    private List<String> dtoItems;
    private ViewPager2 viewPager2;
    public SliderAdapter(List<String> dto, ViewPager2 viewPager2){
        this.dtoItems = dto;
        this.viewPager2 = viewPager2;
    }
    @NonNull
    @Override
    public SliderViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewModel(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item_container,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewModel holder, int position) {
//            holder.setImage(dtoItems.get(position));
        try {
            holder.imageView.setImageBitmap(BitmapConverterFromUrl.drawImageToView(dtoItems.get(position)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dtoItems.size();
    }

    class SliderViewModel extends RecyclerView.ViewHolder{
        private RoundedImageView imageView;
        public SliderViewModel(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.customImageSlider);
        }
    }
}
