package com.kr.bookdoc.bookdoc.bookdocutils;

import java.util.ArrayList;
import java.util.HashMap;

import com.kr.bookdoc.bookdoc.R;


public class BookdocImageList {
    private static HashMap<Integer, Integer> bookdocImageResources;
    private static ArrayList<Integer> bookdocImageArrayList;
    static {
        bookdocImageResources = new HashMap<>();
        bookdocImageResources.put(1, R.drawable.bg1);
        bookdocImageResources.put(2, R.drawable.bg2);
        bookdocImageResources.put(3, R.drawable.bg3);
        bookdocImageResources.put(4, R.drawable.bg4);
        bookdocImageResources.put(5, R.drawable.bg5);
        bookdocImageResources.put(6, R.drawable.bg6);
        bookdocImageResources.put(7, R.drawable.bg7);
        bookdocImageResources.put(8, R.drawable.bg8);
        bookdocImageResources.put(9, R.drawable.bg9);
        bookdocImageResources.put(10, R.drawable.bg10);
        bookdocImageResources.put(11, R.drawable.bg11);
        bookdocImageResources.put(12, R.drawable.bg12);
        bookdocImageResources.put(13, R.drawable.bg13);
        bookdocImageResources.put(14, R.drawable.bg14);
        bookdocImageResources.put(15, R.drawable.bg15);
        bookdocImageResources.put(16, R.drawable.bg16);
        bookdocImageResources.put(17, R.drawable.bg17);
        bookdocImageResources.put(18, R.drawable.bg18);
        bookdocImageResources.put(19, R.drawable.bg19);
        bookdocImageResources.put(20, R.drawable.bg20);
        bookdocImageResources.put(21, R.drawable.bg21);
        bookdocImageResources.put(22, R.drawable.bg22);
        bookdocImageResources.put(23, R.drawable.bg23);
    }
    static {
        bookdocImageArrayList = new ArrayList<>();
        bookdocImageArrayList.add(0, R.drawable.bg1);
        bookdocImageArrayList.add(1, R.drawable.bg2);
        bookdocImageArrayList.add(2, R.drawable.bg3);
        bookdocImageArrayList.add(3, R.drawable.bg4);
        bookdocImageArrayList.add(4, R.drawable.bg5);
        bookdocImageArrayList.add(5, R.drawable.bg6);
        bookdocImageArrayList.add(6, R.drawable.bg7);
        bookdocImageArrayList.add(7, R.drawable.bg8);
        bookdocImageArrayList.add(8, R.drawable.bg9);
        bookdocImageArrayList.add(9, R.drawable.bg10);
        bookdocImageArrayList.add(10, R.drawable.bg11);
        bookdocImageArrayList.add(11, R.drawable.bg12);
        bookdocImageArrayList.add(12, R.drawable.bg13);
        bookdocImageArrayList.add(13, R.drawable.bg14);
        bookdocImageArrayList.add(14, R.drawable.bg15);
        bookdocImageArrayList.add(15, R.drawable.bg16);
        bookdocImageArrayList.add(16, R.drawable.bg17);
        bookdocImageArrayList.add(17, R.drawable.bg18);
        bookdocImageArrayList.add(18, R.drawable.bg19);
        bookdocImageArrayList.add(19, R.drawable.bg20);
        bookdocImageArrayList.add(20, R.drawable.bg21);
        bookdocImageArrayList.add(21, R.drawable.bg22);
        bookdocImageArrayList.add(22, R.drawable.bg23);
    }

    public static Integer getBookdocImage(int key) {
        return bookdocImageResources.get(key);
    }

    public static ArrayList<Integer> getBookdocImageArrayList() {
        return bookdocImageArrayList;
    }
}
