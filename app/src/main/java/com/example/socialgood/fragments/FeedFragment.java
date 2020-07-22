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

import com.example.socialgood.EndlessRecyclerViewScrollListener;
import com.example.socialgood.PostsAdapter;
import com.example.socialgood.R;
import com.example.socialgood.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

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
    public List<Post> posts;
    public PostsAdapter adapter;

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
        query.include(Post.KEY_USER);
        query.include(Post.KEY_CREATED_AT);
        query.include(Post.KEY_CAPTION);
        query.include(Post.KEY_CATEGORIES);
        query.include(Post.KEY_IMAGE);
        query.setLimit(10);
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
}