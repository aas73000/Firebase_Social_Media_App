package com.example.administrator.firebase_social_media_app;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.loadingview.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;
import com.roger.catloadinglibrary.CatLoadingView;
import com.shashank.sony.fancytoastlib.FancyToast;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private ExtendedEditText email, password, username;
    private Button signin, signup;
    private CatLoadingView catLoadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initalizeAllFields();

        //set listner to button
        signup.setOnClickListener(this);
        signin.setOnClickListener(this);

    }

    private void initalizeAllFields() {
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.activityMainEmail);
        username = findViewById(R.id.activityMainUsername);
        password = findViewById(R.id.activityMainPassword);
        signin = findViewById(R.id.activityMainSignInButton);
        signup = findViewById(R.id.activityMainSignUpButton);
        catLoadingView = new CatLoadingView();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseApp.initializeApp(this);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FancyToast.makeText(this, "Transition tyo next activity", FancyToast.LENGTH_LONG,
                    FancyToast.SUCCESS, true).show();
            transitionToSocialMediaActivity();

        }
    }

    @Override
    public void onClick(View v) {
        catLoadingView.show(getSupportFragmentManager(),"Sign in");
        switch (v.getId()) {
            case R.id.activityMainSignUpButton:
                signupUser();
                break;
            case R.id.activityMainSignInButton:
                signinUser();
        }

    }

    private void signinUser() {
        mAuth.signInWithEmailAndPassword(email.getText().toString(),
                password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    transitionToSocialMediaActivity();
                } else {
                    FancyToast.makeText(MainActivity.this, "error sign in", FancyToast.LENGTH_LONG,
                            FancyToast.ERROR, true).show();
                    catLoadingView.dismiss();
                }
            }
        });
    }

    private void signupUser() {
        mAuth.createUserWithEmailAndPassword(email.getText().toString(),
                password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().getReference().child("my_users")
                                    .child(task.getResult().getUser().getUid())
                                    .child("username").setValue(username.getText().toString());
                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(username.getText().toString()).build();
                    FirebaseAuth.getInstance().getCurrentUser().updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                FancyToast.makeText(MainActivity.this, "Sign up successfully", FancyToast.LENGTH_LONG,
                                        FancyToast.SUCCESS, true).show();
                            }
                        }
                    });
                    transitionToSocialMediaActivity();
                } else {
                    FancyToast.makeText(MainActivity.this, "error sign up", FancyToast.LENGTH_LONG,
                            FancyToast.ERROR, true).show();
                    catLoadingView.dismiss();
                }
            }
        });
    }

    private void transitionToSocialMediaActivity() {
        Intent intent = new Intent(this, SocialMediaActivity.class);
        startActivity(intent);
        finish();
    }

}
