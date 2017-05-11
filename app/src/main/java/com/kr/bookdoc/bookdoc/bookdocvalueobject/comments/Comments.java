package com.kr.bookdoc.bookdoc.bookdocvalueobject.comments;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.common.Skipping;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Comments implements BookdocJsonParseHandler {
    @SerializedName("message")
    private String message;
    @SerializedName("totaled")
    private Integer totaled;
    @SerializedName("skipping")
    private Skipping skipping;
    @SerializedName("counted")
    private Integer counted;
    @SerializedName("data")
    private ArrayList<Data> data = new ArrayList<>();

    @Override
    public void setData(JSONObject jobject) {
        message = jobject.optString("message");
        totaled = jobject.optInt("totaled");
        JSONObject skippingJsonObject = jobject.optJSONObject("skipping");
        skipping = new Skipping();
        skipping.setData(skippingJsonObject);
        counted = jobject.optInt("counted");
        JSONArray dataJsonArray = jobject.optJSONArray("data");
        for(int i = 0; i < dataJsonArray.length(); i++) {
            Data mData = new Data();
            JSONObject dataJsonObject = dataJsonArray.optJSONObject(i);
            mData.setData(dataJsonObject);
            data.add(mData);
        }
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public ArrayList<Data> getData() {
        return data;
    }

    public void setData(ArrayList<Data> data) {
        this.data = data;
    }
}
