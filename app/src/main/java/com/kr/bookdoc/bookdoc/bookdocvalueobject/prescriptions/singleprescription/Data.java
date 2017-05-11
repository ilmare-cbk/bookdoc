package com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Data implements BookdocJsonParseHandler {
    @SerializedName("id")
    private Integer id;
    @SerializedName("userId")
    private Integer userId;
    @SerializedName("userName")
    private String userName;
    @SerializedName("userImagePath")
    @Nullable
    private String userImagePath;
    @SerializedName("bookId")
    private Integer bookId;
    @SerializedName("bookIsbn")
    private String bookIsbn;
    @SerializedName("bookTitle")
    private String bookTitle;
    @SerializedName("bookAuthor")
    private String bookAuthor;
    @SerializedName("bookCategory")
    private String bookCategory;
    @SerializedName("bookPublisher")
    private String bookPublisher;
    @SerializedName("bookImagePath")
    @Nullable
    private String bookImagePath;
    @SerializedName("questionId")
    @Nullable
    private Integer questionId;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("imageType")
    @Nullable
    private Integer imageType;
    @SerializedName("imagePath")
    @Nullable
    private String imagePath;
    @SerializedName("liked")
    private Integer liked;
    @SerializedName("commented")
    private Integer commented;
    @SerializedName("created")
    private String created;
    @SerializedName("modified")
    @Nullable
    private String modified;
    @SerializedName("carded")
    private Integer carded;
    @SerializedName("cards")
    private ArrayList<Card> cards = new ArrayList<>();
    @SerializedName("tagged")
    private Integer tagged;
    @SerializedName("tags")
    private ArrayList<com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Tag> tags = new ArrayList<>();
    @SerializedName("userLikes")
    private int userLikes;
    @SerializedName("userKeeps")
    private int userKeeps;

    @Override
    public void setData(JSONObject jobject) {
        id = jobject.optInt("id");
        userId = jobject.optInt("userId");
        userName = jobject.optString("userName");
        userImagePath = jobject.optString("userImagePath");
        bookId = jobject.optInt("bookId");
        bookIsbn = jobject.optString("bookIsbn");
        bookTitle = jobject.optString("bookTitle");
        bookAuthor = jobject.optString("bookAuthor");
        bookCategory = jobject.optString("bookCategory");
        bookPublisher = jobject.optString("bookPublisher");
        bookImagePath = jobject.optString("bookImagePath");
        questionId = jobject.optInt("questionId");
        title = jobject.optString("title");
        description = jobject.optString("description");
        imageType = jobject.optInt("imageType");
        imagePath = jobject.optString("imagePath");
        liked = jobject.optInt("liked");
        commented = jobject.optInt("commented");
        created = jobject.optString("created");
        modified = jobject.optString("modified");
        carded = jobject.optInt("carded");
        JSONArray cardsJsonArray = jobject.optJSONArray("cards");
        for(int i = 0; i < cardsJsonArray.length(); i++){
            JSONObject cardJsonObject = cardsJsonArray.optJSONObject(i);
            Card mCard = new Card();
            mCard.setData(cardJsonObject);
            cards.add(mCard);
        }
        tagged = jobject.optInt("tagged");
        JSONArray tagsJsonArray = jobject.optJSONArray("tags");
        for(int i = 0; i < tagsJsonArray.length(); i++){
            JSONObject tagJsonObject = tagsJsonArray.optJSONObject(i);
            Tag mTag = new Tag();
            mTag.setData(tagJsonObject);
            tags.add(mTag);
        }
        userLikes = jobject.optInt("userLikes");
        userKeeps = jobject.optInt("userKeeps");
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public Integer getBookId() {
        return bookId;
    }


    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    public String getBookImagePath() {
        return bookImagePath;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getImageType() {
        if(imageType == null){
            imageType = 0;
        }
        return imageType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public Integer getCarded() {
        return carded;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public int getUserLikes() {
        return userLikes;
    }

    public int getUserKeeps() {
        return userKeeps;
    }
}
