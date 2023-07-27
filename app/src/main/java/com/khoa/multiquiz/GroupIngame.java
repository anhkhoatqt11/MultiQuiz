package com.khoa.multiquiz;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.FieldIndex;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupIngame extends AppCompatActivity {
    private int CurrentTime = 0;
    private ColorStateList colorStateList;
    int QuestionNumber;
    int CurrentPoint;
    ArrayList<GroupQuestion> groupQuestionArrayList;
    TextView PlayerName, PlayerPoint, GroupTitleName, QuestionText;
    ImageView PlayerImage;
    ProgressBar TimerProgressBar;
    FirebaseDatabase database;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    GroupQuestion groupQuestion;
    int generalStatus;
    Button Answer1Button, Answer2Button, Answer3Button, Answer4Button;
    Button [] AnswerButton;
    GroupQuestionSetInfo groupQuestionSetInfo;
    String JoinID;
    FirebaseUser currentUser;
    DatabaseReference databaseReferenceGeneralStatus, databaseReferenceQuestionNumber,databaseReferenceParticipantInfo, databaseReferenceSendAnswer,databaseReferenceUserPoint;
    CollectionReference collectionReferenceQuestionData, collectionReferenceUserData, collectionReferenceUserAnswer;
    CountDownTimer AnswerTimer, NextQuestionCountdownTimer;
    StorageReference storageReferenceAvatar;
    DocumentSnapshot answerDocument;
    boolean isQuestionStarted = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_ingame);

        PlayerName = findViewById(R.id.PlayerName);
        PlayerPoint = findViewById(R.id.PlayerPoint);
        GroupTitleName = findViewById(R.id.GroupTitleName);
        QuestionText = findViewById(R.id.QuestionText);
        Answer1Button = findViewById(R.id.Answer1ButtonPlayer);
        Answer2Button = findViewById(R.id.Answer2ButtonPlayer);
        Answer3Button = findViewById(R.id.Answer3ButtonPlayer);
        Answer4Button = findViewById(R.id.Answer4ButtonPlayer);
        TimerProgressBar = findViewById(R.id.TimerProgressBar);
        PlayerImage = findViewById(R.id.PlayerImage);

        colorStateList = Answer1Button.getBackgroundTintList();


        AnswerButton = new Button[]{Answer1Button, Answer2Button, Answer3Button, Answer4Button};

        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        groupQuestion = new GroupQuestion();
        groupQuestionArrayList = new ArrayList<>();
        groupQuestionSetInfo = (GroupQuestionSetInfo) getIntent().getSerializableExtra("GroupQuestionSetInfo");
        JoinID = getIntent().getStringExtra("JoinID");

        GroupTitleName.setText(groupQuestionSetInfo.getQuestionSetTitle());

        getUserData();
        getUserImage();
        getGeneralStatus();
        getCurrentUserPoint();
        buttonFunctionInitialize();
        setButtonState(false);
    }

    private void buttonFunctionInitialize(){
        Answer1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAnswer(0);
                setButtonState(false);
                Answer1Button.setBackgroundTintList(ContextCompat.getColorStateList(GroupIngame.this, R.color.md_theme_light_primary));
            }
        });
        Answer2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAnswer(1);
                setButtonState(false);
                Answer2Button.setBackgroundTintList(ContextCompat.getColorStateList(GroupIngame.this, R.color.md_theme_light_primary));
            }
        });
        Answer3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAnswer(2);
                setButtonState(false);
                Answer3Button.setBackgroundTintList(ContextCompat.getColorStateList(GroupIngame.this, R.color.md_theme_light_primary));
            }
        });
        Answer4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAnswer(3);
                setButtonState(false);
                Answer4Button.setBackgroundTintList(ContextCompat.getColorStateList(GroupIngame.this, R.color.md_theme_light_primary));
            }
        });

    }

    private void resetButtonTint(){
        for (int i = 0; i < AnswerButton.length; i++) {
            AnswerButton[i].setBackgroundTintList(colorStateList);
        }
    }

    private void setButtonState(boolean State) {
        if (State){
            for (int i = 0; i < AnswerButton.length; i++) {
                AnswerButton[i].setEnabled(true);
            }} else {
            for (int i = 0; i < AnswerButton.length; i++) {
                AnswerButton[i].setEnabled(false);
            }
        }
    }





    private void getGeneralStatus(){
        databaseReferenceGeneralStatus = database.getReference().child("GroupLobby").child(JoinID);
        databaseReferenceGeneralStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    generalStatus = snapshot.child("generalStatus").getValue(Integer.class);
                    if (generalStatus == 0){
                        resetButtonTint();
                        setButtonState(true);
                        isQuestionStarted = false;
                    }
                    if (generalStatus == 1 && !isQuestionStarted){
                        Log.e("GeneralStatus", "1");
                        getQuestionData();
                        isQuestionStarted = true;
                    }
                    if (generalStatus == 2){
                        openLeaderboard();
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
                        if (userUID.equals(currentUser.getUid())){
                            PlayerPoint.setText(String.valueOf(userPoint));
                            CurrentPoint = Math.toIntExact( userPoint);
                        }
                    }

                    // Sort the userList in descending order based on UserPoint using UserComparator
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getQuestionData() {
        // Step 1: Get the question data from Firestore
        collectionReferenceQuestionData = firestore.collection("GroupQuestionList");
        collectionReferenceQuestionData.whereEqualTo("questionSetID", groupQuestionSetInfo.getQuestonSetID()).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Step 2: Process the question data retrieved from Firestore
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
                            Log.e("GeneralStatus", "Not Empty");
                            // Step 3: After processing the Firestore data, reorder the array by question number

                            // Step 4: Once the array is reordered, get the question number from the Realtime Database
                            getQuestionNumberFromDatabase();

                        } else {
                            Log.e("GeneralStatus", "Empty");
                        }
                        if (QuestionNumber == queryDocumentSnapshots.size()){
                            AnswerTimer.cancel();
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
                displayQuestionAndAnswer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors that may occur during the database operation
            }
        });
    }

    private void displayQuestionAndAnswer(){
        Log.e("QuestionNumber", String.valueOf(QuestionNumber));
        groupQuestion = groupQuestionArrayList.get(QuestionNumber);
        QuestionText.setText(groupQuestion.getQuestionText());
        List<String> Answers = groupQuestion.getAnswer();
        for (int i = 0; i < Math.min(Answers.size(), AnswerButton.length); i++) {
            AnswerButton[i].setText(Answers.get(i));
        }
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
                setButtonState(true);
            }

            @Override
            public void onFinish() {
                openLeaderboard();
            }
        }.start();
    }

    private void openLeaderboard(){
        Intent intent = new Intent(GroupIngame.this, GroupLeaderboard.class);
        intent.putExtra("GroupQuestionSetInfo", groupQuestionSetInfo);
        intent.putExtra("JoinID", JoinID);
        startActivity(intent);
    }


    private void getUserData() {
        collectionReferenceUserData = firestore.getInstance().collection("UserData");
        collectionReferenceUserData.document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String displayName = documentSnapshot.getString("displayName");
                PlayerName.setText(displayName);
            }
        });
    }



    private void getUserImage(){
        storageReferenceAvatar = storage.getReference();
        String childPath = "users/" + currentUser.getUid() + "/avatar.jpg";
            storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(PlayerImage);
                }
            });
        }




    private void sendAnswer(int AnswerID){
        //Stop the timer
        AnswerTimer.cancel();
        //Change user answer state in RealtimeDB
        setParticipantState();
        //Store user answer in firestore
        Map<String, Object> AnswerData = new HashMap<>();
        AnswerData.put("userUID", currentUser.getUid());
        AnswerData.put("questionSetID", groupQuestion.getQuestionSetID());
        AnswerData.put("questionID", groupQuestion.getQuestionID());
        AnswerData.put("selectedAnswer", AnswerID);
        AnswerData.put("timestamp", String.valueOf(CurrentTime));
        AnswerData.put("joinID", JoinID);
        AnswerData.put("questionNumber", QuestionNumber);
        AnswerData.put("createdAt", System.currentTimeMillis());

        firestore.collection("GroupUserAnswerLog").add(AnswerData);
        checkAnswer();
    }

    private void checkAnswer(){
        collectionReferenceUserAnswer = firestore.collection("GroupUserAnswerLog");
        collectionReferenceUserAnswer.whereEqualTo("joinID", JoinID).whereEqualTo("userUID", currentUser.getUid()).whereEqualTo("questionID", groupQuestion.getQuestionID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    answerDocument = documents.get(0);
                    int SelectedAnswer = Math.toIntExact(answerDocument.getLong("selectedAnswer"));
                    int CorrectAnswer = groupQuestion.getCorrectAnswerID();
                    if (SelectedAnswer == CorrectAnswer){

                        AnswerButton[SelectedAnswer].setBackgroundTintList(ContextCompat.getColorStateList(GroupIngame.this, R.color.md_theme_light_tertiaryContainer));

                        String Timestamp = answerDocument.getString("timestamp");
                        int PointGet = Integer.parseInt(String.valueOf(Timestamp.substring(0,3)));

                        Log.e("Timestamp", Timestamp);
                        Log.e("PoitnGet", String.valueOf(PointGet));


                        databaseReferenceParticipantInfo = database.getReference().child("GroupLobby").child(JoinID).child("point");
                        Map<String, Object> participantPoint = new HashMap<>();
                        participantPoint.put(currentUser.getUid(), CurrentPoint + PointGet);
                        databaseReferenceParticipantInfo.setValue(participantPoint).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });


                    } else {
                        AnswerButton[SelectedAnswer].setBackgroundTintList(ContextCompat.getColorStateList(GroupIngame.this, R.color.md_theme_light_error));
                        AnswerButton[CorrectAnswer].setBackgroundTintList(ContextCompat.getColorStateList(GroupIngame.this, R.color.md_theme_light_tertiaryContainer));
                    };
                }
            }
        });
    }
    private void setParticipantState(){


        databaseReferenceParticipantInfo = database.getReference().child("GroupLobby").child(JoinID).child("status");
        Map<String, Object> participantStatus = new HashMap<>();
        participantStatus.put(currentUser.getUid(), 2);
        databaseReferenceParticipantInfo.setValue(participantStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });



    }


}
