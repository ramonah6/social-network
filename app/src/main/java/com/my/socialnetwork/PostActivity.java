package com.my.socialnetwork;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PostActivity extends AppCompatActivity {
    private Toolbar mToolbar;

    private ImageButton SelectPostImage;
    private Button UpdatePostButton;
    private EditText PostDescription;

    private Uri ImageUri;
    private String Description;
    private static final int Gallery_Pick = 1;

    private StorageReference PostImagesRefrence;

    private String saveCurrentDate, saveCurrentTime, postRandomName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        PostImagesRefrence = FirebaseStorage.getInstance().getReference();

        SelectPostImage = (ImageButton)findViewById(R.id.select_post_image);
        UpdatePostButton = (Button)findViewById(R.id.update_post_button);
        PostDescription = (EditText)findViewById(R.id.post_description);

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
                    Toast.makeText(PostActivity.this, "image uploaded successfully to storage...", Toast.LENGTH_SHORT).show();
                }
                else {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error occured: " + message, Toast.LENGTH_SHORT).show();
                }
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
