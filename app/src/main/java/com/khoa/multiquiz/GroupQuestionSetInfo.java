package com.khoa.multiquiz;

import com.google.firebase.firestore.DocumentSnapshot;

import java.io.Serializable;

public class GroupQuestionSetInfo implements Serializable {

    long CreatedAt;
    String QuestionSetTitle;
    String QuestionSetDescription;
    String QuestionSetUID;
    String QuestonSetID;

    public GroupQuestionSetInfo(DocumentSnapshot documentSnapshot){
        this.CreatedAt = documentSnapshot.getLong("createdAt");
        this.QuestionSetTitle = documentSnapshot.getString("questionSetTitle");
        this.QuestionSetDescription = documentSnapshot.getString("questionSetDescription");
        this.QuestionSetUID = documentSnapshot.getString("userUID");
        this.QuestonSetID = documentSnapshot.getId();
    }

    public long getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(long createdAt) {
        CreatedAt = createdAt;
    }

    public String getQuestionSetTitle() {
        return QuestionSetTitle;
    }

    public void setQuestionSetTitle(String questionSetTitle) {
        QuestionSetTitle = questionSetTitle;
    }

    public String getQuestionSetDescription() {
        return QuestionSetDescription;
    }

    public void setQuestionSetDescription(String questionSetDescription) {
        QuestionSetDescription = questionSetDescription;
    }

    public String getQuestionSetUID() {
        return QuestionSetUID;
    }

    public void setQuestionSetUID(String questionSetUID) {
        QuestionSetUID = questionSetUID;
    }

    public String getQuestonSetID() {
        return QuestonSetID;
    }

    public void setQuestonSetID(String questonSetID) {
        QuestonSetID = questonSetID;
    }
}
