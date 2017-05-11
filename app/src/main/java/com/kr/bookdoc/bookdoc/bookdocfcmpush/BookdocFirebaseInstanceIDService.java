package com.kr.bookdoc.bookdoc.bookdocfcmpush;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import com.kr.bookdoc.bookdoc.BookdocPropertyManager;


public class BookdocFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FCMInstanceIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token) {
        BookdocPropertyManager.getInstance().setFcmTokenKey(token);
    }


}
