package com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto;

import android.net.Uri;


public class BookdocImage {
    private Uri imagePath;
    private boolean checkImageFrom;

    public BookdocImage(Uri imagePath, boolean checkImageFrom) {
        this.imagePath = imagePath;
        this.checkImageFrom = checkImageFrom;
    }

    public Uri getImagePath() {
        return imagePath;
    }

    public void setImagePath(Uri imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isCheckImageFrom() {
        return checkImageFrom;
    }
}
