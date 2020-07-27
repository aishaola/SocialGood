package com.example.socialgood.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.socialgood.R;
import com.example.socialgood.adapters.CategoriesAdapter;
import com.example.socialgood.fragments.CreateFragment;
import com.example.socialgood.fragments.ProfileFragment;
import com.example.socialgood.models.ParseUserSocial;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.socialgood.SocialGoodHelpers.hideKeyboard;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TAG = EditProfileActivity.class.getSimpleName();
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 43;
    public final static int PICK_PHOTO_CODE = 1046;

    public String photoFileName = "photo.jpg";
    private File photoFile;

    ImageView ivProfilePic;
    Button btnLaunchCamera;
    Button btnLaunchGallery;
    Button btnSaveChanges;
    RecyclerView rvCategories;
    EditText etAddCategory;
    List<String> categories;
    List<String> categoriesToChooseFrom;
    ParseUser currUser;
    ParseUserSocial currUserSocial;
    CategoriesAdapter categoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Edit Profile");
        setContentView(R.layout.activity_edit_profile);
        currUser = ParseUser.getCurrentUser();
        currUserSocial = new ParseUserSocial(currUser);
        // List of categories User can choose to associate account with
        categoriesToChooseFrom = Arrays.asList("Racial Justice", "Yemen Crisis", "General", "Global Warming");

        ivProfilePic = findViewById(R.id.ivProfilePic);
        btnLaunchCamera = findViewById(R.id.btnLaunchCamera);
        btnLaunchGallery = findViewById(R.id.btnLaunchGallery);
        rvCategories = findViewById(R.id.rvCategories);
        etAddCategory = findViewById(R.id.etAddCategory);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        CategoriesAdapter.OnLongClickListener onLongClickListener = new CategoriesAdapter.OnLongClickListener(){
            @Override
            public void onItemLongClicked(int position) {
                // Delete the item from the model
                categories.remove(position);
                // Notify the adapter
                categoriesAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
            }
        };
        categories = new ArrayList<>();

        categories.addAll(currUserSocial.getCategoriesList());
        categoriesAdapter = new CategoriesAdapter(categories, onLongClickListener);
        rvCategories.setAdapter(categoriesAdapter);
        rvCategories.setLayoutManager(new LinearLayoutManager( this));

        etAddCategory.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE) {
                    String cat = etAddCategory.getText().toString();
                    addCategory(cat);
                    hideKeyboard(EditProfileActivity.this);
                    return true;
                }
                return false;
            }
        });

        ParseFile image = currUserSocial.getProfilePic();
        if(image == null)
            ivProfilePic.setImageResource(R.drawable.action_profile);
        else
            Glide.with(this).load(image.getUrl()).into(ivProfilePic);

        btnLaunchGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGallery();
            }
        });

        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        if(photoFile != null) {
            currUserSocial.setProfilePic(new ParseFile(photoFile));
        }

        currUserSocial.addNewListOfCategories(categories);
        currUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error saving categories", e);
                    return;
                }
                Log.i(TAG, "Categories saved!");
            }
        });

        Intent i = new Intent(this, MainActivity.class);
        i.putExtra(ProfileFragment.TAG, true);
        startActivity(i);
    }

    private void addCategory(String cat){
        etAddCategory.setText("");

        // Checks if User already has the category
        if(userHasCategory(cat)){
            Toast.makeText(EditProfileActivity.this, "User already has this category", Toast.LENGTH_SHORT).show();
            return;
        }

        // Checks if category is valid
        if(!categoryExists(cat)){
            Toast.makeText(EditProfileActivity.this, "Invalid Category! Enter another one", Toast.LENGTH_SHORT).show();
            return;
        }

        // Adds to list, does not save yet tho
        categories.add(cat.trim());
        categoriesAdapter.notifyDataSetChanged();
        Toast.makeText(EditProfileActivity.this, cat + " added!", Toast.LENGTH_SHORT).show();
    }

    private boolean userHasCategory(String category){
        String categoryCheck = category.toLowerCase().trim();
        for (String userCategory: categories) {
            if(userCategory.toLowerCase().trim().equals(categoryCheck))
                return true;
        }
        return false;
    }

    private boolean categoryExists(String category){
        String categoryCheck = category.toLowerCase().trim();
        for (String userCategory: categoriesToChooseFrom) {
            if(userCategory.toLowerCase().trim().equals(categoryCheck))
                return true;
        }
        return false;
    }


    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "com.example.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Trigger gallery selection for a photo
    public void launchGallery() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "com.example.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivProfilePic.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                // Load the image located at photoUri into selectedImage
                Bitmap selectedImage = loadFromUri(photoUri);
                // Load the selected image into a preview
                ivProfilePic.setImageBitmap(selectedImage);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't chosen!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(this.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }




}