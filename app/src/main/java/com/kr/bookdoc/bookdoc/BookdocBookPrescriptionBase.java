package com.kr.bookdoc.bookdoc;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;


public abstract class BookdocBookPrescriptionBase extends AppCompatActivity {
    public ViewPager mPager;
    public int mPagerState;

    public abstract void removePagerItem();
}
