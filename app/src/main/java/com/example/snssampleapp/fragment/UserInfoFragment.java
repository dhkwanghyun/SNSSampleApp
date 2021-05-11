package com.example.snssampleapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.snssampleapp.PostInfo;
import com.example.snssampleapp.R;
import com.example.snssampleapp.UserInfo;
import com.example.snssampleapp.activity.MemberInitActivity;
import com.example.snssampleapp.activity.WritePostActivity;
import com.example.snssampleapp.adapter.UserListAdapter;
import com.example.snssampleapp.listener.OnPostListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoFragment extends Fragment {

    private String TAG = "UserInfoFragment";


    public UserInfoFragment() {
        // Required empty public constructor
    }

    public static UserInfoFragment newInstance(String param1, String param2) {
        UserInfoFragment fragment = new UserInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_user_info, container, false);
        final ImageView profileImageView = view.findViewById(R.id.profileImageView);
        final TextView nameTextView = view.findViewById(R.id.nameTextView);
        final TextView phoneNumberTextView = view.findViewById(R.id.phoneNumberTextView);
        final TextView birthDayTextView = view.findViewById(R.id.birthDayTextView);
        final TextView addressTextView = view.findViewById(R.id.addressTextView);


        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if(document.exists()){
                            if(document.getData().get("name") != null){
                                Glide.with(getActivity())
                                        .load(document.getData().get("name"))
                                        .centerCrop()
                                        .override(500)
                                        .into(profileImageView);
                            }
                            nameTextView.setText(document.getData().get("name").toString());
                            phoneNumberTextView.setText(document.getData().get("phone").toString());
                            birthDayTextView.setText(document.getData().get("birthDay").toString());
                            addressTextView.setText(document.getData().get("address").toString());
                        }else{

                        }
                    }
                } else {

                }
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //postUpdate();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0 :
                if(data != null){
                    //postUpdate();
                }
                break;

        }
    }

}