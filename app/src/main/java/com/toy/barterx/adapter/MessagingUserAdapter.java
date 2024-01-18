package com.toy.barterx.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toy.barterx.ChatActivity;
import com.toy.barterx.R;
import com.toy.barterx.model.Profile;

import java.util.ArrayList;
import java.util.List;

public class MessagingUserAdapter extends RecyclerView.Adapter<MessagingUserAdapter.MessageHolder> {
    private List<Profile> userProfile;
    private Context context;

    public MessagingUserAdapter(Context context){
        this.userProfile = new ArrayList<>();
        this.context = context;
    }

    public  void add(Profile profile){
        userProfile.add(profile);
        notifyDataSetChanged();
    }

    public void clear(){
        userProfile.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_row, parent,false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Profile profile = userProfile.get(position);
        holder.username.setText(profile.getFirstname() +" "+profile.getLastname());
        holder.email.setText(profile.getEmail());
//        Glide.with(context).load(profile.getImageUrl()).into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(context, ChatActivity.class);
                intent.putExtra("id",profile.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userProfile.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView username,email;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.imgUser);
            username = itemView.findViewById(R.id.userNameTextField);
            email = itemView.findViewById(R.id.userEmailTextField);
        }
    }
}
