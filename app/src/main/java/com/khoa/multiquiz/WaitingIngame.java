package com.khoa.multiquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private int User1Status, User2Status;
    TextView OwnerUID, OpponentUID, OwnerStatus, OpponentStatus;
    Button ReadyButton;
    Room currentRoom,readRoomData;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    DatabaseReference databaseReferenceRoom;
    DatabaseReference databaseReferenceRoomDeletion;
    ValueEventListener CheckRoomStatus;
    final String Waiting = "Đang chờ sẵn sàng";
    final String Ready = "Sẵn sàng";
    final String CancelReady = "Huỷ sẵn sàng";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_ingame);

        OwnerUID = findViewById(R.id.RoomOwnerName);
        OpponentUID = findViewById(R.id.OpponentName);
        ReadyButton = findViewById(R.id.ReadyButton);
        OwnerStatus = findViewById(R.id.RoomOwnerStatus);
        OpponentStatus = findViewById(R.id.OpponentStatus);

        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        Toast.makeText(this, "Waiting For Players", Toast.LENGTH_SHORT).show();

        if (getIntent().hasExtra("Room")){
            currentRoom = (Room) getIntent().getSerializableExtra("Room");
        }

        checkStatusOfRoomAndUser();
        readyButtonFunctionInitialize();

    }

/*    @Override
    public void onBackPressed() {
        if (currentUser.getUid().equals(currentRoom.getUserUID())) {
            databaseReferenceRoomDeletion = database.getReference().child("Lobby").child(currentRoom.getRoomID());
            databaseReferenceRoomDeletion.removeValue();
        } else {
            databaseReferenceRoomDeletion = database.getReference().child("Lobby").child(currentRoom.getRoomID()).child("opponentUID");
            databaseReferenceRoomDeletion.removeValue();
        }
        super.onBackPressed();
    }*/


    private void checkStatusOfRoomAndUser() {
        databaseReferenceRoom = database.getReference().child("Lobby").child(currentRoom.getRoomID());
        databaseReferenceRoom.addValueEventListener(CheckRoomStatus = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readRoomData = snapshot.getValue(Room.class);
                if (readRoomData == null){
                    Toast.makeText( WaitingIngame.this, "Owner Deleted The Room", Toast.LENGTH_SHORT).show();
                    finish();
                }
                if (readRoomData != null) {
                    OwnerUID.setText(readRoomData.getUserUID());
                    OpponentUID.setText(readRoomData.getOpponentUID());
                    setUserStatusForItem(readRoomData.getOwnerState(), readRoomData.getOpponentState());
                }
                if (readRoomData.getOwnerState() == 1 && readRoomData.getOpponentState() == 1){
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

    private void setUserStatusForItem(int User1Status, int User2Status){
        switch (User1Status){
            case 0:
                OwnerStatus.setText(Waiting);
                if (currentUser.getUid().equals(readRoomData.getUserUID())){ReadyButton.setText(Ready);}
                break;
            case 1:
                OwnerStatus.setText(Ready);
                if (currentUser.getUid().equals(readRoomData.getUserUID())){ReadyButton.setText(CancelReady);}
                break;
        }
        switch (User2Status){
            case 0:
                OpponentStatus.setText(Waiting);
                if (currentUser.getUid().equals(readRoomData.getOpponentUID())){ReadyButton.setText(Ready);}
                break;
            case 1:
                OpponentStatus.setText(Ready);
                if (currentUser.getUid().equals(readRoomData.getOpponentUID())){ReadyButton.setText(CancelReady);}
                break;
        }
    }
    private void readyButtonFunctionInitialize(){
        ReadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String childPath = null;
                int currentStatus = 0;

                if (currentUser.getUid().equals(readRoomData.getUserUID())){
                    childPath = "ownerState";
                    currentStatus = readRoomData.getOwnerState();
                } else if (currentUser.getUid().equals(readRoomData.getOpponentUID())){
                    childPath = "opponentState";
                    currentStatus = readRoomData.getOpponentState();
                }

                if (currentStatus == 0) {
                    currentStatus = 1;
                } else {
                    currentStatus = 0;
                }

                DatabaseReference databaseReferenceWriteUserStatus = database.getReference().child("Lobby").child(readRoomData.getRoomID()).child(childPath);
                databaseReferenceWriteUserStatus.setValue(currentStatus);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        databaseReferenceRoom.removeEventListener(CheckRoomStatus);
    }
}
