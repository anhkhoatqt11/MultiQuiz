package com.khoa.multiquiz;

import static kotlin.random.RandomKt.Random;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoa.multiquiz.fragment.HomeFragment;

import java.util.Random;

public class ThemeLobby extends AppCompatActivity {

    public com.google.android.material.appbar.MaterialToolbar TopAppBar;
    public Button CreateRoomButton;
    public QuestionTheme questionTheme;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    Room roomInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_theme_lobby);
        TopAppBar = findViewById(R.id.ThemeLobbyTopAppBar);
        CreateRoomButton = findViewById(R.id.CreateRoomButton);
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();

        roomInfo = new Room();
        questionTheme = new QuestionTheme();

        database = FirebaseDatabase.getInstance();


        if (getIntent().hasExtra("Theme")) {
            questionTheme = (QuestionTheme) getIntent().getSerializableExtra("Theme");
        }

        if (questionTheme != null){
            TopAppBar.setTitle(questionTheme.getTheme_name());
        }

        CreateRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewRoomToDatabase(questionTheme.getId(), currentUser.getUid(), 5);
            }
        });

    }

    private void createNewRoomToDatabase(int QuestionThemeID, String UserUID, int NumberOfQuestion){
        String RoomID = String.valueOf(UserUID.substring(UserUID.length()-5));

        roomInfo.setRoomID(RoomID);
        roomInfo.setQuestionThemeID(QuestionThemeID);
        roomInfo.setUserUID(UserUID);
        roomInfo.setNumberOfQuestion(NumberOfQuestion);

        databaseReference = database.getReference().child("Lobby").child(RoomID);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(roomInfo);

                Toast.makeText(ThemeLobby.this, "Đã tạo phòng", Toast.LENGTH_SHORT);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
