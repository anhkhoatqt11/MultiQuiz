package com.khoa.multiquiz;

import java.io.Serializable;

public class Room implements Serializable {
    String RoomID;
    String UserUID;
    String OpponentUID;
    String QuestionThemeID;
    String SessionID;
    int NumberOfQuestion;
    int OwnerState;
    int OpponentState;
    int OwnerPoint;
    int OpponentPoint;


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

    public String getQuestionThemeID() {
        return QuestionThemeID;
    }

    public void setQuestionThemeID(String questionThemeID) {
        QuestionThemeID = questionThemeID;
    }

    public int getNumberOfQuestion() {
        return NumberOfQuestion;
    }

    public void setNumberOfQuestion(int numberOfQuestion) {
        NumberOfQuestion = numberOfQuestion;
    }

    public int getOwnerState() {
        return OwnerState;
    }

    public void setOwnerState(int ownerState) {
        OwnerState = ownerState;
    }

    public int getOpponentState() {
        return OpponentState;
    }

    public void setOpponentState(int opponentState) {
        OpponentState = opponentState;
    }

    public int getOwnerPoint() {
        return OwnerPoint;
    }

    public void setOwnerPoint(int ownerPoint) {
        OwnerPoint = ownerPoint;
    }

    public int getOpponentPoint() {
        return OpponentPoint;
    }

    public void setOpponentPoint(int opponentPoint) {
        OpponentPoint = opponentPoint;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }
}
