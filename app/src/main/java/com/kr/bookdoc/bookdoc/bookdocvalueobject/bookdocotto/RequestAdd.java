package com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question.Data;


public class RequestAdd {
    private Data data;

    public RequestAdd(Data data) {
        this.data = data;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
