package com.kr.bookdoc.bookdoc.bookdocvalueobject.bookmark;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.common.Skipping;


public class Bookmark implements BookdocJsonParseHandler {
    @SerializedName("message")
    public String message;
    @SerializedName("totaled")
    public Integer totaled;
    @SerializedName("skipping")
    public Skipping skipping;
    @SerializedName("counted")
    public Integer counted;
    @SerializedName("data")
    public ArrayList<Data> datas = new ArrayList<>();
    
    @Override
    public void setData(JSONObject jobject) {
        message = jobject.optString("message");
        totaled = jobject.optInt("totaled");
        skipping = new Skipping();
        skipping.setData(jobject.optJSONObject("skipping"));
        counted = jobject.optInt("counted");
        JSONArray dataJsonArray = jobject.optJSONArray("data");
        for(int i=0; i < dataJsonArray.length(); i++){
            Data mData = new Data();
            JSONObject dataJsonObject = dataJsonArray.optJSONObject(i);
            mData.setData(dataJsonObject);
            datas.add(mData);
        }
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<Data> getDatas() {
        return datas;
    }
}
