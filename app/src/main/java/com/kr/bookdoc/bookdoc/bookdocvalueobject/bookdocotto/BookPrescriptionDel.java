package com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto;


public class BookPrescriptionDel {
    private int bookPrescriptionDelId;
    private int bookPrescriptionDelPosition;

    public BookPrescriptionDel(int bookPrescriptionDelId, int bookPrescriptionDelPosition) {
        this.bookPrescriptionDelId = bookPrescriptionDelId;
        this.bookPrescriptionDelPosition = bookPrescriptionDelPosition;
    }

    public int getBookPrescriptionDelId() {
        return bookPrescriptionDelId;
    }


    public int getBookPrescriptionDelPosition() {
        return bookPrescriptionDelPosition;
    }

}
