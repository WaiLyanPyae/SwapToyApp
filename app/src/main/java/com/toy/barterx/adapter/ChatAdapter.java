package com.toy.barterx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toy.barterx.R;
import com.toy.barterx.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageHolder> {
    private List<Message> chats;
    private Context context;

    public ChatAdapter(Context context){
        this.chats = new ArrayList<>();
        this.context = context;
    }

    public  void add(Message msg){
        chats.add(msg);
        notifyDataSetChanged();
    }

    public void clear(){
        chats.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_row, parent,false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Message message = chats.get(position);
        holder.message.setText(message.getMessage());
        if(message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            holder.main.setBackgroundColor(context.getResources().getColor(R.color.teal_700));
            holder.message.setTextColor(context.getResources().getColor(R.color.white));

        }else{
            holder.main.setBackgroundColor(context.getResources().getColor(R.color.black));
            holder.message.setTextColor(context.getResources().getColor(R.color.white));
        }

    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private LinearLayout main;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);
          message = itemView.findViewById(R.id.myMessage);
          main = itemView.findViewById(R.id.mainMessageLayout);
        }
    }
}
