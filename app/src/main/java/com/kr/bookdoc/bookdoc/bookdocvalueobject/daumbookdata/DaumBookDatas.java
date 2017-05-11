package com.kr.bookdoc.bookdoc.bookdocvalueobject.daumbookdata;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class DaumBookDatas implements BookdocJsonParseHandler {
    ArrayList<DaumBookData> daumBookDatas = new ArrayList<>();

    public ArrayList<DaumBookData> getDaumBookDatas() {
        return daumBookDatas;
    }

    @Override
    public void setData(JSONObject jobject) {
        JSONObject jsonObject = jobject.optJSONObject("channel");
        JSONArray jsonArray = jsonObject.optJSONArray("item");
        for(int i=0; i< jsonArray.length(); i++){
            DaumBookData daumBookData = new DaumBookData();
            JSONObject daumBookDataJsonObject = jsonArray.optJSONObject(i);
            daumBookData.setData(daumBookDataJsonObject);
            daumBookDatas.add(daumBookData);
        }
    }
}
