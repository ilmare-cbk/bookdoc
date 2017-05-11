package com.kr.bookdoc.bookdoc.bookdocvalueobject.users;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class AnotherUser implements BookdocJsonParseHandler {
    @SerializedName("message")
    String message;
    @SerializedName("data")
    Data data;


    @Override
    public void setData(JSONObject jobject) {
        message = jobject.optString("message");
        JSONObject jsonObject = jobject.optJSONObject("data");
        data = new Data();
        data.setData(jsonObject);
    }

    public Data getData() {
        return data;
    }
}
