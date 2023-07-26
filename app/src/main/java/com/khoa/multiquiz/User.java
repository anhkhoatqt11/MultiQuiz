package com.khoa.multiquiz;

import java.io.Serializable;

public class User implements Serializable {

    String UserUID;

    public User(){

    }

    public String getUserUID() {
        return UserUID;
    }

    public void setUserUID(String userUID) {
        UserUID = userUID;
    }
}
