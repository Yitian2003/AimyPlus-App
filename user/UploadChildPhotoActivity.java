/**
 * Created by Yitian on 22/02/2017.
 */

package com.aimyplus.consumer.activity.user;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.aimyplus.consumer.Manifest;
import com.aimyplus.consumer.R;
import com.aimyplus.consumer.app.GlobalApplication;
import com.aimyplus.consumer.utils.DateTimeUtil;
import com.aimyplus.photopicker.PhotoPicker;
import com.aimyplus.photopicker.PhotoPreview;
import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

public class UploadChildPhotoActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 329;
    private int REQUEST_CODE_FOR_PHOTOPICKER = 455;
    private ArrayList<String> selectedPhoto = new ArrayList<>();
    ImageLoader imageLoader;
    ImageView imageView;
    MenuItem btnSave;
    Button btnDone;
    Button btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_child_photo);

        imageView = (ImageView) findViewById(R.id.uploadImage);
        imageLoader = GlobalApplication.getInstance().getImageLoader();
        imageView.setImageResource(R.drawable.ic_action_profile);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnDone = (Button) findViewById(R.id.btnDone);

        initActionBar();

        setOnClickListen();

        showPhotoPicker();
    }

    private void showPhotoPicker() {
        checkPermission();  // dynamically check the user permission to the access of the external storage
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setSelected(selectedPhoto) // store the selected in the selected photo list
                .start(UploadChildPhotoActivity.this, REQUEST_CODE_FOR_PHOTOPICKER);
    }

    private void setOnClickListen() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPhotoPicker();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // upload photo and back to profile page
                onBackPressed();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_save_profile, menu);
        btnSave = menu.findItem(R.id.action_profile_save);
        btnSave.setVisible(false);
        return true;
    }

    private void initActionBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(GlobalApplication.getResString(R.string.upload_child_photo));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == REQUEST_CODE_FOR_PHOTOPICKER || requestCode == PhotoPreview.REQUEST_CODE)) {

            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhoto.clear();

            if (photos != null) {

                selectedPhoto.addAll(photos);
            }

            Bitmap bmImg = BitmapFactory.decodeFile(selectedPhoto.get(0));
            imageView.setImageBitmap(bmImg);
            //ivProfileImage.setImageURI(Uri.parse("file://" + selectedPhoto.get(0)));//, imageLoader);
        }
    }
}
