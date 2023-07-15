package com.khoa.multiquiz;

import java.io.Serializable;

public class Room implements Serializable {
    String RoomID;
    String UserUID;
    String OpponentUID;
    int QuestionThemeID;
    int NumberOfQuestion;

    public Room(){

    }

    public String getOpponentUID() {
        return OpponentUID;
    }

    public void setOpponentUID(String opponentUID) {
        OpponentUID = opponentUID;
    }

    public String getRoomID() {
        return RoomID;
    }

    public void setRoomID(String roomID) {
        RoomID = roomID;
    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        UserUID = userUID;
    }

    public int getQuestionThemeID() {
        return QuestionThemeID;
    }

    public void setQuestionThemeID(int questionThemeID) {
        QuestionThemeID = questionThemeID;
    }

    public int getNumberOfQuestion() {
        return NumberOfQuestion;
    }

    public void setNumberOfQuestion(int numberOfQuestion) {
        NumberOfQuestion = numberOfQuestion;
    }
}
