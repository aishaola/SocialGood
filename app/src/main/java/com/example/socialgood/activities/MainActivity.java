package com.example.socialgood.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.socialgood.R;
import com.example.socialgood.fragments.CreateFragment;
import com.example.socialgood.fragments.FeedFragment;
import com.example.socialgood.fragments.LinkEntryDialogFragment;
import com.example.socialgood.fragments.ProfileFragment;
import com.example.socialgood.fragments.SearchFragment;
import com.example.socialgood.models.ParseUserSocial;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.action_home:
                        fragment = new FeedFragment();
                        break;
                    case R.id.action_compose:
                        fragment = new CreateFragment();
                        break;
                    case R.id.action_search:
                        fragment = new SearchFragment();
                        break;
                    default:
                    case R.id.action_profile:
                        fragment = new ProfileFragment(ParseUser.getCurrentUser());
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.frame_holder, fragment).commit();
                return true;
            }
        });


        if(getIntent().getBooleanExtra(ProfileFragment.TAG, false)){
            bottomNavigationView.setSelectedItemId(R.id.action_profile);
        } else{
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        }

        /*
        if(ParseUser.getCurrentUser() != null){
            ParseUserSocial user = ParseUserSocial.getCurrentUser();
            String categories = user.getCategories();
            Toast.makeText(this, "User is logged in! Categories: " + categories, Toast.LENGTH_SHORT).show();
        }*/

    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        LinkEntryDialogFragment linkEntryDialogFragment = LinkEntryDialogFragment.newInstance("Some Title");
        linkEntryDialogFragment.show(fm, "fragment_edit_name");
    }
}
