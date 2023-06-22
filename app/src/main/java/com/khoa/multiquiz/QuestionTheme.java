package com.khoa.multiquiz;

import java.io.Serializable;

public class QuestionTheme implements Serializable {
     int id;
     String link_image;
     String theme_name;
//
//    public QuestionTheme(int id, String link_image, String theme_name) {
//        this.id = id;
//        this.link_image = link_image;
//        this.theme_name = theme_name;
//    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink_image() {
        return link_image;
    }

    public void setLink_image(String link_image) {
        this.link_image = link_image;
    }

    public String getTheme_name() {
        return theme_name;
    }

    public void setTheme_name(String theme_name) {
        this.theme_name = theme_name;
    }
}
