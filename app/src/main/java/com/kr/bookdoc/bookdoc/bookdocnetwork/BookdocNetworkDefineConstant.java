package com.kr.bookdoc.bookdoc.bookdocnetwork;


import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;

public class BookdocNetworkDefineConstant {
    public static final String HOST_URL = BookdocApplication.getHostUrl();
    public static final String DAUM_API_KEY = BookdocApplication.getDaumApi();

    public static String SERVER_URL_REQUEST_PRESCRIPTION;
    public static String SERVER_URL_REQUEST_PRIMARY_PRESCRIPTION;
    public static String SERVER_URL_REQUEST_SINGLE_PRESCRIPTION;
    public static String SERVER_URL_POST_PRESCRIPTION;
    public static String SERVER_URL_PUT_PRESCRIPTION;
    public static String SERVER_URL_PUT_PRESCRIPTION_IMAGE;
    public static String SERVER_URL_REQUEST_PRESCRIPTION_BY_CATEGORY;
    public static String SERVER_URL_DELETE_PRESCRIPTION;

    public static String SERVER_URL_POST_PRESCRIPTION_COMMENTS;
    public static String SERVER_URL_GET_PRESCRIPTION_COMMNETS;
    public static String SERVER_URL_PUT_COMMENTS;
    public static String SERVER_URL_DELETE_COMMENTS;

    public static String SERVER_URL_REQUEST_PRESCRIPTION_SEARCH;
    public static String SERVER_URL_GET_TAGS;
    public static String SERVER_URL_REQUEST_DAUM_API;

    public static String SERVER_URL_REQUEST_QUESTION;
    public static String SERVER_URL_POST_QUESTION;
    public static String SERVER_URL_PUT_QUESTION;
    public static String SERVER_URL_DELETE_QUESTION;
    public static String SERVER_URL_POST_PRESCRIPTION_FOR_QUESTION;
    public static String SERVER_URL_GET_PRESCRIPTION_FOR_QUESTION;

    public static String SERVER_URL_REQUEST_SINGLELINES;
    public static String SERVER_URL_POST_SINGLELINES;
    public static String SERVER_URL_POST_SINGLELINES_DETAIL;
    public static String SERVER_URL_DELETE_SINGLELINES;
    public static String SERVER_URL_PUT_SINGLELINES;
    public static String SERVER_URL_PUT_SINGLELINES_IMAGE;

    public static String SERVER_URL_REQUEST_LIKE;
    public static String SERVER_URL_REQUEST_BOOKMARK;
    public static String SERVER_URL_REQUEST_WATCHER;

    public static String SERVER_URL_LOGIN;
    public static String SERVER_URL_SIGN_UP;
    public static String SERVER_URL_PUT_USER_INFO;
    public static String SERVER_URL_PUT_USER_IMAGE;
    public static String SERVER_URL_GET_USER_INFO;
    public static String SERVER_URL_PUT_USER_SETTING;

    public static String SERVER_URL_GET_MY_BOOKMARK;
    public static String SERVER_URL_GET_MY_REQUEST;
    public static String SERVER_URL_GET_MY_PRESCRIPTION;
    public static String SERVER_URL_GET_MY_BOOKMARK_PRESCRIPTION;

    public static String SERVER_URL_GET_USER_NOTIFICATION;

    static{
        SERVER_URL_REQUEST_PRESCRIPTION =
                HOST_URL + "/prescriptions?skip=%s&count=%s";
        SERVER_URL_REQUEST_PRIMARY_PRESCRIPTION =
                HOST_URL + "/prescriptions?top=1&bottom=3";
        SERVER_URL_REQUEST_SINGLE_PRESCRIPTION =
                HOST_URL + "/prescriptions/%s";
        SERVER_URL_POST_PRESCRIPTION =
                HOST_URL + "/prescriptions";
        SERVER_URL_REQUEST_PRESCRIPTION_BY_CATEGORY =
                HOST_URL + "/prescriptions?skip=%s&count=%s&%s";
        SERVER_URL_DELETE_PRESCRIPTION =
                HOST_URL + "/prescriptions/%s";
        SERVER_URL_PUT_PRESCRIPTION =
                HOST_URL + "/prescriptions/%s";
        SERVER_URL_PUT_PRESCRIPTION_IMAGE =
                HOST_URL + "/prescriptions/%s/image";

        SERVER_URL_POST_PRESCRIPTION_COMMENTS =
                HOST_URL + "/prescriptions/%s/comments";
        SERVER_URL_GET_PRESCRIPTION_COMMNETS =
                HOST_URL + "/prescriptions/%s/comments?skip=%s&count=%s";
        SERVER_URL_PUT_COMMENTS =
                HOST_URL + "/comments/%s";
        SERVER_URL_DELETE_COMMENTS =
                HOST_URL + "/comments/%s";

        SERVER_URL_REQUEST_PRESCRIPTION_SEARCH =
                HOST_URL + "/prescriptions?skip=%s&count=%s&keyword=%s";
        SERVER_URL_GET_TAGS =
                HOST_URL + "/tags/search?letter=%s";
        SERVER_URL_REQUEST_DAUM_API =
                "https://apis.daum.net/search/book?apikey="+DAUM_API_KEY+"&q=%s&output=json";

        SERVER_URL_REQUEST_QUESTION =
                HOST_URL + "/questions?skip=%s&count=%s";
        SERVER_URL_POST_QUESTION =
                HOST_URL + "/questions";
        SERVER_URL_PUT_QUESTION =
                HOST_URL + "/questions/%s";
        SERVER_URL_DELETE_QUESTION =
                HOST_URL + "/questions/%s";
        SERVER_URL_POST_PRESCRIPTION_FOR_QUESTION =
                HOST_URL + "/questions/%s/prescriptions";
        SERVER_URL_GET_PRESCRIPTION_FOR_QUESTION =
                HOST_URL + "/questions/%s/prescriptions?skip=%s&count=%s";

        SERVER_URL_REQUEST_SINGLELINES =
                HOST_URL + "/singlelines?skip=%s&count=%s";
        SERVER_URL_POST_SINGLELINES =
                HOST_URL + "/singlelines";
        SERVER_URL_POST_SINGLELINES_DETAIL =
                HOST_URL + "/singlelines/%s";
        SERVER_URL_DELETE_SINGLELINES =
                HOST_URL + "/singlelines/%s";
        SERVER_URL_PUT_SINGLELINES =
                HOST_URL + "/singlelines/%s";
        SERVER_URL_PUT_SINGLELINES_IMAGE =
                HOST_URL + "/singlelines/%s/image";

        SERVER_URL_REQUEST_LIKE =
                HOST_URL +"/prescriptions/%s/likers";
        SERVER_URL_REQUEST_BOOKMARK =
                HOST_URL + "/books/%s/keepers";
        SERVER_URL_REQUEST_WATCHER =
                HOST_URL + "/questions/%s/watchers";

        SERVER_URL_LOGIN =
                HOST_URL + "/token";
        SERVER_URL_SIGN_UP =
                HOST_URL + "/users";
        SERVER_URL_PUT_USER_INFO =
                HOST_URL + "/users";
        SERVER_URL_PUT_USER_IMAGE =
                HOST_URL + "/users/image";
        SERVER_URL_GET_USER_INFO =
                HOST_URL + "/users";
        SERVER_URL_PUT_USER_SETTING =
                HOST_URL + "/user/setting";

        SERVER_URL_GET_MY_BOOKMARK =
                HOST_URL + "/keeper/books?skip=%s&count=%s";
        SERVER_URL_GET_MY_REQUEST =
                HOST_URL + "/user/questions?skip=%s&count=%s";
        SERVER_URL_GET_MY_PRESCRIPTION =
                HOST_URL + "/user/prescriptions?skip=%s&count=%s";
        SERVER_URL_GET_MY_BOOKMARK_PRESCRIPTION =
                HOST_URL + "/books/%s/prescriptions?skip=%s&count=%s";

        SERVER_URL_GET_USER_NOTIFICATION =
                HOST_URL + "/user/notifications?skip=%s&count=%s";
    }
}