package com.example.socialgood.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialgood.R;
import com.example.socialgood.fragments.ProfileFragment;
import com.example.socialgood.models.Comment;
import com.example.socialgood.models.ParseUserSocial;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class ProfilesAdapter extends RecyclerView.Adapter<ProfilesAdapter.ViewHolder>{
    Context context;
    List<ParseUser> profiles;
    private FragmentManager fm;

    public ProfilesAdapter(Context context, FragmentManager fm, List<ParseUser> profiles){
        this.profiles = profiles;
        this.context = context;
        this.fm = fm;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(profiles.get(position));
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void clear() {
        profiles.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsername;
        TextView tvCategories;
        ImageView ivProfilePic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCategories = itemView.findViewById(R.id.tvCategories);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
        }

        public void bind(final ParseUser user){
            ParseUserSocial userSocial = new ParseUserSocial(user);
            tvUsername.setText(user.getUsername());
            tvCategories.setText(userSocial.getCategories());
            ParseFile image = userSocial.getProfilePic();
            if(image == null)
                ivProfilePic.setImageResource(R.drawable.action_profile);
            else
                Glide.with(context).load(image.getUrl())
                        .placeholder(R.drawable.action_profile).into(ivProfilePic);

            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goProfileFragment(user);
                }
            });
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
