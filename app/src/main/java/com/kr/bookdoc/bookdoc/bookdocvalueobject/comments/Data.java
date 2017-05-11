package com.kr.bookdoc.bookdoc.bookdocvalueobject.comments;

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
    private String userImagePath;
    @SerializedName("description")
    private String description;
    @SerializedName("created")
    private String created;
    @SerializedName("modified")
    private String modified;
    
    
    @Override
    public void setData(JSONObject jobject) {
        id = jobject.optInt("id");
        userId = jobject.optInt("userId");
        userName = jobject.optString("userName");
        userImagePath = jobject.optString("userImagePath");
        description = jobject.optString("description");
        created = jobject.optString("created");
        modified = jobject.optString("modified");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }
}
