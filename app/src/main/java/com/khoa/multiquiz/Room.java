package com.khoa.multiquiz;

public class Room {
    String RoomID;
    String UserUID;
    int QuestionThemeID;
    int NumberOfQuestion;

    public Room(){

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
