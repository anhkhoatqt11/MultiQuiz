package com.khoa.multiquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.khoa.multiquiz.adapter.QuestionAdapter;

import java.util.ArrayList;

public class GroupQuestionCreate extends AppCompatActivity {

    EditText QuestionSetTitle, QuestionSetDescription;
    TextView BackPressButton, SaveQuestionButton;
    Button CreateQuestionButton;
    ArrayList<GroupQuestion> groupQuestionArrayList;
    RecyclerView QuestionRecyclerView;
    QuestionAdapter questionAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create);

        CreateQuestionButton = findViewById(R.id.CreateQuestionButton);
        QuestionSetTitle = findViewById(R.id.QuestionTitleInput);
        QuestionSetDescription = findViewById(R.id.DescriptionInput);
        QuestionRecyclerView = findViewById(R.id.QuestionRecyclerView);

        QuestionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupQuestionArrayList = new ArrayList<>();
        questionAdapter = new QuestionAdapter(GroupQuestionCreate.this, groupQuestionArrayList);
        QuestionRecyclerView.setAdapter(questionAdapter);

        ItemTouchHelper.Callback callback = new ItemMoveCallback(questionAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(QuestionRecyclerView);

        CreateQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupQuestionCreate.this, GroupQuestionDetail.class);
                intent.putExtra("QuestionNumber", groupQuestionArrayList.size()+1);
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
                intent.putExtra("QuestionNumber", selectedQuestion.getQuestionNumber());
                intent.putExtra("GroupQuestionData", selectedQuestion);
                editQuestionLauncher.launch(intent);
            }
        });

    }

    ActivityResultLauncher<Intent> callActivityForGroupQuestion = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data != null){
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
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        if (data != null){
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


}
