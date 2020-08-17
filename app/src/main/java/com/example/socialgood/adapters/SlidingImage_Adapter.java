package com.example.socialgood.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.socialgood.R;
import com.example.socialgood.models.Link;
import com.example.socialgood.models.Page;
import com.example.socialgood.models.Post;

import org.jetbrains.annotations.NotNull;;
import java.util.List;

public class SlidingImage_Adapter extends PagerAdapter {
    private List<Page> pages;
    private LayoutInflater inflater;
    private Context context;


    public SlidingImage_Adapter(Context context, List<Page> pages) {
        this.context = context;
        this.pages = pages;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NotNull
    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);
        View linksLayout = inflater.inflate(R.layout.slidinglinks_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.image);
        final LinearLayout llButtons = linksLayout.findViewById(R.id.llButtons);
        Page page = pages.get(position);

        if(page.getType().equals("image")){
            Glide.with(context).load(page.getImageUrl()).into(imageView);
            view.addView(imageLayout, 0);
            return imageLayout;
        }
        else if(page.getType().equals(Page.TYPE_BITMAP)) {
            imageView.setImageBitmap(page.getBitmap());
            view.addView(imageLayout, 0);
            return imageLayout;
        }
        else if(page.getType().equals("links")){
            // TODO: inflate the link layout page that will be created, ALSO FIX POST DETAILS
            List<Link> links = page.getLinks();
            attachLinkDisplay(llButtons, links);
            view.addView(linksLayout, 0);
            return linksLayout;
        }

        view.addView(imageLayout, 0);
        return imageLayout;
    }

    private void attachLinkDisplay(LinearLayout llButtons, List<Link> links){
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


}
