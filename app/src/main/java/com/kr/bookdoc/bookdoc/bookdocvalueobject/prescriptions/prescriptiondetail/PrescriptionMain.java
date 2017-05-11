package com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptiondetail;

import java.io.Serializable;
import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Tag;


public class PrescriptionMain implements Serializable {
    int position;
    int prescriptionId;
    int userId;
    Integer imageType;
    String imagePath;
    String title;
    String userImagePath;
    String userName;
    ArrayList<Tag> tags = new ArrayList<>();
    String bookImagePath;
    String bookTitle;
    String bookAuthor;
    String bookPublisher;
    String created;
    boolean userLikes;
    boolean userKeeps;

    public PrescriptionMain() {
        this.imagePath = null;
        this.title = null;
        this.userImagePath = null;
        this.userName = null;
        this.tags = null;
        this.bookImagePath = null;
        this.bookTitle = null;
        this.bookAuthor = null;
        this.bookPublisher = null;
        this.created = null;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public void setPrescriptionId(int prescriptionId) {
        this.prescriptionId = prescriptionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserImagePath() {
        return userImagePath;
    }

    public void setUserImagePath(String userImagePath) {
        this.userImagePath = userImagePath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public String getBookImagePath() {
        return bookImagePath;
    }

    public void setBookImagePath(String bookImagePath) {
        this.bookImagePath = bookImagePath;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public String getBookPublisher() {
        return bookPublisher;
    }

    public void setBookPublisher(String bookPublisher) {
        this.bookPublisher = bookPublisher;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getImageType() {
        if(imageType == null){
            imageType = 0;
        }
        return imageType;
    }

    public void setImageType(Integer imageType) {
        if(imageType == null){
            this.imageType = 0;
        }
        this.imageType = imageType;
    }
}
