package com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class PrimaryPrescription implements BookdocJsonParseHandler {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("counted")
    @Expose
    private Integer counted;
    @SerializedName("data")
    @Expose
    private ArrayList<Data> data = new ArrayList<>();

    @Override
    public void setData(JSONObject jobject) {
        message = jobject.optString("message");
        counted = jobject.optInt("counted");
        JSONArray dataJsonArray = jobject.optJSONArray("data");
        for (int i = 0; i < dataJsonArray.length(); i++) {
            JSONObject js = dataJsonArray.optJSONObject(i);
            Data mData = new Data();
            mData.setData(js);
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
