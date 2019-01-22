package com.example.administrator.firebase_social_media_app;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ViewPostActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ListView usersWHoSendPost, viewUserPost;
    private ArrayList<String> arrayListUSers, arrayListDulpicates;
    private FirebaseAuth mAuth;
    private ArrayAdapter arrayAdapter;
    private ImageView imageView;
    private TextView imageDescription;
    private ArrayList<DataSnapshot> dataSnapshots;
    private ArrayList<DataSet> dataSetArrayList;
    //private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);
        initailizeAllFields();
        usersWHoSendPost.setAdapter(arrayAdapter);
        usersWHoSendPost.setOnItemClickListener(this);
        FirebaseDatabase.getInstance().getReference().child("my_users").child(mAuth.getUid())
                .child("received_posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //count++;
                Log.i("datasnap", "onChildAdded: "+dataSnapshot);
                dataSnapshots.add(dataSnapshot);
                String username = (String) dataSnapshot.child("fromWhom").getValue();
                if (!(arrayListUSers.contains(username))) {
                    arrayListUSers.add(username);
                }
                arrayListDulpicates.add(username);
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
    }

    private void initailizeAllFields() {
        mAuth = FirebaseAuth.getInstance();
        usersWHoSendPost = findViewById(R.id.userWhoSendPostListView);
        viewUserPost = findViewById(R.id.viewPostListView);
        arrayListUSers = new ArrayList<>();
        arrayListDulpicates = new ArrayList<>();
        dataSetArrayList = new ArrayList<>();
        dataSnapshots = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayListUSers);
        //count = 0;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int totalPostByUser = countPostByOneUsers(arrayListUSers.get(position));
        int start = arrayListDulpicates.indexOf(arrayListUSers.get(position));
        CustomAdapter customAdapter;
        DataSnapshot dataSnapshot;
  //      String key;
        for (int i = start ;i<start+totalPostByUser;i++){
            try {
                dataSnapshot = dataSnapshots.get(i);
   //             Log.i("datasnapshot",dataSnapshot+"");
  //              key = (String)dataSnapshot.getKey();
   //             Log.i("datasnapkey",key);
    //            Log.i("datasnap", "onItemClick: "+ dataSnapshot.child("imageLink").getValue());
                dataSetArrayList.add(new DataSet((String)dataSnapshot.child("des").getValue(),(String)dataSnapshot.child("imageLink").getValue()));
  //             Log.i("datasnapdes",(String)dataSnapshot.child("des").getValue());
 //              Log.i("datasnapimage",(String)dataSnapshot.child("imageLink").getValue());
            } catch (Exception e) {
                Log.i("try",e.getMessage());
                e.printStackTrace();
            }
        }
     //   Log.i("datasnapshotarraylist",dataSetArrayList.toString());
        customAdapter = new CustomAdapter(getApplicationContext(),dataSetArrayList);
        viewUserPost.setAdapter(customAdapter);
    }

    private int countPostByOneUsers(String username) {
        int no_Of_Posts = 0;
        for (String user : arrayListDulpicates) {
            if (user.equals(username)) {
                no_Of_Posts++;
            }
        }
        return no_Of_Posts;
    }
}
