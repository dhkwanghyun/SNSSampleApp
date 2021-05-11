package com.example.snssampleapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.snssampleapp.PostInfo;
import com.example.snssampleapp.R;
import com.example.snssampleapp.Util;
import com.example.snssampleapp.view.ContentsItemView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import static com.example.snssampleapp.Util.GALLERY_IMAGE;
import static com.example.snssampleapp.Util.GALLERY_VIDEO;
import static com.example.snssampleapp.Util.INTENT_MEDIA;
import static com.example.snssampleapp.Util.INTENT_PATH;
import static com.example.snssampleapp.Util.isImageFile;
import static com.example.snssampleapp.Util.isStorageUrl;
import static com.example.snssampleapp.Util.isVideoFile;
import static com.example.snssampleapp.Util.showToast;
import static com.example.snssampleapp.Util.storageUrlToName;
import static com.example.snssampleapp.Util.storageUrlToType;


public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WritePostActivity";
    private Util util;
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private ArrayList<String> formatList = new ArrayList<>();
    LinearLayout parent;
    private int pathCount,successCount;
    private PostInfo postInfo;

    RelativeLayout buttonsBackgroundLayout;
    private RelativeLayout loaderLayout;

    private ImageView selectedImageView;
    private EditText selectedEditText;

    private EditText contentsEditText;
    private EditText titleEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);
        setToolbarTitle("게시글 작성");

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);
        loaderLayout = findViewById(R.id.loaderLayout);
        contentsEditText = findViewById(R.id.contentsEditText);
        titleEditText = findViewById(R.id.titleEditText);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);

        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.video).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.videoModify).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);
        contentsEditText.setOnFocusChangeListener(onFocusChangeListener);
        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    selectedEditText = null;
                }
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");
        postInit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0 :
                if(resultCode == Activity.RESULT_OK){
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path);

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    if(selectedEditText == null){
                        parent.addView(contentsItemView);
                    }else{
                        for(int i = 0; i < parent.getChildCount(); i++){
                            if(parent.getChildAt(i) == selectedEditText.getParent()){
                                parent.addView(contentsItemView,i+1);
                                break;
                            }
                        }
                    }

                    contentsItemView.setImage(path);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) view;
                        }
                    });
                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View) selectedImageView.getParent())-1,path);
                    Glide.with(this)
                            .load(path)
                            .centerCrop()
                            .override(1000)
                            .into(selectedImageView);
                }
                break;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.check:
                    storageUpdate();
                    break;
                case R.id.image:
                    myStartActivity(GalleryActivity.class,GALLERY_IMAGE,0);
                    break;
                case R.id.video:
                    myStartActivity(GalleryActivity.class,GALLERY_VIDEO,0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if(buttonsBackgroundLayout.getVisibility() == View.VISIBLE){
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    myStartActivity(GalleryActivity.class,GALLERY_IMAGE,1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.videoModify:
                    myStartActivity(GalleryActivity.class,GALLERY_VIDEO,1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.delete:
                    final View selectedView = (View) selectedImageView.getParent();
                    String path = pathList.get(parent.indexOfChild(selectedView) - 1);
                    if(isStorageUrl(path)){
                        StorageReference desertRef = storageRef.child("posts/"+postInfo.getId()+"/"+storageUrlToName(path));
                        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                showToast(WritePostActivity.this,"파일 삭제 성공");
                                pathList.remove(parent.indexOfChild(selectedView)-1);
                                parent.removeView(selectedView);
                                buttonsBackgroundLayout.setVisibility(View.GONE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                showToast(WritePostActivity.this,"파일 삭제 실패");
                            }
                        });
                    }else{
                        pathList.remove(parent.indexOfChild(selectedView)-1);
                        parent.removeView(selectedView);
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
            }
        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if(b){
                selectedEditText = (EditText) view;
            }
        }
    };

    private void storageUpdate(){
        final String title = ((EditText) findViewById(R.id.titleEditText)).getText().toString();

        if(title.length() > 0){
            final ArrayList<String> contentsList = new ArrayList<>();
            final ArrayList<String> formatList = new ArrayList<>();
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

            final DocumentReference documentReference = postInfo == null ?
                    firebaseFirestore.collection("posts").document() : firebaseFirestore.collection("posts").document(postInfo.getId());
            final Date date = postInfo == null ? new Date() : postInfo.getCreatedAt();

            loaderLayout.setVisibility(View.VISIBLE);

            for(int i = 0; i< parent.getChildCount(); i++){
                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for(int ii =0; ii < linearLayout.getChildCount(); ii++){
                    View view = linearLayout.getChildAt(ii);
                    if(view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                            formatList.add("text");
                        }
                    }else if(!isStorageUrl(pathList.get(pathCount))){
                        String path = pathList.get(pathCount);
                        successCount++;
                        contentsList.add(path);

                        if(isImageFile(path)){
                            formatList.add("image");
                        }else if(isVideoFile(path)){
                            formatList.add("video");
                        }else{
                            formatList.add("text");
                        }

                        String[] pathArray = path.split("\\.");
                        final StorageReference mountainsRef = storageRef.child("posts/" + documentReference.getId() + "/" + pathCount + "." + pathArray[pathArray.length - 1]);

                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));

                            StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setCustomMetadata("index",""+(contentsList.size()-1))
                                    .build();

                            UploadTask uploadTask = mountainsRef.putStream(stream,metadata);

                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));

                                    mountainsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            successCount--;
                                            contentsList.set(index,uri.toString());
                                            if(successCount == 0){
                                                PostInfo postInfo = new PostInfo(title, contentsList, formatList, user.getUid(),date,documentReference.getId());
                                                storeUploader(documentReference,postInfo);
                                            }
                                        }
                                    });

                                }
                            });

                        } catch (FileNotFoundException e) {
                            Log.e("로그","에러 : "+e.toString());
                        }

                        pathCount++;
                    }else{
                        String path = pathList.get(pathCount);
                        contentsList.add(path);
                        if(isImageFile(storageUrlToType(path))){
                            formatList.add("image");
                        }else if(isVideoFile(storageUrlToType(path))){
                            formatList.add("video");
                        }else{
                            formatList.add("text");
                        }
                        pathCount++;
                    }
                }
            }
            if(successCount == 0){
                storeUploader(documentReference, new PostInfo(title, contentsList, formatList, user.getUid(),date,documentReference.getId()));
            }

        }else{
            showToast(WritePostActivity.this,"제목을 입력해 주세요.");
        }
    }

    private void storeUploader(DocumentReference documentReference,final PostInfo postInfo){
        documentReference.set(postInfo.getPostInfo())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    showToast(WritePostActivity.this,"저장성공");
                    loaderLayout.setVisibility(View.GONE);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("postInfo",postInfo);
                    setResult(Activity.RESULT_OK,resultIntent);
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    showToast(WritePostActivity.this,"저장실패");
                    loaderLayout.setVisibility(View.GONE);
                }
            });

    }

    private void postInit(){
        if(postInfo != null){
            titleEditText.setText(postInfo.getTitle());

            ArrayList<String> contentsList = postInfo.getContents();

            for(int i=0; i< contentsList.size(); i++){
                String contents = contentsList.get(i);
                if(isStorageUrl(contents)){
                    pathList.add(contents);

                    ContentsItemView contentsItemView = new ContentsItemView(this);

                    parent.addView(contentsItemView);

                    contentsItemView.setImage(contents);
                    contentsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) view;
                        }
                    });

                    contentsItemView.setOnFocusChangeListener(onFocusChangeListener);

                    if(i < contentsList.size() - 1){
                        String nextContents = contentsList.get(i + 1);
                        if(!isStorageUrl(nextContents)){
                            contentsItemView.setText(nextContents);
                        }
                    }

                }else if(i == 0){
                    contentsEditText.setText(contents);
                }
            }

        }
    }

    private void myStartActivity(Class c,int media,int requestCode){
        Intent intent = new Intent(this,c);
        intent.putExtra(INTENT_MEDIA,media);
        startActivityForResult(intent,requestCode);
    }

}
