package com.kr.bookdoc.bookdoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.bookdocutils.OnSetBookInfoListener;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.BookPrescriptionAdd;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.daumbookdata.DaumBookData;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.PrescriptionPost;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionAddCardFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateDefaultFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateFirstQuestionFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateMainFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateSecondQuestionFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateTagFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateThirdQuestionFragment;

public class BookdocCreatePrescriptionActivity extends BookdocBookPrescriptionBase implements OnPageChangeListener {
    private OnSetBookInfoListener onSetBookInfoListener;
    private final int SEARCH_BOOK_REQUEST_CODE = 0;
    Toolbar bookdocCreatePrescriptionToolbar;
    public ViewPager bookdocCreatePrescriptionViewPager;
    public BookPrescriptionCreatePagerAdapter bookPrescriptionCreatePagerAdapter;
    PrescriptionPost prescriptionPost;
    boolean prescriptionForRequest;
    int questionId;
    int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_create_prescription);
        prescriptionForRequest = getIntent().getBooleanExtra("prescriptionForRequest", false);
        questionId = getIntent().getIntExtra("questionId", 0);

        bookdocCreatePrescriptionToolbar = (Toolbar) findViewById(R.id.bookdoc_create_prescription_toolbar);
        setSupportActionBar(bookdocCreatePrescriptionToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);

        prescriptionPost = PrescriptionPost.getInstance();

        bookdocCreatePrescriptionViewPager = (ViewPager) findViewById(R.id.bookdoc_create_prescription_vp);
        mPager = bookdocCreatePrescriptionViewPager;
        if (bookdocCreatePrescriptionViewPager != null) {
            final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36,
                    getResources().getDisplayMetrics());
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13,
                    getResources().getDisplayMetrics());
            bookdocCreatePrescriptionViewPager.setClipToPadding(false);
            bookdocCreatePrescriptionViewPager.setPadding(padding, 0, padding, 0);
            bookdocCreatePrescriptionViewPager.setPageMargin(margin);
            bookdocCreatePrescriptionViewPager.setOffscreenPageLimit(11);
            bookdocCreatePrescriptionViewPager.addOnPageChangeListener(this);
            setBookdocCreatePrescriptionViewPager(bookdocCreatePrescriptionViewPager);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookdoc_create_prescription_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.post_prescription:
                setPrescriptionData();
                BusProvider.getInstance().post(new BookPrescriptionAdd(questionId));
                finish();
            default:
                return false;
        }
    }

    private void setBookdocCreatePrescriptionViewPager(ViewPager viewPager) {
        bookPrescriptionCreatePagerAdapter = new BookPrescriptionCreatePagerAdapter(getSupportFragmentManager());
        bookPrescriptionCreatePagerAdapter.appendBookPrescriptionCreateFragment(BookPrescriptionCreateMainFragment.newInstance());
        bookPrescriptionCreatePagerAdapter.appendBookPrescriptionCreateFragment(BookPrescriptionCreateFirstQuestionFragment.newInstance());
        bookPrescriptionCreatePagerAdapter.appendBookPrescriptionCreateFragment(BookPrescriptionCreateSecondQuestionFragment.newInstance());
        bookPrescriptionCreatePagerAdapter.appendBookPrescriptionCreateFragment(BookPrescriptionCreateThirdQuestionFragment.newInstance());
        bookPrescriptionCreatePagerAdapter.appendBookPrescriptionCreateFragment(BookPrescriptionAddCardFragment.newInstance());
        bookPrescriptionCreatePagerAdapter.appendBookPrescriptionCreateFragment(BookPrescriptionCreateTagFragment.newInstance());
        viewPager.setAdapter(bookPrescriptionCreatePagerAdapter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        currentIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mPagerState = state;
    }

    public class BookPrescriptionCreatePagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> bookPrescriptionCreateFragmentList = new ArrayList<>();

        public BookPrescriptionCreatePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return bookPrescriptionCreateFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return bookPrescriptionCreateFragmentList.size();
        }

        @Override
        public float getPageWidth(int position) {
            return 1f;
        }

        public void appendBookPrescriptionCreateFragment(Fragment fragment) {
            bookPrescriptionCreateFragmentList.add(fragment);
        }

        public void appendBookPrescriptionDefaultFragment(Fragment fragment) {
            if (bookPrescriptionCreateFragmentList.size() >= 11) {
                Toast.makeText(getApplicationContext(), "최대 카드 수를 초과했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                int lastIndex = bookPrescriptionCreateFragmentList.size() - 2;
                bookPrescriptionCreateFragmentList.add(lastIndex, fragment);
                notifyDataSetChanged();
            }
        }

        public void removeView() {
            int totalSize = bookPrescriptionCreateFragmentList.size();
            if (currentIndex < totalSize - 2) {
                bookPrescriptionCreateFragmentList.remove(currentIndex);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void removePagerItem() {
        bookPrescriptionCreatePagerAdapter.removeView();
    }

    public void setPrescriptionData() {
        int fragmentListSize = bookPrescriptionCreatePagerAdapter.getCount();
        int lastIndex = fragmentListSize - 1;
        for (int i = 0; i < fragmentListSize; i++) {
            switch (i) {
                case 0:
                    ((BookPrescriptionCreateMainFragment) bookPrescriptionCreatePagerAdapter.getItem(i)).setPrescriptionMainData();
                    break;
                case 1:
                    ((BookPrescriptionCreateFirstQuestionFragment) bookPrescriptionCreatePagerAdapter.getItem(i)).setPrescriptionMainData();
                    break;
                case 2:
                    ((BookPrescriptionCreateSecondQuestionFragment) bookPrescriptionCreatePagerAdapter.getItem(i)).setPrescriptionMainData();
                    break;
                case 3:
                    ((BookPrescriptionCreateThirdQuestionFragment) bookPrescriptionCreatePagerAdapter.getItem(i)).setPrescriptionMainData();
                    break;
                default:
                    if (i == lastIndex) {
                        ((BookPrescriptionCreateTagFragment) bookPrescriptionCreatePagerAdapter.getItem(i)).setPrescriptionMainData();
                    } else if (i != lastIndex - 1) {
                        ((BookPrescriptionCreateDefaultFragment) bookPrescriptionCreatePagerAdapter.getItem(i)).setPrescriptionMainData();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case SEARCH_BOOK_REQUEST_CODE:
                    if (onSetBookInfoListener != null) {
                        DaumBookData daumBookData = (DaumBookData) data.getBundleExtra("bookinfo").getSerializable("bookInfoBundle");
                        onSetBookInfoListener.setBookInfo(daumBookData);
                    }
                    break;
            }
        } catch (Exception e) {

        }
    }

    public void setOnPassBookInfoListener(OnSetBookInfoListener onSetBookInfoListener) {
        this.onSetBookInfoListener = onSetBookInfoListener;
    }


}

