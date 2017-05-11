package com.kr.bookdoc.bookdoc.bookdocvalueobject.common;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Skipping implements BookdocJsonParseHandler {

    @SerializedName("prev")
    @Nullable
    private String prev;
    @SerializedName("next")
    @Nullable
    private String next;

    @Override
    public void setData(JSONObject jobject) {
        prev = jobject.optString("prev");
        next = jobject.optString("next");
    }
}
