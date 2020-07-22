package com.example.socialgood.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialgood.PostsAdapter;
import com.example.socialgood.R;
import com.example.socialgood.activities.MainActivity;
import com.example.socialgood.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements EditText.OnEditorActionListener{

    public static final String TAG = FeedFragment.class.getSimpleName();
    public RecyclerView rvPosts;
    //public SwipeRefreshLayout swipeContainer;
    public EditText etSearchQuery;
    public List<Post> posts;
    public PostsAdapter adapter;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etSearchQuery = view.findViewById(R.id.etSearchQuery);
        etSearchQuery.setOnEditorActionListener(this);

        posts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), getFragmentManager(), posts);
        rvPosts = view.findViewById(R.id.rvPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvPosts.setAdapter(adapter);

        rvPosts.setLayoutManager(linearLayoutManager);

    }


    public void queryPosts(final String text){
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

                // For each post in db, check if categories list contains text entered in etSearchQuery
                for(Post post: objects){
                    List<String> categories = post.getListCategories();
                    Log.i(TAG, "getting post...." + post.getCategoriesDisplay());

                    for (String cat: categories) {
                        if(cat.equalsIgnoreCase(text)){
                            posts.add(post);
                            Log.i(TAG, "Post has category!");
                        }
                    }
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String text = textView.getText().toString();
            queryPosts(text);
            getActivity().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            //Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}