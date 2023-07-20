package com.khoa.multiquiz;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class Ingame extends AppCompatActivity {

    private int CurrentTime = 0;
    ProgressBar TimerProgressBar;
    ImageView RoomOwnerImage, OpponentImage, QuestionImage;
    TextView RoomOwnerName, RoomOwnerPoint, OpponentName, OpponentPoint, QuestionText;
    Button Answer1Button, Answer2Button, Answer3Button, Answer4Button;
    Room currentRoom;
    FirebaseFirestore FirestoreDB;


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


        if (getIntent().hasExtra("Room")) {
            currentRoom = (Room) getIntent().getSerializableExtra("Room");
        }

        gameStartCountDown();
    }

    private void gameStartCountDown(){
        new CountDownTimer(5000, 100){
            @Override
            public void onTick(long l) {}
            @Override
            public void onFinish() {
                fetchingQuestionAndAnswer(currentRoom.getQuestionThemeID());
            }
        }.start();
    }

    private void timerStart(){
        new CountDownTimer(30000, 100){
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

    private void fetchingQuestionAndAnswer(String QuestionThemeID){

        FirestoreDB.collection(QuestionThemeID).get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()){
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
//                int randomIndex = new Random().nextInt(documents.size());
                DocumentSnapshot randomDocument = documents.get(0);
                displayQuestionAndAnswer(randomDocument);
            } else {

            }
        }).addOnFailureListener(e -> {

        });
    }

    private void displayQuestionAndAnswer(DocumentSnapshot questionSnapshot){
        Picasso.get().load(questionSnapshot.getString("mediaLink")).into(QuestionImage);
        QuestionText.setText(questionSnapshot.getString("questionText"));
        List<String> Answers = (List<String>) questionSnapshot.get("answer");
        Button[] AnswerButton = {Answer1Button, Answer2Button, Answer3Button, Answer4Button};
        for (int i = 0; i < Math.min(Answers.size(), AnswerButton.length); i++) {
            AnswerButton[i].setText(Answers.get(i));
        }
        Toast.makeText(Ingame.this, "Completed", Toast.LENGTH_LONG).show();
        timerStart();
    }

}
