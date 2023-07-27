package com.khoa.multiquiz;

public class DuelMatchHistoryLog {

    long CreatedAt;
    long OpponentPoint;
    String OpponentUID;
    long OwnerPoint;
    String OwnerUID;
    String QuestionThemeID;
    String RoomID;
    String SessionID;

    public DuelMatchHistoryLog(){

    }

    public long getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(long createdAt) {
        CreatedAt = createdAt;
    }

    public long getOpponentPoint() {
        return OpponentPoint;
    }

    public void setOpponentPoint(long opponentPoint) {
        OpponentPoint = opponentPoint;
    }

    public String getOpponentUID() {
        return OpponentUID;
    }

    public void setOpponentUID(String opponentUID) {
        OpponentUID = opponentUID;
    }

    public long getOwnerPoint() {
        return OwnerPoint;
    }

    public void setOwnerPoint(long ownerPoint) {
        OwnerPoint = ownerPoint;
    }

    public String getOwnerUID() {
        return OwnerUID;
    }

    public void setOwnerUID(String ownerUID) {
        OwnerUID = ownerUID;
    }

    public String getQuestionThemeID() {
        return QuestionThemeID;
    }

    public void setQuestionThemeID(String questionThemeID) {
        QuestionThemeID = questionThemeID;
    }

    public String getRoomID() {
        return RoomID;
    }

    public void setRoomID(String roomID) {
        RoomID = roomID;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }
}
