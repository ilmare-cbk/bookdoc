package com.kr.bookdoc.bookdoc;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocmainfragment.BookPrescriptionFragment;
import com.kr.bookdoc.bookdoc.bookdocmainfragment.RequestPrescriptionFragment;
import com.kr.bookdoc.bookdoc.bookdocmainfragment.SimplePrescriptionFragment;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocImageList;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.PrimaryPrescription;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.support.design.widget.TabLayout.OnTabSelectedListener;
import static android.support.design.widget.TabLayout.Tab;
import static com.kr.bookdoc.bookdoc.R.id.bookdoc_main_toolbar;

public class BookdocMainActivity extends AppCompatActivity implements OnTabSelectedListener, ViewPager.OnPageChangeListener {
    private final long FINSH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;
    ViewPager bookdocMainViewPager, bookdocMainContainerViewPager;
    TabLayout bookdocMainTabLayout;
    Toolbar bookdocMainToolbar, bookdocSubToobar;
    AppBarLayout bookdocMainAppbarLayout;
    ImageView firstTab, secondTab, thirdTab;
    ImageView bookdocMainSlideFirst, bookdocMainSlideSecond, bookdocMainSlideThird;
    BookdocMainPagerAdpater bookdocMainPagerAdpater;
    BookdocProgressDialog bookdocProgressDialog;
    int prescriptionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_main);

        prescriptionId = getIntent().getIntExtra("prescriptionId", 0);

        bookdocProgressDialog = new BookdocProgressDialog(this);

        bookdocMainToolbar = (Toolbar) findViewById(bookdoc_main_toolbar);
        bookdocSubToobar = (Toolbar) findViewById(R.id.bookdoc_sub_toolbar);
        bCheckingListener = new AtomicBoolean();

        bookdocMainAppbarLayout = (AppBarLayout) findViewById(R.id.bookdoc_main_appbarlayout);

        setActionBar(bookdocMainToolbar);
        initSlideImage();

        bookdocMainViewPager = (ViewPager) findViewById(R.id.bookdoc_main_vp);
        if (bookdocMainViewPager != null) {
            bookdocMainViewPager.addOnPageChangeListener(this);
            bookdocMainPagerAdpater = new BookdocMainPagerAdpater();
            new BookdocPrimaryPrescriptionAsyncTask().execute();
        }
        bookdocMainContainerViewPager = (ViewPager) findViewById(R.id.bookdoc_main_container_vp);
        if (bookdocMainContainerViewPager != null) {
            bookdocMainContainerViewPager.setOffscreenPageLimit(3);
            setupBookdocContainerViewPager(bookdocMainContainerViewPager);
        }

        bookdocMainTabLayout = (TabLayout) findViewById(R.id.bookdoc_main_tl);
        bookdocMainTabLayout.setupWithViewPager(bookdocMainContainerViewPager);
        initTabLayoutImage();
        bookdocMainTabLayout.addOnTabSelectedListener(this);

    }

    private void setActionBar(Toolbar toobar) {
        setSupportActionBar(toobar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.alarm);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookdoc_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intentSubActivity =
                        new Intent(BookdocMainActivity.this, BookdocNotiActivity.class);
                startActivity(intentSubActivity);
                return true;
            case R.id.search_book_prescription:
                Intent mainSearchIntent = new Intent(BookdocApplication.getBookdocContext(), BookdocMainSearchActivity.class);
                startActivity(mainSearchIntent);
                return true;
            case R.id.mybook:
                Intent myBookIntent = new Intent(BookdocApplication.getBookdocContext(), BookdocUserBookActivity.class);
                myBookIntent.putExtra("anotherUserId", BookdocPropertyManager.getInstance().getUserId());
                startActivity(myBookIntent);
                return true;
            default:
                return false;
        }
    }

    private void setupBookdocContainerViewPager(ViewPager viewPager) {
        BookdocContainerPagerAdapter bookdocContainerPagerAdapter = new BookdocContainerPagerAdapter(getSupportFragmentManager());
        bookdocContainerPagerAdapter.appendBookdocFragment(BookPrescriptionFragment.newInstance());
        bookdocContainerPagerAdapter.appendBookdocFragment(RequestPrescriptionFragment.newInstance());
        bookdocContainerPagerAdapter.appendBookdocFragment(SimplePrescriptionFragment.newInstance());
        viewPager.setAdapter(bookdocContainerPagerAdapter);
    }

    public void initTabLayoutImage() {
        bookdocMainTabLayout.getTabAt(0).setCustomView(R.layout.custom_fisrt_tab);
        bookdocMainTabLayout.getTabAt(1).setCustomView(R.layout.custom_second_tab);
        bookdocMainTabLayout.getTabAt(2).setCustomView(R.layout.custom_third_tab);
        firstTab = (ImageView) bookdocMainTabLayout.findViewById(R.id.first_tab);
        secondTab = (ImageView) bookdocMainTabLayout.findViewById(R.id.second_tab);
        thirdTab = (ImageView) bookdocMainTabLayout.findViewById(R.id.third_tab);
    }

    public void initSlideImage() {
        bookdocMainSlideFirst = (ImageView) findViewById(R.id.bookdoc_main_slide1);
        bookdocMainSlideSecond = (ImageView) findViewById(R.id.bookdoc_main_slide2);
        bookdocMainSlideThird = (ImageView) findViewById(R.id.bookdoc_main_slide3);
    }

    public void toggleSlideImage(int id) {
        switch (id) {
            case 0:
                bookdocMainSlideFirst.setImageResource(R.drawable.slide1);
                bookdocMainSlideSecond.setImageResource(R.drawable.slide2);
                bookdocMainSlideThird.setImageResource(R.drawable.slide2);
                break;
            case 1:
                bookdocMainSlideFirst.setImageResource(R.drawable.slide2);
                bookdocMainSlideSecond.setImageResource(R.drawable.slide1);
                bookdocMainSlideThird.setImageResource(R.drawable.slide2);
                break;
            case 2:
                bookdocMainSlideFirst.setImageResource(R.drawable.slide2);
                bookdocMainSlideSecond.setImageResource(R.drawable.slide2);
                bookdocMainSlideThird.setImageResource(R.drawable.slide1);
                break;
        }
    }

    public void setAppbarExpanded(boolean bExpanded) {
        if (bExpanded) {
            bookdocMainViewPager.setVisibility(View.VISIBLE);
            setActionBar(bookdocMainToolbar);
            bookdocMainAppbarLayout.setExpanded(false, false);

            bookdocMainAppbarLayout.post(new Runnable() {
                @Override
                public void run() {

                    bookdocSubToobar.setAlpha(1f);
                    ViewCompat.animate(bookdocSubToobar).alpha(0).setDuration(600).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            bookdocSubToobar.setVisibility(View.GONE);
                        }
                    }).start();

                    bookdocMainAppbarLayout.setExpanded(true, true);
                }
            });

        } else {
            bookdocMainAppbarLayout.addOnOffsetChangedListener(mOffsetListenr);
            bookdocMainAppbarLayout.setExpanded(false, true);
            bCheckingListener.set(true);
        }

        bookdocMainAppbarLayout.requestLayout();
        ;
    }

    AppBarLayout.OnOffsetChangedListener mOffsetListenr = new AppBarLayout.OnOffsetChangedListener() {
        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

            if (bCheckingListener.get() && Math.abs(verticalOffset) == Math.abs(bookdocMainAppbarLayout.getTotalScrollRange()) && mTabIndex != 0 && bookdocMainViewPager.getVisibility() == View.VISIBLE) {
                bCheckingListener.set(false);
                setActionBar(bookdocSubToobar);
                bookdocSubToobar.setVisibility(View.VISIBLE);
                bookdocMainAppbarLayout.removeOnOffsetChangedListener(mOffsetListenr);
                bookdocMainAppbarLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        bookdocSubToobar.setAlpha(0.9f);
                        bookdocMainAppbarLayout.setExpanded(false, false);
                        bookdocMainViewPager.setVisibility(View.GONE);
                        ViewCompat.animate(bookdocSubToobar).alpha(1f).setDuration(400).start();
                    }
                });
            }
        }
    };

    int mTabIndex;
    AtomicBoolean bCheckingListener;

    @Override
    public void onTabSelected(Tab tab) {
        mTabIndex = tab.getPosition();
        switch (mTabIndex) {
            case 0:
                firstTab.setImageResource(R.drawable.category1);
                secondTab.setImageResource(R.drawable.category2_1);
                thirdTab.setImageResource(R.drawable.category3_1);
                setAppbarExpanded(true);
                break;
            case 1:
                firstTab.setImageResource(R.drawable.category1_1);
                secondTab.setImageResource(R.drawable.category2);
                thirdTab.setImageResource(R.drawable.category3_1);
                setAppbarExpanded(false);
                break;
            case 2:
                firstTab.setImageResource(R.drawable.category1_1);
                secondTab.setImageResource(R.drawable.category2_1);
                thirdTab.setImageResource(R.drawable.category3);
                setAppbarExpanded(false);
                break;
        }
    }

    @Override
    public void onTabUnselected(Tab tab) {

    }

    @Override
    public void onTabReselected(Tab tab) {

    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = currentTime;
            Toast.makeText(getApplicationContext(),
                    "'뒤로' 버튼 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        toggleSlideImage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class BookdocContainerPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> bookdocFragmentContainer = new ArrayList<>();

        public BookdocContainerPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return bookdocFragmentContainer.get(position);
        }

        @Override
        public int getCount() {
            return bookdocFragmentContainer.size();
        }

        public void appendBookdocFragment(Fragment fragment) {
            bookdocFragmentContainer.add(fragment);
        }
    }

    private class BookdocMainPagerAdpater extends PagerAdapter {
        private static final int MAIN_VIEWPAGER_ITEM_COUNT = 3;
        ArrayList<Data> primaryPrescriptionList = new ArrayList<>();

        public BookdocMainPagerAdpater() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = null;
            LayoutInflater inflater = (LayoutInflater) BookdocApplication.getBookdocContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.book_prescription_main_item, null);

            final Data mdata = primaryPrescriptionList.get(position);
            int imageType = mdata.getImageType();
            ImageView bookPrescriptionPrimaryBackground = (ImageView) view.findViewById(R.id.book_prescription_main_item_container);

            if (imageType != 0) {
                Glide.with(BookdocApplication.getBookdocContext())
                        .load(BookdocImageList.getBookdocImage(imageType))
                        .into(bookPrescriptionPrimaryBackground);
            } else {
                Glide.with(BookdocApplication.getBookdocContext())
                        .load(mdata.getImagePath())
                        .into(bookPrescriptionPrimaryBackground);
            }

            CircleImageView circleImageView = (CircleImageView) view.findViewById(R.id.book_prescription_content_user_img);
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(mdata.getUserImagePath())
                    .error(R.drawable.profile_default)
                    .into(circleImageView);

            TextView bookPrescriptionPrimaryTitle = (TextView) view.findViewById(R.id.book_prescription_primary_item_title);
            bookPrescriptionPrimaryTitle.setText(mdata.getTitle());

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BookdocApplication.getBookdocContext(), BookdocBookPrescriptionDetailActivity.class);
                    intent.putExtra("prescriptionId", mdata.getId());
                    intent.putExtra("position", position);
                    intent.putExtra("bookId", mdata.getBookId());
                    startActivity(intent);
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return MAIN_VIEWPAGER_ITEM_COUNT;
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public void addItems(ArrayList<Data> dataList) {
            primaryPrescriptionList.addAll(dataList);
        }

    }

    public class BookdocPrimaryPrescriptionAsyncTask extends AsyncTask<Void, String, PrimaryPrescription> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.show();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(), message, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected PrimaryPrescription doInBackground(Void... voids) {
            Response response = null;
            OkHttpClient toServer;
            PrimaryPrescription primaryPrescription = new PrimaryPrescription();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_PRIMARY_PRESCRIPTION))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .build();

                response = toServer.newCall(request).execute();
                if (response.isSuccessful()) {
                    primaryPrescription.setData(new JSONObject(response.body().string()));
                } else {
                    Log.e("요청에러", response.message().toString());
                }

            } catch (Exception e) {
                Log.e("파싱에러", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return primaryPrescription;
        }

        @Override
        public void onPostExecute(PrimaryPrescription primaryPrescription) {
            super.onPostExecute(primaryPrescription);
            try {
                bookdocMainPagerAdpater.addItems(primaryPrescription.getData());
                bookdocMainViewPager.setAdapter(bookdocMainPagerAdpater);
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prescriptionId != 0) {
            Intent prescriptionIntent = new Intent(this, BookdocBookPrescriptionDetailActivity.class);
            prescriptionIntent.putExtra("prescriptionId", prescriptionId);
            startActivity(prescriptionIntent);
            prescriptionId = 0;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bookdocProgressDialog != null) {
            bookdocProgressDialog.dismiss();
            bookdocProgressDialog = null;
        }
    }
}