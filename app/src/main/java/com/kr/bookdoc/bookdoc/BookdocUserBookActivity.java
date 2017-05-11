package com.kr.bookdoc.bookdoc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.users.AnotherUser;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.users.Data;
import com.kr.bookdoc.bookdoc.mybookfragment.BookMarkFragment;
import com.kr.bookdoc.bookdoc.mybookfragment.MyPrescriptionFragment;
import com.kr.bookdoc.bookdoc.mybookfragment.MyRequestFragment;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookdocUserBookActivity extends AppCompatActivity implements OnTabSelectedListener {
    private int anotherUserId;
    private CircleImageView bookdocUserBookCv;
    private TextView bookdocUserBookDescription, bookdocUserBookPrescribed,
            bookdocUserBookQuestioned, bookdocUserBookUserName;
    private Toolbar bookdocUserBookToolbar;
    private ViewPager bookdocUserBookViewPager;
    private TabLayout bookdocUserBookTabLayout;
    private ImageView firstTab, secondTab, thirdTab;
    private BookdocProgressDialog bookdocProgressDialog;
    private BookdocPropertyManager bookdocPropertyManager;
    private MenuItem setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_user_book);
        anotherUserId = getIntent().getIntExtra("anotherUserId", 0);
        bookdocProgressDialog = new BookdocProgressDialog(this);
        bookdocPropertyManager = BookdocPropertyManager.getInstance();

        bookdocUserBookToolbar = (Toolbar) findViewById(R.id.bookdoc_user_book_toolbar);
        setSupportActionBar(bookdocUserBookToolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        bookdocUserBookCv = (CircleImageView) findViewById(R.id.bookdoc_user_book_user_image);
        bookdocUserBookDescription = (TextView) findViewById(R.id.bookdoc_user_book_deacription);
        bookdocUserBookPrescribed = (TextView) findViewById(R.id.bookdoc_user_book_prescribed);
        bookdocUserBookQuestioned = (TextView) findViewById(R.id.bookdoc_user_book_questioned);
        bookdocUserBookUserName = (TextView) findViewById(R.id.bookdoc_user_book_username);

        bookdocUserBookViewPager = (ViewPager) findViewById(R.id.bookdoc_user_book_vp);
        if (bookdocUserBookViewPager != null) {
            bookdocUserBookViewPager.setOffscreenPageLimit(3);
            setupUserBookViewPager(bookdocUserBookViewPager);
        }

        bookdocUserBookTabLayout = (TabLayout) findViewById(R.id.bookdoc_user_book_tablayout);
        bookdocUserBookTabLayout.setupWithViewPager(bookdocUserBookViewPager);
        bookdocUserBookTabLayout.addOnTabSelectedListener(this);

        initTabLayoutImage();
        if (anotherUserId != 0 && anotherUserId != bookdocPropertyManager.getUserId()) {
            new BookdocAnotherInfoAsyncTask().execute(anotherUserId);
        } else {
            setUserinfo();
            new BookdocUserInfoAsyncTask().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.bookdoc_mybook_menu, menu);
        setting = menu.findItem(R.id.setting);
        if (anotherUserId != 0 && anotherUserId != bookdocPropertyManager.getUserId()) {
            setting.setVisible(false);
        } else {
            setting.setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.setting:
                Intent intent = new Intent(this, BookdocSettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
    }

    public void initTabLayoutImage() {
        bookdocUserBookTabLayout.getTabAt(0).setCustomView(R.layout.custom_mybook_first_tab);
        bookdocUserBookTabLayout.getTabAt(1).setCustomView(R.layout.custom_mybook_second_tab);
        bookdocUserBookTabLayout.getTabAt(2).setCustomView(R.layout.custom_mybook_third_tab);
        firstTab = (ImageView) bookdocUserBookTabLayout.findViewById(R.id.mybook_first_tab);
        secondTab = (ImageView) bookdocUserBookTabLayout.findViewById(R.id.mybook_second_tab);
        thirdTab = (ImageView) bookdocUserBookTabLayout.findViewById(R.id.mybook_third_tab);
    }

    private void setupUserBookViewPager(ViewPager viewPager) {
        UserBookPagerAdapter userBookPagerAdapter = new UserBookPagerAdapter(getSupportFragmentManager());
        userBookPagerAdapter.appendMyBookFragment(BookMarkFragment.newInstance(anotherUserId));
        userBookPagerAdapter.appendMyBookFragment(MyRequestFragment.newInstance(anotherUserId));
        userBookPagerAdapter.appendMyBookFragment(MyPrescriptionFragment.newInstance(anotherUserId));
        viewPager.setAdapter(userBookPagerAdapter);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                firstTab.setImageResource(R.drawable.tap1_1);
                secondTab.setImageResource(R.drawable.tap2);
                thirdTab.setImageResource(R.drawable.tap3);
                break;
            case 1:
                firstTab.setImageResource(R.drawable.tap1);
                secondTab.setImageResource(R.drawable.tap2_1);
                thirdTab.setImageResource(R.drawable.tap3);
                break;
            case 2:
                firstTab.setImageResource(R.drawable.tap1);
                secondTab.setImageResource(R.drawable.tap2);
                thirdTab.setImageResource(R.drawable.tap3_1);
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private class UserBookPagerAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> myBookFragmentList = new ArrayList<>();
        private final int MY_BOOK_FRAGMENT_COUNT = 3;

        public UserBookPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return myBookFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return MY_BOOK_FRAGMENT_COUNT;
        }

        public void appendMyBookFragment(Fragment fragment) {
            myBookFragmentList.add(fragment);
        }
    }

    private class BookdocAnotherInfoAsyncTask extends AsyncTask<Integer, String, AnotherUser> {

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
        protected AnotherUser doInBackground(Integer... integers) {
            int anotherUserId = integers[0];
            Response response = null;
            OkHttpClient toServer;
            AnotherUser anotherUser = new AnotherUser();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_USER_INFO))
                        .header("Accept", "application/json")
                        .addHeader("anotherUserId", String.valueOf(anotherUserId))
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("anotherbookprofile", "responsemessage: " + responseMessage);
                if (response.isSuccessful()) {
                    anotherUser.setData(new JSONObject(responseMessage));
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
            return anotherUser;
        }

        @Override
        protected void onPostExecute(AnotherUser anotherUser) {
            super.onPostExecute(anotherUser);
            try {
                if (anotherUser != null) {
                    Data data = anotherUser.getData();
                    if (data != null) {
                        setAnotherinfo(data);
                    }
                }
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }

        }
    }

    private class BookdocUserInfoAsyncTask extends AsyncTask<Void, String, String> {

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(), message, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            Response response = null;
            OkHttpClient toServer;
            String message = null;

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_USER_INFO))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("mybookprofile", "responsemessage: " + responseMessage);
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(responseMessage);
                    message = jsonObject.optString("message");
                    JSONObject dataJsonObject = jsonObject.optJSONObject("data");
                    BookdocPropertyManager bookdocPropertyManager = BookdocPropertyManager.getInstance();
                    bookdocPropertyManager.setUserId(dataJsonObject.optInt("id"));
                    bookdocPropertyManager.setUserName(dataJsonObject.optString("name"));
                    bookdocPropertyManager.setFieldUserDescription(dataJsonObject.optString("description"));
                    bookdocPropertyManager.setFieldUserImage(dataJsonObject.optString("imagePath"));
                    bookdocPropertyManager.setUserEmail(dataJsonObject.optString("email"));
                    bookdocPropertyManager.setFieldPushCheck(dataJsonObject.optInt("push"));
                    bookdocPropertyManager.setFieldPrescriptionCount(dataJsonObject.optInt("prescribed"));
                    bookdocPropertyManager.setFieldQuestionCount(dataJsonObject.optInt("questioned"));
                    bookdocPropertyManager.setAccessToken(dataJsonObject.optString("token"));
                } else {
                    Log.e("요청에러", response.message().toString());
                    message = new JSONObject(responseMessage).optString("message");
                }

            } catch (Exception e) {
                Log.e("파싱에러", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (!TextUtils.isEmpty(s) && s != null && s.equals("success")) {
                    setUserinfo();
                }
            } catch (Exception e) {

            }
        }
    }

    public void setAnotherinfo(Data data) {
        bookdocUserBookUserName.setText(data.getName());
        Glide.with(getApplicationContext())
                .load(data.getImagePath())
                .placeholder(R.drawable.profile_default)
                .error(R.drawable.profile_default)
                .into(bookdocUserBookCv);
        bookdocUserBookDescription.setText(data.getDescription());
        bookdocUserBookPrescribed.setText(String.valueOf(data.getPrescribed()));
        bookdocUserBookQuestioned.setText(String.valueOf(data.getQuestioned()));
    }

    public void setUserinfo() {
        bookdocUserBookUserName.setText(bookdocPropertyManager.getUserName());
        Glide.with(getApplicationContext())
                .load(bookdocPropertyManager.getFieldUserImage())
                .placeholder(R.drawable.profile_default)
                .error(R.drawable.profile_default)
                .into(bookdocUserBookCv);
        bookdocUserBookDescription.setText(bookdocPropertyManager.getFieldUserDescription());
        bookdocUserBookPrescribed.setText(String.valueOf(bookdocPropertyManager.getFieldPrescriptionCount()));
        bookdocUserBookQuestioned.setText(String.valueOf(bookdocPropertyManager.getFieldQuestionCount()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookdocProgressDialog != null) {
            bookdocProgressDialog.dismiss();
            bookdocProgressDialog = null;
        }
    }
}
