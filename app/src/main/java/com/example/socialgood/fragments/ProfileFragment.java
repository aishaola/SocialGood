package com.example.socialgood.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.socialgood.activities.EditProfileActivity;
import com.example.socialgood.activities.IntroActivity;
import com.example.socialgood.R;
import com.example.socialgood.models.ParseUserSocial;
import com.example.socialgood.models.Post;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends FeedFragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();
    Button btnLogout;
    Button btnEditProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(ParseUser user) {
        // Required empty public constructor
    }

    @Override
    public void queryPosts(){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_CREATED_AT);
        query.include(Post.KEY_CAPTION);
        query.include(Post.KEY_CATEGORIES);
        query.include(Post.KEY_IMAGE);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        query.setLimit(5);
        query.addDescendingOrder(Post.KEY_CREATED_AT);



        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Network error: Issue with getting posts!", e);
                    return;
                }
                adapter.clear();
                for(Post post: objects){
                    posts.add(post);
                }
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
        adapter.notifyDataSetChanged();
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });
    }

    private void logout() {
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "logout(): Error logging out!!", e);
                }
                Log.i(TAG, "logout(): Successfully logged out!");
                getContext().startActivity(new Intent(getContext(), IntroActivity.class));
            }
        });
    }
    */

}