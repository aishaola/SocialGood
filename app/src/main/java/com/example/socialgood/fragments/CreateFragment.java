package com.example.socialgood.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialgood.ImageSupport;
import com.example.socialgood.R;

import java.io.File;
// PICK_PHOTO_CODE is a constant integer

import com.example.socialgood.R;
import com.example.socialgood.SocialGoodHelpers;
import com.example.socialgood.models.Link;
import com.example.socialgood.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.socialgood.SocialGoodHelpers.categoryExists;
import static com.example.socialgood.SocialGoodHelpers.hideKeyboard;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment implements LinkEntryDialogFragment.LinkEntryDialogListener  {

    public static final String TAG = CreateFragment.class.getSimpleName();
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 43;
    public final static int PICK_PHOTO_CODE = 1046;
    public static final int TYPE_LINK = 1;
    public static final int TYPE_IMAGE = 0;

    public String photoFileName = "photo.jpg";

    View addPicView;
    View addPickFromGalleryView;
    View addLinkView;

    Post post;
    Link link;
    List<Link> links;
    ImageView ivImage;
    EditText etCaption;
    EditText etAddCategory;
    Button btnAddCategory;
    Button btnSubmit;
    TextView tvCategories;
    LinearLayout llLinkDisplay;

    List<String> categories;
    byte[] galleryPhotoBitmap;
    int postType;
    private File photoFile;
    ImageSupport imageSupport;




    public CreateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Create Post");
        ivImage = view.findViewById(R.id.ivImage);
        addPicView = view.findViewById(R.id.addPicView);
        addPickFromGalleryView = view.findViewById(R.id.addPicFromGalleryView);
        addLinkView = view.findViewById(R.id.addLinkView);
        btnSubmit = view.findViewById(R.id.btnCreatePost);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);
        etCaption = view.findViewById(R.id.etCaption);
        etAddCategory = view.findViewById(R.id.etAddCategory);
        tvCategories = view.findViewById(R.id.tvCategories);
        llLinkDisplay = view.findViewById(R.id.llUrlDisplays);

        postType = TYPE_IMAGE;
        imageSupport = new ImageSupport(getContext(), photoFile, photoFileName, TAG);

        addPicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });

        addPickFromGalleryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto();
            }
        });

        addLinkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLinkEditDialog();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitPost();
            }
        });

        btnAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCategory();
            }
        });

        etAddCategory.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                return action(i);
            }
        });

        links = new ArrayList<>();
        categories = new ArrayList<>();
        post = new Post();
        galleryPhotoBitmap = null;

    }

    private void onSubmitPost() {
        if(postType == TYPE_IMAGE && photoFile == null) {
            Toast.makeText(getContext(), "Photo is null!!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(postType == TYPE_LINK && link == null) {
            Toast.makeText(getContext(), "Link is null!!", Toast.LENGTH_SHORT).show();
            return;
        }
        String caption = etCaption.getText().toString();
        if(caption == null){
            Toast.makeText(getContext(), "Caption is null!!", Toast.LENGTH_SHORT).show();
            return;
        }
        savePost(link, photoFile, ParseUser.getCurrentUser(), caption);

    }

    public void savePost(Link link, File photo, ParseUser user, String caption){
        if(postType == TYPE_LINK){
            //post.setLink(link.toJSON());
            post.setLinks(links);
            post.setType(Post.LINK_TYPE);
        } else{
            post.setType(Post.IMAGE_TYPE);
            if(galleryPhotoBitmap == null)
                post.setImage(new ParseFile(photoFile));
            else
                post.setImage(new ParseFile(galleryPhotoBitmap));
        }

        post.setCaption(caption);
        post.setUser(ParseUser.getCurrentUser());
        post.saveCategories();
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Post save error: post couldn't save", e);
                    return;
                }
                Toast.makeText(getContext(), "Post Created!", Toast.LENGTH_SHORT).show();
                goFeedFragment();
            }
        });

    }

    private void addCategory() {
        String cat = etAddCategory.getText().toString().trim();
        etAddCategory.setText("");
        if(cat.isEmpty()){
            Toast.makeText(getContext(), "Please enter a category!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!categoryExists(cat)){
            Toast.makeText(getContext(), "Invalid Category", Toast.LENGTH_SHORT).show();
            return;
        }
        categories.add(cat);
        post.addCategory(cat);
        Toast.makeText(getContext(), "Category Added!", Toast.LENGTH_SHORT).show();
        tvCategories.setText(post.getTempCategoriesDisplay());
    }

    private void goFeedFragment() {
        // Create new fragment and transaction
        Fragment newFragment = new FeedFragment();
        // consider using Java coding conventions (upper first char class names!!!)
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.frame_holder, newFragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    private void showLinkEditDialog() {
        FragmentManager fm = getFragmentManager();
        LinkEntryDialogFragment linkEntryDialogFragment = LinkEntryDialogFragment.newInstance("Some Title");
        // SETS the target fragment for use later when sending results
        linkEntryDialogFragment.setTargetFragment(CreateFragment.this, 300);
        linkEntryDialogFragment.show(fm, "fragment_edit_name");
    }

    // This is called when the dialog is completed and the results have been passed
    @Override
    public void onFinishEditDialog(String title, String url) {
        postType = TYPE_LINK;
        link = new Link(title, url);
        links.add(link);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tvUrlView = new TextView(getContext());
        tvUrlView.setLayoutParams(lp);
        tvUrlView.setText("Title: " + title + ", Url: " + url);
        llLinkDisplay.addView(tvUrlView);
        ivImage.setVisibility(View.GONE);
        //Toast.makeText(getContext(), "Title: " + title + ", Url: " + url, Toast.LENGTH_SHORT).show();
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = imageSupport.getPhotoFileUri();

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto() {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        photoFile = imageSupport.getPhotoFileUri();

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
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
                ivImage.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                Bitmap selectedImage = imageSupport.loadFromUri(photoUri);
                galleryPhotoBitmap = imageSupport.bitmapToByteArray(selectedImage);
                // Load the selected image into a preview
                ivImage.setImageBitmap(selectedImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't chosen!", Toast.LENGTH_SHORT).show();
            }
        }
    }




    public boolean action(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            hideKeyboard((Activity) getContext());
            return true;
        }
        return false;
    }
}