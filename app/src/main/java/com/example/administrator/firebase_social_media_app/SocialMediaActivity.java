package com.example.administrator.firebase_social_media_app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.github.loadingview.LoadingDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.roger.catloadinglibrary.CatLoadingView;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class SocialMediaActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private ImageView imageView;
    private Bitmap bitmap;
    private Button button;
    private ExtendedEditText extendedEditText;
    CatLoadingView catLoadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);
        mAuth = FirebaseAuth.getInstance();
        initializeAllFields();
        imageView.setOnClickListener(this);
        button.setOnClickListener(this);
    }

    private void initializeAllFields() {
        imageView = findViewById(R.id.socialMediaShareImage);
        button = findViewById(R.id.socialMediaShareButton);
        catLoadingView = new CatLoadingView();
        extendedEditText = findViewById(R.id.socialMediaPostDesc);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        logoutUser();
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.social_media_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        logoutUser();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.socialMediaShareImage:
               getImageFromStorage();
               break;
           case R.id.socialMediaShareButton:
               catLoadingView.show(getSupportFragmentManager(),"");
               //loadingDialog = LoadingDialog.Companion.get(SocialMediaActivity.this).show();
               uploadImageToServer();
               break;
               default:
                   Log.i("ERROR INSWITCH","id mismatch");
       }

    }

    private void uploadImageToServer() {
        if (bitmap != null) {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            String imageIdentifier = UUID.randomUUID()+".png";
            UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("Users_Image")
                    .child(imageIdentifier).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FancyToast.makeText(SocialMediaActivity.this,e.getMessage(),FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,true).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    FancyToast.makeText(SocialMediaActivity.this,"Upload successfully",FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS,true).show();
                    catLoadingView.dismiss();
                    extendedEditText.setVisibility(View.VISIBLE);
                }
            });

        } else {
            FancyToast.makeText(SocialMediaActivity.this, "Please select a image by clicking on the above sample image",
                    FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
        }
        //    catLoadingView.dismiss();
       //loadingDialog.hide();
    }

    private void getImageFromStorage() {
        Permissions.check(this, Manifest.permission.READ_EXTERNAL_STORAGE, "Please provide read permission so that we can access photos",
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        FancyToast.makeText(SocialMediaActivity.this,"Permission granted to access the gallery",FancyToast.LENGTH_LONG,
                                FancyToast.SUCCESS,true).show();
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2000);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 2000 && resultCode == Activity.RESULT_OK && data != null){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
