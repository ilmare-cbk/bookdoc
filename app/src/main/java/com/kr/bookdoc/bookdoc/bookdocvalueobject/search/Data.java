package com.kr.bookdoc.bookdoc.bookdocvalueobject.search;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Data implements BookdocJsonParseHandler{
    @SerializedName("id")
    int id;
    @SerializedName("description")
    String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void setData(JSONObject jobject) {
        id = jobject.optInt("id");
        description = jobject.optString("description");
    }
}
