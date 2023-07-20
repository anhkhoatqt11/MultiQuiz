package com.khoa.multiquiz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WaitingIngame extends AppCompatActivity {

    TextView OwnerUID, OpponentUID;
    Room currentRoom;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    DatabaseReference databaseReferenceRoom;
    DatabaseReference databaseReferenceRoomDeletion;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_ingame);

        OwnerUID = findViewById(R.id.RoomOwnerName);
        OpponentUID = findViewById(R.id.OpponentName);

        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        Toast.makeText(this, "Waiting For Players", Toast.LENGTH_SHORT).show();

        if (getIntent().hasExtra("Room")){
            currentRoom = (Room) getIntent().getSerializableExtra("Room");
        }

        databaseReferenceRoom = database.getReference().child("Lobby").child(currentRoom.getRoomID());
        databaseReferenceRoom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Room readRoomData = snapshot.getValue(Room.class);
                if (readRoomData == null){
                    Toast.makeText( WaitingIngame.this, "Owner Deleted The Room", Toast.LENGTH_SHORT).show();
                    finish();
                }
                if (readRoomData != null) {
                    OwnerUID.setText(readRoomData.getUserUID());
                    OpponentUID.setText(readRoomData.getOpponentUID());
                }
                if (readRoomData.getOpponentUID() != null){
                    Intent intent = new Intent(WaitingIngame.this, Ingame.class);
                    intent.putExtra("Room", currentRoom);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (currentUser.getUid().equals(currentRoom.getUserUID())) {
            databaseReferenceRoomDeletion = database.getReference().child("Lobby").child(currentRoom.getRoomID());
            databaseReferenceRoomDeletion.removeValue();
        } else {
            databaseReferenceRoomDeletion = database.getReference().child("Lobby").child(currentRoom.getRoomID()).child("opponentUID");
            databaseReferenceRoomDeletion.removeValue();
        }
        super.onBackPressed();
    }
}
