package com.example.socialgood;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SocialGoodHelpers {

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }



    public static final List<String> SG_CATEGORIES = Arrays.asList("Racial Justice", "Yemen Crisis", "General", "Global Warming");

    public static boolean categoryExists(String category){
        String categoryCheck = category.toLowerCase().trim();
        for (String userCategory: SG_CATEGORIES) {
            if(userCategory.toLowerCase().trim().equals(categoryCheck))
                return true;
        }
        return false;
    }
}
