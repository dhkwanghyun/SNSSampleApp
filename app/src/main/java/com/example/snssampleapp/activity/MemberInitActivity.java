package com.example.snssampleapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.example.snssampleapp.UserInfo;
import com.example.snssampleapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static com.example.snssampleapp.Util.INTENT_PATH;

public class MemberInitActivity extends BasicActivity {
    private static String TAG ="MemberInitActivity";
    private ImageView profileImageView;
    private String profilePath;
    private FirebaseUser user;
    private RelativeLayout loaderLayout;
    private RelativeLayout buttonsBackgroundLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_init);
        setToolbarTitle("회원정보");

        profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(onClickListener);
        loaderLayout = findViewById(R.id.loaderLayout);
        buttonsBackgroundLayout = findViewById(R.id.buttonsBackgroundLayout);

        buttonsBackgroundLayout.setOnClickListener(onClickListener);
        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.picture).setOnClickListener(onClickListener);
        findViewById(R.id.gallery).setOnClickListener(onClickListener);


    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0 : {
                if(resultCode == Activity.RESULT_OK){
                    profilePath = data.getStringExtra(INTENT_PATH);
                    Glide.with(this)
                            .load(profilePath)
                            .centerCrop()
                            .override(500)
                            .into(profileImageView);
                    buttonsBackgroundLayout.setVisibility(View.GONE);

                }
                break;
            }
        }
    }

    private void storageUploader(){
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String phone = ((EditText) findViewById(R.id.phoneNumberEditText)).getText().toString();
        final String birthDay = ((EditText) findViewById(R.id.birthDayEditText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();

        if(name.length() > 0 && phone.length() > 9 && birthDay.length() > 5 && address.length() > 0){
            loaderLayout.setVisibility(View.VISIBLE);

            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference to "mountains.jpg"
            user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainsRef = storageRef.child("users/"+user.getUid()+"profileImage.png");

            if(profilePath == null){
                UserInfo userInfo = new UserInfo(name, phone, birthDay,address);
                storeUploder(userInfo);
            }else{
                InputStream stream = null;
                try {
                    stream = new FileInputStream(new File(profilePath));
                    UploadTask uploadTask = mountainsRef.putStream(stream);

                    uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                Log.d("실패", "실패 ~~~");
                                throw task.getException();
                            }
                            return mountainsRef.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                Log.d("성공", "성공 ~~~"+downloadUri);

                                UserInfo userInfo = new UserInfo(name, phone, birthDay,address,downloadUri.toString());
                                storeUploder(userInfo);


                            } else {
                                startToast("회원정보를 저장하는데 실패했습니다.");
                            }
                        }
                    });

                } catch (FileNotFoundException e) {
                    Log.e("로그","에러 : "+e.toString());
                }
            }



        }else{
            startToast("회원정보를 입력해 주세요.");
        }
    }

    private void storeUploder(UserInfo userInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(userInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("회원정보 등록을 성공하였습니다.");
                        loaderLayout.setVisibility(View.GONE);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("회원정보 등록을 실패하였습니다."+e);
                        loaderLayout.setVisibility(View.GONE);
                    }
                });

    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //startActivity(intent);
        startActivityForResult(intent,0);
    }


    private void startToast(String msg){
        Toast.makeText(MemberInitActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.checkButton:
                    storageUploader();
                    break;
                case R.id.profileImageView:
                    buttonsBackgroundLayout.setVisibility(View.VISIBLE);
                    //myStartActivity(CameraActivity.class);
                    break;
                case R.id.buttonsBackgroundLayout:
                    buttonsBackgroundLayout.setVisibility(View.GONE);
                    break;
                case R.id.picture:
                    myStartActivity(CameraActivity.class);
                    break;
                case R.id.gallery:
                    myStartActivity(GalleryActivity.class);
                    break;
            }
        }
    };

}