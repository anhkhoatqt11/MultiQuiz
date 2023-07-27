package com.khoa.multiquiz;

import java.io.Serializable;

public class User implements Serializable {

    String UserUID;
    Long UserPoint;


    public User(){

    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        UserUID = userUID;
    }

    public Long getUserPoint() {
        return UserPoint;
    }

    public void setUserPoint(Long userPoint) {
        UserPoint = userPoint;
    }
}
