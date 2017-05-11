package com.kr.bookdoc.bookdoc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocShareDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptiondetail.PrescriptionMain;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.SinglePrescription;
import com.kr.bookdoc.bookdoc.bookprescriptiondetailfragment.BookPrescriptionGuideQuestionFragment;
import com.kr.bookdoc.bookdoc.bookprescriptiondetailfragment.BookPrescriptionMainFragment;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BookdocBookPrescriptionDetailActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    LinearLayout bookPrescriptionMainHorizontalSlide;
    ViewPager bookPrescriptionDetailViewPager;
    BookPrescriptionDetailPagerAdapter bookPrescriptionDetailPagerAdapter;
    Toolbar bookPrescriptionDetailToolbar;
    ActionBar actionBar;
    LinearLayout bookPrescriptionDetailLikeContainer;
    LinearLayout bookPrescriptionDetailLikeContainerFill;
    LinearLayout bookPrescriptionDetailBookmarkContainer;
    LinearLayout bookPrescriptionDetailBookmarkContainerFill;
    LinearLayout bookPrescriptionDetailCommentContainer;
    LinearLayout bookPrescriptionDetailShareContainer;
    ImageView bookPrescriptionDetailComment;
    ImageView bookPrescriptionDetailShare;
    int prescriptionId;
    int position;
    int bookId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_book_prescription_detail);

        bookPrescriptionMainHorizontalSlide = (LinearLayout) findViewById(R.id.book_prescription_main_horizontal_guide);

        prescriptionId = getIntent().getIntExtra("prescriptionId", 0);
        position = getIntent().getIntExtra("position", 0);
        bookId = getIntent().getIntExtra("bookId", 0);

        bookPrescriptionDetailToolbar = (Toolbar) findViewById(R.id.book_prescription_detail_toolbar);
        setSupportActionBar(bookPrescriptionDetailToolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_white_24dp);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        bookPrescriptionDetailViewPager = (ViewPager) findViewById(R.id.book_prescription_detail_vp);
        bookPrescriptionDetailPagerAdapter = new BookPrescriptionDetailPagerAdapter(getSupportFragmentManager());
        bookPrescriptionDetailViewPager.addOnPageChangeListener(this);
        bookPrescriptionDetailViewPager.setAdapter(bookPrescriptionDetailPagerAdapter);
        bookPrescriptionDetailLikeContainer = (LinearLayout) findViewById(R.id.bookdoc_book_prescription_detail_like_container);
        bookPrescriptionDetailLikeContainer.setOnClickListener(this);
        bookPrescriptionDetailLikeContainerFill = (LinearLayout) findViewById(R.id.bookdoc_book_prescription_detail_like_container_fill);
        bookPrescriptionDetailLikeContainerFill.setOnClickListener(this);
        bookPrescriptionDetailBookmarkContainer = (LinearLayout) findViewById(R.id.bookdoc_book_prescription_detail_bookmark_container);
        bookPrescriptionDetailBookmarkContainer.setOnClickListener(this);
        bookPrescriptionDetailBookmarkContainerFill = (LinearLayout) findViewById(R.id.bookdoc_book_prescription_detail_bookmark_container_fill);
        bookPrescriptionDetailBookmarkContainerFill.setOnClickListener(this);
        bookPrescriptionDetailComment = (ImageView) findViewById(R.id.bookdoc_book_prescription_detail_comment);
        bookPrescriptionDetailCommentContainer = (LinearLayout) findViewById(R.id.bookdoc_book_prescription_detail_comment_container);
        bookPrescriptionDetailCommentContainer.setOnClickListener(this);
        bookPrescriptionDetailShare = (ImageView) findViewById(R.id.bookdoc_book_prescription_detail_share);
        bookPrescriptionDetailShareContainer = (LinearLayout) findViewById(R.id.bookdoc_book_prescription_detail_share_container);
        bookPrescriptionDetailShareContainer.setOnClickListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bookPrescriptionMainHorizontalSlide.setVisibility(View.GONE);
            }
        }, 1500);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (bookPrescriptionDetailPagerAdapter.bookPrescriptionFragmentList.size() > 0) {
            bookPrescriptionDetailPagerAdapter.removeBookPrescriptionFragment();
        }
        new BookdocPrescriptionDetailAsyncTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            actionBar.setHomeAsUpIndicator(R.drawable.back_white_24dp);
        } else {
            actionBar.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bookdoc_book_prescription_detail_like_container:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setLike();
                    }
                }).start();
                break;
            case R.id.bookdoc_book_prescription_detail_like_container_fill:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cancleLike();
                    }
                }).start();
                break;
            case R.id.bookdoc_book_prescription_detail_bookmark_container:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setBookMark();
                    }
                }).start();
                break;
            case R.id.bookdoc_book_prescription_detail_bookmark_container_fill:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        cancleBookMark();
                    }
                }).start();
                break;
            case R.id.bookdoc_book_prescription_detail_comment_container:
                Intent bookdocDetailCommentIntent = new Intent(this, BookdocCommentActivity.class);
                bookdocDetailCommentIntent.putExtra("prescriptionId", prescriptionId);
                startActivity(bookdocDetailCommentIntent);
                break;
            case R.id.bookdoc_book_prescription_detail_share_container:
                BookdocShareDialog bookdocShareDialog = BookdocShareDialog.newInstances();
                bookdocShareDialog.show(getSupportFragmentManager(), "share");
                break;
        }
    }


    private class BookPrescriptionDetailPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> bookPrescriptionFragmentList = new ArrayList<>();

        public BookPrescriptionDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return bookPrescriptionFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return bookPrescriptionFragmentList.size();
        }

        public void appendBookPrescriptionFragment(Fragment fragment) {
            bookPrescriptionFragmentList.add(fragment);
        }

        public void removeBookPrescriptionFragment() {
            bookPrescriptionFragmentList.clear();
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    public class BookdocPrescriptionDetailAsyncTask extends AsyncTask<Void, String, SinglePrescription> {

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(), message, Toast.LENGTH_SHORT).show();
        }


        @Override
        protected SinglePrescription doInBackground(Void... voids) {
            Response response = null;
            OkHttpClient toServer;
            SinglePrescription singlePrescription = new SinglePrescription();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_SINGLE_PRESCRIPTION, prescriptionId))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .build();

                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                Log.d("jsondata", responsedMessage);
                if (response.isSuccessful()) {
                    singlePrescription.setData(new JSONObject(responsedMessage));
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
            return singlePrescription;
        }

        @Override
        protected void onPostExecute(SinglePrescription singlePrescription) {
            super.onPostExecute(singlePrescription);
            PrescriptionMain prescriptionMain = new PrescriptionMain();
            Data data = singlePrescription.getData();
            try {
                bookId = data.getBookId();
                int userLikes = data.getUserLikes();
                int userKeeps = data.getUserKeeps();
                if (userLikes == 0) {
                    bookPrescriptionDetailLikeContainerFill.setVisibility(View.GONE);
                    bookPrescriptionDetailLikeContainer.setVisibility(View.VISIBLE);
                } else if (userLikes == 1) {
                    bookPrescriptionDetailLikeContainer.setVisibility(View.GONE);
                    bookPrescriptionDetailLikeContainerFill.setVisibility(View.VISIBLE);
                }
                if (userKeeps == 0) {
                    bookPrescriptionDetailBookmarkContainerFill.setVisibility(View.GONE);
                    bookPrescriptionDetailBookmarkContainer.setVisibility(View.VISIBLE);
                } else if (userKeeps == 1) {
                    bookPrescriptionDetailBookmarkContainer.setVisibility(View.GONE);
                    bookPrescriptionDetailBookmarkContainerFill.setVisibility(View.VISIBLE);
                }

                prescriptionMain.setPosition(position);
                prescriptionMain.setPrescriptionId(data.getId());
                prescriptionMain.setUserId(data.getUserId());
                prescriptionMain.setImageType(data.getImageType());
                prescriptionMain.setImagePath(data.getImagePath());
                prescriptionMain.setTitle(data.getTitle());
                prescriptionMain.setUserImagePath(data.getUserImagePath());
                prescriptionMain.setUserName(data.getUserName());
                prescriptionMain.setTags(data.getTags());
                prescriptionMain.setBookImagePath(data.getBookImagePath());
                prescriptionMain.setBookTitle(data.getBookTitle());
                prescriptionMain.setBookAuthor(data.getBookAuthor());
                prescriptionMain.setBookPublisher(data.getBookPublisher());
                prescriptionMain.setCreated(data.getCreated());

                bookPrescriptionDetailPagerAdapter.appendBookPrescriptionFragment(BookPrescriptionMainFragment.newInstance(prescriptionMain));
                int newCardCount = data.getCarded();
                ArrayList<Card> cards = data.getCards();
                for (int i = 0; i < newCardCount; i++) {
                    Card card = cards.get(i);
                    bookPrescriptionDetailPagerAdapter.appendBookPrescriptionFragment(new BookPrescriptionGuideQuestionFragment().newInstance(card, i));
                }
                bookPrescriptionDetailPagerAdapter.notifyDataSetChanged();
            } catch (Exception e) {

            }
        }
    }

    public void setLike() {
        Response response = null;
        OkHttpClient toServer;

        try {
            toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", String.valueOf(BookdocPropertyManager.getInstance().getUserId()))
                    .build();

            Request request = new Request.Builder()
                    .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_LIKE, prescriptionId))
                    .header("Accept", "application/json")
                    .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                    .post(requestBody)
                    .build();

            response = toServer.newCall(request).execute();
            String responsedMessage = response.body().string();
            Log.d("jsondata", responsedMessage);
            if (response.isSuccessful()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookPrescriptionDetailLikeContainer.setVisibility(View.GONE);
                        bookPrescriptionDetailLikeContainerFill.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                Log.e("요청에러", response.message().toString());
            }

        } catch (Exception e) {
            Log.e("파싱에러", e.toString());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void cancleLike() {
        Response response = null;
        OkHttpClient toServer;

        try {
            toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", String.valueOf(BookdocPropertyManager.getInstance().getUserId()))
                    .build();

            Request request = new Request.Builder()
                    .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_LIKE, prescriptionId))
                    .header("Accept", "application/json")
                    .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                    .delete(requestBody)
                    .build();

            response = toServer.newCall(request).execute();
            String responsedMessage = response.body().string();
            Log.d("jsondata", responsedMessage);
            if (response.isSuccessful()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookPrescriptionDetailLikeContainerFill.setVisibility(View.GONE);
                        bookPrescriptionDetailLikeContainer.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                Log.e("요청에러", response.message().toString());
            }

        } catch (Exception e) {
            Log.e("파싱에러", e.toString());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void setBookMark() {
        Response response = null;
        OkHttpClient toServer;

        try {
            toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", String.valueOf(BookdocPropertyManager.getInstance().getUserId()))
                    .build();

            Request request = new Request.Builder()
                    .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_BOOKMARK, bookId))
                    .header("Accept", "application/json")
                    .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                    .post(requestBody)
                    .build();

            response = toServer.newCall(request).execute();
            String responsedMessage = response.body().string();
            Log.d("jsondata", responsedMessage);
            if (response.isSuccessful()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookPrescriptionDetailBookmarkContainer.setVisibility(View.GONE);
                        bookPrescriptionDetailBookmarkContainerFill.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                Log.e("요청에러", response.message().toString());
            }

        } catch (Exception e) {
            Log.e("파싱에러", e.toString());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public void cancleBookMark() {
        Response response = null;
        OkHttpClient toServer;

        try {
            toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", String.valueOf(BookdocPropertyManager.getInstance().getUserId()))
                    .build();

            Request request = new Request.Builder()
                    .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_BOOKMARK, bookId))
                    .header("Accept", "application/json")
                    .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                    .delete(requestBody)
                    .build();

            response = toServer.newCall(request).execute();
            String responsedMessage = response.body().string();
            Log.d("jsondata", responsedMessage);
            if (response.isSuccessful()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bookPrescriptionDetailBookmarkContainerFill.setVisibility(View.GONE);
                        bookPrescriptionDetailBookmarkContainer.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                Log.e("요청에러", response.message().toString());
            }

        } catch (Exception e) {
            Log.e("파싱에러", e.toString());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }


}
