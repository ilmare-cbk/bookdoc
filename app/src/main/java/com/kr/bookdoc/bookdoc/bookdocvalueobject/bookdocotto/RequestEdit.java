package com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question.Data;


public class RequestEdit {
    private Data data;
    private int position;

    public RequestEdit(Data data, int position) {
        this.data = data;
        this.position = position;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
