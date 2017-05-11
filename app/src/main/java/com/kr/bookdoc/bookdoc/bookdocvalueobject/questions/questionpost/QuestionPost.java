package com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.questionpost;

import com.google.gson.annotations.SerializedName;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.PrescriptionPost;


public class QuestionPost {

    @SerializedName("userId")
    int userId;

    @SerializedName("description")
    String description;

    private volatile static QuestionPost uniqueInstance;

    public static QuestionPost getInstance() {
        if (uniqueInstance == null) {
            synchronized (PrescriptionPost.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new QuestionPost();
                }
            }
        }
        return uniqueInstance;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
