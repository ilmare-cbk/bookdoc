package com.kr.bookdoc.bookdoc.bookdocvalueobject.notifications;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocvalueobject.BookdocJsonParseHandler;


public class Data implements BookdocJsonParseHandler {
    @SerializedName("id")
    int id;
    @SerializedName("prescriptionId")
    int prescriptionId;
    @SerializedName("prescriberName")
    String prescriberName;
    @SerializedName("prescriberImagePath")
    String prescriberImagePath;
    @SerializedName("questionId")
    int questionId;
    @SerializedName("isQuestioner")
    int isQuestioner;
    @SerializedName("created")
    String created;
    @Override

    public void setData(JSONObject jobject) {
        id = jobject.optInt("id");
        prescriptionId = jobject.optInt("prescriptionId");
        prescriberName = jobject.optString("prescriberName");
        prescriberImagePath = jobject.optString("prescriberImagePath");
        questionId = jobject.optInt("questionId");
        isQuestioner = jobject.optInt("isQuestioner");
        created = jobject.optString("created");
    }

    public int getId() {
        return id;
    }

    public int getPrescriptionId() {
        return prescriptionId;
    }

    public int getIsQuestioner() {
        return isQuestioner;
    }

    public String getCreated() {
        return created;
    }

    public String getPrescriberName() {
        return prescriberName;
    }

    public String getPrescriberImagePath() {
        return prescriberImagePath;
    }
}
