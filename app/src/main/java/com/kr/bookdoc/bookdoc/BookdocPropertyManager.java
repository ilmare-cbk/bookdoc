package com.kr.bookdoc.bookdoc;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;


public class BookdocPropertyManager {
    private static BookdocPropertyManager instance;

    public static BookdocPropertyManager getInstance() {
        if (instance == null) {
            instance = new BookdocPropertyManager();
        }
        return instance;
    }

    SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;

    private BookdocPropertyManager() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(BookdocApplication.getBookdocContext());
        mEditor = mPrefs.edit();
    }

    private static final String FIELD_USER_ID = "userid";
    private static final String FIELD_PASSWORD = "password";
    private static final String FIELD_FACEBOOK_USER_ID = "facebookUserId";
    private static final String FIELD_ACCESSTOKEN = "accesstoken";
    private static final String FIELD_FACEBOOK_USER_IMGAGE = "facebookUserImage";
    private static final String FIELD_USER_NAME = "username";
    private static final String FIELD_USER_EMAIL = "useremail";
    private static final String FCM_TOKEN_KEY = "fcmTokenKey";
    private static final String UUID_KEY = "uuidKey";
    private static final String FIELD_FACEBOOK_USER_NAME = "facebookUserName";
    private static final String FIELD_USER_DESCRIPTION = "userDescription";
    private static final String FIELD_USER_IMAGE = "userImage";
    private static final String FIELD_PUSH_CHECK = "pushCheck";
    private static final String FIELD_PRESCRIPTION_COUNT = "prescriptionCount";
    private static final String FIELD_QUESTION_COUNT = "questionCount";
    private static final String FIELD_FACEBOOK_ACCESSTOKEN = "facebookAccessToken";
    private static final String CHECK_COMPLETE_USER_INFO = "checkUserInfo";
    private static final String CHECK_FACEBOOK_USER = "checkFacebookUser";

    public void setCheckFacebookUser(boolean checkFacebookUser) {
        mEditor.putBoolean(CHECK_FACEBOOK_USER, checkFacebookUser);
        mEditor.commit();
    }

    public boolean isCheckFacebookUser() {
        return mPrefs.getBoolean(CHECK_FACEBOOK_USER, false);
    }

    public void setCheckCompleteUserInfo(boolean checkCompleteUserInfo) {
        mEditor.putBoolean(CHECK_COMPLETE_USER_INFO, checkCompleteUserInfo);
        mEditor.commit();
    }

    public void setFieldFacebookAccesstoken(String facebookAccesstoken) {
        mEditor.putString(FIELD_FACEBOOK_ACCESSTOKEN, facebookAccesstoken);
        mEditor.commit();
    }

    public String getFieldFacebookAccesstoken() {
        return mPrefs.getString(FIELD_FACEBOOK_ACCESSTOKEN, "");
    }

    public void setFieldQuestionCount(int questionCount) {
        mEditor.putInt(FIELD_QUESTION_COUNT, questionCount);
        mEditor.commit();
    }

    public int getFieldQuestionCount() {
        return mPrefs.getInt(FIELD_QUESTION_COUNT, 0);
    }

    public void setFieldPrescriptionCount(int prescriptionCount) {
        mEditor.putInt(FIELD_PRESCRIPTION_COUNT, prescriptionCount);
        mEditor.commit();
    }

    public int getFieldPrescriptionCount() {
        return mPrefs.getInt(FIELD_PRESCRIPTION_COUNT, 0);
    }

    public void setFieldPushCheck(int pushCheck) {
        mEditor.putInt(FIELD_PUSH_CHECK, pushCheck);
        mEditor.commit();
    }

    public int getFieldPushCheck() {
        return mPrefs.getInt(FIELD_PUSH_CHECK, 1);
    }

    public void setFieldUserImage(String userImage) {
        mEditor.putString(FIELD_USER_IMAGE, userImage);
        mEditor.commit();
    }

    public String getFieldUserImage() {
        return mPrefs.getString(FIELD_USER_IMAGE, "");
    }

    public void setFieldUserDescription(String userDescription) {
        mEditor.putString(FIELD_USER_DESCRIPTION, userDescription);
        mEditor.commit();
    }

    public String getFieldUserDescription() {
        return mPrefs.getString(FIELD_USER_DESCRIPTION, "");
    }


    public void setFieldFacebookUserName(String facebookUserName) {
        mEditor.putString(FIELD_FACEBOOK_USER_NAME, facebookUserName);
        mEditor.commit();
    }

    public void setFcmTokenKey(String fcmTokenKey) {
        mEditor.putString(FCM_TOKEN_KEY, fcmTokenKey);
        mEditor.commit();
    }

    public String getFcmTokenKey() {
        return mPrefs.getString(FCM_TOKEN_KEY, "");
    }

    public void setUserId(int userid) {
        mEditor.putInt(FIELD_USER_ID, userid);
        mEditor.commit();
    }

    public int getUserId() {
        return mPrefs.getInt(FIELD_USER_ID, 0);
    }

    public void setPassword(String password) {
        mEditor.putString(FIELD_PASSWORD, password);
        mEditor.commit();
    }

    public String getPassword() {
        return mPrefs.getString(FIELD_PASSWORD, "");
    }

    public void setFaceBookUserId(String faceBookUserId) {
        mEditor.putString(FIELD_FACEBOOK_USER_ID, faceBookUserId);
        mEditor.commit();
    }


    public void setAccessToken(String accessToken) {
        mEditor.putString(FIELD_ACCESSTOKEN, accessToken);
        mEditor.commit();
    }

    public String getAccessToken() {
        return mPrefs.getString(FIELD_ACCESSTOKEN, "");
    }

    public void setFacebookUserImage(String facebookUserImage) {
        mEditor.putString(FIELD_FACEBOOK_USER_IMGAGE, facebookUserImage);
        mEditor.commit();
    }


    public void setUserName(String userName) {
        mEditor.putString(FIELD_USER_NAME, userName);
        mEditor.commit();
    }

    public String getUserName() {
        return mPrefs.getString(FIELD_USER_NAME, "");
    }

    public void setUserEmail(String userEmail) {
        mEditor.putString(FIELD_USER_EMAIL, userEmail);
        mEditor.commit();
    }

    public String getUserEmail() {
        return mPrefs.getString(FIELD_USER_EMAIL, "");
    }

}
