package com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Data implements BookdocJsonParseHandler {
    @SerializedName("id")
    private Integer id;
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("userImagePath")
    @Nullable
    private String userImagePath;
    @SerializedName("bookId")
    private Integer bookId;
    @SerializedName("bookIsbn")
    private String bookIsbn;
    @SerializedName("bookTitle")
    private String bookTitle;
    @SerializedName("bookAuthor")
    private String bookAuthor;
    @SerializedName("bookCategory")
    private String bookCategory;
    @SerializedName("bookPublisher")
    private String bookPublisher;
    @SerializedName("bookImagePath")
    @Nullable
    private String bookImagePath;
    @SerializedName("title")
    private String title;
    @SerializedName("liked")
    private Integer liked;
    @SerializedName("commented")
    private Integer commented;
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
        userImagePath = jobject.optString("userImagePath");
        bookId = jobject.optInt("bookId");
        bookIsbn = jobject.optString("bookIsbn");
        bookTitle = jobject.optString("bookTitle");
        bookCategory = jobject.optString("bookCategory");
        bookPublisher = jobject.optString("bookPublisher");
        bookImagePath = jobject.optString("bookImagePath");
        title = jobject.optString("title");
        liked = jobject.optInt("liked");
        commented= jobject.optInt("commented");
        imageType = jobject.optInt("imageType");
        imagePath = jobject.optString("imagePath");
        created = jobject.optString("created");
        modified = jobject.optString("modified");
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public Integer getBookId() {
        return bookId;
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

    public String getBookImagePath() {
        return bookImagePath;
    }

    public String getTitle() {
        return title;
    }

    public Integer getLiked() {
        return liked;
    }

    public Integer getCommented() {
        return commented;
    }

    public Integer getImageType() {
        return imageType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImageType(@Nullable Integer imageType) {
        this.imageType = imageType;
    }

    public void setImagePath(@Nullable String imagePath) {
        this.imagePath = imagePath;
    }

    public void setModified(@Nullable String modified) {
        this.modified = modified;
    }
}
