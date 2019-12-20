package com.my.socialnetwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ProgressDialog loadingBar;

    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;

    private Uri ImageUri;
    private String Description;
    private static final int Gallery_Pick = 1;

    private StorageReference PostImagesRefrence;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;
    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl, current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getUid();

        PostImagesRefrence = FirebaseStorage.getInstance().getReference();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        SelectPostImage = (ImageButton)findViewById(R.id.select_post_image);
        UpdatePostButton = (Button)findViewById(R.id.update_post_button);
        PostDescription = (EditText)findViewById(R.id.click_post_description);
        loadingBar = new ProgressDialog(this);

        mToolbar = (Toolbar)findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");
        
        SelectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenGallery();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidatePostInfo();
            }
        });
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    private void ValidatePostInfo() {
        Description = PostDescription.getText().toString();

        if(ImageUri == null) {
            Toast.makeText(this, "Please select post image...",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Please say something about your image...",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Add new post");
            loadingBar.setMessage("Please wait, while we updating your new post...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            StoringImageToFirebaseStorage();
        }
    }

    private void StoringImageToFirebaseStorage() {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime= currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = PostImagesRefrence.child("Post Images").child(ImageUri.getLastPathSegment()+postRandomName+".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    downloadUrl = task.getResult().toString();
                    Toast.makeText(PostActivity.this, "image uploaded successfully to storage...", Toast.LENGTH_SHORT).show();
                    
                    SavingPostInformationToDataBase();
                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavingPostInformationToDataBase()
    {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists())
               {
                   String userFullName = dataSnapshot.child("fullname").getValue().toString();
                   String userProfileImage = dataSnapshot.child("profileimage").getValue().toString();

                   HashMap postMap = new HashMap();
                   postMap.put("uid", current_user_id);
                   postMap.put("data", saveCurrentDate);
                   postMap.put("time", saveCurrentTime);
                   postMap.put("description", Description);
                   postMap.put("postimage", downloadUrl);
                   postMap.put("profileimage", userProfileImage);
                   postMap.put("fullname", userFullName);
                   PostsRef.child(current_user_id + postRandomName).updateChildren(postMap)
                           .addOnCompleteListener(new OnCompleteListener() {
                               @Override
                               public void onComplete(@NonNull Task task) {
                                   if(task.isSuccessful())
                                   {
                                       SendUserToMainActivity();
                                       Toast.makeText(PostActivity.this, "New Post is updated successful...", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                   }
                                   else
                                   {
                                       String message = task.getException().getMessage();
                                       Toast.makeText(PostActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                   }
                               }
                           });
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data!=null) {
            ImageUri = data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home) {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity() {
        Intent mainInent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainInent);
    }
}
