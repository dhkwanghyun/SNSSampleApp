package com.example.snssampleapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.example.snssampleapp.MemberInfo;
import com.example.snssampleapp.R;
import com.example.snssampleapp.WriteInfo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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


public class WritePostActivity extends BasicActivity {
    private static final String TAG = "WritePostActivity";
    private FirebaseUser user;
    private ArrayList<String> pathList = new ArrayList<>();
    LinearLayout parent;
    private int pathCount,successCount;

    RelativeLayout buttonsBackgroundLayout;

    private ImageView selectedImageView;
    private EditText selectedEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        parent = findViewById(R.id.contentsLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        findViewById(R.id.check).setOnClickListener(onClickListener);
        findViewById(R.id.image).setOnClickListener(onClickListener);
        findViewById(R.id.video).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify).setOnClickListener(onClickListener);
        findViewById(R.id.videoModify).setOnClickListener(onClickListener);
        findViewById(R.id.delete).setOnClickListener(onClickListener);
        findViewById(R.id.contentsEditText).setOnFocusChangeListener(onFocusChangeListener);
        findViewById(R.id.titleEditText).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    selectedEditText = null;
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0 :
                if(resultCode == Activity.RESULT_OK){
                    String profilePath = data.getStringExtra("profilePath");
                    pathList.add(profilePath);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    LinearLayout linearLayout = new LinearLayout(WritePostActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    if(selectedEditText == null){
                        parent.addView(linearLayout);
                    }else{
                        for(int i = 0; i < parent.getChildCount(); i++){
                            if(parent.getChildAt(i) == selectedEditText.getParent()){
                                parent.addView(linearLayout,i+1);
                                break;
                            }
                        }
                    }

                    ImageView imageView = new ImageView(WritePostActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) view;
                        }
                    });
                    Glide.with(this)
                            .load(profilePath)
                            .centerCrop()
                            .override(1000)
                            .into(imageView);
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(WritePostActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);



                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    String profilePath = data.getStringExtra("profilePath");
                    Glide.with(this)
                            .load(profilePath)
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
                    myStartActivity(GalleryActivity.class,"image",0);
                    break;
                case R.id.video:
                    myStartActivity(GalleryActivity.class,"video",0);
                    break;
                case R.id.buttonsBackgroundLayout:
                    if(buttonsBackgroundLayout.getVisibility() == View.VISIBLE){
                        buttonsBackgroundLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.imageModify:
                    myStartActivity(GalleryActivity.class,"image",1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.videoModify:
                    myStartActivity(GalleryActivity.class,"video",1);
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.delete:
                    parent.removeView((View) selectedImageView.getParent());
                    buttonsBackgroundLayout.setVisibility(View.GONE);
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
            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore filebaseFirestore = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = filebaseFirestore.collection("posts").document();

            for(int i = 0; i< parent.getChildCount(); i++){

                LinearLayout linearLayout = (LinearLayout) parent.getChildAt(i);
                for(int ii =0; ii < linearLayout.getChildCount(); ii++){
                    View view = linearLayout.getChildAt(ii);
                    if(view instanceof EditText) {
                        String text = ((EditText) view).getText().toString();
                        if (text.length() > 0) {
                            contentsList.add(text);
                        }
                    }else{

                        contentsList.add(pathList.get(pathCount));

                        final StorageReference mountainsRef = storageRef.child("posts/"+documentReference.getId()+"/"+pathCount+".png");

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
                                            Log.d("로그","Uri : "+uri);
                                            contentsList.set(index,uri.toString());
                                            successCount++;
                                            if(pathList.size() == successCount){
                                                WriteInfo writeInfo = new WriteInfo(title, contentsList, user.getUid(),new Date());
                                                storeUploader(documentReference,writeInfo);
                                            }
                                        }
                                    });

                                }
                            });

                        } catch (FileNotFoundException e) {
                            Log.e("로그","에러 : "+e.toString());
                        }

                        pathCount++;
                    }

                }


            }


        }else{
            startToast("제목을 입력해 주세요.");
        }
    }

    private void storeUploader(DocumentReference documentReference, WriteInfo writeInfo){
        documentReference.set(writeInfo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    startToast("저장성공");
                    finish();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    startToast("저장실패");
                }
            });

    }

    private void startToast(String msg){
        Toast.makeText(WritePostActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c,String media,int requestCode){
        Intent intent = new Intent(this,c);
        intent.putExtra("media",media);
        startActivityForResult(intent,requestCode);
    }

}
