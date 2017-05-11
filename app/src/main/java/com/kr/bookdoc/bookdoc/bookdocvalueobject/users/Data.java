package com.kr.bookdoc.bookdoc.bookdocvalueobject.users;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Data implements BookdocJsonParseHandler {
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("description")
    String description;
    @SerializedName("imagePath")
    String imagePath;
    @SerializedName("prescribed")
    int prescribed;
    @SerializedName("questioned")
    int questioned;

    @Override
    public void setData(JSONObject jobject) {
        id = jobject.optInt("id");
        name = jobject.optString("name");
        description = jobject.optString("description");
        imagePath = jobject.optString("imagePath");
        prescribed = jobject.optInt("prescribed");
        questioned = jobject.optInt("questioned");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public int getPrescribed() {
        return prescribed;
    }

    public int getQuestioned() {
        return questioned;
    }
}
