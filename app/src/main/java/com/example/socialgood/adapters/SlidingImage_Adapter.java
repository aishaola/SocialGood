package com.example.socialgood.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.socialgood.R;
import com.example.socialgood.models.Page;

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

        assert imageLayout != null;
        final ImageView imageView = imageLayout.findViewById(R.id.image);
        Page page = pages.get(position);

        if(page.getType().equals("image"))
            Glide.with(context).load(page.getImageUrl()).into(imageView);

        view.addView(imageLayout, 0);

        return imageLayout;
    }


}
