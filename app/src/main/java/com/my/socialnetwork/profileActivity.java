package com.my.socialnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class profileActivity extends AppCompatActivity {

    private TextView userName, userProfName, userStatus, userCountry, userGender,userDOB, userRelation;
    private CircleImageView userProfileImage;

    private DatabaseReference profileUserRef;
    private FirebaseAuth mAuth;

    
    private String currentUserid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        userName = (TextView)findViewById(R.id.my_username);
        userProfName= (TextView)findViewById(R.id.my_profile_full_name);
        userStatus = (TextView)findViewById(R.id.my_profile_status);
        userCountry = (TextView)findViewById(R.id.my_country);
        userGender = (TextView)findViewById(R.id.my_gender);
        userRelation = (TextView)findViewById(R.id.my_relationship);
        userDOB = (TextView)findViewById(R.id.my_dob);
        userProfileImage = (CircleImageView)findViewById(R.id.my_profile_pic);
    }
}
