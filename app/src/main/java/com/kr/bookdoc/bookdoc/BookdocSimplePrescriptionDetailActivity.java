package com.kr.bookdoc.bookdoc;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocShareDialog;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocSimplePrescriptionDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocswipestack.SwipeStack;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocImageList;
import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.bookdocutils.CustomImageView;
import com.kr.bookdoc.bookdoc.bookdocutils.OnSimplePrescriptionClickListener;
import com.kr.bookdoc.bookdoc.bookdocutils.OnSimplePrescriptionShareListener;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.SinglePrescriptionDel;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlelinedetail.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlelinedetail.SinglelineDetail;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BookdocSimplePrescriptionDetailActivity extends AppCompatActivity implements SwipeStack.SwipeStackListener,
        SwipeStack.SwipeProgressListener {
    private SwipeStack swipeStack;
    private BookdocSwipeStackAdapter bookdocSwipeStackAdapter;
    private BookdocProgressDialog bookdocProgressDialog;
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private int currentPosition;
    private int skip;
    View captureView;
    String captureTitle;
    Boolean check;
    OnSimplePrescriptionClickListener onSimplePrescriptionClickListener = new OnSimplePrescriptionClickListener() {
        @Override
        public void onSimplePrescriptionClick(Data data, int position) {
            BookdocSimplePrescriptionDialog bookdocSimplePrescriptionDialog
                    = BookdocSimplePrescriptionDialog.newInstances(data, position, currentPosition);
            bookdocSimplePrescriptionDialog.setOnDeleteSimplePrescription(new BookdocSimplePrescriptionDialog.OnDeleteSimplePrescriptionListener() {
                @Override
                public void onDeleteSimplePrescription(int id, int position) {
                    new BookSinglelineDeleteAsyncTask().execute(id);
                    Log.d("position", String.valueOf(currentPosition + position));
                    BusProvider.getInstance().post(new SinglePrescriptionDel(currentPosition + position));
                }
            });
            bookdocSimplePrescriptionDialog.show(getSupportFragmentManager(), "simplePrescriptiontdialog");
        }
    };
    OnSimplePrescriptionShareListener onSimplePrescriptionShareListener = new OnSimplePrescriptionShareListener() {
        @Override
        public void onSimplePrescriptionShare() {
            BookdocShareDialog bookdocShareDialog = BookdocShareDialog.newInstances();
            bookdocShareDialog.show(getSupportFragmentManager(), "share");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_simple_prescription_detail);
        check = false;
        skip = getIntent().getIntExtra("currentPosition", 0);
        currentPosition = skip;
        bookdocProgressDialog = new BookdocProgressDialog(this);
        swipeStack = (SwipeStack) findViewById(R.id.bookdoc_simple_prescription_detail_swipestack);
        swipeStack.setListener(this);
        swipeStack.setSwipeProgressListener(this);
        new BookSinglelineDetailAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, currentPosition,9);
    }

    @Override
    public void onViewSwipedToLeft(int position) {

    }

    @Override
    public void onViewSwipedToRight(int position) {

    }

    @Override
    public void onViewSwipedToUp(int position) {

    }

    @Override
    public void onViewSwipedToDown(int position) {

    }

    @Override
    public void onStackEmpty() {
        finish();

    }

    @Override
    public void onSwipeStart(int position) {
        int itemSize = bookdocSwipeStackAdapter.getCount();
        if (position + 4 > itemSize) {
            skip = skip + 9;
            new BookSinglelineDetailAsyncTask().execute(skip, 9);
        }
    }

    @Override
    public void onSwipeProgress(int position, float progress) {

    }

    @Override
    public void onSwipeEnd(int position) {
    }

    private class BookdocSwipeStackAdapter<T> extends ArrayAdapter<Data> {
        OnSimplePrescriptionClickListener onSimplePrescriptionClickListener;
        OnSimplePrescriptionShareListener onSimplePrescriptionShareListener;
        private BookdocSimplePrescriptionViewHolder bookdocSimplePrescriptionViewHolder = null;
        private int ressource;
        private Context context;
        Typeface typeNanumR = Typeface.createFromAsset(getAssets(), "NanumBarunGothic.ttf");
        Typeface typeMyeongjo = Typeface.createFromAsset(getAssets(), "NanumMyeongjo.otf");
        Typeface typeBrush = Typeface.createFromAsset(getAssets(), "NanumBrush.otf");

        public BookdocSwipeStackAdapter(Context context, int resource) {
            super(context, resource);
            this.context = context;
            this.ressource = resource;
        }

        public void addItems(ArrayList<Data> datas) {
            this.addAll(datas);
            notifyDataSetInvalidated();
        }


        @NonNull
        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View view = convertView;

            Log.e("sssss", "start GetView");

            if (view == null) {
                bookdocSimplePrescriptionViewHolder = new BookdocSimplePrescriptionViewHolder();
                view = LayoutInflater.from(context).inflate(ressource, parent, false);
                bookdocSimplePrescriptionViewHolder.bookdocSimplePrescriptionView = (RelativeLayout) view.findViewById(R.id.relativelayout_simple_prescription_view);
                bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionDown = (LinearLayout) view.findViewById(R.id.linearlayout_btn_singleline_detail_down);
                bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionShare = (LinearLayout) view.findViewById(R.id.linearlayout_btn_singleline_detail_share);
                bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionMore = (LinearLayout) view.findViewById(R.id.linearlayout_btn_singleline_detail_more);
                bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionContent = (TextView) view.findViewById(R.id.bookdoc_simple_prescription_detail_text);
                bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionBookImg = (CustomImageView) view.findViewById(R.id.bookdoc_simple_prescription_detail_bookimg);
                bookdocSimplePrescriptionViewHolder.bokdocSimpleDescriptionBookTitle = (TextView) view.findViewById(R.id.bookdoc_simple_prescription_detail_booktitle);
                bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionBookAuthor = (TextView) view.findViewById(R.id.bookdoc_simple_prescription_detail_bookauthor);
                bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionBookPublisher = (TextView) view.findViewById(R.id.bookdoc_simple_prescription_detail_bookpublisher);
                bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionBackground = (ImageView) view.findViewById(R.id.bookdoc_simple_prescription_detail_img);
                view.setTag(bookdocSimplePrescriptionViewHolder);

            } else {
                bookdocSimplePrescriptionViewHolder = (BookdocSimplePrescriptionViewHolder) view.getTag();
             }

            final Data data = getItem (position);
            if (data.getUserId() == BookdocPropertyManager.getInstance().getUserId())
            {
                bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionMore.setVisibility(View.VISIBLE);
            }

            switch (data.getFont()) {
                case 1:
                    bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionContent.setTypeface(typeNanumR);
                    break;
                case 2:
                    bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionContent.setTypeface(typeMyeongjo);
                    break;
                case 3:
                    bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionContent.setTypeface(typeBrush);
                    break;
                default:
                    bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionContent.setTypeface(typeNanumR);
                    break;
            }

            switch (data.getAlignment()) {
                case 1:
                    bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionContent.setGravity(Gravity.LEFT);
                    break;
                case 2:
                    bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionContent.setGravity(Gravity.CENTER);
                    break;
                case 3:
                    bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionContent.setGravity(Gravity.RIGHT);
                    break;
                default:
                    bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionContent.setGravity(Gravity.CENTER);
                    break;
            }

            final int imageType = data.getImageType();

            if(imageType != 0)
            {
                Glide.with(context)
                        .load(BookdocImageList.getBookdocImage(imageType).intValue()).listener ((RequestListener<? super Integer, GlideDrawable>) listener)
                        .into(bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionBackground);
            }else{
                Glide.with(context)
                        .load(data.getImagePath()).listener ((RequestListener<? super String, GlideDrawable>) listener)
                        .into(bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionBackground);
            }

            bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionContent.setText(data.getDescription());

            Glide.with(context)
                    .load(data.getBookImagePath())
                    .placeholder(R.drawable.book_loading)
                    .listener ((RequestListener<? super String, GlideDrawable>) listener)
                    .error(R.drawable.mybook_error).into(bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionBookImg);

            bookdocSimplePrescriptionViewHolder.bokdocSimpleDescriptionBookTitle.setText(data.getBookTitle());
            bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionBookAuthor.setText(data.getBookAuthor());
            bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionBookPublisher.setText(data.getBookPublisher());

            bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onSimplePrescriptionClickListener != null) {
                        onSimplePrescriptionClickListener.onSimplePrescriptionClick(data, position);
                    }
                }
            });
            bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionDown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    captureView = ((ViewGroup)view.getParent().getParent()).getChildAt(0);
                    captureTitle = data.getBookTitle();
                    checkPermission();
                }
            });
            bookdocSimplePrescriptionViewHolder.bookdocSimpleDescriptionShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(onSimplePrescriptionShareListener != null){
                        onSimplePrescriptionShareListener.onSimplePrescriptionShare();
                    }
                }
            });
            return view;
        }

        private class BookdocSimplePrescriptionViewHolder {
            RelativeLayout bookdocSimplePrescriptionView = null;
            LinearLayout bookdocSimpleDescriptionShare = null;
            LinearLayout bookdocSimpleDescriptionDown = null;
            LinearLayout bookdocSimpleDescriptionMore = null;
            TextView bookdocSimpleDescriptionContent = null;
            CustomImageView bookdocSimpleDescriptionBookImg = null;
            TextView bokdocSimpleDescriptionBookTitle = null;
            TextView bookdocSimpleDescriptionBookAuthor = null;
            TextView bookdocSimpleDescriptionBookPublisher = null;
            ImageView bookdocSimpleDescriptionBackground = null;
        }

        public void setSimplePrescriptionClickListener(OnSimplePrescriptionClickListener onSimplePrescriptionClickListener) {
            this.onSimplePrescriptionClickListener = onSimplePrescriptionClickListener;
        }

        public void setSimplePrescriptionShareListener(OnSimplePrescriptionShareListener onSimplePrescriptionShareListener){
            this.onSimplePrescriptionShareListener = onSimplePrescriptionShareListener;
        }

        RequestListener<?, GlideDrawable> listener = new RequestListener<T, GlideDrawable> ()
        {
            @Override
            public boolean onException (Exception e, T model, Target<GlideDrawable> target, boolean isFirstResource)
            {
                e.printStackTrace ();
                return false;
            }

            @Override
            public boolean onResourceReady (GlideDrawable resource, T model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource)
            {
                Log.d("test","download complete");
                return false;
            }
        };

    }



    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || this.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_STORAGE);
            } else {
                screenshot();
            }
        } else {
            screenshot();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    screenshot();
                }
                break;
        }
    }

    private void screenshot() {
        View v1 = captureView;
        v1.setDrawingCacheEnabled(true);
        Bitmap bm = v1.getDrawingCache();

        try {
            String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath();
            File path = new File(ex_storage + "/bookdoc");

            if (!path.isDirectory()) {
                path.mkdirs();
            }
            Calendar calendar = Calendar.getInstance();
            String temp = ex_storage + "/bookdoc/";
            temp = temp + "bookdoc_";
            temp = temp + captureTitle + "_";
            temp = temp + calendar.get(Calendar.MINUTE) + calendar.get(Calendar.SECOND);
            temp = temp + ".jpg";
            File file = new File(temp);
            try {
                file.createNewFile();
            } catch(IOException ie){

            }
            String imaegPath = file.getPath();
            FileOutputStream out = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
            Toast cToast = Toast.makeText(getApplicationContext(), "이미지가 bookdoc폴더에 저장 되었습니다.", Toast.LENGTH_SHORT);
            cToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            cToast.show();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.parse("file://" + imaegPath)));
        } catch (FileNotFoundException e) {
            Log.d("FileNotFoundException:", e.getMessage());
        }
    }

    public class BookSinglelineDetailAsyncTask extends AsyncTask<Integer, String, SinglelineDetail> {

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
            finish();
        }

        @Override
        protected SinglelineDetail doInBackground(Integer... integers) {

            int skip = integers[0];
            int count = integers[1];
            Response response = null;
            OkHttpClient toServer;
            SinglelineDetail singlelineDetail = new SinglelineDetail();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_SINGLELINES, skip, count))
                        .header("Accept", "application/json")
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("singleline json data", "data: " + responseMessage);
                if (response.isSuccessful()) {
                    singlelineDetail.setData(new JSONObject(responseMessage));
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
            return singlelineDetail;
        }

        @Override
        public void onPostExecute(SinglelineDetail singlelineDetail) {
            super.onPostExecute(singlelineDetail);
            try {
                if (singlelineDetail != null) {
                    if(bookdocSwipeStackAdapter == null  ){
                        bookdocSwipeStackAdapter = new BookdocSwipeStackAdapter(BookdocApplication.getBookdocContext(),
                                R.layout.bookdoc_simple_prescription_card_item);
                        bookdocSwipeStackAdapter.setSimplePrescriptionClickListener(onSimplePrescriptionClickListener);
                        bookdocSwipeStackAdapter.setSimplePrescriptionShareListener(onSimplePrescriptionShareListener);
                        swipeStack.setAdapter(bookdocSwipeStackAdapter);
                    }
                    bookdocSwipeStackAdapter.addItems(singlelineDetail.getData());
                }
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }
    }

    private class BookSinglelineDeleteAsyncTask extends AsyncTask<Integer, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s != null || !TextUtils.isEmpty(s) || !s.equals("") || s != "") {
                    if (s.equals("success")) {
                        swipeStack.removeTopView();
                    }
                }
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(BookdocApplication.getBookdocContext(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(Integer... integers) {
            int id = integers[0];
            String message = null;
            Response response = null;
            OkHttpClient toServer;


            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_DELETE_SINGLELINES, id))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .delete()
                        .build();

                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(responsedMessage);
                    message = jsonObject.optString("message");
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
            return message;
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