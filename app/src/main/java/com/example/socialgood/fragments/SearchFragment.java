package com.example.socialgood.fragments;

import android.app.Activity;
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
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialgood.adapters.PostsAdapter;
import com.example.socialgood.R;
import com.example.socialgood.adapters.ProfilesAdapter;
import com.example.socialgood.models.ParseUserSocial;
import com.example.socialgood.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
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
    public List<Post> posts;
    public List<ParseUser> profiles;
    public PostsAdapter adapter;
    public ProfilesAdapter profilesAdapter;


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

        etSearchQuery = view.findViewById(R.id.etSearchQuery);
        etSearchQuery.setOnEditorActionListener(this);

        posts = new ArrayList<>();
        profiles = new ArrayList<>();

        adapter = new PostsAdapter(getContext(), getFragmentManager(), posts);
        profilesAdapter = new ProfilesAdapter(getContext(), getFragmentManager(), profiles);

        rvPosts = view.findViewById(R.id.rvPosts);
        rvProfiles = view.findViewById(R.id.rvProfiles);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(linearLayoutManager);

        rvProfiles.setAdapter(profilesAdapter);
        rvProfiles.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    public void queryPosts(final String textQuery){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_CREATED_AT);
        query.include(Post.KEY_CAPTION);
        query.include(Post.KEY_CATEGORIES);
        query.include(Post.KEY_IMAGE);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        query.whereDoesNotExist(Post.KEY_TYPE);



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
                        cat = cat.toLowerCase().trim();
                        if(compareStringsAlgorithm(cat, textQuery, 4)){
                            posts.add(post);
                            Log.i(TAG, "Post has category!");
                            break;
                        }
                    }
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    public void queryUsers(final String textQuery){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include(ParseUserSocial.KEY_PROFILE_PIC);
        query.include(ParseUserSocial.KEY_CATEGORIES);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                profilesAdapter.clear();

                // For each post in db, check if categories list contains text entered in etSearchQuery
                for(ParseUser userFound: objects){
                    ParseUserSocial userFoundSocial = new ParseUserSocial(userFound);
                    List<String> categories = userFoundSocial.getCategoriesList();

                    for (String cat: categories) {
                        cat = cat.toLowerCase().trim();
                        if(compareStringsAlgorithm(cat, textQuery, 4)){
                            profiles.add(userFound);
                            Log.i(TAG, "Post has category!");
                            break;
                        }
                    }
                }
            }
        });
        profilesAdapter.notifyDataSetChanged();
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
        for (int i = 0; i < Math.min(catChars.length, queryChars.length) && errorCount < errorMax && consecErrorCount < errorMax; i++) {
            if(catChars[i] != queryChars[i]){
                errorCount++;
                //consecErrorCount++;
            } else{
                //consecErrorCount = 0;
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
            queryUsers(text);
            hideKeyboard((Activity) getContext());
            //Toast.makeText(getContext(), toRomanNumeral(17), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}