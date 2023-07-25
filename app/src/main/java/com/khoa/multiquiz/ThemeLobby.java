package com.khoa.multiquiz;

import static kotlin.random.RandomKt.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoa.multiquiz.adapter.LobbyAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

public class ThemeLobby extends AppCompatActivity {

    public com.google.android.material.appbar.MaterialToolbar TopAppBar;
    public Button CreateRoomButton;
    public QuestionTheme questionTheme;
    String imageUrl;
    ImageView ThemeImage;
    FirebaseDatabase database;
    DatabaseReference databaseReferenceLobbyCreation;
    DatabaseReference databaseReferenceLobbyFetching;
    DatabaseReference databaseReferenceCreateOpponent;
    FirebaseUser currentUser;
    FirebaseStorage storage;
    Room roomInfo;
    ArrayList<Room> roomLists;
    RecyclerView LobbyRecyclerView;
    LobbyAdapter lobbyAdapter;
    SharedPreferences sharedPreferences;
    StorageReference storageReferenceAvatar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_duel_theme_lobby);

        TopAppBar = findViewById(R.id.ThemeLobbyTopAppBar);
        CreateRoomButton = findViewById(R.id.CreateRoomButton);
        LobbyRecyclerView = findViewById(R.id.LobbyRecyclerView);
        ThemeImage = findViewById(R.id.ThemeImage);
        sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);


        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        roomInfo = new Room();
        questionTheme = new QuestionTheme();
        LobbyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        roomLists = new ArrayList<>();
        lobbyAdapter = new LobbyAdapter(ThemeLobby.this, roomLists, storage);
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
        getThemeImage();

        CreateRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserDisplayName = sharedPreferences.getString("displayName", "");
                createNewRoomToDatabase(questionTheme.getId(), currentUser.getUid(), UserDisplayName, 10);
                Intent intent = new Intent(ThemeLobby.this, DuelWaitingIngame.class);
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

                Intent intent = new Intent(ThemeLobby.this, DuelWaitingIngame.class);
                intent.putExtra("Room", room);
                startActivity(intent);
            }
        });


    }

    private void createNewRoomToDatabase(String QuestionThemeID, String UserUID, String UserDisplayName, int NumberOfQuestion){
        String RoomID = String.valueOf(UserUID.substring(UserUID.length()-5));
        String RoomName = "Phòng của " + UserDisplayName;
        Random rnd = new Random();

        roomInfo.setRoomName(RoomName);
        roomInfo.setOwnerCreatedName(UserDisplayName);
        roomInfo.setRoomID(RoomID);
        roomInfo.setQuestionThemeID(QuestionThemeID);
        roomInfo.setUserUID(UserUID);
        roomInfo.setNumberOfQuestion(NumberOfQuestion);
        roomInfo.setOwnerState(0);
        roomInfo.setOpponentState(0);
        roomInfo.setOwnerPoint(0);
        roomInfo.setOwnerPoint(0);

        databaseReferenceLobbyCreation = database.getReference().child("Lobby").child(RoomID);
        databaseReferenceLobbyCreation.setValue(roomInfo);
    }

    private void createOpponentInfo(String UserUID, Room room){

        databaseReferenceCreateOpponent = database.getReference().child("Lobby").child(room.getRoomID()).child("opponentUID");
        databaseReferenceCreateOpponent.setValue(UserUID);

    }

    private void getThemeImage(){
        Picasso.get().load(questionTheme.getLink_image()).into(ThemeImage);
    }


}
