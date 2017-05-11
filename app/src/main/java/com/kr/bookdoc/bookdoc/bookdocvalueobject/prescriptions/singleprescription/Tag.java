package com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Tag implements BookdocJsonParseHandler, Serializable {
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("description")
    @Expose
    public String description;

    @Override
    public void setData(JSONObject jobject) {
        id = jobject.optInt("id");
        description = jobject.optString("description");
    }

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
