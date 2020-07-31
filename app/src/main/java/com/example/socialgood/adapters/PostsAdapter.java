package com.example.socialgood.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialgood.R;
import com.example.socialgood.activities.PostDetailActivity;
import com.example.socialgood.fragments.ProfileFragment;
import com.example.socialgood.models.Donation;
import com.example.socialgood.models.Fundraiser;
import com.example.socialgood.models.Link;
import com.example.socialgood.models.Post;
import com.github.chrisbanes.photoview.PhotoView;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Post> posts;
    private ParseUser user;
    private FragmentManager fm;

    public static final int IMAGE_LINK_TYPE = 0;
    public static final int DONATION_TYPE = 1;

    public PostsAdapter(Context context, FragmentManager fm, List<Post> posts) {
        this.context = context;
        this.posts = posts;
        this.fm = fm;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case DONATION_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.item_donation_post, parent, false);
                return new ViewHolderDonation(view);

            default:
            case IMAGE_LINK_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
                return new ViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        String type = posts.get(position).getType();

        if(type != null && type.equals(Post.DONATION_TYPE))
            return DONATION_TYPE;
        else
            return IMAGE_LINK_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case IMAGE_LINK_TYPE:
                ViewHolder viewHolder = (ViewHolder)holder;
                viewHolder.bind(posts.get(position));
                break;

            case DONATION_TYPE:
                ViewHolderDonation viewHolderDonation = (ViewHolderDonation)holder;
                viewHolderDonation.bind(posts.get(position));
                break;
        }
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
        PhotoView ivImage;
        TextView tvUsername;
        TextView tvCaption;
        TextView tvCategories;
        TextView tvTimestamp;
        TextView tvUserFollowCat;
        TextView tvResharedUsername;
        View rlReshare;
        View cvRoot;
        Button linkButton;
        ImageView ivProfileImage;
        ImageView ivReshare;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = (PhotoView) itemView.findViewById(R.id.ivPostImage);
            ivReshare = itemView.findViewById(R.id.ivReshare);
            ivProfileImage = itemView.findViewById(R.id.ivProfilePic);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCategories = itemView.findViewById(R.id.tvCategories);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            linkButton = itemView.findViewById(R.id.linkButton);
            tvUserFollowCat = itemView.findViewById(R.id.tvFollowingCat);
            tvResharedUsername = itemView.findViewById(R.id.tvResharedUsername);
            rlReshare = itemView.findViewById(R.id.rlReshare);
            cvRoot = itemView.findViewById(R.id.cvRoot);
            itemView.findViewById(R.id.commentContainer).setVisibility(View.GONE);



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

            cvRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goPostDetailsActivity(post);
                }
            });

            ivReshare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Post resharedPost = Post.reshare(ParseUser.getCurrentUser(), post);
                    resharedPost.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                Log.e("Reshare", "User cannot reshare", e);
                                return;
                            }
                            Toast.makeText(context, "Post was reshared!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            if(!post.isUserFollowsCat())
                tvUserFollowCat.setVisibility(View.GONE);

            if(post.getUserReshared() != null) {
                rlReshare.setVisibility(View.VISIBLE);
                tvResharedUsername.setText(post.getUserReshared().getUsername());
            } else{
                rlReshare.setVisibility(View.GONE);
            }


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

    public class ViewHolderDonation extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvUsername;
        TextView tvDonationAmount;
        TextView tvFundraiserName;
        TextView tvTimestamp;
        TextView tvResharedUsername;
        View rlReshare;
        Button btnDonate;
        ImageView ivProfileImage;
        ImageView ivReshare;


        public ViewHolderDonation(@NonNull View itemView) {
            super(itemView);

            tvDonationAmount = itemView.findViewById(R.id.tvDonationAmount);
            tvFundraiserName = itemView.findViewById(R.id.tvFundraiserName);
            btnDonate = itemView.findViewById(R.id.btnDonate);

            ivImage = itemView.findViewById(R.id.ivPostImage);
            ivReshare = itemView.findViewById(R.id.ivReshare);
            ivProfileImage = itemView.findViewById(R.id.ivProfilePic);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvResharedUsername = itemView.findViewById(R.id.tvResharedUsername);
            rlReshare = itemView.findViewById(R.id.rlReshare);
            itemView.findViewById(R.id.commentContainer).setVisibility(View.GONE);
        }

        public void bind(final Post post) {
            Donation donation = post.getDonation();
            final Fundraiser fundraiser = donation.getFundraiser();
            // elements specific to the donation post
            tvDonationAmount.setText("Donated $"  + donation.getAmountDonated() + ": ");
            fundraiser.fetchIfNeededInBackground(new GetCallback<Fundraiser>() {
                @Override
                public void done(Fundraiser object, ParseException e) {
                    tvFundraiserName.setText(object.getTitle());
                }
            });


            // All post views have elements below
            tvUsername.setText(post.getUser().getUsername());
            tvTimestamp.setText(post.getRelativeTimeAgo());
            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goProfileFragment(post.getUser());
                }
            });
            ivReshare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Post resharedPost = Post.reshare(ParseUser.getCurrentUser(), post);
                    resharedPost.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                Log.e("Reshare", "User cannot reshare", e);
                                return;
                            }
                            Toast.makeText(context, "Post was reshared!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            if(post.getUserReshared() != null) {
                rlReshare.setVisibility(View.VISIBLE);
                tvResharedUsername.setText(post.getUserReshared().getUsername());
            } else{
                rlReshare.setVisibility(View.GONE);
            }

            ParseFile profileImage = post.getUserSocial().getProfilePic();
            if(profileImage != null) {
                int radius = 5; // corner radius, higher value = more rounded
                int margin = 0; // crop margin, set to 0 for corners with no crop

                Glide.with(context).load(profileImage.getUrl()).transform(new RoundedCornersTransformation(radius, margin))
                        .placeholder(R.drawable.action_profile).into(ivProfileImage);
            }
            else {
                ivProfileImage.setImageResource(R.drawable.action_profile);
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
