package com.kr.bookdoc.bookdoc.bookdocutils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class BookdocDateFormat {
    private class TIME_MAXIMUM{
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }

    public static String setDateFormat(String inputFormat, String outputFormat, String date){
        Log.d("date","Date: "+date);
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
        SimpleDateFormat output = new SimpleDateFormat(outputFormat);
        try {
            return output.format(sdf.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getDate(String inputFormat, String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat(inputFormat, Locale.getDefault());
        Date convertedDate;
        Calendar calendar = Calendar.getInstance();
        try {
            convertedDate = dateFormat.parse(date);
            calendar.setTime(convertedDate);
            calendar.add(Calendar.HOUR, 9);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTime();
    }

    public static String formatTimeString(String inputFormat, String date) {

        long curTime = System.currentTimeMillis();
        long regTime = getDate(inputFormat, date).getTime();
        long diffTime = (curTime - regTime) / 1000;

        String msg;
        if (diffTime < TIME_MAXIMUM.SEC) {
            msg = "방금 전";
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            msg = diffTime + "분 전";
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + "시간 전";
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            msg = (diffTime) + "일 전";
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + "달 전";
        } else {
            msg = (diffTime) + "년 전";
        }

        return msg;
    }
}
