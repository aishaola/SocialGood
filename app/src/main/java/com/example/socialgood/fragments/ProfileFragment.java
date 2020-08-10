package com.example.socialgood.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.socialgood.activities.EditProfileActivity;
import com.example.socialgood.activities.IntroActivity;
import com.example.socialgood.R;
import com.example.socialgood.models.Donation;
import com.example.socialgood.models.Fundraiser;
import com.example.socialgood.models.ParseUserSocial;
import com.example.socialgood.models.Post;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends FeedFragment {

    public static final String TAG = ProfileFragment.class.getSimpleName();
    ParseUser profileUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(ParseUser user) {
        profileUser = user;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.clearAllButHeader(profileUser);
    }


    @Override
    public void queryPosts(int page, final boolean isRefresh){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_CREATED_AT);
        query.include(Post.KEY_CAPTION);
        query.include(Post.KEY_CATEGORIES);
        query.include(Post.KEY_IMAGE);
        query.include(Post.KEY_TYPE);
        query.include(Post.KEY_DONATION);

        query.include(Post.KEY_DONATION + "." + Donation.KEY_FUNDRAISER);
        query.include(Post.KEY_DONATION + "." + Donation.KEY_FUNDRAISER + "." + Fundraiser.KEY_TITLE);

        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_CAPTION);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_USER);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_IMAGE);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_CAPTION);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_CREATED_AT);

        query.whereEqualTo(Post.KEY_USER, profileUser);
        query.setLimit(10);
        query.setSkip(page * 10);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Network error: Issue with getting posts!", e);
                    return;
                }
                if(isRefresh)
                    adapter.clearAllButHeader(profileUser);
                for(Post post: objects){
                    posts.add(post);
                }
                if(posts.size()>1)
                    rlEmpty.setVisibility(View.GONE);
                else
                    rlEmpty.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
                pd.dismiss();
            }
        });
        adapter.notifyDataSetChanged();
    }

}