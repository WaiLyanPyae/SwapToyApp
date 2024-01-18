package com.toy.barterx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.toy.barterx.adapter.ChatAdapter;
import com.toy.barterx.adapter.MessagingChatAdapter;
import com.toy.barterx.databinding.ActivityChatBinding;
import com.toy.barterx.model.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ChatAdapter chatAdapter;
    private ActivityChatBinding binding;
    private String receiverId;
    private ValueEventListener messageEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        chatAdapter = new ChatAdapter(this);
        binding.chatRecyclerView.setAdapter(chatAdapter);
        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        receiverId = getIntent().getStringExtra("id");
        if (getIntent().getBooleanExtra("chatIdCheck", false)) {
            receiverId = getIntent().getStringExtra("chatId");
        }

        List<String> ids = Arrays.asList(user.getUid(), receiverId);
        Collections.sort(ids);
        String roomId = ids.get(0) + ids.get(1);
        databaseReference = FirebaseDatabase.getInstance().getReference("chats").child(roomId);

        binding.floatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        receiveMessages();
    }

    private void sendMessage() {
        String msg = binding.messageBox.getEditText().getText().toString();
        String timestamp = new SimpleDateFormat("dd-MM-yy HH:mm a").format(Calendar.getInstance().getTime());
        if (msg.trim().length() > 0) {
            String msgId = databaseReference.push().getKey();
            Message chat = new Message(msgId, user.getUid(), msg, timestamp);
            chatAdapter.add(chat);
            databaseReference.child(msgId).setValue(chat);
            binding.messageBox.getEditText().setText("");
        }
    }

    private void receiveMessages() {
        messageEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatAdapter.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Message message = snap.getValue(Message.class);
                    chatAdapter.add(message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        databaseReference.addValueEventListener(messageEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageEventListener != null) {
            databaseReference.removeEventListener(messageEventListener);
        }
    }
}