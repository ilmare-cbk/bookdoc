package com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;



public class SinglePrescription implements BookdocJsonParseHandler {
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public Data data;

    @Override
    public void setData(JSONObject jobject) {
        message = jobject.optString("message");
        JSONObject dataJSonObject= jobject.optJSONObject("data");
        data = new Data();
        data.setData(dataJSonObject);
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }
}
