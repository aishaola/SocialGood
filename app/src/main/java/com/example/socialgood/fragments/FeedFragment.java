package com.example.socialgood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public RecyclerView rvPosts;
    public SwipeRefreshLayout swipeContainer;
    public List<ParseObject> posts;
    public PostsAdapter adapter;
    public List<String> userCategories;
    public List<ParseUser> following;

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
        userCategories = ParseUserSocial.getCurrentUser().getCategoriesList();
        following = ParseUserSocial.getCurrentUser().getProfilesFollowing();
        following.add(ParseUser.getCurrentUser());
        posts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), getFragmentManager(), posts);
        rvPosts = view.findViewById(R.id.rvPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        // *** Swipe to REFRESH feature implemented here: ***
        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // *** Swipe to REFRESH feature ends here: ***



        rvPosts.setAdapter(adapter);
        queryPosts();

        rvPosts.setLayoutManager(linearLayoutManager);
    }

    public void queryPosts(){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        /*
        List<String> categories = ParseUserSocial.getCurrentUser().getCategoriesList();
        List<ParseUser> following = ParseUserSocial.getCurrentUser().getProfilesFollowing();
        following.add(ParseUser.getCurrentUser());
        q1.whereContainedIn(Post.KEY_USER, following);

        List<ParseQuery<Post>> queries = new ArrayList<>();
        queries.add(q1);
        for(String cat : categories){
            ParseQuery<Post> catQuery = ParseQuery.getQuery(Post.class);
            catQuery.whereEqualTo(Post.KEY_CATEGORIES, cat);
            queries.add(catQuery);
        }
        ParseQuery<Post> query = ParseQuery.or(queries);*/

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

        query.setLimit(20);
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
                    if(postMatchesFollowing(post)) {
                        posts.add(post);
                    } else if (!post.isPostReshare() && postMatchesCategories(post)){
                        post.setUserFollowsCat(true);
                        posts.add(post);
                    }
                }
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