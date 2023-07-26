package com.khoa.multiquiz;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class DuelWaitingIngame extends AppCompatActivity {

    private int User1Status, User2Status;
    TextView OwnerName, OpponentName, OwnerStatus, OpponentStatus, RoomName;
    ImageView OwnerAvatarImageTop, OwnerAvatarImage, OpponentAvatarImage, EditRoomButton;
    Button ReadyButton;
    Room currentRoom,readRoomData;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    DatabaseReference databaseReferenceRoom;
    DatabaseReference databaseReferenceRoomDeletion;
    CollectionReference collectionReferenceUserData;
    StorageReference storageReferenceAvatar;
    ValueEventListener CheckRoomStatus;
    String CurrentRoomName;
    int CurrentNumberOfQuestion;
    final String Waiting = "Đang chờ sẵn sàng";
    final String Ready = "Sẵn sàng";
    final String CancelReady = "Huỷ sẵn sàng";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel_waiting_ingame);

        OwnerName = findViewById(R.id.RoomOwnerName);
        OpponentName = findViewById(R.id.OpponentName);
        ReadyButton = findViewById(R.id.ReadyButton);
        RoomName = findViewById(R.id.RoomName);
        OwnerStatus = findViewById(R.id.RoomOwnerStatus);
        OpponentStatus = findViewById(R.id.OpponentStatus);
        OwnerAvatarImage = findViewById(R.id.RoomOwnerImage);
        OpponentAvatarImage = findViewById(R.id.OpponentImage);
        OwnerAvatarImageTop = findViewById(R.id.RoomOwnerImageTop);
        EditRoomButton = findViewById(R.id.EditRoomInfoButton);


        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = MAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        if (getIntent().hasExtra("Room")){
            currentRoom = (Room) getIntent().getSerializableExtra("Room");
        }

        checkStatusOfRoomAndUser();
        readyButtonFunctionInitialize();

        EditRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditRoomDialog();
            }
        });

    }

    private void checkStatusOfRoomAndUser() {
        databaseReferenceRoom = database.getReference().child("Lobby").child(currentRoom.getRoomID());
        databaseReferenceRoom.addValueEventListener(CheckRoomStatus = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readRoomData = snapshot.getValue(Room.class);
                if (readRoomData == null){
                    Toast.makeText( DuelWaitingIngame.this, "Owner Deleted The Room", Toast.LENGTH_SHORT).show();
                    finish();
                }
                if (readRoomData != null) {
//                    OwnerUID.setText(readRoomData.getUserUID());
//                    OpponentUID.setText(readRoomData.getOpponentUID());
                    RoomName.setText(readRoomData.getRoomName());
                    CurrentRoomName = readRoomData.getRoomName();
                    CurrentNumberOfQuestion = readRoomData.getNumberOfQuestion();
                    getUserData(readRoomData.getUserUID(), readRoomData.getOpponentUID());
                    getUserImage(readRoomData.getUserUID(), readRoomData.getOpponentUID());
                    setUserStatusForItem(readRoomData.getOwnerState(), readRoomData.getOpponentState());
                }
                if (readRoomData.getOpponentUID() == null){
                    OpponentName.setText("Người chơi 2");
                    OpponentStatus.setText(Waiting);
                    String childPath = "DefaultAvatar/GeneralProfileImage.png";
                    storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(OpponentAvatarImage);
                        }
                    });
                }
                if (readRoomData.getOwnerState() == 1 && readRoomData.getOpponentState() == 1){
                    Intent intent = new Intent(DuelWaitingIngame.this, DuelIngame.class);
                    intent.putExtra("Room", currentRoom);
                    startActivity(intent);
                    finish();
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

    private void getUserData(String ownerUID, String opponentUID){
        collectionReferenceUserData = firestore.collection("UserData");
        if (ownerUID != null) {
            collectionReferenceUserData.document(ownerUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String displayName = documentSnapshot.getString("displayName");
                    OwnerName.setText(displayName);
                }
            });
        }
        if (opponentUID != null) {
            collectionReferenceUserData.document(opponentUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String displayName = documentSnapshot.getString("displayName");
                    OpponentName.setText(displayName);
                }
            });
        }
    }

    private void getUserImage(String ownerUID, String opponentUID){
        storageReferenceAvatar = storage.getReference();
        if (ownerUID != null) {
            String childPath = "users/" + ownerUID + "/avatar.jpg";
            storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(OwnerAvatarImage);
                    Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(OwnerAvatarImageTop);
                }
            });
        }
        if (opponentUID != null) {
            String childPath = "users/" + opponentUID + "/avatar.jpg";
            storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(OpponentAvatarImage);
                }
            });
        }
    }

    private void openEditRoomDialog(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_roominfo);

        Window window = dialog.getWindow();
        if (window == null){
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttribute = window.getAttributes();
        windowAttribute.gravity = Gravity.CENTER;
        window.setAttributes(windowAttribute);

        dialog.setCancelable(false);

        EditText NewRoomNameEditText = dialog.findViewById(R.id.RoomNameInput);
        TextInputLayout textInputLayout = dialog.findViewById((R.id.NumberOfQuestionMenu));
        AutoCompleteTextView autoCompleteTextView = textInputLayout.findViewById(R.id.NumberOfQuestion);
        Button ButtonCancel = dialog.findViewById(R.id.ButtonCancel);
        Button ButtonConfirm = dialog.findViewById(R.id.ButtonConfirm);

        NewRoomNameEditText.setText(CurrentRoomName);
        autoCompleteTextView.setText(String.valueOf(CurrentNumberOfQuestion));

        if (!currentUser.getUid().equals(currentRoom.getUserUID())){
            ButtonConfirm.setEnabled(false);
        }

        List<String> items = Arrays.asList("5", "10", "15", "20");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, items);
        autoCompleteTextView.setAdapter(adapter);

        ButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        ButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String NewRoomName = NewRoomNameEditText.getText().toString();
                int NumberOfQuestion = Integer.parseInt(String.valueOf(autoCompleteTextView.getText()));
                databaseReferenceRoom = database.getReference().child("Lobby").child(currentRoom.getRoomID()).child("roomName");
                databaseReferenceRoom.setValue(NewRoomName);
                Log.e("New Room Name", NewRoomName);
                databaseReferenceRoom = database.getReference().child("Lobby").child(currentRoom.getRoomID()).child("numberOfQuestion");
                databaseReferenceRoom.setValue(NumberOfQuestion);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    protected void onPause() {
        databaseReferenceRoom.removeEventListener(CheckRoomStatus);
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (currentUser.getUid().equals(readRoomData.getOpponentUID())) {
            databaseReferenceRoomDeletion = database.getReference().child("Lobby").child(currentRoom.getRoomID()).child("opponentUID");
            databaseReferenceRoomDeletion.removeValue();
        }
        super.onBackPressed();
    }
}
