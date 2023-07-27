package com.khoa.multiquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupControlIngame extends AppCompatActivity {
    private int CurrentTime = 0;
    int QuestionNumber;
    TextView QuestionNumberOfGroup,ParticipantAnswered,QuestionTextGroup;
    ArrayList<GroupQuestion> groupQuestionArrayList;
    Button [] AnswerButton;
    ProgressBar TimerProgressBar;
    Button Answer1Button, Answer2Button, Answer3Button, Answer4Button;
    FirebaseDatabase database;
    FirebaseFirestore firestore;
    GroupQuestion groupQuestion;
    GroupQuestionSetInfo groupQuestionSetInfo;
    String JoinID;
    int generalStatus;
    DatabaseReference databaseReferenceGeneralStatus, databaseReferenceQuestionNumber;
    CollectionReference collectionReferenceQuestionData;
    CountDownTimer AnswerTimer, NextQuestionCountdownTimer;
    boolean isQuestionStarted = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_control_ingame);
        TimerProgressBar = findViewById(R.id.TimerProgressBar);
        QuestionNumberOfGroup = findViewById(R.id.QuestionNumberOfGroup);
        QuestionTextGroup = findViewById(R.id.QuestionTextGroup);
        Answer1Button = findViewById(R.id.Answer1Button);
        Answer2Button = findViewById(R.id.Answer2Button);
        Answer3Button = findViewById(R.id.Answer3Button);
        Answer4Button = findViewById(R.id.Answer4Button);

        AnswerButton = new Button[]{Answer1Button, Answer2Button, Answer3Button, Answer4Button};

        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();

        groupQuestion = new GroupQuestion();
        groupQuestionArrayList = new ArrayList<>();
        groupQuestionSetInfo = (GroupQuestionSetInfo) getIntent().getSerializableExtra("GroupQuestionSetInfo");
        JoinID = getIntent().getStringExtra("JoinID");

        setGeneralStatus(0);
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

    private void setQuestionNumber(int QuestionNumber){

        Map<String, Object> uploadGeneralStatus = new HashMap<>();
        uploadGeneralStatus.put("questionNumber", QuestionNumber);
        databaseReferenceQuestionNumber = database.getReference().child("GroupLobby").child(JoinID);
        databaseReferenceQuestionNumber.updateChildren(uploadGeneralStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getQuestionData();
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
                    if (generalStatus == 0 && !isQuestionStarted){
                        isQuestionStarted = true;
                        nextQuestion();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getQuestionData() {
        collectionReferenceQuestionData = firestore.collection("GroupQuestionList");
        collectionReferenceQuestionData.whereEqualTo("questionSetID", groupQuestionSetInfo.getQuestonSetID()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                GroupQuestion groupQuestion = new GroupQuestion();
                                groupQuestion.setQuestionID(documentSnapshot.getId());
                                groupQuestion.setQuestionNumber(Math.toIntExact(documentSnapshot.getLong("questionNumber")));
                                groupQuestion.setQuestionTime(Math.toIntExact(documentSnapshot.getLong("questionTime")));
                                groupQuestion.setQuestionText(documentSnapshot.getString("questionText"));
                                groupQuestion.setAnswer((List<String>) documentSnapshot.get("answer"));
                                groupQuestion.setCorrectAnswerID(Math.toIntExact(documentSnapshot.getLong("correctAnswerID")));
                                groupQuestion.setQuestionSetID(documentSnapshot.getString("questionSetID"));
                                groupQuestionArrayList.add(groupQuestion);
                            }
                            getQuestionNumberFromDatabase();
                        }
                        if (QuestionNumber == queryDocumentSnapshots.size()){
                            finish();
                        }
                    }
                });
    }


    private void getQuestionNumberFromDatabase() {
        // Get the question number from the Realtime Database
        databaseReferenceQuestionNumber = database.getReference().child("GroupLobby").child(JoinID);
        databaseReferenceQuestionNumber.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Step 5: Retrieve the question number from the Realtime Database
                QuestionNumber = snapshot.child("questionNumber").getValue(Integer.class);

                // Step 6: Display the question and answer
                if (generalStatus == 0 || generalStatus == 3) displayQuestionAndAnswer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that may occur during the database operation
            }
        });
    }
    private void displayQuestionAndAnswer(){
        Log.e("Question Number", String.valueOf(QuestionNumber));
        groupQuestion = groupQuestionArrayList.get(QuestionNumber);
        String QuestionNumberText = "Câu hỏi " + QuestionNumber;
        QuestionTextGroup.setText(QuestionNumberText);
        QuestionTextGroup.setText(groupQuestion.getQuestionText());
        List<String> Answers = groupQuestion.getAnswer();
        for (int i = 0; i < Math.min(Answers.size(), AnswerButton.length); i++) {
            AnswerButton[i].setText(Answers.get(i));
        }
        setGeneralStatus(1);
        timerStart();
    }

    private void reorderArrayByQuestionNumber() {
        Collections.sort(groupQuestionArrayList, new Comparator<GroupQuestion>() {
            @Override
            public int compare(GroupQuestion q1, GroupQuestion q2) {
                return q1.getQuestionNumber() - q2.getQuestionNumber();
            }
        });
    }

    private void timerStart(){
        long TimeToAnswer = Long.valueOf(groupQuestion.getQuestionTime() * 1000);
        AnswerTimer = new CountDownTimer(TimeToAnswer, 100){
            @Override
            public void onTick(long l) {
                CurrentTime = (int) (l);
                TimerProgressBar.setProgress(CurrentTime);
                TimerProgressBar.setMax((int)TimeToAnswer);
            }

            @Override
            public void onFinish() {
                setGeneralStatus(2);
                isQuestionStarted = false;
                getQuestionData();
                NextQuestionCountdownTimer.cancel();
                openLeaderboard();
            }
        }.start();
    }

    private void nextQuestion(){
        NextQuestionCountdownTimer = new CountDownTimer(3000, 100){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                setQuestionNumber(QuestionNumber + 1);
            }
        }.start();
    }

    private void openLeaderboard(){
        Intent intent = new Intent(GroupControlIngame.this, GroupLeaderboard.class);
        intent.putExtra("GroupQuestionSetInfo", groupQuestionSetInfo);
        intent.putExtra("JoinID", JoinID);
        startActivity(intent);
    }

}
