package com.kr.bookdoc.bookdoc.bookdocutils;

import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.kr.bookdoc.bookdoc.R;


public class BookdocApplication extends Application {
    private static Context mContext;
    private static String daumApi;
    private static String hostUrl;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        daumApi = getResources().getString(R.string.daum_api);
        hostUrl = getResources().getString(R.string.host_url);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
    public static Context getBookdocContext(){
        return mContext;
    }

    public static String getDaumApi(){
        return daumApi;
    }

    public static String getHostUrl(){
        return hostUrl;
    }



}
