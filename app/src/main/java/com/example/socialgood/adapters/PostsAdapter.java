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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socialgood.R;
import com.example.socialgood.activities.EditProfileActivity;
import com.example.socialgood.activities.IntroActivity;
import com.example.socialgood.activities.PostDetailActivity;
import com.example.socialgood.fragments.DonateAppFragment;
import com.example.socialgood.fragments.ProfileFragment;
import com.example.socialgood.models.Donation;
import com.example.socialgood.models.Fundraiser;
import com.example.socialgood.models.Link;
import com.example.socialgood.models.ParseUserSocial;
import com.example.socialgood.models.Post;
import com.github.chrisbanes.photoview.PhotoView;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private List<ParseObject> posts;
    private ParseUser user;
    private FragmentManager fm;

    public static final int IMAGE_LINK_TYPE = 0;
    public static final int DONATION_TYPE = 1;
    public static final int PROFILE_TYPE = 2;
    private static final int PROFILE_HEADER_TYPE = 3;

    public PostsAdapter(Context context, FragmentManager fm, List<ParseObject> posts) {
        this.context = context;
        this.posts = posts;
        this.fm = fm;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch(viewType){
            case PROFILE_HEADER_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.fragment_profile, parent, false);
                return new ViewHolderProfileHeader(view);
            case PROFILE_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false);
                return new ViewHolderProfile(view);
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
        ParseObject parseObject = posts.get(position);
        // If instance of PareUser return parseUser type
        if(parseObject instanceof ParseUser) {
            if (position == 0)
                return PROFILE_HEADER_TYPE;
            return PROFILE_TYPE;
        }
        Post post = (Post) parseObject;
        String type = post.getType();
        if(post.isPostReshare())
            type = post.getPostReshared().getType();

        if(type != null && type.equals(Post.DONATION_TYPE))
            return DONATION_TYPE;
        else
            return IMAGE_LINK_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ParseObject parseObject = posts.get(position);
        switch (holder.getItemViewType()) {
            case PROFILE_HEADER_TYPE:
                ParseUser user = (ParseUser) parseObject;
                boolean isCurrUser = (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId()));

                ViewHolderProfileHeader viewHolderProfileHeader = (ViewHolderProfileHeader)holder;
                viewHolderProfileHeader.bind(user, isCurrUser);
                break;
            case PROFILE_TYPE:
                ParseUser user1 = (ParseUser) parseObject;
                ViewHolderProfile viewHolderProfile = (ViewHolderProfile)holder;
                viewHolderProfile.bind(user1);
                break;

            case IMAGE_LINK_TYPE:
                Post post = (Post) parseObject;
                ViewHolder viewHolder = (ViewHolder)holder;
                viewHolder.bind(post, position, post.getType());
                break;

            case DONATION_TYPE:
                Post postD = (Post) parseObject;
                ViewHolderDonation viewHolderDonation = (ViewHolderDonation)holder;
                viewHolderDonation.bind(postD, position, postD.getType());
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

    public void clearAllButHeader(ParseUser user) {
        posts.clear();
        posts.add(user);
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
        ImageView tvDeletePost;
        ImageView ivProfileImage;
        ImageView ivReshare;
        LinearLayout llButtons;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = (PhotoView) itemView.findViewById(R.id.ivPostImage);
            ivReshare = itemView.findViewById(R.id.ivReshare);
            ivProfileImage = itemView.findViewById(R.id.ivProfilePic);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCategories = itemView.findViewById(R.id.tvCategories);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvDeletePost = itemView.findViewById(R.id.tvDeletePost);
            tvUserFollowCat = itemView.findViewById(R.id.tvFollowingCat);
            tvResharedUsername = itemView.findViewById(R.id.tvResharedUsername);
            rlReshare = itemView.findViewById(R.id.rlReshare);
            llButtons = itemView.findViewById(R.id.llButtons);
            cvRoot = itemView.findViewById(R.id.cvRoot);
            itemView.findViewById(R.id.commentContainer).setVisibility(View.GONE);
        }

        private void goPostDetailsActivity(Post post) {
            Intent i = new Intent(context, PostDetailActivity.class);
            i.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
            context.startActivity(i);
        }

        public void bind(final Post post, final int position, String type){
            if(type != null && type.equals(Post.RESHARE_TYPE)){
                bindPost(post.getPostReshared(), post, post.isPostCurrUsers());
            } else{
                bindPost(post, null, post.isPostCurrUsers());
            }

            tvDeletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    post.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                Log.e("Issue", "done: Error deleting post", e);
                                return;
                            }
                            Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                            posts.remove(post);
                            notifyItemRemoved(position);
                        }
                    });
                    Post.removeAllReshares(post);
                }
            });
        }

        public void bindPost(final Post post, Post reshare, boolean isPostCurrUsers) {
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
            else
                tvUserFollowCat.setVisibility(View.VISIBLE);

            if(reshare != null) {
                rlReshare.setVisibility(View.VISIBLE);
                tvResharedUsername.setText(reshare.getUser().getUsername());
            } else{
                rlReshare.setVisibility(View.GONE);
            }

            // Only show delete button if post is current user's

            if(isPostCurrUsers)
                tvDeletePost.setVisibility(View.VISIBLE);
            else
                tvDeletePost.setVisibility(View.GONE);


            ParseFile image = post.getImage();

            ParseFile profileImage = post.getUserSocial().getProfilePic();

            // If there is an image in the Image field, show image
            if(post.getType().equals(Post.IMAGE_TYPE)) {
                ivImage.setVisibility(View.VISIBLE);
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
            llButtons.removeAllViews();
            for (int i = 0; i < links.size(); i++) {
                final Link link = links.get(i);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final Button linkButton = new Button(context);
                linkButton.setText(link.getTitle());
                linkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl())));
                    }
                });
                lp.width = 850;
                linkButton.setLayoutParams(lp);
                llButtons.addView(linkButton);
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
        ImageView tvDeletePost;
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
            tvDeletePost = itemView.findViewById(R.id.tvDeletePost);
            itemView.findViewById(R.id.commentContainer).setVisibility(View.GONE);
        }

        public void bind(final Post post, final int position, String type){
            if(type != null && type.equals(Post.RESHARE_TYPE)){
                bindPost(post.getPostReshared(), post, post.isPostCurrUsers());
            } else{
                bindPost(post, null, post.isPostCurrUsers());
            }

            tvDeletePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    post.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                Log.e("Issue", "done: Error deleting post", e);
                                return;
                            }
                            Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show();
                            posts.remove(post);
                            notifyItemRemoved(position);
                        }
                    });
                }
            });
        }

        public void bindPost(final Post post, Post reshare, boolean isPostCurrUsers) {
            Donation donation = post.getDonation();
            final Fundraiser fundraiser = donation.getFundraiser();
            // elements specific to the donation post
            String pattern = "###,###.00";
            DecimalFormat decimalFormat = new DecimalFormat(pattern);
            String amountFormatted = decimalFormat.format(donation.getAmountDonated());

            tvDonationAmount.setText("Donated $"  + amountFormatted + ": ");

            fundraiser.fetchIfNeededInBackground(new GetCallback<Fundraiser>() {
                @Override
                public void done(Fundraiser object, ParseException e) {
                    tvFundraiserName.setText(object.getTitle());
                }
            });

            btnDonate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goDonateFragment();
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

            // Resharing feature implemented here
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
                            Toast.makeText(context, "Post was reshared!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            if(reshare != null) {
                rlReshare.setVisibility(View.VISIBLE);
                tvResharedUsername.setText(reshare.getUser().getUsername());
            } else{
                rlReshare.setVisibility(View.GONE);
            }

            // Only show delete button if post is current user's

            if(isPostCurrUsers)
                tvDeletePost.setVisibility(View.VISIBLE);
            else
                tvDeletePost.setVisibility(View.GONE);


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

        private void goDonateFragment() {
            // Create new fragment and transaction
            Fragment newFragment = new DonateAppFragment();
            // consider using Java coding conventions (upper first char class names!!!)
            FragmentTransaction transaction = fm.beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.frame_holder, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
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

    public class ViewHolderProfile extends RecyclerView.ViewHolder {

        TextView tvUsername;
        TextView tvCategories;
        ImageView ivProfilePic;

        public ViewHolderProfile(@NonNull View itemView) {
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

    public class ViewHolderProfileHeader extends RecyclerView.ViewHolder{

        public final String TAG = ViewHolderProfileHeader.class.getSimpleName();
        Button btnLogout;
        Button btnEditProfile;
        Button btnFollow;
        Button btnDonate;
        TextView tvUsername;
        TextView tvCategories;
        ImageView ivProfilePic;
        ParseUser profileUser;
        View buttons;
        View buttonsForOtherProfiles;
        boolean isCurrentUser;
        boolean isFollowing;

        public ViewHolderProfileHeader(@NonNull View view) {
            super(view);
            btnLogout = view.findViewById(R.id.btnLogout);
            btnEditProfile = view.findViewById(R.id.btnEditProfile);
            ivProfilePic = view.findViewById(R.id.ivProfilePic);
            tvUsername = view.findViewById(R.id.tvUsername);
            tvCategories = view.findViewById(R.id.tvCategories);
            buttons = view.findViewById(R.id.buttons);
            buttonsForOtherProfiles = view.findViewById(R.id.buttonsForOtherProfiles);
            btnFollow = view.findViewById(R.id.btnFollow);
            btnDonate = view.findViewById(R.id.btnDonate);
        }

        public void bind(ParseUser user, boolean isCurrUser){
            profileUser = user;
            isCurrentUser = user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId());
            isFollowing = ParseUserSocial.getCurrentUser().userIsFollowing(user);

            ParseUserSocial userSocial = new ParseUserSocial(profileUser);

            // Initialize follow button
            if(!isCurrentUser)
                updateFollowButton();

            // fill in username and category textViews
            tvUsername.setText(profileUser.getUsername());
            tvCategories.setText(userSocial.getCategories());

            // hide edit profile buttons if profile isn't current User's
            if(!isCurrentUser)
                buttons.setVisibility(View.GONE);
            else
                buttonsForOtherProfiles.setVisibility(View.GONE);

            // log out when logout button pressed
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                }
            });

            // launch edit profile when edit profile button pressed
            btnEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    context.startActivity(new Intent(context, EditProfileActivity.class));
                }
            });

            // load user's profile pic or use default
            ParseFile image = userSocial.getProfilePic();
            if(image != null){
                Glide.with(context).load(image.getUrl()).into(ivProfilePic);
            } else {
                ivProfilePic.setImageResource(R.drawable.action_profile);
            }

            btnDonate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goDonateFragment();
                }
            });

        }

        private void goDonateFragment() {
            // Create new fragment and transaction
            Fragment newFragment = new DonateAppFragment();
            // consider using Java coding conventions (upper first char class names!!!)
            FragmentTransaction transaction = fm.beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack
            transaction.replace(R.id.frame_holder, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }

        public void updateFollowButton(){
            if(isFollowing){
                btnFollow.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                btnFollow.setText("Unfollow");
                btnFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        unfollowUser();
                    }
                });
            } else {
                btnFollow.setTextColor(context.getResources().getColor(R.color.design_default_color_primary));
                btnFollow.setText("Follow");
                btnFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        followUser();
                    }
                });
            }

        }

        private void followUser() {
            final ParseUser currUser = ParseUser.getCurrentUser();
            ParseUserSocial helper = new ParseUserSocial(currUser);
            helper.addProfileFollowing(profileUser);
            currUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null){
                        Log.e(TAG, "Error saving follow", e);
                        return;
                    }
                    Log.i(TAG, "Profile Followed: " + currUser.getUsername() + " followed " + profileUser.getUsername() );
                    isFollowing = !isFollowing;
                    updateFollowButton();
                }
            });
        }

        private void unfollowUser() {
            final ParseUser currUser = ParseUser.getCurrentUser();
            ParseUserSocial helper = new ParseUserSocial(currUser);
            helper.removeProfileFollowing(profileUser);
            currUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null){
                        Log.e(TAG, "Error saving follow", e);
                        return;
                    }
                    Log.i(TAG, "Profile Followed: " + currUser.getUsername() + " unfollowed " + profileUser.getUsername() );
                    isFollowing = !isFollowing;
                    updateFollowButton();
                }
            });
        }

        private void logout() {
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(TAG, "logout(): Error logging out!!", e);
                    }
                    Log.i(TAG, "logout(): Successfully logged out!");
                    context.startActivity(new Intent(context, IntroActivity.class));
                }
            });
        }
    }
}
