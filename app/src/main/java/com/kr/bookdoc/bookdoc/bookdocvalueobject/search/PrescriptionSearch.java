package com.kr.bookdoc.bookdoc.bookdocvalueobject.search;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class PrescriptionSearch implements BookdocJsonParseHandler{
    @SerializedName("message")
    String message;
    @SerializedName("counted")
    int counted;
    @SerializedName("data")
    ArrayList<Data> dataArrayList = new ArrayList<>();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Data> getData() {
        return dataArrayList;
    }

    public void setData(ArrayList<Data> dataArrayList) {
        this.dataArrayList = dataArrayList;
    }


    @Override
    public void setData(JSONObject jobject) {
        message = jobject.optString("message");
        counted = jobject.optInt("counted");
        JSONArray dataJsonArray = jobject.optJSONArray("data");
        for(int i=0; i< dataJsonArray.length(); i++){
            Data data = new Data();
            JSONObject dataJsonObject = dataJsonArray.optJSONObject(i);
            data.setData(dataJsonObject);
            dataArrayList.add(data);
        }
    }
}
