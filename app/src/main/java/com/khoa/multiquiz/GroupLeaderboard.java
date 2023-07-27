package com.khoa.multiquiz;

import static android.view.View.GONE;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.khoa.multiquiz.adapter.LeaderboardAdpter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GroupLeaderboard extends AppCompatActivity {
    int generalStatus;
    Button NextQuestionButton;
    String JoinID;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    FirebaseUser currentUser;
    RecyclerView LeaderboardRecyclerView;
    ArrayList<User> userArrayList;
    LeaderboardAdpter leaderboardAdpter;
    DatabaseReference databaseReferenceUserPoint, databaseReferenceGeneralStatus;
    GroupQuestionSetInfo groupQuestionSetInfo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_leaderboard);
        LeaderboardRecyclerView = findViewById(R.id.LeaderboardRecyclerView);
        NextQuestionButton = findViewById(R.id.NextQuestionButton);


        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();


        LeaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userArrayList = new ArrayList<>();
        leaderboardAdpter = new LeaderboardAdpter(GroupLeaderboard.this, userArrayList, firestore, storage);
        LeaderboardRecyclerView.setAdapter(leaderboardAdpter);


        JoinID = getIntent().getStringExtra("JoinID");
        groupQuestionSetInfo = (GroupQuestionSetInfo) getIntent().getSerializableExtra("GroupQuestionSetInfo");

        getCurrentUserPoint();
        getGeneralStatus();
        if (!currentUser.getUid().equals(groupQuestionSetInfo.getQuestionSetUID())){
            NextQuestionButton.setVisibility(GONE);
        }

        NextQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGeneralStatus(0);
            }
        });
    }

    private void setGeneralStatus(int Status){

        Map<String, Object> uploadGeneralStatus = new HashMap<>();
        uploadGeneralStatus.put("generalStatus", Status);
        databaseReferenceGeneralStatus = database.getReference().child("GroupLobby").child(JoinID);
        databaseReferenceGeneralStatus.updateChildren(uploadGeneralStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getGeneralStatus();
            }
        });

    }

    private void getGeneralStatus(){
        databaseReferenceGeneralStatus = database.getReference().child("GroupLobby").child(JoinID);
        databaseReferenceGeneralStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    generalStatus = snapshot.child("generalStatus").getValue(Integer.class);
                    if (generalStatus == 0){
                        finish();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCurrentUserPoint() {
        databaseReferenceUserPoint = database.getReference().child("GroupLobby").child(JoinID).child("point");

        databaseReferenceUserPoint.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<User> userList = new ArrayList<>();

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String userUID = userSnapshot.getKey();
                        Long userPoint = userSnapshot.getValue(Long.class);

                        User user = new User();
                        user.setUserUID(userUID);
                        user.setUserPoint(userPoint);
                        userArrayList.add(user);
                    }

                    // Sort the userList in descending order based on UserPoint using UserComparator
                    UserComparator userComparator = new UserComparator();
                    Collections.sort(userArrayList, userComparator);
                    leaderboardAdpter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
