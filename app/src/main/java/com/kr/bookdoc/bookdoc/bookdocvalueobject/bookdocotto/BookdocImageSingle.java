package com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto;

import android.net.Uri;


public class BookdocImageSingle {
    private Uri imagePath;
    private boolean checkImageFrom;

    public BookdocImageSingle(Uri imagePath, boolean checkImageFrom) {
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
