package com.khoa.multiquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.FieldIndex;
import com.khoa.multiquiz.adapter.QuestionAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupQuestionCreate extends AppCompatActivity {

    String QuestionSetID;
    boolean editMode;
    GroupQuestionSetInfo groupQuestionSetInfo;
    EditText QuestionSetTitle, QuestionSetDescription;
    TextView BackPressButton, SaveQuestionButton, CreateQuestionSetTitle;
    Button CreateQuestionButton;
    ArrayList<GroupQuestion> groupQuestionArrayList;
    RecyclerView QuestionRecyclerView;
    QuestionAdapter questionAdapter;
    FirebaseFirestore firestore;
    FirebaseUser currentUser;
    CollectionReference collectionReferenceGroupQuestionSet, collectionReferenceGroupQuestion;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        CreateQuestionButton = findViewById(R.id.CreateQuestionButton);
        QuestionSetTitle = findViewById(R.id.QuestionTitleInput);
        QuestionSetDescription = findViewById(R.id.DescriptionInput);
        QuestionRecyclerView = findViewById(R.id.QuestionRecyclerView);
        BackPressButton = findViewById(R.id.ButtonCancelCreateQuestion);
        SaveQuestionButton = findViewById(R.id.ButtonSaveQuestion);
        CreateQuestionSetTitle = findViewById(R.id.CreateQuestionSetTitle);

        firestore = FirebaseFirestore.getInstance();
        FirebaseAuth MAuth = FirebaseAuth.getInstance();
        currentUser = MAuth.getCurrentUser();

        QuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupQuestionArrayList = new ArrayList<>();
        questionAdapter = new QuestionAdapter(GroupQuestionCreate.this, groupQuestionArrayList);
        QuestionRecyclerView.setAdapter(questionAdapter);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(questionAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(QuestionRecyclerView);


        editMode = getIntent().getBooleanExtra("EditMode", false);
        if (editMode) {
            CreateQuestionSetTitle.setText("Sửa bộ câu hỏi");
            groupQuestionSetInfo = (GroupQuestionSetInfo) getIntent().getSerializableExtra("GroupQuestionSetInfo");
            QuestionSetTitle.setText(groupQuestionSetInfo.getQuestionSetTitle());
            QuestionSetDescription.setText(groupQuestionSetInfo.getQuestionSetDescription());
            collectionReferenceGroupQuestionSet = firestore.collection("GroupQuestionList");
            collectionReferenceGroupQuestionSet.whereEqualTo("questionSetID", groupQuestionSetInfo.getQuestonSetID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            GroupQuestion groupQuestion = new GroupQuestion();
                            groupQuestion.setQuestionID(documentSnapshot.getId());
                            groupQuestion.setQuestionNumber(Math.toIntExact(documentSnapshot.getLong("questionNumber")));
                            groupQuestion.setQuestionTime(Math.toIntExact(documentSnapshot.getLong("questionTime")));
                            groupQuestion.setQuestionText(documentSnapshot.getString("questionText"));
                            groupQuestion.setAnswer((List<String>) documentSnapshot.get("answer"));
                            groupQuestion.setCorrectAnswerID(Math.toIntExact(documentSnapshot.getLong("correctAnswerID")));
                            groupQuestion.setQuestionSetID(documentSnapshot.getString("questionSetID"));
                            groupQuestionArrayList.add(groupQuestion);
                        }
                        reorderArrayByQuestionNumber();
                        questionAdapter.notifyDataSetChanged();
                    }
                }
            });

        }

        CreateQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupQuestionCreate.this, GroupQuestionDetail.class);
                if (editMode) {
                    intent.putExtra("QuestionSetID", groupQuestionSetInfo.getQuestonSetID());
                    intent.putExtra("editCollectionCreateQuestion", true);
                    intent.putExtra("QuestionSetInfo", groupQuestionSetInfo);
                };
                callActivityForGroupQuestion.launch(intent);
            }
        });
        questionAdapter.setOnDeleteClickListener(new QuestionAdapter.OnDeleteClickListener() {
            @Override
            public void onDeleteClick(int position) {
                groupQuestionArrayList.remove(position);
                for (int i = position; i < groupQuestionArrayList.size(); i++) {
                    groupQuestionArrayList.get(i).setQuestionNumber(i + 1);
                }
                questionAdapter.notifyItemRemoved(position);
                questionAdapter.notifyDataSetChanged();
            }
        });

        questionAdapter.setOnItemClickListener(new QuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                GroupQuestion selectedQuestion = groupQuestionArrayList.get(position);
                Intent intent = new Intent(GroupQuestionCreate.this, GroupQuestionDetail.class);
                intent.putExtra("EditMode", true);
                intent.putExtra("QuestionSetInfo", groupQuestionSetInfo);
                intent.putExtra("QuestionNumber", selectedQuestion.getQuestionNumber());
                intent.putExtra("GroupQuestionData", selectedQuestion);
                editQuestionLauncher.launch(intent);
            }
        });

        BackPressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        SaveQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendQuestionSetToFirebase();
            }
        });
    }

    ActivityResultLauncher<Intent> callActivityForGroupQuestion = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            GroupQuestion groupQuestionReceived = (GroupQuestion) data.getSerializableExtra("GroupQuestionCreated");
                            groupQuestionArrayList.add(groupQuestionReceived);
                            questionAdapter.setData(groupQuestionArrayList);
                            questionAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
    );
    ActivityResultLauncher<Intent> editQuestionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            GroupQuestion updatedGroupQuestion = (GroupQuestion) data.getSerializableExtra("UpdatedGroupQuestion");
                            if (updatedGroupQuestion != null) {
                                int position = -1;
                                for (int i = 0; i < groupQuestionArrayList.size(); i++) {
                                    if (groupQuestionArrayList.get(i).getQuestionNumber() == updatedGroupQuestion.getQuestionNumber()) {
                                        position = i;
                                        break;
                                    }
                                }
                                if (position != -1) {
                                    Log.e("Edit", "Editing");
                                    Log.e("postion", String.valueOf(position));
                                    groupQuestionArrayList.set(position, updatedGroupQuestion);
                                    questionAdapter.notifyItemChanged(position);
                                    questionAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    }
                }
            }
    );

    private void sendQuestionSetToFirebase() {
        if (!editMode) {
            collectionReferenceGroupQuestionSet = firestore.collection("GroupQuestionSet");
            collectionReferenceGroupQuestion = firestore.collection("GroupQuestionList");

            Map<String, Object> QuestionSetData = new HashMap<>();
            QuestionSetData.put("questionSetTitle", String.valueOf(QuestionSetTitle.getText()));
            QuestionSetData.put("questionSetDescription", String.valueOf(QuestionSetDescription.getText()));
            QuestionSetData.put("userUID", currentUser.getUid());
            QuestionSetData.put("createdAt", System.currentTimeMillis());

            collectionReferenceGroupQuestionSet.add(QuestionSetData)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                DocumentReference documentReference = task.getResult();
                                String questionSetId = documentReference.getId();
                                for (GroupQuestion groupQuestion : groupQuestionArrayList) {
                                    groupQuestion.setQuestionSetID(questionSetId);
                                    collectionReferenceGroupQuestion.add(groupQuestion).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(GroupQuestionCreate.this, "Thêm bộ câu hỏi thành công", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(GroupQuestionCreate.this, "Thêm bộ câu hỏi thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                            finish();
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(GroupQuestionCreate.this, "Thêm bộ câu hỏi thất bại", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
        } else {
            collectionReferenceGroupQuestionSet = firestore.collection("GroupQuestionSet");
            collectionReferenceGroupQuestion = firestore.collection("GroupQuestionList");

            Map<String, Object> QuestionSetData = new HashMap<>();
            QuestionSetData.put("questionSetTitle", String.valueOf(QuestionSetTitle.getText()));
            QuestionSetData.put("questionSetDescription", String.valueOf(QuestionSetDescription.getText()));
            QuestionSetData.put("userUID", currentUser.getUid());
            QuestionSetData.put("createdAt", System.currentTimeMillis());

            collectionReferenceGroupQuestionSet.document(groupQuestionSetInfo.getQuestonSetID()).set(QuestionSetData);
            collectionReferenceGroupQuestion.whereEqualTo("questionSetID", groupQuestionSetInfo.getQuestonSetID())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            List<DocumentSnapshot> existingQuestions = querySnapshot.getDocuments();

                            // Compare existing questions with local questions and delete if not present
                            for (DocumentSnapshot existingQuestion : existingQuestions) {
                                String questionId = existingQuestion.getId();
                                boolean questionExistsLocally = false;
                                for (GroupQuestion localQuestion : groupQuestionArrayList) {
                                    if (localQuestion.getQuestionID() != null && localQuestion.getQuestionID().equals(questionId)) {
                                        questionExistsLocally = true;
                                        break;
                                    }
                                }

                                if (!questionExistsLocally) {
                                    collectionReferenceGroupQuestion.document(questionId)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Firestore", "Question deleted: " + questionId);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("Firestore", "Error deleting question: " + questionId, e);
                                                }
                                            });
                                }
                            }

                            for (GroupQuestion groupQuestion : groupQuestionArrayList) {
                                if (groupQuestion.getQuestionID() == null) {
                                    // This is a new question, add it to the collection
                                    collectionReferenceGroupQuestion
                                            .add(groupQuestion)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    String newQuestionId = documentReference.getId();
                                                    groupQuestion.setQuestionID(newQuestionId);
                                                    Log.d("Firestore", "New question added: " + newQuestionId);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("Firestore", "Error adding new question", e);
                                                }
                                            });
                                } else {
                                    // This is an existing question, update it
                                    collectionReferenceGroupQuestion
                                            .document(groupQuestion.getQuestionID())
                                            .set(groupQuestion)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("Firestore", "Question updated: " + groupQuestion.getQuestionID());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.e("Firestore", "Error updating question: " + groupQuestion.getQuestionID(), e);
                                                }
                                            });
                                }
                            }

                            Toast.makeText(GroupQuestionCreate.this, "Bộ câu hỏi đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Firestore", "Error retrieving existing questions", e);
                            Toast.makeText(GroupQuestionCreate.this, "Cập nhật bộ câu hỏi thất bại", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    });

        }
    }
    private void reorderArrayByQuestionNumber() {
        Collections.sort(groupQuestionArrayList, new Comparator<GroupQuestion>() {
            @Override
            public int compare(GroupQuestion q1, GroupQuestion q2) {
                return q1.getQuestionNumber() - q2.getQuestionNumber();
            }
        });

        // Notify the RecyclerView adapter that the data has changed
        questionAdapter.notifyDataSetChanged();
    }
}