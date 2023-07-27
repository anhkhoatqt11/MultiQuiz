package com.khoa.multiquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
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
import com.khoa.multiquiz.adapter.ParticipantAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GroupLobbyOwnerControl extends AppCompatActivity {

    String JoinID;
    TextView GroupQuestionSetName, JoinCode;
    Button CloseGroupLobby;
    Button StartGroupLobby;
    FirebaseDatabase database;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    FirebaseUser currentUser;
    GroupQuestionSetInfo groupQuestionSetInfo;
    RecyclerView ParticipantRecyclerView;
    ArrayList<User> userArrayList;
    ParticipantAdapter participantAdapter;
    DatabaseReference databaseReferenceCreateLobby, databaseReferenceParticipant, databaseReferenceKickParticipant, databaseReferenceRemoveLobby, databaseReferenceGeneralStatus;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_lobby_owner_control);
        GroupQuestionSetName = findViewById(R.id.GroupQuestionSetName);
        JoinCode = findViewById(R.id.JoinCode);
        ParticipantRecyclerView = findViewById(R.id.JoinerRecyclerView);
        CloseGroupLobby = findViewById(R.id.CloseGroupLobby);
        StartGroupLobby = findViewById(R.id.StartGroupLobby);

        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ParticipantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        userArrayList = new ArrayList<>();
        participantAdapter = new ParticipantAdapter(GroupLobbyOwnerControl.this, userArrayList, firestore, storage);
        ParticipantRecyclerView.setAdapter(participantAdapter);

        participantAdapter.setOnKickButtonClickListener(new ParticipantAdapter.OnKickButtonClickListener() {
            @Override
            public void onKickButtonClick(int position, User user) {
                databaseReferenceKickParticipant = database.getReference().child(JoinID).child("participant");

                databaseReferenceParticipant.child(user.getUserUID()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        participantAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        groupQuestionSetInfo = (GroupQuestionSetInfo) getIntent().getSerializableExtra("GroupQuestionSetInfo");

        GroupQuestionSetName.setText(groupQuestionSetInfo.getQuestionSetTitle());
        createLobby();
        getParticipant();
        setButtonFunction();
    }


    private void createLobby() {
        Map<String, Object> GroupLobbyValue = new HashMap<>();
        GroupLobbyValue.put("questionSetID", groupQuestionSetInfo.getQuestonSetID());
        GroupLobbyValue.put("ownerUID", currentUser.getUid());
        JoinID = String.valueOf(generateRandomNumber());
        databaseReferenceCreateLobby = database.getReference().child("GroupLobby").child(JoinID);
        databaseReferenceCreateLobby.setValue(GroupLobbyValue);
        JoinCode.setText(JoinID);
    }

    private void getParticipant(){
        databaseReferenceParticipant = database.getReference().child("GroupLobby").child(JoinID).child("participant");
        databaseReferenceParticipant.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    userArrayList.clear();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()){
                        User user = new User();
                        user.setUserUID(childSnapshot.getKey());
                        userArrayList.add(user);
                    }
                    participantAdapter.notifyDataSetChanged();
                } else {
                    userArrayList.clear();
                    participantAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private int generateRandomNumber() {
        Random random = new Random();
        return random.nextInt(999999 - 100000 + 1) + 100000;
    }

    private void setButtonFunction(){

        StartGroupLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupLobbyOwnerControl.this, GroupControlIngame.class);
                intent.putExtra("GroupQuestionSetInfo", groupQuestionSetInfo);
                intent.putExtra("JoinID", JoinID);
                setGeneralStatus(3);
                startActivity(intent);
            }
        });

        CloseGroupLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReferenceRemoveLobby = database.getReference().child("GroupLobby").child(JoinID);
                databaseReferenceRemoveLobby.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
                finish();
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
            }
        });

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
