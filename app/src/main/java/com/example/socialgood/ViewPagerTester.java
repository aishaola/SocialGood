package com.example.socialgood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.socialgood.activities.MainActivity;
import com.example.socialgood.adapters.SlidingImage_Adapter;
import com.example.socialgood.models.Page;
import com.example.socialgood.models.Post;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerTester extends AppCompatActivity {

    private ViewPager mPager;
    private PagerAdapter adapter;
    private Post post;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private List<Page> pages;
    private ArrayList<Integer> ImagesArray = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_tester);
        init();
    }

    private void init() {


        pages = new ArrayList<>();
        mPager = findViewById(R.id.vpImages);

        adapter = new SlidingImage_Adapter(this, pages);
        mPager.setAdapter(adapter);
        getPostImages();

        /*
        CirclePageIndicator indicator = (CirclePageIndicator)
                findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

 */

        final float density = getResources().getDisplayMetrics().density;

    }

    public void getPostImages(){
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.whereEqualTo(ParseObject.KEY_OBJECT_ID, "T3XkyUyYUr");
        query.getFirstInBackground(new GetCallback<Post>() {
            @Override
            public void done(Post object, ParseException e) {
                if(e != null){
                    Toast.makeText(ViewPagerTester.this, "No object found oh noo", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(ViewPagerTester.this, "Post found...", Toast.LENGTH_SHORT).show();
                pages.addAll(object.getPagesFromMediaList());
                adapter.notifyDataSetChanged();
            }
        });
    }
}