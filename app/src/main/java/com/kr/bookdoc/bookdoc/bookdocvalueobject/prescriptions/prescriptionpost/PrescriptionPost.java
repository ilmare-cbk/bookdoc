package com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;


public class PrescriptionPost {
    @SerializedName("userId")
    int userId;

    @SerializedName("bookIsbn")
    String bookIsbn;

    @SerializedName("title")
    String title;

    @SerializedName("description")
    String description;

    @SerializedName("cards")
    ArrayList<Card> cards = new ArrayList<>();

    @SerializedName("tags")
    ArrayList<String> tags = new ArrayList<>();

    @SerializedName("imageType")
    @Nullable
    int imageType;

    @SerializedName("image")
    @Nullable
    String image;

    private volatile static PrescriptionPost uniqueInstance;

    public static PrescriptionPost getInstance() {
        if (uniqueInstance == null) {
            synchronized (PrescriptionPost.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new PrescriptionPost();
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

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
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

    public ArrayList<com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.Card> getCards() {
        return cards;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public int getImageType() {
        return imageType;
    }

    public void setImageType(int imageType) {
        this.imageType = imageType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void initData(){
        bookIsbn = null;
        title = null;
        description = null;
        cards.clear();
        tags.clear();
        image = null;
    }
}
