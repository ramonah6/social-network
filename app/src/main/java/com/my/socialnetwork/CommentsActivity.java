package com.my.socialnetwork;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.common.images.internal.PostProcessedResourceCache;

public class CommentsActivity extends AppCompatActivity {

    private ImageButton PostCommentButton;
    private EditText CommentInputText;
    private RecyclerView CommentListsts;

    private String Post_Key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();

        CommentListsts = (RecyclerView) findViewById(R.id.comment_list);
        CommentListsts.setHasFixedSize(true);
        LinearLayoutManager linearlayoutManager = new LinearLayoutManager(this);
        linearlayoutManager.setReverseLayout(true);
        linearlayoutManager.setStackFromEnd(true);
        CommentListsts.setLayoutManager(linearlayoutManager);

        CommentInputText = (EditText) findViewById(R.id.comment_input);
        PostCommentButton = (ImageButton)findViewById(R.id.post_comment_button);
    }
}