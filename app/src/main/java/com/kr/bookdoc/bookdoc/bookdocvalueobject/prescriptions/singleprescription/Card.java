package com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Card implements BookdocJsonParseHandler, Serializable{
    @SerializedName("id")
    public Integer id;
    @SerializedName("numbered")
    public Integer numbered;
    @SerializedName("title")
    @Nullable
    public String title;
    @SerializedName("description")
    public String description;

    @Override
    public void setData(JSONObject jobject) {
        id = jobject.optInt("id");
        numbered = jobject.optInt("numbered");
        title = jobject.optString("title");
        description = jobject.optString("description");
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
