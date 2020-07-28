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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.socialgood.activities.EditProfileActivity;
import com.example.socialgood.activities.IntroActivity;
import com.example.socialgood.R;
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
    Button btnLogout;
    Button btnEditProfile;
    Button btnFollow;
    TextView tvUsername;
    TextView tvCategories;
    ImageView ivProfilePic;
    ParseUser profileUser;
    View buttons;
    View buttonsForOtherProfiles;
    boolean isCurrentUser;
    boolean isFollowing;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public ProfileFragment(ParseUser user) {
        if(user == null)
            profileUser = ParseUser.getCurrentUser();
        profileUser = user;
        isCurrentUser = user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
        isFollowing = ParseUserSocial.getCurrentUser().userIsFollowing(user);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Profile Page");

        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvCategories = view.findViewById(R.id.tvCategories);
        buttons = view.findViewById(R.id.buttons);
        buttonsForOtherProfiles = view.findViewById(R.id.buttonsForOtherProfiles);
        btnFollow = view.findViewById(R.id.btnFollow);
        
        ParseUserSocial userSocial = new ParseUserSocial(profileUser);

        // Initialize follow button
        if(!isCurrentUser)
            updateFollowButton();

        // fill in username and category textViews
        tvUsername.setText(profileUser.getUsername());
        tvCategories.setText(userSocial.getCategories());

        // hide edit profile buttons if profile isn't current User's
        if(!isCurrentUser)
            buttons.setVisibility(View.GONE);
        else
            buttonsForOtherProfiles.setVisibility(View.GONE);

        // log out when logout button pressed
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });

        // launch edit profile when edit profile button pressed
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), EditProfileActivity.class));
            }
        });

        // load user's profile pic or use default
        ParseFile image = userSocial.getProfilePic();
        if(image != null){
            Glide.with(getContext()).load(image.getUrl()).into(ivProfilePic);
        } else {
            ivProfilePic.setImageResource(R.drawable.action_profile);
        }

    }

    public void updateFollowButton(){
        if(isFollowing){
            btnFollow.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            btnFollow.setText("Unfollow");
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unfollowUser();
                }
            });
        } else {
            btnFollow.setTextColor(getResources().getColor(R.color.design_default_color_primary));
            btnFollow.setText("Follow");
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    followUser();
                }
            });
        }

    }

    private void followUser() {
        final ParseUser currUser = ParseUser.getCurrentUser();
        ParseUserSocial helper = new ParseUserSocial(currUser);
        helper.addProfileFollowing(profileUser);
        currUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error saving follow", e);
                    return;
                }
                Log.i(TAG, "Profile Followed: " + currUser.getUsername() + " followed " + profileUser.getUsername() );
                isFollowing = !isFollowing;
                updateFollowButton();
            }
        });
    }

    private void unfollowUser() {
        final ParseUser currUser = ParseUser.getCurrentUser();
        ParseUserSocial helper = new ParseUserSocial(currUser);
        helper.removeProfileFollowing(profileUser);
        currUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error saving follow", e);
                    return;
                }
                Log.i(TAG, "Profile Followed: " + currUser.getUsername() + " unfollowed " + profileUser.getUsername() );
                isFollowing = !isFollowing;
                updateFollowButton();
            }
        });
    }


    @Override
    public void queryPosts(){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_CREATED_AT);
        query.include(Post.KEY_CAPTION);
        query.include(Post.KEY_CATEGORIES);
        query.include(Post.KEY_IMAGE);
        query.include(Post.KEY_IS_RESHARE);

        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_CAPTION);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_USER);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_IMAGE);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_CAPTION);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_CREATED_AT);

        query.whereEqualTo(Post.KEY_USER, profileUser);
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
                    if(post.isPostReshare()){
                        Post postReshared = post.getPostReshared();
                        postReshared.setUserReshared(profileUser);
                        posts.add(postReshared);
                    } else {
                        posts.add(post);
                    }

                }
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
        adapter.notifyDataSetChanged();
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

}