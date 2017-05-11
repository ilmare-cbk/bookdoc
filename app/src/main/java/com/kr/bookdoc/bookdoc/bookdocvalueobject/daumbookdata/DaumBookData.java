package com.kr.bookdoc.bookdoc.bookdocvalueobject.daumbookdata;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.io.Serializable;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class DaumBookData implements BookdocJsonParseHandler, Serializable{
    @SerializedName("title")
    String title;
    @SerializedName("author")
    String author;
    @SerializedName("pub_nm")
    String pub_nm;
    @SerializedName("isbn")
    String isbn;
    @SerializedName("isbn13")
    String isbn13;
    @SerializedName("cover_l_url")
    String cover_l_url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public String getPub_nm() {
        return pub_nm;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public String getCover_l_url() {
        return cover_l_url;
    }

    @Override
    public void setData(JSONObject jobject) {
        title = jobject.optString("title");
        author = jobject.optString("author");
        pub_nm = jobject.optString("pub_nm");
        isbn = jobject.optString("isbn");
        isbn13 = jobject.optString("isbn13");
        cover_l_url = jobject.optString("cover_l_url");
    }
}
