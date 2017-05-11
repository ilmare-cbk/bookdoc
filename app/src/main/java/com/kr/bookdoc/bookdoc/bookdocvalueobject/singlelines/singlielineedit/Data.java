package com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlielineedit;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Data implements BookdocJsonParseHandler {
    @SerializedName("id")
    int id;
    @SerializedName("userId")
    int userId;
    @SerializedName("userName")
    String userName;
    @SerializedName("bookId")
    int bookId;
    @SerializedName("bookIsbn")
    String bookIsbn;
    @SerializedName("bookTitle")
    String bookTitle;
    @SerializedName("bookAuthor")
    String bookAuthor;
    @SerializedName("bookPublisher")
    String bookPublisher;
    @SerializedName("bookImagePath")
    @Nullable
    String bookImagePath;
    @SerializedName("description")
    String description;
    @SerializedName("font")
    int font;
    @SerializedName("alignment")
    int alignment;
    @SerializedName("imageType")
    @Nullable
    int imageType;
    @SerializedName("imagePath")
    @Nullable
    String imagePath;
    @SerializedName("created")
    String created;
    @SerializedName("modified")
    String modified;


    @Override
    public void setData(JSONObject jobject) {
        id = jobject.optInt("id");
        userId = jobject.optInt("userId");
        userName = jobject.optString("userName");
        bookId = jobject.optInt("bookId");
        bookIsbn = jobject.optString("bookIsbn");
        bookTitle = jobject.optString("bookTitle");
        bookAuthor = jobject.optString("bookAuthor");
        bookPublisher = jobject.optString("bookPublisher");
        bookImagePath = jobject.optString("bookImagePath");
        description = jobject.optString("description");
        font = jobject.optInt("font");
        alignment = jobject.optInt("alignment");
        imageType = jobject.optInt("imageType");
        imagePath = jobject.optString("imagePath");
        created = jobject.optString("created");
        modified = jobject.optString("modified");
    }
}
