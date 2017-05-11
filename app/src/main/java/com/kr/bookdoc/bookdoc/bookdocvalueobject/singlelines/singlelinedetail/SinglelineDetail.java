package com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlelinedetail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.common.Skipping;


public class SinglelineDetail implements BookdocJsonParseHandler
{

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("totaled")
    @Expose
    private Integer totaled;

    @SerializedName("skipping")
    @Expose
    private Skipping skipping;

    @SerializedName("counted")
    @Expose
    private Integer counted;


    @SerializedName("data")
    @Expose
    private ArrayList<Data> data = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    @Override
    public void setData(JSONObject jobject) {
        message = jobject.optString("message");
        totaled = jobject.optInt("totaled");
        JSONObject skippingObject = jobject.optJSONObject("skipping");
        skipping = new Skipping();
        skipping.setData(skippingObject);
        JSONArray dataJsonArray = jobject.optJSONArray("data");
        for (int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject js = dataJsonArray.optJSONObject(i);
            Data mData = new Data();
            mData.setData(js);
            data.add(mData);
        }
        counted = jobject.optInt("counted");


    }

    public ArrayList<Data> getData() {
        return data;
    }



}
