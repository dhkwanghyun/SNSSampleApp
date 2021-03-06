package com.example.snssampleapp.activity;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.snssampleapp.FirebaseHelper;
import com.example.snssampleapp.PostInfo;
import com.example.snssampleapp.R;
import com.example.snssampleapp.listener.OnPostListener;
import com.example.snssampleapp.view.ContentsItemView;
import com.example.snssampleapp.view.ReadContentsView;

import static com.example.snssampleapp.Util.INTENT_PATH;

public class PostActivity extends BasicActivity {
    private PostInfo postInfo;
    private FirebaseHelper firebaseHelper;
    private ReadContentsView readContentsView;
    private LinearLayout contentsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postInfo = (PostInfo)getIntent().getSerializableExtra("postInfo");


        contentsLayout = findViewById(R.id.contentsLayout);
        readContentsView = findViewById(R.id.readContentsView);
        uiUpdate();


        firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.setOnPostListener(onPostListener);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0 :
                if(resultCode == Activity.RESULT_OK){
                    postInfo = (PostInfo) data.getSerializableExtra("postInfo");
                    contentsLayout.removeAllViews();
                    uiUpdate();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                firebaseHelper.storageDelete(postInfo);
                finish();
                return true;

            case R.id.modify:
                myStartActivity(WritePostActivity.class,postInfo);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            Log.e("로그", "삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그", "수정 성공");
        }
    };

    private void uiUpdate(){
        readContentsView.setPostInfo(postInfo);
        setToolbarTitle(postInfo.getTitle());
    }

    private void myStartActivity(Class c,PostInfo postInfo){
        Intent intent = new Intent(this,c);
        intent.putExtra("postInfo",postInfo);
        startActivityForResult(intent,0);
    }
    
    
}
