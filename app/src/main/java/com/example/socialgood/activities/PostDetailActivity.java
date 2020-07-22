package com.example.socialgood.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.socialgood.R;
import com.example.socialgood.fragments.ProfileFragment;
import com.example.socialgood.models.Link;
import com.example.socialgood.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostDetailActivity extends AppCompatActivity {

    ImageView ivImage;
    TextView tvUsername;
    TextView tvCaption;
    TextView tvCategories;
    TextView tvTimestamp;
    Button linkButton;
    ImageView ivProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_post);

        Post post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        ivImage = findViewById(R.id.ivPostImage);
        ivProfileImage = findViewById(R.id.ivProfilePic);
        tvCaption = findViewById(R.id.tvCaption);
        tvUsername = findViewById(R.id.tvUsername);
        tvCategories = findViewById(R.id.tvCategories);
        tvTimestamp = findViewById(R.id.tvTimestamp);
        linkButton = findViewById(R.id.linkButton);

        bind(post);
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

        ParseFile image = post.getImage();
        Link link = post.getLink();

        ParseFile profileImage = post.getUserSocial().getProfilePic();

        // If there is an image in the Image field, show image
        if(image != null) {
            Glide.with(this).load(image.getUrl()).into(ivImage);
        }
        else {
            ivImage.setVisibility(View.GONE);
        }

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
        if(link != null){
            String title = link.getTitle();
            final String urlLink = link.getUrl();
            linkButton.setText(title);
            linkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink)));
                }
            });
        } else {
            linkButton.setVisibility(View.GONE);
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