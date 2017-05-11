package com.kr.bookdoc.bookdoc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptiondetail.PrescriptionMain;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionedit.PrescriptionEdit;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.PrescriptionPost;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.SinglePrescription;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Tag;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionAddCardFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateDefaultFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateFirstQuestionFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateMainFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateSecondQuestionFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateTagFragment;
import com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment.BookPrescriptionCreateThirdQuestionFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

public class BookdocBookPrescriptionEditAcitivity extends BookdocBookPrescriptionBase implements OnPageChangeListener {
    private Toolbar bookdocBookPrescriptionEditToolbar;
    public ViewPager bookdocBookPrescriptionEditViewPager;
    public BookdocBookPrescriptionEditPagerAdapter bookdocBookPrescriptionEditPagerAdapter;
    private int prescriptionId;
    private int position;
    private int currentIndex;
    private final int FIRST_GUIDE_FRAGMENT_INDEX = 0;
    private final int SECOND_GUIDE_FRAGMENT_INDEX = 1;
    private final int LAST_GUIDE_FRAGMENT_INDEX = 2;
    private BookdocProgressDialog bookdocProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_book_prescription_edit_acitivity);
        bookdocProgressDialog = new BookdocProgressDialog(this);

        bookdocBookPrescriptionEditToolbar = (Toolbar) findViewById(R.id.bookdoc_edit_prescription_toolbar);
        setSupportActionBar(bookdocBookPrescriptionEditToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        bookdocBookPrescriptionEditViewPager = (ViewPager) findViewById(R.id.bookdoc_edit_prescription_vp);
        mPager = bookdocBookPrescriptionEditViewPager;
        if (bookdocBookPrescriptionEditViewPager != null) {
            final int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36,
                    getResources().getDisplayMetrics());
            final int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 13,
                    getResources().getDisplayMetrics());
            bookdocBookPrescriptionEditViewPager.setClipToPadding(false);
            bookdocBookPrescriptionEditViewPager.setPadding(padding, 0, padding, 0);
            bookdocBookPrescriptionEditViewPager.setPageMargin(margin);
            bookdocBookPrescriptionEditViewPager.setOffscreenPageLimit(11);
            bookdocBookPrescriptionEditViewPager.addOnPageChangeListener(this);
        }
        if (savedInstanceState == null) {
            prescriptionId = getIntent().getIntExtra("prescriptionId", 0);
            position = getIntent().getIntExtra("position", 0);
        } else {
            prescriptionId = savedInstanceState.getInt("prescriptionId");
            position = savedInstanceState.getInt("position");
        }
        new BookdocPrescriptionDetailAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookdoc_edit_prescription_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.edit_prescription:
                new BookdocEditPrescriptionAsyncTask().execute(PrescriptionPost.getInstance());
                if (PrescriptionPost.getInstance().getImage() != null || PrescriptionPost.getInstance().getImageType() != 0) {
                    new BookdocEditPrescriptionImageAsyncTask().execute(PrescriptionPost.getInstance());
                }
                finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("prescriptionId", prescriptionId);
        outState.putInt("position", position);
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

    public class BookdocBookPrescriptionEditPagerAdapter extends FragmentStatePagerAdapter {
        ArrayList<Fragment> bookPrescriptionEditFragmentList = new ArrayList<>();

        public BookdocBookPrescriptionEditPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return bookPrescriptionEditFragmentList.get(position);
        }

        @Override
        public int getCount() {
            Log.d("size", "size: " + bookPrescriptionEditFragmentList.size());
            return bookPrescriptionEditFragmentList.size();
        }

        @Override
        public float getPageWidth(int position) {
            return 1f;
        }

        public void appendBookPrescriptionEditFragment(Fragment fragment) {
            bookPrescriptionEditFragmentList.add(fragment);
        }

        public void appendBookPrescriptionDefaultFragment(Fragment fragment) {
            if (bookPrescriptionEditFragmentList.size() >= 11) {
                Toast.makeText(getApplicationContext(), "최대 카드 수를 초과했습니다.", Toast.LENGTH_SHORT).show();
            } else {
                int lastIndex = bookPrescriptionEditFragmentList.size() - 2;
                bookPrescriptionEditFragmentList.add(lastIndex, fragment);
                notifyDataSetChanged();
            }
        }

        public void removeView() {
            int totalSize = bookPrescriptionEditFragmentList.size();
            if (currentIndex < totalSize - 2) {
                bookPrescriptionEditFragmentList.remove(currentIndex);
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void removePagerItem() {
        bookdocBookPrescriptionEditPagerAdapter.removeView();
    }

    public class BookdocPrescriptionDetailAsyncTask extends AsyncTask<Void, String, SinglePrescription> {
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
        protected SinglePrescription doInBackground(Void... voids) {
            Response response = null;
            OkHttpClient toServer;
            SinglePrescription singlePrescription = new SinglePrescription();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_SINGLE_PRESCRIPTION, prescriptionId))
                        .header("Accept", "application/json")
                        .build();

                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    singlePrescription = gson.fromJson(responsedMessage, SinglePrescription.class);
                } else {
                    Log.e("요청에러", response.message().toString());
                }

            } catch (UnknownHostException une) {
                e("aaa", une.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
            } catch (Exception e) {
                e("ccc", e.toString());
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
            try {
                com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Data data = singlePrescription.getData();
                prescriptionMain.setPosition(position);
                prescriptionMain.setPrescriptionId(data.getId());
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
                bookdocBookPrescriptionEditPagerAdapter = new BookdocBookPrescriptionEditPagerAdapter(getSupportFragmentManager());
                bookdocBookPrescriptionEditPagerAdapter.appendBookPrescriptionEditFragment(BookPrescriptionCreateMainFragment.newInstance(prescriptionMain));
                int newCardCount = data.getCarded();
                for (int i = 0; i < newCardCount; i++) {
                    Card card = data.getCards().get(i);
                    switch (i) {
                        case FIRST_GUIDE_FRAGMENT_INDEX:
                            card.title = getResources().getString(R.string.first_guide_text);
                            bookdocBookPrescriptionEditPagerAdapter.appendBookPrescriptionEditFragment(BookPrescriptionCreateFirstQuestionFragment.newInstance(card, i));
                            break;
                        case SECOND_GUIDE_FRAGMENT_INDEX:
                            card.title = getResources().getString(R.string.second_guide_text);
                            bookdocBookPrescriptionEditPagerAdapter.appendBookPrescriptionEditFragment(BookPrescriptionCreateSecondQuestionFragment.newInstance(card, i));
                            break;
                        case LAST_GUIDE_FRAGMENT_INDEX:
                            card.title = getResources().getString(R.string.third_guide_text);
                            bookdocBookPrescriptionEditPagerAdapter.appendBookPrescriptionEditFragment(BookPrescriptionCreateThirdQuestionFragment.newInstance(card, i));
                            break;
                        default:
                            bookdocBookPrescriptionEditPagerAdapter.appendBookPrescriptionEditFragment(BookPrescriptionCreateDefaultFragment.newInstance(card, i));
                            break;
                    }
                }
                bookdocBookPrescriptionEditPagerAdapter.appendBookPrescriptionEditFragment(BookPrescriptionAddCardFragment.newInstance());
                ArrayList<Tag> tagList = data.getTags();
                bookdocBookPrescriptionEditPagerAdapter.appendBookPrescriptionEditFragment(BookPrescriptionCreateTagFragment.newInstance(tagList));
                bookdocBookPrescriptionEditViewPager.setAdapter(bookdocBookPrescriptionEditPagerAdapter);
            } catch (Exception e) {
            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }
    }

    public class BookdocEditPrescriptionAsyncTask extends AsyncTask<PrescriptionPost, String, SinglePrescription> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.show();
            }
            setPrescriptionData();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(), message, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected SinglePrescription doInBackground(PrescriptionPost... prescriptionPosts) {
            Response response = null;
            OkHttpClient toServer;
            PrescriptionPost prescriptionPost = prescriptionPosts[0];
            PrescriptionEdit prescriptionEdit = new PrescriptionEdit();
            SinglePrescription singlePrescription = new SinglePrescription();
            Gson gson = new Gson();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                prescriptionEdit.setUserId(BookdocPropertyManager.getInstance().getUserId());
                prescriptionEdit.setTitle(prescriptionPost.getTitle());
                prescriptionEdit.setDescription(prescriptionPost.getDescription());
                prescriptionEdit.setCards(prescriptionPost.getCards());
                prescriptionEdit.setTags(prescriptionPost.getTags());
                Log.d("jsondata", gson.toJson(prescriptionEdit));

                final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(JSON, gson.toJson(prescriptionEdit));

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_PUT_PRESCRIPTION, prescriptionId))
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .put(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                Log.d("jsondata", responsedMessage);
                if (response.isSuccessful()) {
                    singlePrescription = gson.fromJson(responsedMessage, SinglePrescription.class);
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
            try {
                if (PrescriptionPost.getInstance().getImage() == null) {
                    PrescriptionPost.getInstance().initData();
                    if (bookdocProgressDialog.isShowing()) {
                        bookdocProgressDialog.dismiss();
                    }
                }
            } catch (Exception e) {

            }
        }
    }

    public class BookdocEditPrescriptionImageAsyncTask extends AsyncTask<PrescriptionPost, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(), message, Toast.LENGTH_SHORT).show();
        }


        @Override
        protected Void doInBackground(PrescriptionPost... prescriptionPosts) {
            final MediaType pngType = MediaType.parse("image/*");
            PrescriptionPost prescriptionPost = prescriptionPosts[0];
            RequestBody postBody = null;
            Response response = null;
            OkHttpClient toServer = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            try {
                int imageType = prescriptionPost.getImageType();
                if (imageType != 0) {
                    postBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("imageType", String.valueOf(imageType))
                            .build();
                } else {
                    File file = new File(prescriptionPost.getImage());
                    postBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", file.getName(), RequestBody.create(pngType, file))
                            .build();
                }
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_PUT_PRESCRIPTION_IMAGE, prescriptionId))
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .put(postBody)
                        .build();
                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                if (response.isSuccessful()) {
                    Log.d("json", responsedMessage);
                } else {
                    Log.e("요청에러", response.message().toString());
                }

            } catch (UnknownHostException une) {
                e("aaa", une.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
            } catch (Exception e) {
                e("ccc", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                PrescriptionPost.getInstance().initData();
                if (bookdocProgressDialog.isShowing()) {
                    bookdocProgressDialog.dismiss();
                }
            } catch (Exception e) {

            }
        }
    }

    public void setPrescriptionData() {
        int fragmentListSize = bookdocBookPrescriptionEditPagerAdapter.getCount();
        int lastIndex = fragmentListSize - 1;
        for (int i = 0; i < fragmentListSize; i++) {
            switch (i) {
                case 0:
                    ((BookPrescriptionCreateMainFragment) bookdocBookPrescriptionEditPagerAdapter.getItem(i)).setPrescriptionMainData();
                    break;
                case 1:
                    ((BookPrescriptionCreateFirstQuestionFragment) bookdocBookPrescriptionEditPagerAdapter.getItem(i)).setPrescriptionMainData();
                    break;
                case 2:
                    ((BookPrescriptionCreateSecondQuestionFragment) bookdocBookPrescriptionEditPagerAdapter.getItem(i)).setPrescriptionMainData();
                    break;
                case 3:
                    ((BookPrescriptionCreateThirdQuestionFragment) bookdocBookPrescriptionEditPagerAdapter.getItem(i)).setPrescriptionMainData();
                    break;
                default:
                    if (i == lastIndex) {
                        ((BookPrescriptionCreateTagFragment) bookdocBookPrescriptionEditPagerAdapter.getItem(i)).setPrescriptionMainData();
                    } else if (i != lastIndex - 1) {
                        ((BookPrescriptionCreateDefaultFragment) bookdocBookPrescriptionEditPagerAdapter.getItem(i)).setPrescriptionMainData();
                    }
                    break;
            }
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
