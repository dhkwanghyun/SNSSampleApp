package com.example.snssampleapp;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.example.snssampleapp.activity.MainActivity;
import com.example.snssampleapp.listener.OnPostListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static com.example.snssampleapp.Util.isStorageUrl;
import static com.example.snssampleapp.Util.showToast;
import static com.example.snssampleapp.Util.storageUrlToName;

public class FirebaseHelper {
    private int successCount;
    private Activity activity;
    private OnPostListener onPostListener;

    public FirebaseHelper(Activity activity){
        this.activity = activity;
    }

    public void setOnPostListener(OnPostListener onPostListener){
        this.onPostListener = onPostListener;
    }


    public void storageDelete(final PostInfo postInfo){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final String id = postInfo.getId();
        ArrayList<String> contentsList = postInfo.getContents();

        for(int i=0; i< contentsList.size(); i++){
            String contents = contentsList.get(i);

            if(isStorageUrl(contents)){
                successCount++;
                StorageReference desertRef = storageRef.child("posts/"+id+"/"+storageUrlToName(contents));

                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        successCount--;
                        storeDelete(id,postInfo);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToast(activity,"게시글을 삭제에 실패하였습니다.11111");
                    }
                });

            }
        }
        if(contentsList.size() == 0){
            storeDelete(id,postInfo);
        }
        //storeDelete(id);
    }

    private void storeDelete(String id, final PostInfo postInfo){
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        if(successCount == 0){
            firebaseFirestore.collection("posts").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(activity,"게시글을 삭제하였습니다.");
                            onPostListener.onDelete(postInfo);
                            //postUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(activity,"게시글을 삭제에 실패하였습니다.222222");
                        }
                    });
        }
    }
}
