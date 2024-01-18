package com.toy.barterx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.toy.barterx.R;
import com.toy.barterx.model.Message;

import java.util.List;

public class MessagingChatAdapter extends RecyclerView.Adapter<MessagingChatAdapter.ChatHolder> {
    private Context context;
    private List<Message> messageList;

    public MessagingChatAdapter(Context context, List<Message> messages){
        this.context = context;
        this.messageList = messages;
    }

    public  void addMessage(Message message){
        messageList.add(0,message);
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_screen, parent,false);
        return new ChatHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {
        holder.message.setText(messageList.get(position).getMessage());
        holder.dateTime.setText(messageList.get(position).getDateTime());
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


    public class ChatHolder extends RecyclerView.ViewHolder {
        private TextView username,message, dateTime;
        public ChatHolder(@NonNull View itemView) {
            super(itemView);
            username  = itemView.findViewById(R.id.userEmailText);
            message = itemView.findViewById(R.id.messageText);
            dateTime = itemView.findViewById(R.id.messageDateText);
        }
    }
}
