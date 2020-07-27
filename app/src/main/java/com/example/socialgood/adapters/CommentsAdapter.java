package com.example.socialgood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialgood.R;
import com.example.socialgood.models.Comment;
import com.example.socialgood.models.ParseUserSocial;
import com.parse.Parse;
import com.parse.ParseFile;

import java.io.File;
import java.util.List;


public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    Context context;
    List<Comment> comments;

    public CommentsAdapter(List<Comment> comments, Context context){
        this.comments = comments;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername;
        TextView tvComment;
        ImageView ivProfilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvComment = itemView.findViewById(R.id.tvComment);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
        }

        public void bind(Comment comment){
            ParseUserSocial user = new ParseUserSocial(comment.getUser());
            tvUsername.setText(comment.getUser().getUsername());
            tvComment.setText(comment.getComment());
            ParseFile image = user.getProfilePic();
            if(image == null)
                ivProfilePic.setImageResource(R.drawable.action_profile);
            else
                Glide.with(context).load(image.getUrl())
                        .placeholder(R.drawable.action_profile).into(ivProfilePic);
        }
    }
}
