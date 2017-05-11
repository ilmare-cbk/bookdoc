package com.kr.bookdoc.bookdoc.bookdocvalueobject.notifications;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.common.Skipping;


public class Notifications implements BookdocJsonParseHandler {
    @SerializedName("message")
    String message;
    @SerializedName("totaled")
    int totaled;
    @SerializedName("skipping")
    Skipping skipping;
    @SerializedName("counted")
    int counted;
    @SerializedName("data")
    ArrayList<Data> data = new ArrayList<>();

    @Override
    public void setData(JSONObject jobject) {
        message = jobject.optString("message");
        totaled = jobject.optInt("totaled");
        skipping = new Skipping();
        JSONObject skippingJsonObject = jobject.optJSONObject("skipping");
        skipping.setData(skippingJsonObject);
        counted = jobject.optInt("counted");
        JSONArray dataJsonArray = jobject.optJSONArray("data");
        for(int i =0; i< dataJsonArray.length(); i++){
            JSONObject dataJsonObject = dataJsonArray.optJSONObject(i);
            Data mData = new Data();
            mData.setData(dataJsonObject);
            data.add(mData);
        }
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Data> getData() {
        return data;
    }
}
