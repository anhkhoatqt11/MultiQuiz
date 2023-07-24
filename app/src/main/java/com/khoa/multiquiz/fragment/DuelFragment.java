package com.khoa.multiquiz.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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
import com.khoa.multiquiz.Profile;
import com.khoa.multiquiz.QuestionTheme;
import com.khoa.multiquiz.R;
import com.khoa.multiquiz.ThemeLobby;
import com.khoa.multiquiz.adapter.CarouselAdapter;

import java.util.ArrayList;

public class DuelFragment extends Fragment {

    DatabaseReference databaseFirebase;
    ArrayList<QuestionTheme> questionThemes;
    CarouselAdapter carouselAdapter;
    ImageView UserAvatar;
    FirebaseStorage storage;
    FirebaseUser currentUser;
    StorageReference storageReferenceAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_duel, container, false);

        RecyclerView carouselRecyclerView = rootView.findViewById(R.id.CarouselRecyclerView);

        storage = FirebaseStorage.getInstance();
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();

        databaseFirebase = FirebaseDatabase.getInstance().getReference("QuestionTheme");
        carouselRecyclerView.setLayoutManager(new CarouselLayoutManager());
        questionThemes = new ArrayList<>();
        carouselAdapter = new CarouselAdapter(getContext(), questionThemes);
        carouselRecyclerView.setAdapter(carouselAdapter);

        databaseFirebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                questionThemes.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    QuestionTheme questionTheme = dataSnapshot. getValue(QuestionTheme.class);
                    questionThemes.add(questionTheme);
                }
                carouselAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        carouselAdapter.setOnClickListener(new CarouselAdapter.OnClickListener(){
            @Override
            public void onClick(int position, QuestionTheme questionTheme) {
                Intent intent = new Intent(getActivity(), ThemeLobby.class);
                intent.putExtra("Theme", questionTheme);
                startActivity(intent);
            }
        });
        return rootView;
    }
}
