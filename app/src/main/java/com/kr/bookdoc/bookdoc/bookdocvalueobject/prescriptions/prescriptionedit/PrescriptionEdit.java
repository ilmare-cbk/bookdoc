package com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionedit;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.Card;


public class PrescriptionEdit {
    @SerializedName("userId")
    int userId;

    @SerializedName("title")
    String title;

    @SerializedName("description")
    String description;

    @SerializedName("cards")
    ArrayList<Card> cards = new ArrayList<>();

    @SerializedName("tags")
    ArrayList<String> tags = new ArrayList<>();

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
}
