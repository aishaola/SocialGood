package com.example.socialgood.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.socialgood.EndlessRecyclerViewScrollListener;
import com.example.socialgood.ParseApplication;
import com.example.socialgood.adapters.PostsAdapter;
import com.example.socialgood.R;
import com.example.socialgood.models.Donation;
import com.example.socialgood.models.Fundraiser;
import com.example.socialgood.models.ParseUserSocial;
import com.example.socialgood.models.Post;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {

    public static final String TAG = FeedFragment.class.getSimpleName();
    public RelativeLayout rlDonate;
    public Button btnDonate;
    public ImageView ivX;
    public RelativeLayout rlEmpty;
    public RecyclerView rvPosts;
    public SwipeRefreshLayout swipeContainer;
    public List<ParseObject> posts;
    public PostsAdapter adapter;
    public List<String> userCategories;
    public List<ParseUser> following;
    public ProgressDialog pd;
    private EndlessRecyclerViewScrollListener scrollListener;

    public FeedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Feed");
        rlDonate = view.findViewById(R.id.rlDonate);
        rlEmpty = view.findViewById(R.id.rlEmptyFeed);
        btnDonate = view.findViewById(R.id.btnDonate);
        ivX = view.findViewById(R.id.ivX);

        rlEmpty.setVisibility(View.GONE);

        final ParseApplication pa = (ParseApplication) getActivity().getApplication();
        if(pa.getUserHasClicked())
            rlDonate.setVisibility(View.GONE);

        btnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goDonateFragment();
                pa.setUserHasClicked();
            }
        });

        ivX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                YoYo.with(Techniques.FadeOut).duration(150).playOn(rlDonate);
                pa.setUserHasClicked();
            }
        });

        userCategories = ParseUserSocial.getCurrentUser().getCategoriesList();
        following = ParseUserSocial.getCurrentUser().getProfilesFollowing();
        following.add(ParseUser.getCurrentUser());
        posts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), getFragmentManager(), posts);
        rvPosts = view.findViewById(R.id.rvPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // *** Endless recycler view implemented here
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                queryPosts(page, false);
            }
        };


        // *** Swipe to REFRESH feature implemented here: ***
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts(0, true);
                scrollListener.resetState();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // *** Swipe to REFRESH feature ends here: ***

        // Show Progress dialog when posts are first loading
        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        rvPosts.setAdapter(adapter);
        queryPosts(0, false);

        rvPosts.addOnScrollListener(scrollListener);
        rvPosts.setLayoutManager(linearLayoutManager);

    }

    private void goDonateFragment() {
        // Create new fragment and transaction
        Fragment newFragment = new DonateAppFragment();
        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.frame_holder, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    public void queryPosts(int page, final boolean isRefresh){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);
        query.include(Post.KEY_CREATED_AT);
        query.include(Post.KEY_CAPTION);
        query.include(Post.KEY_CATEGORIES);
        query.include(Post.KEY_IMAGE);
        query.include(Post.KEY_TYPE);
        query.include(Post.KEY_DONATION);
        query.include(Post.KEY_LINK_LIST);

        query.include(Post.KEY_DONATION + "." + Donation.KEY_FUNDRAISER);
        query.include(Post.KEY_DONATION + "." + Donation.KEY_FUNDRAISER + "." + Fundraiser.KEY_TITLE);

        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_CAPTION);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_USER);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_IMAGE);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_CAPTION);
        query.include(Post.KEY_POST_RESHARED + "." + Post.KEY_CREATED_AT);

        query.setLimit(10);
        query.setSkip(page * 10);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null){
                    pd.dismiss();
                    Toast.makeText(getContext(), "Network error: Issue with getting posts!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Network error: Issue with getting posts!", e);
                    return;
                }
                if(isRefresh)
                    adapter.clear();
                for(Post post: objects) {
                    if (postMatchesFollowing(post)) {
                        posts.add(post);
                    } else if (!post.isPostReshare() && postMatchesCategories(post)) {
                        post.setUserFollowsCat(true);
                        posts.add(post);
                    }
                }
                if(posts.size() > 0)
                    rlEmpty.setVisibility(View.GONE);
                pd.dismiss();
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private boolean postMatchesCategories(Post post) {
        List<String> postCat = post.getListCategories();
        for(String cat: userCategories){
            if(postCat.contains(cat))
                return true;
        }
        return false;
    }

    private boolean postMatchesFollowing(Post post){
        ParseUser postUser = post.getUser();
        for(ParseUser user: following){
            if(user.getObjectId().equals(postUser.getObjectId()))
                return true;
        }
        return false;
    }

}
