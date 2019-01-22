package com.example.administrator.firebase_social_media_app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;
import com.roger.catloadinglibrary.CatLoadingView;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class SocialMediaActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private FirebaseAuth mAuth;
    private ImageView imageView;
    private Bitmap bitmap;
    private Button button;
    private ArrayAdapter arrayAdapter;
    private ArrayList<String> usernamesArrayList, uidsArrayList;
    private CatLoadingView catLoadingView;
    private ListView listView;
    private String imageDownloadLink;
    private String imageIdentifier;
    private ExtendedEditText extendedEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);
        mAuth = FirebaseAuth.getInstance();
        initializeAllFields();
        imageView.setOnClickListener(this);
        button.setOnClickListener(this);
        arrayAdapter = new ArrayAdapter(SocialMediaActivity.this, android.R.layout.simple_list_item_1, usernamesArrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(this);
    }

    private void initializeAllFields() {
        imageView = findViewById(R.id.socialMediaShareImage);
        button = findViewById(R.id.socialMediaShareButton);
        catLoadingView = new CatLoadingView();
        usernamesArrayList = new ArrayList<>();
        uidsArrayList = new ArrayList<>();
        listView = findViewById(R.id.socialMediaListView);
        extendedEditText = findViewById(R.id.socialMediaPostDesc);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.socialMediaViewPostMenu:
                Intent intent = new Intent(this, ViewPostActivity.class);
                startActivity(intent);
                break;
            case R.id.socialMediaLogOutMenu:
                logoutUser();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.social_media_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        logoutUser();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.socialMediaShareImage:
                getImageFromStorage();
                break;
            case R.id.socialMediaShareButton:
                catLoadingView.show(getSupportFragmentManager(), "");
                //loadingDialog = LoadingDialog.Companion.get(SocialMediaActivity.this).show();
                uploadImageToServer();
                break;
            default:
                Log.i("ERROR INSWITCH", "id mismatch");
        }

    }

    private void uploadImageToServer() {
        if (bitmap != null) {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();
            imageIdentifier = UUID.randomUUID() + ".png";
            UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("Users_Image")
                    .child(imageIdentifier).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    FancyToast.makeText(SocialMediaActivity.this, e.getMessage(), FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS, true).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    FancyToast.makeText(SocialMediaActivity.this, "Upload successfully", FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS, true).show();
                    catLoadingView.dismiss();
                    findViewById(R.id.socialMediaEdittext).setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference().child("my_users").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            uidsArrayList.add(dataSnapshot.getKey());
                            String username = (String) dataSnapshot.child("username").getValue();
                            usernamesArrayList.add(username);
                            arrayAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    task.getResult().getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                imageDownloadLink = task.getResult().toString();
                            }
                        }
                    });
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
                        FancyToast.makeText(SocialMediaActivity.this, "Permission granted to access the gallery", FancyToast.LENGTH_LONG,
                                FancyToast.SUCCESS, true).show();
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, 2000);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2000 && resultCode == Activity.RESULT_OK && data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> dataMap = new HashMap<>();
        dataMap.put("fromWhom", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        dataMap.put("imageIdentifier", imageIdentifier);
        dataMap.put("imageLink", imageDownloadLink);
        dataMap.put("des", extendedEditText.getText().toString());
        FirebaseDatabase.getInstance().getReference().child("my_users").child(uidsArrayList.get(position))
                .child("received_posts").push().setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FancyToast.makeText(SocialMediaActivity.this, "data send", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                }
            }
        });
    }
}
