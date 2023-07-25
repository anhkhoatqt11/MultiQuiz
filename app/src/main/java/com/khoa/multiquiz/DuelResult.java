package com.khoa.multiquiz;

import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.khoa.multiquiz.adapter.AnswerAdapter;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.units.qual.A;

import java.security.acl.Owner;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DuelResult extends AppCompatActivity {
    private final String YOU_LOSE = "BẠN THUA";
    private final String YOU_VICTORY = "BẠN THẮNG";
    private final String LOSE = "THUA CUỘC";
    private final String VICTORY = "CHIẾN THẮNG";
    private final String DRAW = "HOÀ";
    int OwnerPointNumber, OpponentPointNumber;
    String SessionID, OwnerUID, OpponentUID;
    TextView ResultTitle, OwnerPoint, OwnerName, OwnerCompetitiveResult, OpponentPoint, OpponentName, OpponentCompetitiveResult;
    ImageView OwnerAvatar, OpponentAvatar, ReturnButton;
    ListView UserAnswerListView;
    FirebaseDatabase database;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    FirebaseUser currentUser;
    Room currentRoom;
    CollectionReference collectionReferenceMatchData, collectionReferenceUserData, collectionReferenceUserAnswer;
    DocumentSnapshot historyDocument;
    StorageReference storageReferenceAvatar;
    DatabaseReference databaseReferenceRoomDeletion;
    ArrayList<DuelUserAnswerLog> duelUserAnswerLogArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel_result);

        ResultTitle = findViewById(R.id.ResultTitle);
        OwnerPoint = findViewById(R.id.OwnerPoint);
        OwnerName = findViewById(R.id.OwnerName);
        OwnerCompetitiveResult = findViewById(R.id.OwnerCompetitiveResult);
        OpponentPoint = findViewById(R.id.OpponentPoint);
        OpponentName = findViewById(R.id.OpponentName);
        OpponentCompetitiveResult = findViewById(R.id.OpponentCompetitiveResult);
        OwnerAvatar = findViewById(R.id.OwnerAvatar);
        OpponentAvatar = findViewById(R.id.OpponentAvatar);
        UserAnswerListView = findViewById(R.id.UserAnswerListView);
        ReturnButton = findViewById(R.id.ReturnButton);

        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        currentUser = MAuth.getCurrentUser();

        duelUserAnswerLogArrayList = new ArrayList<>();

        if (getIntent().hasExtra("SessionID")) {
            SessionID = getIntent().getStringExtra("SessionID");
        }

        if (getIntent().hasExtra("PreviousActivity")) {
            String PreviousActivity = getIntent().getStringExtra("PreviousActivity");
            currentRoom = (Room) getIntent().getSerializableExtra("Room");
            if (Objects.equals(PreviousActivity, "DuelIngame")){
                deleteRoom();
            }
        }

        getMatchInfo();

        ReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getMatchInfo(){
        collectionReferenceMatchData = firestore.collection("DuelMatchHistoryLog");
        collectionReferenceMatchData.whereEqualTo("sessionID", SessionID).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    historyDocument = documents.get(0);
                    OwnerUID = historyDocument.getString("ownerUID");
                    OpponentUID = historyDocument.getString("opponentUID");
                    Log.e("OwnerUIDResult", OwnerUID);
                    Log.e("OppoUUIDResult", OpponentUID);
                    OwnerPoint.setText(String.valueOf(historyDocument.getLong("ownerPoint")));
                    OpponentPoint.setText(String.valueOf(historyDocument.getLong("opponentPoint")));
                    OwnerPointNumber = Math.toIntExact(historyDocument.getLong("ownerPoint"));
                    OpponentPointNumber = Math.toIntExact(historyDocument.getLong("opponentPoint"));
                    getUserData();
                    getUserImage();
                    setUserCompetitiveResult();
                    fetchUserAnswer();
                }
            }
        });

    }

    private void getUserData(){
        Log.e("UserData", "Getting");
        collectionReferenceUserData = firestore.collection("UserData");
        if (OwnerUID != null) {
            collectionReferenceUserData.document(OwnerUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String displayName = documentSnapshot.getString("displayName");
                    OwnerName.setText(displayName);
                }
            });
        }
        if (OpponentUID != null) {
            collectionReferenceUserData.document(OpponentUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String displayName = documentSnapshot.getString("displayName");
                    OpponentName.setText(displayName);
                }
            });
        }
    }

    private void getUserImage(){
        Log.e("UserImage", "Getting");
        storageReferenceAvatar = storage.getReference();
        if (OwnerUID != null) {
            String childPath = "users/" + OwnerUID + "/avatar.jpg";
            storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(OwnerAvatar);
                }
            });
        }
        if (OpponentUID != null) {
            String childPath = "users/" + OpponentUID + "/avatar.jpg";
            storageReferenceAvatar.child(childPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String imageUrl = uri.toString();
                    Picasso.get().load(imageUrl).placeholder(R.drawable.img_useravatar).into(OpponentAvatar);
                }
            });
        }
    }

    private void setUserCompetitiveResult() {
        if (currentUser.getUid().equals(OwnerUID)) {
            ResultTitle.setText(OwnerPointNumber > OpponentPointNumber ? YOU_VICTORY :
                    OwnerPointNumber < OpponentPointNumber ? YOU_LOSE : DRAW);
        } else if (currentUser.getUid().equals(OpponentUID)) {
            ResultTitle.setText(OpponentPointNumber > OwnerPointNumber ? YOU_VICTORY :
                    OpponentPointNumber < OwnerPointNumber ? YOU_LOSE : DRAW);
        }
        OwnerCompetitiveResult.setText(OwnerPointNumber > OpponentPointNumber ? VICTORY :
                OwnerPointNumber < OpponentPointNumber ? LOSE : DRAW);
        OpponentCompetitiveResult.setText(OpponentPointNumber > OwnerPointNumber ? VICTORY :
                OpponentPointNumber < OwnerPointNumber ? LOSE : DRAW);
    }

    private void fetchUserAnswer(){
        collectionReferenceUserAnswer = firestore.collection("DuelUserAnswerLog");
        collectionReferenceUserAnswer.whereEqualTo("sessionID" , SessionID).whereEqualTo("userUID", currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                    DuelUserAnswerLog duelUserAnswerLog = document.toObject(DuelUserAnswerLog.class);
                    duelUserAnswerLogArrayList.add(duelUserAnswerLog);
                }

                AnswerAdapter adapter = new AnswerAdapter(DuelResult.this, duelUserAnswerLogArrayList, firestore);
                adapter.sortDataByQuestionNumber();
                UserAnswerListView.setAdapter(adapter);
            }
        });
    }
    private void deleteRoom(){
        new CountDownTimer(3000, 100){
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                if (currentUser.getUid().equals(currentRoom.getUserUID())) {
                    databaseReferenceRoomDeletion = database.getReference().child("Lobby").child(currentRoom.getRoomID());
                    databaseReferenceRoomDeletion.removeValue();
                }
            }
        }.start();
    }


}
