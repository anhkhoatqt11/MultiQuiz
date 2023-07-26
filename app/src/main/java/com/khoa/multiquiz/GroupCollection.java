package com.khoa.multiquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.khoa.multiquiz.adapter.QuestionAdapter;
import com.khoa.multiquiz.adapter.QuestionSetAdapter;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class GroupCollection extends AppCompatActivity {

    RecyclerView CollectionRecyclerView;
    FirebaseUser currentUser;
    FirebaseFirestore firestore;
    ArrayList<GroupQuestionSetInfo> groupQuestionSetInfos;
    QuestionSetAdapter questionSetAdapter;
    CollectionReference collectionReferenceGroupQuestionSet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_collection);

        CollectionRecyclerView = findViewById(R.id.CollectionRecyclerView);

        firestore = FirebaseFirestore.getInstance();
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();

        CollectionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupQuestionSetInfos = new ArrayList<>();
        questionSetAdapter = new QuestionSetAdapter(GroupCollection.this, groupQuestionSetInfos);
        CollectionRecyclerView.setAdapter(questionSetAdapter);

        getQuestionSetData();

        questionSetAdapter.setOnClickListener(new QuestionSetAdapter.OnClickListener() {
            @Override
            public void onClick(int position, GroupQuestionSetInfo groupQuestionSetInfo) {
                Intent intent = new Intent(GroupCollection.this, GroupQuestionCreate.class);
                intent.putExtra("EditMode", true);
                intent.putExtra("GroupQuestionSetInfo", groupQuestionSetInfo);
                groupQuestionCreateLauncher.launch(intent);
            }
        });

    }

    private void getQuestionSetData(){
        collectionReferenceGroupQuestionSet = firestore.collection("GroupQuestionSet");
        collectionReferenceGroupQuestionSet.whereEqualTo("userUID", currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()){
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        GroupQuestionSetInfo groupQuestionSetInfo = new GroupQuestionSetInfo(documentSnapshot);
                        groupQuestionSetInfo.setQuestonSetID(documentSnapshot.getId());
                        groupQuestionSetInfos.add(groupQuestionSetInfo);
                    }
                    questionSetAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    ActivityResultLauncher<Intent> groupQuestionCreateLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    questionSetAdapter.notifyDataSetChanged();
                }
            }
    );
}
