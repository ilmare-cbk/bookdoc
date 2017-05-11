package com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;


public class Card {
    @SerializedName("title")
    @Nullable
    public String title;
    @SerializedName("description")
    public String description;

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
}
