package com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question;

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
    @SerializedName("description")
    private String description;
    @SerializedName("watched")
    private Integer watched;
    @SerializedName("prescribed")
    private Integer prescribed;
    @SerializedName("created")
    private String created;
    @SerializedName("modified")
    private String modified;
    @SerializedName("userWatches")
    private int userWatches;

    @Override
    public void setData(JSONObject jobject) {
        id = jobject.optInt("id");
        userId = jobject.optInt("userId");
        userName = jobject.optString("userName");
        userImagePath = jobject.optString("userImagePath");
        description = jobject.optString("description");
        watched = jobject.optInt("watched");
        prescribed= jobject.optInt("prescribed");
        created = jobject.optString("created");
        modified = jobject.optString("modified");
        userWatches = jobject.optInt("userWatches");
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

    public String getDescription() {
        return description;
    }

    public Integer getWatched() {
        return watched;
    }

    public Integer getPrescribed() {
        return prescribed;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public int getUserWatches() {
        return userWatches;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWatched(Integer watched) {
        this.watched = watched;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public void setUserWatches(int userWatches) {
        this.userWatches = userWatches;
    }
}
