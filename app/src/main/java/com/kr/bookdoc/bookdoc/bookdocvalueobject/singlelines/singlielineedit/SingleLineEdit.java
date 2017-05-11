package com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlielineedit;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singleline.Data;


public class SingleLineEdit implements BookdocJsonParseHandler {
    @SerializedName("message")
    String message;
    @SerializedName("data")
    com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singleline.Data data;
    int position;

    @Override
    public void setData(JSONObject jobject) {
        message = jobject.optString("message");
        data = new com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singleline.Data();
        JSONObject dataJsonObject = jobject.optJSONObject("data");
        data.setData(dataJsonObject);
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singleline.Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
