package com.example.snssampleapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.snssampleapp.R;
import com.example.snssampleapp.adapter.GalleryAdapter;

import java.util.ArrayList;

import static com.example.snssampleapp.Util.GALLERY_IMAGE;
import static com.example.snssampleapp.Util.GALLERY_VIDEO;
import static com.example.snssampleapp.Util.INTENT_MEDIA;
import static com.example.snssampleapp.Util.showToast;

public class GalleryActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setToolbarTitle("갤러리");

        if (ContextCompat.checkSelfPermission(
                GalleryActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            recyclerInit();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(GalleryActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(GalleryActivity.this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    1);
        } else {
            ActivityCompat.requestPermissions(GalleryActivity.this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                    1);
            showToast(GalleryActivity.this,getResources().getString(R.string.please_grant_permission));
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    recyclerInit();
                }  else {
                    finish();
                    showToast(GalleryActivity.this,getResources().getString(R.string.please_grant_permission));
                }
        }
    }

    private void recyclerInit(){
        final int numberOfColumns = 3;

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        //String[] myDataset = {"강아지","고양이","드래곤","치킨"};
        RecyclerView.Adapter adapter = new GalleryAdapter(this,getImagesPath(this));
        recyclerView.setAdapter(adapter);
    }

    public ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        String PathOfImage = null;
        String[] projection;

        Intent intent = getIntent();
        final int media = intent.getIntExtra(INTENT_MEDIA,GALLERY_IMAGE);
        if(media == GALLERY_VIDEO){
            uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] {MediaStore.MediaColumns.DATA,MediaStore.Video.Media.BUCKET_DISPLAY_NAME};
        }else{
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            projection = new String[] {MediaStore.MediaColumns.DATA,MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        }

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);
        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

}
