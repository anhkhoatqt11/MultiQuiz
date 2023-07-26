package com.khoa.multiquiz;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.List;

public class GroupQuestion implements Serializable {

    String QuestionID;
    int QuestionNumber;
    int QuestionTime;
    String QuestionText;
    List<String> Answer;
    int CorrectAnswerID;
    String QuestionSetID;

    public GroupQuestion(){

    }

    public String getQuestionID() {
        return QuestionID;
    }

    public void setQuestionID(String questionID) {
        QuestionID = questionID;
    }

    public int getQuestionNumber() {
        return QuestionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        QuestionNumber = questionNumber;
    }

    public int getQuestionTime() {
        return QuestionTime;
    }

    public void setQuestionTime(int questionTime) {
        QuestionTime = questionTime;
    }

    public String getQuestionText() {
        return QuestionText;
    }

    public void setQuestionText(String questionText) {
        QuestionText = questionText;
    }

    public List<String> getAnswer() {
        return Answer;
    }

    public void setAnswer(List<String> answer) {
        Answer = answer;
    }

    public int getCorrectAnswerID() {
        return CorrectAnswerID;
    }

    public void setCorrectAnswerID(int corrrectAnswerID) {
        CorrectAnswerID = corrrectAnswerID;
    }

    public String getQuestionSetID() {
        return QuestionSetID;
    }

    public void setQuestionSetID(String questionSetID) {
        QuestionSetID = questionSetID;
    }
}
