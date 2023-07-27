package com.khoa.multiquiz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class GroupLobbyWaiting extends AppCompatActivity {

    int generalStatus;
    String JoinCode, questionSetID, OwnerName;
    TextView GroupQuestionSetNameWaiting, UserQuestionSetCreatedName;
    FirebaseDatabase database;
    FirebaseFirestore firestore;
    DocumentReference documentReferenceLobby;
    DatabaseReference databaseReferenceLobby, databaseReferenceParticipantInfo, databaseReferenceLobbyKickChecking, databaseReferenceGeneralStatus;
    CollectionReference collectionReferenceUserData, collectionReferenceLobby;
    FirebaseUser currentUser;
    GroupQuestionSetInfo groupQuestionSetInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_lobby_waiting);
        GroupQuestionSetNameWaiting = findViewById(R.id.GroupQuestionSetNameWaiting);
        UserQuestionSetCreatedName = findViewById(R.id.UserQuestionSetCreatedName);

        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();

        groupQuestionSetInfo = new GroupQuestionSetInfo();
        JoinCode = getIntent().getStringExtra("JoinCode");

        getDataQuestionSetID();
        addParticipantInfo();
        checkKickStatus();
        checkGeneralStatus();
    }

    private void getDataQuestionSetID(){

        databaseReferenceLobby = database.getReference().child("GroupLobby").child(JoinCode).child("questionSetID");
        databaseReferenceLobby.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    questionSetID = snapshot.getValue(String.class);
                    getDataQuestionSetInfo();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getDataQuestionSetInfo() {
        documentReferenceLobby = firestore.collection("GroupQuestionSet").document(questionSetID);

        documentReferenceLobby.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get specific values from the document using get method
                        String questionSetTitle = document.getString("questionSetTitle");
                        String questionSetUID = document.getString("userUID");

                        groupQuestionSetInfo.setQuestionSetTitle(questionSetTitle);
                        groupQuestionSetInfo.setQuestionSetUID(questionSetUID);
                        groupQuestionSetInfo.setQuestonSetID(document.getId());
                        GroupQuestionSetNameWaiting.setText(questionSetTitle);
                        getDisplayname(questionSetUID);
                    } else {
                        Log.e("DocumentError", "Document not found!");
                    }
                } else {
                    Log.e("FirestoreError", "Error getting document: " + task.getException());
                }
            }
        });
    }


    private void getDisplayname(String UserUID){
        collectionReferenceUserData = firestore.collection("UserData");
        collectionReferenceUserData.document(UserUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                OwnerName =  documentSnapshot.getString("displayName");
                UserQuestionSetCreatedName.setText(OwnerName);
            }
        });
    }

    private void addParticipantInfo(){
        databaseReferenceParticipantInfo = database.getReference().child("GroupLobby").child(JoinCode).child("participant");
        Map<String, Object> participantInfo = new HashMap<>();
        participantInfo.put(currentUser.getUid(), true);
        databaseReferenceParticipantInfo.updateChildren(participantInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
        databaseReferenceParticipantInfo = database.getReference().child("GroupLobby").child(JoinCode).child("point");
        Map<String, Object> participantPoint = new HashMap<>();
        participantPoint.put(currentUser.getUid(), 0);
        databaseReferenceParticipantInfo.updateChildren(participantPoint).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });

        databaseReferenceParticipantInfo = database.getReference().child("GroupLobby").child(JoinCode).child("status");
        Map<String, Object> participantStatus = new HashMap<>();
        participantStatus.put(currentUser.getUid(), 0);
        databaseReferenceParticipantInfo.updateChildren(participantStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });



    }

    private void checkKickStatus(){

        databaseReferenceLobbyKickChecking = database.getReference().child("GroupLobby").child(JoinCode).child("participant").child(currentUser.getUid());

        databaseReferenceLobbyKickChecking.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                } else {
                    Toast.makeText(GroupLobbyWaiting.this, "Bạn đã bị chủ phòng mời ra", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void checkGeneralStatus(){
        databaseReferenceGeneralStatus = database.getReference().child("GroupLobby").child(JoinCode);
        databaseReferenceGeneralStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("generalStatus").exists() && !snapshot.child("generalStatus").getValue().equals(null)) {
                    generalStatus = snapshot.child("generalStatus").getValue(Integer.class);
                    if (generalStatus == 3){
                        Intent intent = new Intent(GroupLobbyWaiting.this, GroupIngame.class);
                        intent.putExtra("GroupQuestionSetInfo", groupQuestionSetInfo);
                        intent.putExtra("JoinID", JoinCode);
                        startActivity(intent);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
