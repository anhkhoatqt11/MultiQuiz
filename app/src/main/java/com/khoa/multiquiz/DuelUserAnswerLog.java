package com.khoa.multiquiz;

public class DuelUserAnswerLog {

    long CreatedAt;
    String QuestionID;
    int QuestionNumber;
    String QuestionThemeID;
    String RoomID;
    String SessionID;
    String Timestamp;
    String UserUID;
    int SelectedAnswer;

    public DuelUserAnswerLog(){

    }

    public long getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(long createdAt) {
        CreatedAt = createdAt;
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

    public String getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(String timestamp) {
        Timestamp = timestamp;
    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        UserUID = userUID;
    }

    public int getSelectedAnswer() {
        return SelectedAnswer;
    }

    public void setSelectedAnswer(int selectedAnswer) {
        SelectedAnswer = selectedAnswer;
    }
}
