package com.my.socialnetwork;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private ImageButton SearchButton;
//    private EditText SearchInputText;
    private SearchView SearchInputText;

    private RecyclerView SearchResultList;
    private DatabaseReference allUsersDatabaseRef;
    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    ArrayList<FindFriends> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        allUsersDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mToolBar = (Toolbar)findViewById(R.id.find_friends_appbar_layout);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        SearchResultList = (RecyclerView)findViewById(R.id.search_result_list);
        SearchResultList.setHasFixedSize(true);
        SearchResultList.setLayoutManager(new LinearLayoutManager(this));

//        SearchButton = (ImageButton)findViewById(R.id.search_people_friends_button);
//        SearchInputText = (EditText)findViewById(R.id.search_box_input);
        SearchInputText = (SearchView) findViewById(R.id.search_box_input);

        SearchPeopleFriends("ariana");
//        SearchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String searchBoxInput = SearchInputText.getText().toString();
//                SearchPeopleFriends(searchBoxInput);
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(allUsersDatabaseRef != null)
        {
            allUsersDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        list = new ArrayList<FindFriends>();
                        for(DataSnapshot ds: dataSnapshot.getChildren())
                        {
                            FindFriends ff = new FindFriends(ds.child("profileimage").getValue().toString(),ds.child("fullname").getValue().toString(),ds.child("status").getValue().toString(),ds.getKey());
                            list.add(ff);
                        }
                        AdapterClass adapterClass = new AdapterClass(list, getApplicationContext());
                        SearchResultList.setAdapter(adapterClass);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        if(SearchInputText != null)
        {
            SearchInputText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }

        firebaseRecyclerAdapter.startListening();

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseRecyclerAdapter.stopListening();
    }

    private void search(String str)
    {
        ArrayList<FindFriends> myList = new ArrayList<>();
        for(FindFriends ob: list)
        {
            if(ob.fullname.toLowerCase().contains(str.toLowerCase()))
            {
                myList.add(ob);
            }
        }
        AdapterClass adapterClass = new AdapterClass(myList, getApplicationContext());
        SearchResultList.setAdapter(adapterClass);
    }

    private void SearchPeopleFriends(String searchBoxInput)
    {
        Query searchPeopleandFriendQuery = allUsersDatabaseRef.orderByChild("fullname")
                .startAt(searchBoxInput).endAt(searchBoxInput + "\uf8ff");

        FirebaseRecyclerOptions<FindFriends> options =
                new FirebaseRecyclerOptions.Builder<FindFriends>()
                        .setQuery(searchPeopleandFriendQuery, FindFriends.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder holder, final int position, @NonNull FindFriends model) {
                holder.setFullname(model.getFullname());
                holder.setStatus(model.getStatus());
                holder.setProfileimage(getApplicationContext(), model.getProfileimage());
                //Toast.makeText(FindFriendsActivity.this,model.getFullname(), Toast.LENGTH_LONG ).show();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_user_id = getRef(position).getKey();

                        Intent profileIntent = new Intent (FindFriendsActivity.this, PersonProfileActivity.class);
                        profileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(profileIntent);
                    }
                });


//                holder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        String visit_user_id = getRef(position).getKey();
//
//                        Intent profileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
//                        profileIntent.putExtra("visit_user_id", visit_user_id);
//                        startActivity(profileIntent);
//                    }
//                });
            }

            @Override
            public FindFriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.all_users_display_layout, parent, false);

                return new FindFriendsViewHolder(view);
            }
        };

        SearchResultList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public FindFriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileimage(Context ctx, String profileiamge) {
            CircleImageView myImage = (CircleImageView) mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(ctx).load(profileiamge).placeholder(R.drawable.profile).into(myImage);
        }

        public void setFullname(String fullname) {
            TextView myName = (TextView) mView.findViewById(R.id.all_users_profile_full_name);
            myName.setText(fullname);
        }

        public void setStatus(String status) {
            TextView myStatus = (TextView) mView.findViewById(R.id.all_users_status);
            myStatus.setText(status);
        }

    }

}

