package com.my.socialnetwork;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userStatus, userCountry, userGender,userDOB, userRelation;
    private CircleImageView userProfileImage;
    private Button SendFriendRquestButton, DeclineFriendRequestButton;

    private DatabaseReference profileUserRef, UsersRef;
    private FirebaseAuth mAuth;
    private String senderUserid, receviverUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        senderUserid = mAuth.getCurrentUser().getUid();

        receviverUserId = getIntent().getExtras().get("visit)user_id").toString();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        intializeFields();

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus= dataSnapshot.child("status").getValue().toString();
                    String myDOB= dataSnapshot.child("dob").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationStatus = dataSnapshot.child("relationshipstatus").getValue().toString();

                    Picasso.with(PersonProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
                    userName.setText("@" + myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText("DOB: " + myDOB);
                    userCountry.setText("Country: " + myCountry);
                    userGender.setText("Gender: " + myGender);
                    userRelation.setText("Relationship: " + myRelationStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void intializeFields() {
        userName = (TextView)findViewById(R.id.person_username);
        userProfName= (TextView)findViewById(R.id.person_full_name);
        userStatus = (TextView)findViewById(R.id.person_profile_status);
        userCountry = (TextView)findViewById(R.id.person_country);
        userGender = (TextView)findViewById(R.id.person_gender);
        userRelation = (TextView)findViewById(R.id.person_relationship);
        userDOB = (TextView)findViewById(R.id.person_dob);
        userProfileImage = (CircleImageView)findViewById(R.id.person_profile_pic);
        SendFriendRquestButton = (Button)findViewById(R.id.person_send_friend_request_btn);
        DeclineFriendRequestButton = (Button)findViewById(R.id.person_decline_friend_request);
    }
}
