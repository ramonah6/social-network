package com.my.socialnetwork;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfileActivity extends AppCompatActivity {
    private TextView userName, userProfName, userStatus, userCountry, userGender,userDOB, userRelation;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef, FriendsRef, PostsRef;
    private FirebaseAuth mAuth;
    private Button MyPosts, MyFriends;

    private String currentUserid;
    private int countFriends = 0, countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserid = mAuth.getCurrentUser().getUid();
        profileUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserid);
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        userName = (TextView)findViewById(R.id.my_username);
        userProfName= (TextView)findViewById(R.id.my_full_name);
        userStatus = (TextView)findViewById(R.id.my_profile_status);
        userCountry = (TextView)findViewById(R.id.my_country);
        userGender = (TextView)findViewById(R.id.my_gender);
        userRelation = (TextView)findViewById(R.id.my_relationship);
        userDOB = (TextView)findViewById(R.id.my_dob);
        userProfileImage = (CircleImageView)findViewById(R.id.my_profile_pic);
        MyFriends = (Button) findViewById(R.id.my_friends_button);
        MyPosts = (Button) findViewById(R.id.my_post_button);

        MyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUsertoFriendsActivity();
            }
        });

        MyPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUsertoMyPostsActivity();
            }
        });

        PostsRef.orderByChild("uid")
                .startAt(currentUserid).endAt(currentUserid + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            countPosts = (int) dataSnapshot.getChildrenCount();
                            MyPosts.setText(Integer.toString(countPosts) + "  Posts");
                        }
                        else
                        {
                            MyPosts.setText("0  Posts");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        FriendsRef.child(currentUserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    countFriends = (int) dataSnapshot.getChildrenCount();
                    MyFriends.setText(Integer.toString(countFriends)+"  Friends");
                }
                else
                {
                    MyFriends.setText("0  Friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileUserRef.addValueEventListener(new ValueEventListener() {
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

                    Picasso.with(MyProfileActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfileImage);
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

    private void SendUsertoFriendsActivity() {
        Intent settingsIntent =  new Intent(MyProfileActivity.this, FriendsActivity.class);
        startActivity(settingsIntent);
    }

    private void SendUsertoMyPostsActivity() {
        Intent settingsIntent =  new Intent(MyProfileActivity.this, MyPostsActivity.class);
        startActivity(settingsIntent);
    }
}
