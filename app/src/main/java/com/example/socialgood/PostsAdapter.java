package com.example.socialgood;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialgood.activities.PostDetailActivity;
import com.example.socialgood.fragments.FeedFragment;
import com.example.socialgood.fragments.ProfileFragment;
import com.example.socialgood.models.Link;
import com.example.socialgood.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    private ParseUser user;
    private FragmentManager fm;

    public PostsAdapter(Context context, FragmentManager fm, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        this.fm = fm;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvUsername;
        TextView tvCaption;
        TextView tvCategories;
        TextView tvTimestamp;
        Button linkButton;
        ImageView ivProfileImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivPostImage);
            ivProfileImage = itemView.findViewById(R.id.ivProfilePic);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCategories = itemView.findViewById(R.id.tvCategories);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            linkButton = itemView.findViewById(R.id.linkButton);

            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(getAdapterPosition() > -1)
                        goPostDetailsActivity(posts.get(getAdapterPosition()));
                }
            });
        }

        private void goPostDetailsActivity(Post post) {
            Intent i = new Intent(context, PostDetailActivity.class);
            i.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
            context.startActivity(i);
        }

        public void bind(final Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvCategories.setText(post.getCategoriesDisplay());
            tvTimestamp.setText(post.getRelativeTimeAgo());
            tvCaption.setText(post.getCaption());
            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goProfileFragment(post.getUser());
                }
            });

            ParseFile image = post.getImage();
            Link link = post.getLink();

            ParseFile profileImage = post.getUserSocial().getProfilePic();

            // If there is an image in the Image field, show image
            if(image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
            else {
                ivImage.setVisibility(View.GONE);
            }

            if(profileImage != null) {
                int radius = 5; // corner radius, higher value = more rounded
                int margin = 0; // crop margin, set to 0 for corners with no crop

                Glide.with(context).load(profileImage.getUrl()).transform(new RoundedCornersTransformation(radius, margin))
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
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink)));
                    }
                });
                tvCaption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        goPostDetailsActivity(post);
                    }
                });
            } else {
                linkButton.setVisibility(View.GONE);
            }
        }

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
        }
    }
}
