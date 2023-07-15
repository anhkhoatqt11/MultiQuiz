package com.khoa.multiquiz;

import static kotlin.random.RandomKt.Random;

import android.app.Person;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khoa.multiquiz.adapter.LobbyAdapter;
import com.khoa.multiquiz.fragment.HomeFragment;

import java.util.ArrayList;
import java.util.Random;

public class ThemeLobby extends AppCompatActivity {

    public com.google.android.material.appbar.MaterialToolbar TopAppBar;
    public Button CreateRoomButton;
    public QuestionTheme questionTheme;
    FirebaseDatabase database;
    DatabaseReference databaseReferenceLobbyCreation;
    DatabaseReference databaseReferenceLobbyFetching;
    DatabaseReference databaseReferenceCreateOpponent;
    FirebaseUser currentUser;
    Room roomInfo;
    ArrayList<Room> roomLists;
    RecyclerView LobbyRecyclerView;
    LobbyAdapter lobbyAdapter;

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
        LobbyRecyclerView = findViewById(R.id.LobbyRecyclerView);

        LobbyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomLists = new ArrayList<>();
        lobbyAdapter = new LobbyAdapter(ThemeLobby.this, roomLists);
        LobbyRecyclerView.setAdapter(lobbyAdapter);

        TopAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

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
                Intent intent = new Intent(ThemeLobby.this, WaitingIngame.class);
                intent.putExtra("Room", roomInfo);
                startActivity(intent);

            }
        });

        databaseReferenceLobbyFetching = database.getReference("Lobby");
        databaseReferenceLobbyFetching.orderByChild("questionThemeID").equalTo(questionTheme.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Room room = dataSnapshot.getValue(Room.class);
                    roomLists.add(room);
                }
                lobbyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        lobbyAdapter.setOnClickListener(new LobbyAdapter.OnClickListener() {
            @Override
            public void onClick(int position, Room room) {

                if (!currentUser.getUid().equals(room.getUserUID())){
                    createOpponentInfo(currentUser.getUid(),room);
                }

                Intent intent = new Intent(ThemeLobby.this, WaitingIngame.class);
                intent.putExtra("Room", room);
                startActivity(intent);
            }
        });


    }

    private void createNewRoomToDatabase(int QuestionThemeID, String UserUID, int NumberOfQuestion){
        String RoomID = String.valueOf(UserUID.substring(UserUID.length()-5));

        roomInfo.setRoomID(RoomID);
        roomInfo.setQuestionThemeID(QuestionThemeID);
        roomInfo.setUserUID(UserUID);
        roomInfo.setNumberOfQuestion(NumberOfQuestion);


        databaseReferenceLobbyCreation = database.getReference().child("Lobby").child(RoomID);
        databaseReferenceLobbyCreation.setValue(roomInfo);
    }

    private void createOpponentInfo(String UserUID, Room room){



        databaseReferenceCreateOpponent = database.getReference().child("Lobby").child(room.getRoomID()).child("opponentUID");
        databaseReferenceCreateOpponent.setValue(UserUID);

    }

}