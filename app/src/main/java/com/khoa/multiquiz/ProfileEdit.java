package com.khoa.multiquiz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.view.Change;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileEdit extends AppCompatActivity {
    Button ChangeProfilePicture, SaveAll;
    Uri selectedImageUri;
    ImageView UserAvatarProfileEdit;
    EditText UsernameTextInput;
    FirebaseUser currentUser;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    StorageReference storageReferenceUploadAvatar;
    ActivityResultLauncher<Intent> imagePickerLauncher;
    CollectionReference collectionReferenceUserData;
    StorageReference storageReferenceAvatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);


        UserAvatarProfileEdit = findViewById(R.id.UserAvatarProfileEdit);
        ChangeProfilePicture = findViewById(R.id.ChangeProfilePictureButton);
        SaveAll = findViewById(R.id.SaveAll);
        UsernameTextInput = findViewById(R.id.UsernameTextInput);

        storage = FirebaseStorage.getInstance();
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        currentUser = MAuth.getCurrentUser();
        getUserData();
        getUserAvatarImage();

        // Initialize the ActivityResultLauncher
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Handle the selected image here
                        if (result.getData() != null) {
                            selectedImageUri = result.getData().getData();

                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                                UserAvatarProfileEdit.setImageBitmap(bitmap);
                            } catch (IOException e){
                                e.printStackTrace();
                            }

                        }
                    }
                }
        );

        ChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectAvatarImage();
            }
        });

        SaveAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UsernameTextInput.getText().toString().equals("")) {
                    uploadAvatarImage();
                    changeUserData();
                    Toast.makeText(ProfileEdit.this, "Thay đổi thông tin thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileEdit.this, "Nhập tên người dùng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getUserData(){
        collectionReferenceUserData = firestore.collection("UserData");
        collectionReferenceUserData.document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String displayName = documentSnapshot.getString("displayName");
                UsernameTextInput.setText(displayName);
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
                        UserAvatarProfileEdit.setImageBitmap(bitmap);
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
                                UserAvatarProfileEdit.setImageBitmap(bitmap);
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

    private void selectAvatarImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Chọn ảnh đại diện"));
    }

    private void uploadAvatarImage(){
        if (selectedImageUri != null){
            storageReferenceUploadAvatar = storage.getReference().child("users/" + currentUser.getUid() + "/avatar.jpg");
            storageReferenceUploadAvatar.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

    private void changeUserData(){
        if (!UsernameTextInput.getText().toString().equals("")) {
            Map<String, Object> UserData = new HashMap<>();
            UserData.put("displayName", String.valueOf(UsernameTextInput.getText()));
            UserData.put("userUID", currentUser.getUid());
            firestore.collection("UserData").document(currentUser.getUid()).set(UserData);
        }
    }
}
