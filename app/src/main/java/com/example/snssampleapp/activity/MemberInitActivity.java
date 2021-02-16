package com.example.snssampleapp.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.snssampleapp.MemberInfo;
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

public class MemberInitActivity extends AppCompatActivity {
    private static String TAG ="MemberInitActivity";
    private ImageView profileImageView;
    private String profilePath;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setOnClickListener(onClickListener);

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
                    profilePath = data.getStringExtra("profilePath");
                    Log.e("profilePath :",profilePath);
                    Bitmap bmp = BitmapFactory.decodeFile(profilePath);
                    profileImageView.setImageBitmap(bmp);

                }
                break;
            }
        }
    }

    private void profileUpdate(){
        final String name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
        final String phone = ((EditText) findViewById(R.id.phoneNumberEditText)).getText().toString();
        final String birthDay = ((EditText) findViewById(R.id.birthDayEditText)).getText().toString();
        final String address = ((EditText) findViewById(R.id.addressEditText)).getText().toString();

        if(name.length() > 0 && phone.length() > 9 && birthDay.length() > 5 && address.length() > 0){

            FirebaseStorage storage = FirebaseStorage.getInstance();

            // Create a storage reference from our app
            StorageReference storageRef = storage.getReference();

            // Create a reference to "mountains.jpg"
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final StorageReference mountainsRef = storageRef.child("users/"+user.getUid()+"profileImage.png");

            if(profilePath == null){
                MemberInfo memberInfo = new MemberInfo(name, phone, birthDay,address);
                uploader(memberInfo);
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

                                MemberInfo memberInfo = new MemberInfo(name, phone, birthDay,address,downloadUri.toString());
                                uploader(memberInfo);


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

    private void uploader(MemberInfo memberInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getUid()).set(memberInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        startToast("회원정보 등록을 성공하였습니다.");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        startToast("회원정보 등록을 실패하였습니다."+e);
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
                    profileUpdate();
                    break;
                case R.id.profileImageView:
                    CardView cardView = findViewById(R.id.buttonsCardView);
                    if(cardView.getVisibility() == View.VISIBLE){
                        cardView.setVisibility(View.GONE);
                    }else{
                        cardView.setVisibility(View.VISIBLE);
                    }
                    //myStartActivity(CameraActivity.class);
                    break;
                case R.id.picture:
                    myStartActivity(CameraActivity.class);
                    break;
                case R.id.gallery:
                    if (ContextCompat.checkSelfPermission(
                            MemberInitActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED) {
                        myStartActivity(GalleryActivity.class);
                    } else if (ActivityCompat.shouldShowRequestPermissionRationale(MemberInitActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(MemberInitActivity.this,
                                new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                1);
                        startToast("권한을 허용해 주세요.");
                    }

                    break;
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    myStartActivity(GalleryActivity.class);
                }  else {
                    startToast("권한을 허용해 주세요.");
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

}