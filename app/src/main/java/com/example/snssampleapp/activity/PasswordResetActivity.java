package com.example.snssampleapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.snssampleapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.snssampleapp.Util.showToast;

public class PasswordResetActivity extends BasicActivity {
    private static String TAG ="SignActivity";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
        setToolbarTitle("비밀번호 찾기");

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.sendButton).setOnClickListener(onClickListener);

    }

    private void send(){
        String email = ((EditText) findViewById(R.id.emailEditText)).getText().toString();


        FirebaseAuth auth = FirebaseAuth.getInstance();

        if(email.length() > 0){
            final RelativeLayout loaderLayout = findViewById(R.id.loaderLayout);
            loaderLayout.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loaderLayout.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                showToast(PasswordResetActivity.this,"이메일을 보냈습니다.");
                            }
                        }
                    });

        }else{
            showToast(PasswordResetActivity.this,"이메일을 입력해 주세요.");
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.sendButton:
                    send();
                    break;
            }
        }
    };
}