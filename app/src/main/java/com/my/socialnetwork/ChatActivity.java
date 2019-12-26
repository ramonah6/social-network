package com.my.socialnetwork;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar ChatToolBar;
    private ImageButton SendMessageButton, SendImageFileButton;
    private EditText userMessageInput;
    private RecyclerView userMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;

    private String messageReceiverID, messageReceiverName, messageSenderID, saveCurrentDate, saveCurrentTime;

    private TextView receiverName, userLastSeen;
    private CircleImageView receiverProfileImage;

    private DatabaseReference RootRef,UserRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();

        RootRef = FirebaseDatabase.getInstance().getReference();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("userName").toString();

        InitializeFields();

        DisplayReceiverInfo();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage();
            }
        });

        FetchMessages();
    }

    private void FetchMessages() {
        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            messagesList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage() {
        updateUserStatus("online");

        String messageText = userMessageInput.getText().toString();

        if (TextUtils.isEmpty(messageText)) {
            Toast.makeText(this, "Please type a messge first...", Toast.LENGTH_SHORT).show();
        }
        else {
            String message_sender_ref = "Messages/" + messageSenderID + "/" + messageReceiverID;
            String message_receiver_ref = "Messages/" + messageReceiverID + "/" + messageSenderID;

            DatabaseReference user_message_key= RootRef.child("Messages").child(messageSenderID)
                    .child(messageReceiverID).push();

            String message_push_id = user_message_key.getKey();

            final Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
            saveCurrentTime= currentTime.format(calForTime.getTime());

            Map messageTestBody = new HashMap();
            messageTestBody.put("message", messageText);
            messageTestBody.put("time", saveCurrentTime);
            messageTestBody.put("date", saveCurrentDate);
            messageTestBody.put("type", "text");
            messageTestBody.put("from", messageSenderID);

            Map messageBodyDetials = new HashMap();
            messageBodyDetials.put(message_sender_ref + "/" + message_push_id, messageTestBody);
            messageBodyDetials.put(message_receiver_ref + "/" + message_push_id, messageTestBody);

            RootRef.updateChildren(messageBodyDetials).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");
                    }
                    else {
                        String message = task.getException().getMessage();
                        Toast.makeText(ChatActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        userMessageInput.setText("");
                    }

                }
            });
        }
    }

    private void DisplayReceiverInfo() {
        receiverName.setText(messageReceiverName);

        RootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String profileImage = dataSnapshot.child("profileimage").getValue().toString();

                    if(dataSnapshot.child("userState").exists()) {
                        final String type = dataSnapshot.child("userState").child("type").getValue().toString();
                        final String lastDate = dataSnapshot.child("userState").child("date").getValue().toString();
                        final String lastTime = dataSnapshot.child("userState").child("time").getValue().toString();

                        if (type.equals("online")) {
                            userLastSeen.setText("online");
                        } else {
                            userLastSeen.setText("last seen: " + lastTime + "  " + lastDate);
                        }
                    }
                    Picasso.with(ChatActivity.this).load(profileImage).placeholder(R.drawable.profile).into(receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitializeFields()
    {
        ChatToolBar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutinflater = (LayoutInflater)this.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
        View action_bar_view = layoutinflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        receiverName = (TextView)findViewById(R.id.custom_profile_name);
        userLastSeen = (TextView) findViewById(R.id.custom_user_last_seen);
        receiverProfileImage = (CircleImageView)findViewById(R.id.custom_profile_image);

        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        SendImageFileButton = (ImageButton) findViewById(R.id.send_image_file_button);
        userMessageInput = (EditText) findViewById(R.id.input_message);

        messageAdapter = new MessageAdapter((messagesList));
        userMessageList = (RecyclerView) findViewById( (R.id.message_list_users));
        linearLayoutManager = new LinearLayoutManager(this);
        userMessageList.setHasFixedSize(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messageAdapter);
    }

    public  void updateUserStatus(String state)
    {
        String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime= new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        Map currentMap = new HashMap<>();
        currentMap.put("time", saveCurrentTime);
        currentMap.put("date", saveCurrentDate);
        currentMap.put("type", state);

        UserRef.child(messageSenderID).child("userState")
                .updateChildren(currentMap);

    }
}
