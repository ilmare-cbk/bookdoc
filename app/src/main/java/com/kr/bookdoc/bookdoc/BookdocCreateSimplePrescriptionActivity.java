package com.kr.bookdoc.bookdoc;

import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocImageSelectDialog;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocImageList;
import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.bookdocutils.CustomImageView;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.BookdocImageSingle;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.daumbookdata.DaumBookData;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singleline.Singlelines;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlelinedetail.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlelinepost.CreateSinglelinePost;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlielineedit.SingleLineEdit;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

public class BookdocCreateSimplePrescriptionActivity extends AppCompatActivity implements View.OnClickListener {
    CreateSinglelinePost createSinglelinePost;
    Toolbar bookdocCreateSimplePrescriptionToolbar;
    LinearLayout bookdocCreateSimplePrescriptionGalleryBtn;
    LinearLayout bookdocCreateSimplePrescriptionGothic;
    LinearLayout bookdocCreateSimplePrescriptionSerif;
    LinearLayout bookdocCreateSimplePrescriptionFree;
    LinearLayout bookdocCreateSimplePrescriptionLeftAlignBtn;
    LinearLayout bookdocCreateSimplePrescriptionCenterAlignBtn;
    LinearLayout bookdocCreateSimplePrescriptionRightAlignBtn;
    EditText bookdocCreateSimplePrescriptionEdit;
    LinearLayout bookdocCreateSimplePrescriptionOptinoMenu;
    LinearLayout bookdocCreateSimplePrescriptionFontMenu;
    LinearLayout bookdocCreateSimplePrescriptionGothicBtn;
    LinearLayout bookdocCreateSimplePrescriptionSerifBtn;
    LinearLayout bookdocCreateSimplePrescriptionFreeBtn;
    LinearLayout bookdocCreateSimplePrescriptionOptionBackBtn;
    Integer singlelineUserId;
    String singlelineBookIsbn;
    String singlelineDescription;
    Integer singlelineFont;
    Integer singlelineAlignment;
    private String realImagePath;
    Integer singlelineImageType;
    Typeface typeNanumR, typeMyeongjo, typeBrush;
    private Uri imageCaptureUri;
    ImageView bookCreateSimplePrescriptionBackgroundImg;
    CustomImageView bookdocCreateSimplePrescriptionBookImg;
    TextView bookdocCreateSimplePrescriptionBookTitle;
    TextView bookdocCreateSimplePrescriptionBookAuthor;
    TextView bookdocCreateSimplePrescriptionBookPublisher;
    DaumBookData daumBookData;
    BookdocProgressDialog bookdocProgressDialog;

    private boolean checkEdit;
    private int position;
    private Data data;
    private int imageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_create_simple_prescription);
        BusProvider.getInstance().register(this);
        bookdocProgressDialog = new BookdocProgressDialog(this);
        bookdocCreateSimplePrescriptionToolbar = (Toolbar) findViewById(R.id.bookdoc_create_simple_prescription_toolbar);
        setSupportActionBar(bookdocCreateSimplePrescriptionToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
        bookCreateSimplePrescriptionBackgroundImg = (ImageView) findViewById(R.id.bookdoc_create_simple_prescription_background);
        bookdocCreateSimplePrescriptionGalleryBtn = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_gallery_btn);
        bookdocCreateSimplePrescriptionGalleryBtn.setOnClickListener(this);
        bookdocCreateSimplePrescriptionGothic = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_gothic);
        bookdocCreateSimplePrescriptionGothic.setOnClickListener(this);
        bookdocCreateSimplePrescriptionSerif = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_serif);
        bookdocCreateSimplePrescriptionSerif.setOnClickListener(this);
        bookdocCreateSimplePrescriptionFree = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_free);
        bookdocCreateSimplePrescriptionFree.setOnClickListener(this);
        bookdocCreateSimplePrescriptionLeftAlignBtn = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_left_align_btn);
        bookdocCreateSimplePrescriptionLeftAlignBtn.setOnClickListener(this);
        bookdocCreateSimplePrescriptionCenterAlignBtn = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_center_align_btn);
        bookdocCreateSimplePrescriptionCenterAlignBtn.setOnClickListener(this);
        bookdocCreateSimplePrescriptionRightAlignBtn = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_right_align_btn);
        bookdocCreateSimplePrescriptionRightAlignBtn.setOnClickListener(this);
        bookdocCreateSimplePrescriptionEdit = (EditText) findViewById(R.id.bookdoc_create_simple_prescription_edit);
        bookdocCreateSimplePrescriptionOptinoMenu = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_option_menu);
        bookdocCreateSimplePrescriptionFontMenu = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_font_menu);
        bookdocCreateSimplePrescriptionGothicBtn = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_gothic_btn);
        bookdocCreateSimplePrescriptionGothicBtn.setOnClickListener(this);
        bookdocCreateSimplePrescriptionSerifBtn = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_serif_btn);
        bookdocCreateSimplePrescriptionSerifBtn.setOnClickListener(this);
        bookdocCreateSimplePrescriptionFreeBtn = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_free_btn);
        bookdocCreateSimplePrescriptionFreeBtn.setOnClickListener(this);
        bookdocCreateSimplePrescriptionOptionBackBtn = (LinearLayout) findViewById(R.id.bookdoc_create_simple_prescription_option_back);
        bookdocCreateSimplePrescriptionOptionBackBtn.setOnClickListener(this);
        typeNanumR = Typeface.createFromAsset(getAssets(), "NanumBarunGothic.ttf");
        typeMyeongjo = Typeface.createFromAsset(getAssets(), "NanumMyeongjo.otf");
        typeBrush = Typeface.createFromAsset(getAssets(), "NanumBrush.otf");
        createSinglelinePost = CreateSinglelinePost.getInstance();

        singlelineFont = 2;
        singlelineAlignment = 2;

        bookdocCreateSimplePrescriptionBookImg = (CustomImageView) findViewById(R.id.bookdoc_create_simple_prescription_bookimg);
        bookdocCreateSimplePrescriptionBookTitle = (TextView) findViewById(R.id.bookdoc_create_simple_prescription_book_title);
        bookdocCreateSimplePrescriptionBookAuthor = (TextView) findViewById(R.id.bookdoc_create_simple_prescription_author);
        bookdocCreateSimplePrescriptionBookPublisher = (TextView) findViewById(R.id.bokdoc_create_simple_prescription_publisher);

        checkEdit = getIntent().getBooleanExtra("checkEdit", false);
        if (checkEdit) {
            position = getIntent().getIntExtra("position", 0);
            data = (Data) getIntent().getBundleExtra("data").getSerializable("dataBundle");
            int imageType = data.getImageType();
            if (imageType != 0) {
                Glide.with(BookdocApplication.getBookdocContext())
                        .load(BookdocImageList.getBookdocImage(imageType))
                        .into(bookCreateSimplePrescriptionBackgroundImg);
            } else {
                Glide.with(BookdocApplication.getBookdocContext())
                        .load(data.getImagePath())
                        .into(bookCreateSimplePrescriptionBackgroundImg);
            }
            bookdocCreateSimplePrescriptionEdit.setText(data.getDescription());
            setFontType(data.getFont());
            setAlignment(data.getAlignment());
        } else {
            daumBookData = (DaumBookData) getIntent().getBundleExtra("bookinfo").getSerializable("bookInfoBundle");
        }

        if (savedInstanceState != null) {
            imageCaptureUri = Uri.parse(savedInstanceState.getString("imagePath"));
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(imageCaptureUri)
                    .into(bookCreateSimplePrescriptionBackgroundImg);
            daumBookData = (DaumBookData) savedInstanceState.getSerializable("bookInfoBundle");

        }

        if (checkEdit) {
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(data.getBookImagePath())
                    .into(bookdocCreateSimplePrescriptionBookImg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bookdocCreateSimplePrescriptionBookTitle.setText(Html.fromHtml(Html.fromHtml(data.getBookTitle(), Html.FROM_HTML_MODE_LEGACY).toString(), Html.FROM_HTML_MODE_LEGACY));
                bookdocCreateSimplePrescriptionBookAuthor.setText(Html.fromHtml(Html.fromHtml(data.getBookAuthor(), Html.FROM_HTML_MODE_LEGACY).toString(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                bookdocCreateSimplePrescriptionBookTitle.setText(Html.fromHtml(Html.fromHtml(data.getBookTitle()).toString()));
                bookdocCreateSimplePrescriptionBookAuthor.setText(Html.fromHtml(Html.fromHtml(data.getBookAuthor()).toString()));
            }
            bookdocCreateSimplePrescriptionBookPublisher.setText(data.getBookPublisher());
        } else {
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(daumBookData.getCover_l_url())
                    .into(bookdocCreateSimplePrescriptionBookImg);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bookdocCreateSimplePrescriptionBookTitle.setText(Html.fromHtml(Html.fromHtml(daumBookData.getTitle(), Html.FROM_HTML_MODE_LEGACY).toString(), Html.FROM_HTML_MODE_LEGACY));
                bookdocCreateSimplePrescriptionBookAuthor.setText(Html.fromHtml(Html.fromHtml(daumBookData.getAuthor(), Html.FROM_HTML_MODE_LEGACY).toString(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                bookdocCreateSimplePrescriptionBookTitle.setText(Html.fromHtml(Html.fromHtml(daumBookData.getTitle()).toString()));
                bookdocCreateSimplePrescriptionBookAuthor.setText(Html.fromHtml(Html.fromHtml(daumBookData.getAuthor()).toString()));
            }
            bookdocCreateSimplePrescriptionBookPublisher.setText(daumBookData.getPub_nm());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookdoc_create_simple_prescription_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.post_simple_prescription:
                if (checkEdit) {
                    setCreateSinglelineData();
                    int imageType = createSinglelinePost.getInstance().getImageType();
                    int id = createSinglelinePost.getInstance().getId();
                    String image = createSinglelinePost.getInstance().getImage();
                    new BookdocEditSingleImageAsyncTask().execute(String.valueOf(imageType), String.valueOf(id), image);
                    new BookdocEditSinglelineAsyncTask().execute(createSinglelinePost.getInstance());
                    finish();
                } else {
                    if (realImagePath != null || imageType != 0) {
                        setCreateSinglelineData();
                        new BookdocCreateSinglelineAysncTask().execute(CreateSinglelinePost.getInstance());
                        finish();
                        return true;
                    }
                    Toast.makeText(getApplicationContext(), "사진을 선택 해주세요.", Toast.LENGTH_SHORT).show();
                }
                return false;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.bookdoc_create_simple_prescription_gallery_btn:
                BookdocImageSelectDialog bookdocImageSelectDialog = BookdocImageSelectDialog.newInstance(false);
                bookdocImageSelectDialog.show(getSupportFragmentManager(), "imageSelect");
                break;
            case R.id.bookdoc_create_simple_prescription_gothic:
            case R.id.bookdoc_create_simple_prescription_serif:
            case R.id.bookdoc_create_simple_prescription_free:
                bookdocCreateSimplePrescriptionOptinoMenu.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionFontMenu.setVisibility(View.VISIBLE);
                break;
            case R.id.bookdoc_create_simple_prescription_left_align_btn:
            case R.id.bookdoc_create_simple_prescription_center_align_btn:
            case R.id.bookdoc_create_simple_prescription_right_align_btn:
                toggleAlignProperties(viewId);
                break;
            case R.id.bookdoc_create_simple_prescription_gothic_btn:
            case R.id.bookdoc_create_simple_prescription_serif_btn:
            case R.id.bookdoc_create_simple_prescription_free_btn:
            case R.id.bookdoc_create_simple_prescription_option_back:
                toggleFontProperties(viewId);
                break;
        }
    }

    public void setFontType(int type) {
        switch (type) {
            case 1:
                bookdocCreateSimplePrescriptionEdit.setTypeface(typeNanumR);
                break;
            case 2:
                bookdocCreateSimplePrescriptionEdit.setTypeface(typeMyeongjo);
                break;
            case 3:
                bookdocCreateSimplePrescriptionEdit.setTypeface(typeBrush);
                break;
            default:
                bookdocCreateSimplePrescriptionEdit.setTypeface(typeNanumR);
                break;
        }


    }

    public void setAlignment(int type) {
        switch (type) {
            case 1:
                bookdocCreateSimplePrescriptionEdit.setGravity(Gravity.CENTER | Gravity.LEFT);
                break;
            case 2:
                bookdocCreateSimplePrescriptionEdit.setGravity(Gravity.CENTER);
                break;
            case 3:
                bookdocCreateSimplePrescriptionEdit.setGravity(Gravity.CENTER | Gravity.RIGHT);
                break;
            default:
                bookdocCreateSimplePrescriptionEdit.setGravity(Gravity.CENTER);
                break;
        }
    }

    public void toggleAlignProperties(int id) {
        switch (id) {
            case R.id.bookdoc_create_simple_prescription_left_align_btn:
                bookdocCreateSimplePrescriptionLeftAlignBtn.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionCenterAlignBtn.setVisibility(View.VISIBLE);
                bookdocCreateSimplePrescriptionRightAlignBtn.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionEdit.setGravity(Gravity.CENTER);
                singlelineAlignment = 2;
                break;
            case R.id.bookdoc_create_simple_prescription_center_align_btn:
                bookdocCreateSimplePrescriptionLeftAlignBtn.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionCenterAlignBtn.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionRightAlignBtn.setVisibility(View.VISIBLE);
                bookdocCreateSimplePrescriptionEdit.setGravity(Gravity.CENTER | Gravity.RIGHT);
                singlelineAlignment = 3;
                break;
            case R.id.bookdoc_create_simple_prescription_right_align_btn:
                bookdocCreateSimplePrescriptionLeftAlignBtn.setVisibility(View.VISIBLE);
                bookdocCreateSimplePrescriptionCenterAlignBtn.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionRightAlignBtn.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionEdit.setGravity(Gravity.CENTER | Gravity.LEFT);
                singlelineAlignment = 1;
                break;
        }
    }

    public void toggleFontProperties(int id) {
        switch (id) {
            case R.id.bookdoc_create_simple_prescription_gothic_btn:
                bookdocCreateSimplePrescriptionGothic.setVisibility(View.VISIBLE);
                bookdocCreateSimplePrescriptionSerif.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionFree.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionEdit.setTypeface(typeNanumR);
                singlelineFont = 1;
                break;
            case R.id.bookdoc_create_simple_prescription_serif_btn:
                bookdocCreateSimplePrescriptionGothic.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionSerif.setVisibility(View.VISIBLE);
                bookdocCreateSimplePrescriptionFree.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionEdit.setTypeface(typeMyeongjo);
                singlelineFont = 2;
                break;
            case R.id.bookdoc_create_simple_prescription_free_btn:
                bookdocCreateSimplePrescriptionGothic.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionSerif.setVisibility(View.GONE);
                bookdocCreateSimplePrescriptionFree.setVisibility(View.VISIBLE);
                bookdocCreateSimplePrescriptionEdit.setTypeface(typeBrush);
                singlelineFont = 3;
                break;
        }
        bookdocCreateSimplePrescriptionFontMenu.setVisibility(View.GONE);
        bookdocCreateSimplePrescriptionOptinoMenu.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void setBookImage(BookdocImageSingle bookdocImageSingle) {
        boolean imageFrom = bookdocImageSingle.isCheckImageFrom();
        if (imageFrom) {
            imageCaptureUri = bookdocImageSingle.getImagePath();
            realImagePath = getRealPathFromURI(imageCaptureUri);
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(imageCaptureUri)
                    .into(bookCreateSimplePrescriptionBackgroundImg);
            imageType = 0;
        } else {
            imageType = Integer.parseInt(String.valueOf(bookdocImageSingle.getImagePath()));
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(BookdocImageList.getBookdocImage(imageType))
                    .into(bookCreateSimplePrescriptionBackgroundImg);
            imageCaptureUri = null;
        }
    }

    public void setCreateSinglelineData() {
        CreateSinglelinePost createSinglelinePost = CreateSinglelinePost.getInstance();
        singlelineUserId = BookdocPropertyManager.getInstance().getUserId();
        if (checkEdit) {
            singlelineBookIsbn = data.getBookIsbn();
            createSinglelinePost.setId(data.getId());
        } else {
            String isbn = daumBookData.getIsbn13();
            if (!TextUtils.isEmpty(isbn) || isbn != null || !isbn.equals("")) {
                singlelineBookIsbn = isbn;
            } else {
                singlelineBookIsbn = daumBookData.getIsbn();
            }
        }
        singlelineDescription = bookdocCreateSimplePrescriptionEdit.getText().toString();
        singlelineImageType = imageType;
        createSinglelinePost.setImageType(singlelineImageType);
        createSinglelinePost.setImage(realImagePath);
        createSinglelinePost.setBookIsbn(singlelineBookIsbn);
        createSinglelinePost.setUserId(singlelineUserId);
        createSinglelinePost.setBookIsbn(singlelineBookIsbn);
        createSinglelinePost.setDescription(singlelineDescription);
        createSinglelinePost.setFont(singlelineFont);
        createSinglelinePost.setAlignment(singlelineAlignment);


    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        String imageUri;
        @SuppressWarnings("deprecation")
        Cursor cursor = this.managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        imageUri = cursor.getString(column_index);
        return imageUri;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imagePath", String.valueOf(imageCaptureUri));
        outState.putSerializable("bookInfoBundle", daumBookData);
    }


    public class BookdocCreateSinglelineAysncTask extends AsyncTask<CreateSinglelinePost, String, Singlelines> {

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
        protected Singlelines doInBackground(CreateSinglelinePost... singlinePosts) {
            final MediaType pngType = MediaType.parse("image/*");
            CreateSinglelinePost singlelinePost = singlinePosts[0];
            Singlelines singlelines = new Singlelines();
            Gson gson = new Gson();
            RequestBody postBody = null;
            Response response = null;
            OkHttpClient toServer = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            try {
                int imageType = singlelinePost.getImageType();
                if (imageType != 0) {
                    postBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("userId", String.valueOf(singlelinePost.getUserId()))
                            .addFormDataPart("bookIsbn", singlelinePost.getBookIsbn())
                            .addFormDataPart("description", singlelinePost.getDescription())
                            .addFormDataPart("font", String.valueOf(singlelinePost.getFont()))
                            .addFormDataPart("alignment", String.valueOf(singlelinePost.getAlignment()))
                            .addFormDataPart("imageType", String.valueOf(imageType))
                            .build();
                } else {
                    File file = new File(singlelinePost.getImage());
                    postBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("userId", String.valueOf(singlelinePost.getUserId()))
                            .addFormDataPart("bookIsbn", singlelinePost.getBookIsbn())
                            .addFormDataPart("description", singlelinePost.getDescription())
                            .addFormDataPart("font", String.valueOf(singlelinePost.getFont()))
                            .addFormDataPart("alignment", String.valueOf(singlelinePost.getAlignment()))
                            .addFormDataPart("image", file.getName(), RequestBody.create(pngType, file))
                            .build();
                }
                Request request = new Request.Builder()
                        .url(BookdocNetworkDefineConstant.SERVER_URL_POST_SINGLELINES)
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .post(postBody)
                        .build();
                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                Log.d("json", responsedMessage);
                if (response.isSuccessful()) {
                    singlelines = gson.fromJson(responsedMessage, Singlelines.class);
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
            return singlelines;
        }

        @Override
        protected void onPostExecute(Singlelines singlelines) {
            super.onPostExecute(singlelines);
            CreateSinglelinePost.getInstance().initData();
            try {
                BusProvider.getInstance().post(singlelines.getData().get(0));
            } catch (Exception e) {

            }
            if (bookdocProgressDialog != null && bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }
    }

    public class BookdocEditSinglelineAsyncTask extends AsyncTask<CreateSinglelinePost, String, SingleLineEdit> {
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
        protected SingleLineEdit doInBackground(CreateSinglelinePost... singlinePosts) {
            CreateSinglelinePost singlelinePost = singlinePosts[0];
            SingleLineEdit singleLineEdit = new SingleLineEdit();
            OkHttpClient toServer;
            Response response = null;

            toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
            try {
                RequestBody requestBody = new FormBody.Builder()
                        .add("description", singlelinePost.getDescription())
                        .add("font", String.valueOf(singlelinePost.getFont()))
                        .add("alignment", String.valueOf(singlelinePost.getAlignment()))
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_PUT_SINGLELINES, createSinglelinePost.getId()))
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .put(requestBody)
                        .build();
                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                Log.d("json", responsedMessage);
                if (response.isSuccessful()) {
                    singleLineEdit.setData(new JSONObject(responsedMessage));
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
            return singleLineEdit;
        }

        @Override
        protected void onPostExecute(SingleLineEdit singleLineEdit) {
            super.onPostExecute(singleLineEdit);
            CreateSinglelinePost.getInstance().initData();
            try {
                singleLineEdit.setPosition(position);
                BusProvider.getInstance().post(singleLineEdit);
            } catch (Exception e) {

            }
            if (bookdocProgressDialog != null && bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }
    }

    public class BookdocEditSingleImageAsyncTask extends AsyncTask<String, String, Void> {
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... strings) {
            final MediaType pngType = MediaType.parse("image/*");
            OkHttpClient toServer;
            Response response = null;
            RequestBody postBody = null;
            toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
            try {
                int imageType = Integer.parseInt(strings[0]);
                int id = Integer.parseInt(strings[1]);
                String image = strings[2];
                if (imageType != 0) {
                    postBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("imageType", String.valueOf(imageType))
                            .build();
                } else {
                    File file = new File(image);
                    postBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("image", file.getName(), RequestBody.create(pngType, file))
                            .build();
                }

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_PUT_SINGLELINES_IMAGE, id))
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .put(postBody)
                        .build();
                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                Log.d("json", responsedMessage);
                if (response.isSuccessful()) {

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
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        if (bookdocProgressDialog != null) {
            bookdocProgressDialog.dismiss();
            bookdocProgressDialog = null;
        }
    }
}
