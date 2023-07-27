package com.khoa.multiquiz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoa.multiquiz.adapter.HistoryAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    Button EditProfileButton, LogoutButton;
    ImageView AvatarImage;
    TextView UserDisplayName;
    FirebaseStorage storage;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;
    StorageReference storageReferenceAvatar;
    CollectionReference collectionReferenceUserData;
    SharedPreferences sharedPreferences;
    RecyclerView HistoryRecyclerView;
    ArrayList<DuelMatchHistoryLog> duelMatchHistoryLogs;
    HistoryAdapter historyAdapter;
    CollectionReference collectionReferenceMatchData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        EditProfileButton = findViewById(R.id.EditProfileButton);
        LogoutButton = findViewById(R.id.LogoutButon);
        AvatarImage = findViewById(R.id.AvatarImage);
        UserDisplayName = findViewById(R.id.UserDisplayName);
        HistoryRecyclerView = findViewById(R.id.HistoryRecyclerView);
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);

        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();

        HistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        duelMatchHistoryLogs = new ArrayList<>();
        historyAdapter = new HistoryAdapter(Profile.this, duelMatchHistoryLogs, firestore, storage);
        HistoryRecyclerView.setAdapter(historyAdapter);

        getUserAvatarImage();
        getUserData();
        getUserMatchHistory();

        historyAdapter.setOnClickListener(new HistoryAdapter.OnClickListener() {
            @Override
            public void onClick(int position, DuelMatchHistoryLog duelMatchHistoryLog) {
                Intent intent = new Intent(Profile.this, DuelResult.class);
                intent.putExtra("SessionID", duelMatchHistoryLog.getSessionID());
                startActivity(intent);
            }
        });
        EditProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Profile.this, ProfileEdit.class);
                startActivity(intent);
            }
        });

        LogoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

    }

    private void getUserData(){
        collectionReferenceUserData = firestore.collection("UserData");
        collectionReferenceUserData.document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String displayName = documentSnapshot.getString("displayName");
                UserDisplayName.setText(displayName);
            }
        });
    }
    private void getUserAvatarImage(){
        storageReferenceAvatar = storage.getReference();
        String childPath = "users/" + currentUser.getUid() + "/avatar.jpg";
            storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            AvatarImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    storageReferenceAvatar.child("DefaultAvatar/GeneralProfileImage.png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Picasso.get().load(imageUrl).into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    AvatarImage.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });
                        }
                    });
                }
            });
        }



    private void getUserMatchHistory(){
        collectionReferenceMatchData = firestore.collection("DuelMatchHistoryLog");
        Query matchQuery = collectionReferenceMatchData.where(Filter.or(
                Filter.equalTo("ownerUID", currentUser.getUid()),
                Filter.equalTo("opponentUID", currentUser.getUid())
        ));
        matchQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots){
                    DuelMatchHistoryLog duelMatchHistoryLog = document.toObject(DuelMatchHistoryLog.class);

                    duelMatchHistoryLogs.add(duelMatchHistoryLog);
                }
                historyAdapter.notifyDataSetChanged();
            }
        });
    }

    private void logoutUser(){
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        MAuth.signOut();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent i = new Intent(Profile.this, Login.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}
