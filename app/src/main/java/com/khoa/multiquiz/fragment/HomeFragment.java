package com.khoa.multiquiz.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.carousel.CarouselLayoutManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoa.multiquiz.QuestionTheme;
import com.khoa.multiquiz.R;
import com.khoa.multiquiz.ThemeLobby;
import com.khoa.multiquiz.adapter.CarouselAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    DatabaseReference databaseFirebase;
    ArrayList<QuestionTheme> questionThemes;
    CarouselAdapter carouselAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mainpage, container, false);


        databaseFirebase = FirebaseDatabase.getInstance().getReference("QuestionTheme");

        RecyclerView carouselRecyclerView = rootView.findViewById(R.id.carousel_recycler_view);
        carouselRecyclerView.setLayoutManager(new CarouselLayoutManager());
//        List<String> imageUrls = new ArrayList<>(); // Replace with your list of image URLs
//        imageUrls.add("https://media.dolenglish.vn/PUBLIC/MEDIA/15114359-77ed-45bf-a9f9-3b0f018d3e00.jpg");
//        imageUrls.add("https://idvielts.com/wp-content/uploads/2020/03/history.jpg");
//        imageUrls.add("https://chuyennguyentrai.edu.vn/uploads/Trang/nangkhieu2020-2021/%C4%91%E1%BB%8Ba%20l%C3%BD/dia-ly.png");


//        CarouselAdapter carouselAdapter = new CarouselAdapter(imageUrls);
//        carouselRecyclerView.setAdapter(carouselAdapter);
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
