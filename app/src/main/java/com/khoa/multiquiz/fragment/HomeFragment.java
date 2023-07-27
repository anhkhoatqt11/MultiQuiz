package com.khoa.multiquiz.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoa.multiquiz.MainActivity;
import com.khoa.multiquiz.Profile;
import com.khoa.multiquiz.QuestionTheme;
import com.khoa.multiquiz.R;
import com.khoa.multiquiz.ThemeLobby;
import com.khoa.multiquiz.adapter.CarouselAdapter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    DatabaseReference databaseFirebase;
    ArrayList<QuestionTheme> questionThemes;
    CarouselAdapter carouselAdapter;
    ImageView UserAvatar;
    FirebaseStorage storage;
    FirebaseUser currentUser;
    StorageReference storageReferenceAvatar;
    LinearLayout DualLinearLayout, GroupLinearLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mainpage, container, false);

        UserAvatar = rootView.findViewById(R.id.UserAvatar);
        DualLinearLayout = rootView.findViewById(R.id.DuelLinearLayout);
        GroupLinearLayout = rootView.findViewById(R.id.GroupLinearLayout);

        storage = FirebaseStorage.getInstance();
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();

        getUserAvatarImage();
        UserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Profile.class);
                startActivity(intent);
            }
        });

        DualLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    DuelFragment targetFragment = new DuelFragment();
                    ((MainActivity) requireActivity()).openFragment(targetFragment);
            }
        });

        GroupLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupFragment targetFragment = new GroupFragment();
                ((MainActivity) requireActivity()).openFragment(targetFragment);
            }
        });
        return rootView;
    }

    private void getUserAvatarImage(){
        storageReferenceAvatar = storage.getReference();
        String childPath = "users/" + currentUser.getUid() + "/avatar.jpg";
        Log.e("childPath", childPath);
        storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String imageUrl = uri.toString();
                Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(UserAvatar);
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
                                UserAvatar.setImageBitmap(bitmap);
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
}
