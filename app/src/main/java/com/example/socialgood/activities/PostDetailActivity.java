package com.example.socialgood.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialgood.R;
import com.example.socialgood.adapters.CommentsAdapter;
import com.example.socialgood.fragments.ProfileFragment;
import com.example.socialgood.models.Comment;
import com.example.socialgood.models.Link;
import com.example.socialgood.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.socialgood.SocialGoodHelpers.hideKeyboard;

public class PostDetailActivity extends AppCompatActivity {

    public final String TAG = this.getClass().getSimpleName();

    ImageView ivImage;
    TextView tvUsername;
    TextView tvCaption;
    TextView tvCategories;
    TextView tvTimestamp;
    ImageView tvDeletePost;
    Button linkButton;
    ImageView ivProfileImage;
    RecyclerView rvComments;
    EditText etComment;
    Button btnComment;
    TextView tvUserFollowCat;
    LinearLayout llButtons;

    CommentsAdapter adapter;
    List<Comment> comments;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_post);

        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        comments = new ArrayList<>();

        ivImage = findViewById(R.id.ivPostImage);
        ivProfileImage = findViewById(R.id.ivProfilePic);
        tvCaption = findViewById(R.id.tvCaption);
        tvUsername = findViewById(R.id.tvUsername);
        tvCategories = findViewById(R.id.tvCategories);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        tvDeletePost = findViewById(R.id.tvDeletePost);
        tvUserFollowCat = findViewById(R.id.tvFollowingCat);
        linkButton = findViewById(R.id.linkButton);
        rvComments = findViewById(R.id.rvComments);
        btnComment = findViewById(R.id.btnComment);
        etComment = findViewById(R.id.etComment);
        llButtons = findViewById(R.id.llButtons);

        bind(post);

        adapter = new CommentsAdapter(comments, this);
        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(this));
        getPostComments();

        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userComment = etComment.getText().toString();
                addComment(userComment);
                hideKeyboard(PostDetailActivity.this);
            }
        });

    }

    private void addComment(String userComment) {
        final Comment comment = new Comment(ParseUser.getCurrentUser(), userComment, post);
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "addComment(): Comment couldn't post", e);
                    Toast.makeText(PostDetailActivity.this, "Could not post comment!", Toast.LENGTH_SHORT).show();
                    return;
                }
                etComment.setText("");
                comments.add(comment);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void getPostComments(){
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);

        query.include(Comment.KEY_USER);
        query.include(Comment.KEY_USER_COMMENT);
        query.include(Comment.KEY_POST);

        query.whereEqualTo(Comment.KEY_POST, post);

        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if (e != null){
                    Log.e("Post", "getPostComments(): Error retrieving comments from Post class", e);
                    return;
                }
                comments.addAll(objects);
                Log.i("Comments: ", "COMMENT RETREIVAL WOKRED " + comments.size());
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void bind(final Post post) {
        tvUsername.setText(post.getUser().getUsername());
        tvCategories.setText(post.getCategoriesDisplay());
        tvTimestamp.setText(post.getRelativeTimeAgo());
        tvCaption.setText(post.getCaption());

        /*
        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goProfileFragment(post.getUser());
            }
        });*/

        if(post.isPostCurrUsers())
            tvDeletePost.setVisibility(View.VISIBLE);
        else
            tvDeletePost.setVisibility(View.GONE);

        ParseFile image = post.getImage();

        ParseFile profileImage = post.getUserSocial().getProfilePic();

        // If there is an image in the Image field, show image
        if(image != null) {
            Glide.with(this).load(image.getUrl()).into(ivImage);
        }
        else {
            ivImage.setVisibility(View.GONE);
        }

        if(!post.isUserFollowsCat())
            tvUserFollowCat.setVisibility(View.GONE);

        if(profileImage != null) {
            int radius = 5; // corner radius, higher value = more rounded
            int margin = 0; // crop margin, set to 0 for corners with no crop

            Glide.with(this).load(profileImage.getUrl()).transform(new RoundedCornersTransformation(radius, margin))
                    .placeholder(R.drawable.action_profile).into(ivProfileImage);
        }
        else {
            ivProfileImage.setImageResource(R.drawable.action_profile);
        }

        // If there is an object in the link field, show link button that launches new activity
        // of the url in the browser
        if(post.isLink()) {
            llButtons.setVisibility(View.VISIBLE);
            showLinkDisplay(post);
        } else {
            llButtons.setVisibility(View.GONE);
        }
    }

    private void showLinkDisplay(Post post){
        List<Link> links = post.getLinks();
        // Loops through all the links in the post links, creates a button, and adds it to the view
        for (int i = 0; i < links.size(); i++) {
            final Link link = links.get(i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            final Button linkButton = new Button(this);
            linkButton.setText(link.getTitle());
            linkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl())));
                }
            });
            lp.width = 850;
            linkButton.setLayoutParams(lp);
            llButtons.addView(linkButton);
        }

    }

    /*

    private void goProfileFragment(ParseUser user) {
        // Create new fragment and transaction
        Fragment newFragment = new ProfileFragment(user);
        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = fm.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.frame_holder, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    } */
}