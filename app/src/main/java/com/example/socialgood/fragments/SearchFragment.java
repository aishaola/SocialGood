package com.example.socialgood.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.socialgood.adapters.PostsAdapter;
import com.example.socialgood.R;
import com.example.socialgood.models.ParseUserSocial;
import com.example.socialgood.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.example.socialgood.SocialGoodHelpers.categoryExists;
import static com.example.socialgood.SocialGoodHelpers.hideKeyboard;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements EditText.OnEditorActionListener{

    public static final String TAG = FeedFragment.class.getSimpleName();
    public RecyclerView rvPosts;
    public RecyclerView rvProfiles;
    //public SwipeRefreshLayout swipeContainer;
    public EditText etSearchQuery;
    public TextView tvEmptyFeedText;
    public List<ParseObject> posts;
    public List<ParseUser> profiles;
    public PostsAdapter adapter;
    public RelativeLayout rlEmpty;
    public ProgressDialog pd;


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
        getActivity().setTitle("Search");

        rlEmpty = view.findViewById(R.id.rlEmptyFeed);
        tvEmptyFeedText = view.findViewById(R.id.tvEmptyFeedText);
        etSearchQuery = view.findViewById(R.id.etSearchQuery);
        etSearchQuery.setOnEditorActionListener(this);

        posts = new ArrayList<>();
        // profiles = new ArrayList<>();

        adapter = new PostsAdapter(getContext(), getFragmentManager(), posts);

        rvPosts = view.findViewById(R.id.rvPosts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(linearLayoutManager);


    }


    public void queryPosts(final String textQuery){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_CREATED_AT);
        query.include(Post.KEY_CAPTION);
        query.include(Post.KEY_CATEGORIES);
        query.include(Post.KEY_IMAGE);
        query.include(Post.KEY_TYPE);
        query.include(Post.KEY_LINK_LIST);
        query.setLimit(20);
        query.whereDoesNotExist(Post.KEY_DONATION);
        query.whereDoesNotExist(Post.KEY_POST_RESHARED);
        query.addDescendingOrder(Post.KEY_CREATED_AT);

        pd = new ProgressDialog(getContext());
        pd.setMessage("Loading...");
        pd.setCancelable(false);
        pd.show();

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null){
                    pd.dismiss();
                    Log.e(TAG, "Network error: Issue with getting posts!", e);
                    return;
                }
                adapter.clear();

                // For each post in db, check if categories list contains text entered in etSearchQuery
                for(Post post: objects){
                    List<String> categories = post.getListCategories();
                    Log.i(TAG, "getting post...." + post.getCategoriesDisplay());

                    for (String cat: categories) {
                        cat = cat.toLowerCase().trim();
                        if(compareStringsAlgorithm(cat, textQuery, 3)){
                            posts.add(post);
                            Log.i(TAG, "Post has category!");
                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                queryUsers(textQuery);
            }
        });

    }

    public void queryUsers(final String textQuery){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.setLimit(15);
        query.include(ParseUserSocial.KEY_PROFILE_PIC);
        query.include(ParseUserSocial.KEY_CATEGORIES);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {

                if(e != null){
                    Log.e(TAG, "done: ", e);
                    pd.dismiss();
                    return;
                }
                // For each post in db, check if categories list contains text entered in etSearchQuery
                for(ParseUser userFound: objects){
                    ParseUserSocial userFoundSocial = new ParseUserSocial(userFound);
                    List<String> categories = userFoundSocial.getCategoriesList();

                    for (String cat: categories) {
                        cat = cat.toLowerCase().trim();
                        if(compareStringsAlgorithm(cat, textQuery, 3)){
                            posts.add(userFound);
                            Log.i(TAG, "Post has category!");
                            break;
                        }
                    }
                }

                if(posts.size() > 0){
                    rlEmpty.setVisibility(View.GONE);
                } else {
                    rlEmpty.setVisibility(View.VISIBLE);
                    tvEmptyFeedText.setText("No posts or profiles found. Try another keyword!");
                }
                adapter.notifyDataSetChanged();
                pd.dismiss();
            }
        });
        adapter.notifyDataSetChanged();
    }

    private boolean compareStringsAlgorithm(String cat, String textQuery, int errorMax) {
        String[] categoryWordsArray = cat.split(" ");
        String[] queryWordsArray = textQuery.split(" ");
        String catNoSpaces = cat.replaceAll("\\s","");
        String queryNoSpaces = textQuery.replaceAll("\\s","");
        char[] catChars = catNoSpaces.toCharArray();
        char[] queryChars = queryNoSpaces.toCharArray();

        // 1. return true if query contains the category
        if(catNoSpaces.contains(queryNoSpaces) || queryNoSpaces.contains(catNoSpaces))
            return true;

        // 2. return true if the first word matches the first word of category and query match any other category better than show OR
        if(!categoryExists(textQuery) && categoryWordsArray[0].equals(queryWordsArray[0]))
            return true;

        // 3. return true if query is just a couple of letters off (remove the spaces) BUT return false if it's consecutive
        int errorCount = 0;
        int consecErrorCount = 0;
        for (int i = 0; i < Math.min(catChars.length, queryChars.length) && errorCount < errorMax; i++) {
            if(catChars[i] != queryChars[i]){
                errorCount++;
                // consecErrorCount++;
            } else{
                // consecErrorCount = 0;
            }
        }
        if(errorCount < errorMax)
            return true;


        return false;
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            String text = textView.getText().toString().toLowerCase().trim();
            queryPosts(text);
            hideKeyboard((Activity) getContext());
            //Toast.makeText(getContext(), toRomanNumeral(17), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}