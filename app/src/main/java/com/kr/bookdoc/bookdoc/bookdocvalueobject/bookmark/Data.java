package com.kr.bookdoc.bookdoc.bookdocvalueobject.bookmark;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Data implements BookdocJsonParseHandler {
    @SerializedName("id")
    public Integer id;
    @SerializedName("isbn")
    public String isbn;
    @SerializedName("title")
    public String title;
    @SerializedName("author")
    public String author;
    @SerializedName("category")
    public String category;
    @SerializedName("publisher")
    public String publisher;
    @SerializedName("imagePath")
    public String imagePath;
    @SerializedName("created")
    public String created;
    
    @Override
    public void setData(JSONObject jobject) {
        id = jobject.optInt("id");
        isbn = jobject.optString("isbn");
        title = jobject.optString("title");
        author = jobject.optString("author");
        category = jobject.optString("category");
        publisher = jobject.optString("publisher");
        imagePath = jobject.optString("imagePath");
        created = jobject.optString("created");
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCreated() {
        return created;
    }
}
