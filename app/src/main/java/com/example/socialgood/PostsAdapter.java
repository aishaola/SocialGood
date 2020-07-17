package com.example.socialgood;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialgood.activities.PostDetailActivity;
import com.example.socialgood.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    private ParseUser user;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivPostImage);
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
            i.putExtra("Post", post);
            context.startActivity(i);
        }

        public void bind(final Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvCategories.setText(post.getCategories());
            tvTimestamp.setText(post.getRelativeTimeAgo());
            tvCaption.setText(post.getCaption());

            ParseFile image = post.getImage();
            String[] link = post.getLink();

            // If there is an image in the Image field, show image
            if(image != null) {
                Glide.with(context).load(image.getUrl()).into(ivImage);
            }
            else {
                ivImage.setVisibility(View.GONE);
            }

            // If there is an object in the link field, show link button that launches new activity
            // of the url in the browser
            if(link != null){
                String title = link[0];
                final String urlLink = link[1];
                linkButton.setText(title);
                linkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlLink)));
                    }
                });
            } else {
                linkButton.setVisibility(View.GONE);
            }
        }
    }
}
