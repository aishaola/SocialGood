package com.example.socialgood;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivPostImage);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCategories = itemView.findViewById(R.id.tvCategories);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);

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

        public void bind(Post post) {
            tvUsername.setText(post.getUser().getUsername());
            tvCategories.setText(post.getCategories());
            tvTimestamp.setText(post.getRelativeTimeAgo());
            tvCaption.setText(post.getCaption());

            ParseFile image = post.getImage();
            if(image != null)
                Glide.with(context).load(image.getUrl()).into(ivImage);
        }
    }
}
