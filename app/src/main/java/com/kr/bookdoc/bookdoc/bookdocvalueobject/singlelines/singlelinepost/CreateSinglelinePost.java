package com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlelinepost;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;


public class CreateSinglelinePost {

    @SerializedName("id")
    int id;
    @SerializedName("userId")
    int userId;
    @SerializedName("bookIsbn")
    String bookIsbn;
    @SerializedName("description")
    String description;
    @SerializedName("font")
    private Integer font;
    @SerializedName("alignment")
    private Integer alignment;

    @SerializedName("imageType")
    @Nullable
    Integer imageType;

    @SerializedName("image")
    @Nullable
    String image;

    private volatile static CreateSinglelinePost uniqueInstance;

    public static CreateSinglelinePost getInstance() {
        if (uniqueInstance == null) {
            synchronized (CreateSinglelinePost.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new CreateSinglelinePost();
                }
            }
        }
        return uniqueInstance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFont() {
        return font;
    }
    public void setFont(Integer font) {
        this.font = font;
    }

    public Integer getAlignment() {
        return alignment;
    }
    public void setAlignment(Integer alignment) {
        this.alignment = alignment;
    }
    public int getImageType() {
        if(imageType == null){
            imageType = 0;
        }
        return imageType;
    }

    public void setImageType(Integer imageType) {
        if(imageType == null){
            imageType = 0;
        }
        this.imageType = imageType;
    }
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public void initData(){
        bookIsbn = null;
        description = null;
        font = null;
        alignment = null;
        image = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
