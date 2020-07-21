package com.example.socialgood.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialgood.R;

import java.io.File;
// PICK_PHOTO_CODE is a constant integer

import com.example.socialgood.R;
import com.example.socialgood.models.Link;
import com.example.socialgood.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateFragment} factory method to
 * create an instance of this fragment.
 */
public class CreateFragment extends Fragment implements LinkEntryDialogFragment.LinkEntryDialogListener {

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
    ImageView ivImage;
    EditText etCaption;
    Button btnSubmit;
    TextView tvLinkDisplay;
    int postType;
    private File photoFile;




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
        ivImage = view.findViewById(R.id.ivImage);
        addPicView = view.findViewById(R.id.addPicView);
        addPickFromGalleryView = view.findViewById(R.id.addPicFromGalleryView);
        addLinkView = view.findViewById(R.id.addLinkView);
        btnSubmit = view.findViewById(R.id.btnCreatePost);
        etCaption = view.findViewById(R.id.etCaption);
        tvLinkDisplay = view.findViewById(R.id.tvLinkView);
        postType = TYPE_IMAGE;

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

        post = new Post();

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
        savePost(link, photoFile, ParseUser.getCurrentUser(), "Racial Justice", caption);

    }

    public void savePost(Link link, File photo, ParseUser user, String category, String caption){
        if(postType == TYPE_LINK)
            post.setLink(link.toJSON());
        else
            post.setImage(new ParseFile(photoFile));

        post.setCaption(caption);
        post.setUser(ParseUser.getCurrentUser());
        post.addCategory(category);
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
        tvLinkDisplay.setText("Title: " + title + ", Url: " + url);
        ivImage.setVisibility(View.GONE);
        //Toast.makeText(getContext(), "Title: " + title + ", Url: " + url, Toast.LENGTH_SHORT).show();
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
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

        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
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
                // Load the image located at photoUri into selectedImage
                Bitmap selectedImage = loadFromUri(photoUri);
                // Load the selected image into a preview
                ivImage.setImageBitmap(selectedImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't chosen!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}