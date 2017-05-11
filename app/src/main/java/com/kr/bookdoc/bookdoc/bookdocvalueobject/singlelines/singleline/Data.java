package com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singleline;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Data implements BookdocJsonParseHandler {

    @SerializedName("id")
    private int id;
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("bookId")
    private int bookId;
    @SerializedName("bookIsbn")
    private String bookIsbn;
    @SerializedName("bookTitle")
    private String bookTitle;
    @SerializedName("bookAuthor")
    private String bookAuthor;
    @SerializedName("bookPublisher")
    private String bookPublisher;
    @SerializedName("bookImagePath")
    @Nullable
    private String bookImagePath;
    @SerializedName("description")
    private String description;
    @SerializedName("font")
    private Integer font;
    @SerializedName("alignment")
    private Integer alignment;
    @SerializedName("imageType")
    @Nullable
    private Integer imageType;
    @SerializedName("imagePath")
    @Nullable
    private String imagePath;
    @SerializedName("created")
    private String created;
    @SerializedName("modified")
    @Nullable
    private String modified;


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
    public Integer getUserId() {
        return userId;
    }
    public String getDescription() {
        return description;
    }
    public Integer getFont() {
        return font;
    }
    public Integer getAlignment() {
        return alignment;
    }
    public Integer getImageType() {
        if(imageType == null){
            imageType = 0;
        }
        return imageType;
    }
    public String getImagePath() {
        return imagePath;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    @Nullable
    public String getBookImagePath() {
        return bookImagePath;
    }

    public String getCreated() {
        return created;
    }

    @Nullable
    public String getModified() {
        return modified;
    }
}
