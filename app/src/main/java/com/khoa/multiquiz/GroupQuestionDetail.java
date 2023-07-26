package com.khoa.multiquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroupQuestionDetail extends AppCompatActivity {
    boolean editMode;
    int QuestionNumber;
    TextInputLayout TimeTextInputLayout;
    AutoCompleteTextView TimeAutoCompleteTextView;
    TextView BackPressButton, SaveQuestionButton, CreateQuestionTitle;
    EditText QuestionEditText, Answer0EditText, Answer1EditText, Answer2EditText, Answer3EditText;
    CheckBox CheckBoxAnswer0, CheckBoxAnswer1, CheckBoxAnswer2, CheckBoxAnswer3;
    GroupQuestion groupQuestion, existingGroupQuestion;
    GroupQuestionSetInfo groupQuestionSetInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_create_question);

        TimeTextInputLayout = findViewById(R.id.NumberOfQuestionMenu);
        TimeAutoCompleteTextView = findViewById(R.id.TimeOfQuestion);
        QuestionEditText = findViewById(R.id.QuestionTextInput);
        Answer0EditText = findViewById(R.id.Answer0Input);
        Answer1EditText = findViewById(R.id.Answer1Input);
        Answer2EditText = findViewById(R.id.Answer2Input);
        Answer3EditText = findViewById(R.id.Answer3Input);
        CheckBoxAnswer0 = findViewById(R.id.CheckBoxAnswer0);
        CheckBoxAnswer1 = findViewById(R.id.CheckBoxAnswer1);
        CheckBoxAnswer2 = findViewById(R.id.CheckBoxAnswer2);
        CheckBoxAnswer3 = findViewById(R.id.CheckBoxAnswer3);
        BackPressButton = findViewById(R.id.BackPressButton);
        SaveQuestionButton = findViewById(R.id.SaveQuestionButton);
        CreateQuestionTitle = findViewById(R.id.CreateQuestionTitle);

        groupQuestion = new GroupQuestion();

        List<String> items = Arrays.asList("10", "15", "20");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item, items);
        TimeAutoCompleteTextView.setAdapter(adapter);

        editMode = getIntent().getBooleanExtra("EditMode", false);
        if (editMode) {
            CreateQuestionTitle.setText("Chỉnh sửa câu hỏi");
            existingGroupQuestion = (GroupQuestion) getIntent().getSerializableExtra("GroupQuestionData");
            groupQuestionSetInfo = (GroupQuestionSetInfo) getIntent().getSerializableExtra("QuestionSetInfo");
            QuestionNumber = existingGroupQuestion.getQuestionNumber();
            TimeAutoCompleteTextView.setText(String.valueOf(existingGroupQuestion.getQuestionTime()));
            QuestionEditText.setText(existingGroupQuestion.getQuestionText());
            List<String> answers = existingGroupQuestion.getAnswer();
            Answer0EditText.setText(answers.get(0));
            Answer1EditText.setText(answers.get(1));
            Answer2EditText.setText(answers.get(2));
            Answer3EditText.setText(answers.get(3));
            int correctAnswerID = existingGroupQuestion.getCorrectAnswerID();;
            setCheckBoxCheck(correctAnswerID);
        }

        groupQuestionSetInfo = (GroupQuestionSetInfo) getIntent().getSerializableExtra("QuestionSetInfo");

        BackPressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SaveQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendQuestionToPreviousActivity();
            }
        });

        createCheckBoxListenerEvent();
    }

    private void createCheckBoxListenerEvent(){
        CheckBoxAnswer0.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    setLogicForCheckBox(0);
                }
            }
        });

        CheckBoxAnswer1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    setLogicForCheckBox(1);
                }
            }
        });
        CheckBoxAnswer2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    setLogicForCheckBox(2);
                }
            }
        });
        CheckBoxAnswer3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    setLogicForCheckBox(3);
                }
            }
        });
    }

    private void setCheckBoxCheck(int CheckBoxID){
        switch (CheckBoxID){
            case 0:
                CheckBoxAnswer0.setChecked(true);
                break;
            case 1:
                CheckBoxAnswer1.setChecked(true);
                break;
            case 2:
                CheckBoxAnswer2.setChecked(true);
                break;
            case 3:
                CheckBoxAnswer3.setChecked(true);
                break;
        }
    }
    private void setLogicForCheckBox(int CheckBoxID){
        switch (CheckBoxID){
            case 0:
                CheckBoxAnswer1.setChecked(false);
                CheckBoxAnswer2.setChecked(false);
                CheckBoxAnswer3.setChecked(false);
                break;
            case 1:
                CheckBoxAnswer0.setChecked(false);
                CheckBoxAnswer2.setChecked(false);
                CheckBoxAnswer3.setChecked(false);
                break;
            case 2:
                CheckBoxAnswer0.setChecked(false);
                CheckBoxAnswer1.setChecked(false);
                CheckBoxAnswer3.setChecked(false);
                break;
            case 3:
                CheckBoxAnswer0.setChecked(false);
                CheckBoxAnswer1.setChecked(false);
                CheckBoxAnswer2.setChecked(false);
                break;
        }
    }

    private int getCorrectAnswerCheck(){
        if (CheckBoxAnswer0.isChecked()){
            return 0;
        } else if (CheckBoxAnswer1.isChecked()){
            return 1;
        } else if (CheckBoxAnswer2.isChecked()){
            return 2;
        } else if (CheckBoxAnswer3.isChecked()){
            return 3;
        }
        return 0;
    }

    private void sendQuestionToPreviousActivity(){
        int QuestionTime = Integer.parseInt(String.valueOf(TimeAutoCompleteTextView.getText()));
        String QuestionText = String.valueOf(QuestionEditText.getText());
        String Answer0Text = String.valueOf(Answer0EditText.getText());
        String Answer1Text = String.valueOf(Answer1EditText.getText());
        String Answer2Text = String.valueOf(Answer2EditText.getText());
        String Answer3Text = String.valueOf(Answer3EditText.getText());
        int CorrectAnswerID = getCorrectAnswerCheck();

        List<String> Answer = new ArrayList<String>();

        Answer.add(Answer0Text);
        Answer.add(Answer1Text);
        Answer.add(Answer2Text);
        Answer.add(Answer3Text);

        groupQuestion.setQuestionTime(QuestionTime);
        groupQuestion.setQuestionText(QuestionText);
        groupQuestion.setAnswer(Answer);
        groupQuestion.setCorrectAnswerID(CorrectAnswerID);

        Intent resultIntent = new Intent();
        if (!editMode) {
            if (groupQuestionSetInfo != null){
                groupQuestion.setQuestionSetID(groupQuestionSetInfo.getQuestonSetID());
            }
            resultIntent.putExtra("GroupQuestionCreated", groupQuestion);
        } else {
            groupQuestion.setQuestionNumber(QuestionNumber);
            groupQuestion.setQuestionID(existingGroupQuestion.getQuestionID());
            groupQuestion.setQuestionSetID(groupQuestionSetInfo.getQuestonSetID());
            resultIntent.putExtra("UpdatedGroupQuestion", groupQuestion);
        }
        setResult(Activity.RESULT_OK, resultIntent);
        finish();

    }

}
