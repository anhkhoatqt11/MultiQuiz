package com.khoa.multiquiz;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Ingame extends AppCompatActivity {

    private int CurrentTime = 0;
    private int QuestionNumber = 0;
    private boolean isNextQuestionCalled = false;
    private ColorStateList colorStateList;

    ProgressBar TimerProgressBar;
    ImageView RoomOwnerImage, OpponentImage, QuestionImage;
    TextView RoomOwnerName, RoomOwnerPoint, OpponentName, OpponentPoint, QuestionText;
    Button Answer1Button, Answer2Button, Answer3Button, Answer4Button;
    Button [] AnswerButton;
    CountDownTimer UserAnswerTimer;
    Room currentRoom,readRoomData;
    FirebaseFirestore FirestoreDB;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    DatabaseReference databaseReferenceRoom;
    DatabaseReference databaseReferenceRoomSendAnswer;
    CollectionReference collectionReferenceUserAnswer;
    DocumentSnapshot questionDocument, answerDocument;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingame);

        TimerProgressBar = findViewById(R.id.TimerProgressBar);

        RoomOwnerImage = findViewById(R.id.RoomOwnerImage);
        OpponentImage = findViewById(R.id.OpponentImage);
        QuestionImage = findViewById(R.id.QuestionImage);

        RoomOwnerName = findViewById(R.id.RoomOwnerName);
        RoomOwnerPoint = findViewById(R.id.RoomOwnerPoint);
        OpponentName = findViewById(R.id.OpponentName);
        OpponentPoint = findViewById(R.id.OpponentPoint);
        QuestionText = findViewById(R.id.QuestionText);

        Answer1Button = findViewById(R.id.Answer1Button);
        Answer2Button = findViewById(R.id.Answer2Button);
        Answer3Button = findViewById(R.id.Answer3Button);
        Answer4Button = findViewById(R.id.Answer4Button);

        FirestoreDB = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();

        colorStateList = Answer1Button.getBackgroundTintList();

        AnswerButton = new Button[]{Answer1Button, Answer2Button, Answer3Button, Answer4Button};


        if (getIntent().hasExtra("Room")) {
            currentRoom = (Room) getIntent().getSerializableExtra("Room");
        }

        getRoomData();
        generateSessionID();
        buttonFunctionInitialize();
        gameStartCountDown();
    }

    private void buttonFunctionInitialize(){
        Answer1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAnswer(0);
                setButtonState(false);
                Answer1Button.setBackgroundTintList(ContextCompat.getColorStateList(Ingame.this, R.color.md_theme_light_primary));
            }
        });
        Answer2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAnswer(1);
                setButtonState(false);
                Answer2Button.setBackgroundTintList(ContextCompat.getColorStateList(Ingame.this, R.color.md_theme_light_primary));
            }
        });
        Answer3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAnswer(2);
                setButtonState(false);
                Answer3Button.setBackgroundTintList(ContextCompat.getColorStateList(Ingame.this, R.color.md_theme_light_primary));
            }
        });
        Answer4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAnswer(3);
                setButtonState(false);
                Answer4Button.setBackgroundTintList(ContextCompat.getColorStateList(Ingame.this, R.color.md_theme_light_primary));
            }
        });

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

    private void resetButtonTint(){
        for (int i = 0; i < AnswerButton.length; i++) {
            AnswerButton[i].setBackgroundTintList(colorStateList);
        }
    }


    private void getRoomData() {
        databaseReferenceRoom = database.getReference().child("Lobby").child(currentRoom.getRoomID());
        databaseReferenceRoom.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readRoomData = snapshot.getValue(Room.class);
                RoomOwnerName.setText(readRoomData.getUserUID());
                OpponentName.setText(readRoomData.getOpponentUID());
                RoomOwnerPoint.setText(Integer.toString(readRoomData.getOwnerPoint()));
                OpponentPoint.setText(Integer.toString(readRoomData.getOpponentPoint()));
                if (checkBothUserState(3) && !isNextQuestionCalled){
                    nextQuestion();
                    isNextQuestionCalled = true;
                }
                if (QuestionNumber > readRoomData.NumberOfQuestion){
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void generateSessionID(){
        if (currentUser.getUid().equals(currentRoom.getUserUID())){
            DatabaseReference databaseReferenceAddSessionID = database.getReference().child("Lobby").child(currentRoom.getRoomID()).child("sessionID");
            databaseReferenceAddSessionID.setValue(getAlphaNumericString(12));
        }
    }

    private void gameStartCountDown(){
        new CountDownTimer(5000, 100){
            @Override
            public void onTick(long l) {}
            @Override
            public void onFinish() {
                fetchingQuestionAndAnswer();
            }
        }.start();
    }

    private void timerStart(){
         UserAnswerTimer = new CountDownTimer(30000, 100){
            @Override
            public void onTick(long l) {
                CurrentTime = (int) (l);
                TimerProgressBar.setProgress(CurrentTime);
                TimerProgressBar.setMax(30000);
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void setUserState(int State){
        if (currentUser.getUid().equals(readRoomData.getUserUID())){
            databaseReferenceRoomSendAnswer = database.getReference().child("Lobby").child(currentRoom.getRoomID()).child("ownerState");
            databaseReferenceRoomSendAnswer.setValue(State);
        } else if (currentUser.getUid().equals(readRoomData.getOpponentUID())) {
            databaseReferenceRoomSendAnswer = database.getReference().child("Lobby").child(currentRoom.getRoomID()).child("opponentState");
            databaseReferenceRoomSendAnswer.setValue(State);
        }
    }

    private boolean checkBothUserState(int State){
        if (readRoomData.getOwnerState() == State && readRoomData.getOpponentState() == State){
            return true;
        } else {
            return false;
        }
    }

    private void fetchingQuestionAndAnswer(){
        FirestoreDB.collection(readRoomData.getQuestionThemeID()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
//                int randomIndex = new Random().nextInt(documents.size());
                questionDocument = documents.get(QuestionNumber);
                displayQuestionAndAnswer();
                QuestionNumber = QuestionNumber + 1;
            } else {

            }
        }).addOnFailureListener(e -> {

        }
        );
    }

    private void displayQuestionAndAnswer(){
        Picasso.get().load(questionDocument.getString("mediaLink")).into(QuestionImage);
        QuestionText.setText(questionDocument.getString("questionText"));
        List<String> Answers = (List<String>) questionDocument.get("answer");
        for (int i = 0; i < Math.min(Answers.size(), AnswerButton.length); i++) {
            AnswerButton[i].setText(Answers.get(i));
        }
        setUserState(2);
        timerStart(); //Start the timer of the game
    }

    private void sendAnswer(int AnswerID){
        //Stop the timer
        UserAnswerTimer.cancel();
        //Change user answer state in RealtimeDB
        Log.e("Current User UID", currentUser.getUid());
        Log.e("Room Owner UID", readRoomData.getUserUID());
        Log.e("Opponent UID", readRoomData.getOpponentUID());
        setUserState(3);
        //Store user answer in FirestoreDB
        Map<String, Object> AnswerData = new HashMap<>();
        AnswerData.put("userUID", currentUser.getUid());
        AnswerData.put("questionThemeID", readRoomData.getQuestionThemeID());
        AnswerData.put("questionID", questionDocument.getId());
        AnswerData.put("selectedAnswer", AnswerID);
        AnswerData.put("timestamp", String.valueOf(CurrentTime));
        AnswerData.put("roomID", readRoomData.getRoomID());
        AnswerData.put("sessionID", readRoomData.getSessionID());
        AnswerData.put("createdAt", System.currentTimeMillis());

        FirestoreDB.collection("UserAnswerLog").add(AnswerData);
        checkAnswer();
    }

    private void checkAnswer(){
        collectionReferenceUserAnswer = FirestoreDB.collection("UserAnswerLog");
        Log.e("sessionID", readRoomData.getSessionID());
        Log.e("questionID", questionDocument.getId());
        collectionReferenceUserAnswer.whereEqualTo("roomID",readRoomData.getRoomID()).whereEqualTo("userUID", currentUser.getUid()).whereEqualTo("questionID", questionDocument.getId()).whereEqualTo("sessionID", readRoomData.getSessionID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.e("questionID", String.valueOf(queryDocumentSnapshots.size()));
                if (!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    answerDocument = documents.get(0);
                    int SelectedAnswer = Math.toIntExact(answerDocument.getLong("selectedAnswer"));
                    int CorrectAnswer = Math.toIntExact(questionDocument.getLong("correctAnswerID"));
                    if (Objects.equals(answerDocument.getLong("selectedAnswer"), questionDocument.getLong("correctAnswerID"))){

                        AnswerButton[SelectedAnswer].setBackgroundTintList(ContextCompat.getColorStateList(Ingame.this, R.color.md_theme_light_tertiaryContainer));

                        String Timestamp = answerDocument.getString("timestamp");
                        int PointGet = Integer.parseInt(String.valueOf(Timestamp.substring(0,2)));

                        if (currentUser.getUid().equals(readRoomData.getUserUID())){
                            databaseReferenceRoomSendAnswer = database.getReference().child("Lobby").child(currentRoom.getRoomID()).child("ownerPoint");
                            databaseReferenceRoomSendAnswer.setValue(readRoomData.getOwnerPoint() + PointGet);
                        } else if (currentUser.getUid().equals(readRoomData.getOpponentUID())) {
                            databaseReferenceRoomSendAnswer = database.getReference().child("Lobby").child(currentRoom.getRoomID()).child("opponentPoint");
                            databaseReferenceRoomSendAnswer.setValue(readRoomData.getOpponentPoint() + PointGet);
                        }


                    } else {
                        AnswerButton[SelectedAnswer].setBackgroundTintList(ContextCompat.getColorStateList(Ingame.this, R.color.md_theme_light_error));
                        AnswerButton[CorrectAnswer].setBackgroundTintList(ContextCompat.getColorStateList(Ingame.this, R.color.md_theme_light_tertiaryContainer));
                    };
                }
            }
        });
    }

    private void nextQuestion(){
        new CountDownTimer(3000, 100){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                resetButtonTint();
                setButtonState(true);
                fetchingQuestionAndAnswer();
                isNextQuestionCalled = false;
            }
        }.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    static String getAlphaNumericString(int n)
    {

        // choose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}
