package com.kr.bookdoc.bookdoc;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocDateFormat;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocLoadMoreScrollListener;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.notifications.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.notifications.Notifications;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BookdocNotiActivity extends AppCompatActivity implements OnRefreshListener {
    private RecyclerView bookdocNotiRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private NoticeRecyclerViewAdapter noticeRecyclerViewAdapter;
    private Toolbar noticeToolbar;
    private ActionBar actionBar;
    private BookdocProgressDialog bookdocProgressDialog;
    private SwipeRefreshLayout bookdocNotiSpl;
    private BookdocLoadMoreScrollListener bookdocLoadMoreScrollListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_notice);
        bookdocProgressDialog = new BookdocProgressDialog(this);

        noticeToolbar = (Toolbar) findViewById(R.id.toolbar_notice);
        setSupportActionBar(noticeToolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        bookdocNotiSpl = (SwipeRefreshLayout) findViewById(R.id.bookdoc_notice_spl);
        bookdocNotiSpl.setOnRefreshListener(this);
        bookdocNotiRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_notice);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        noticeRecyclerViewAdapter = new NoticeRecyclerViewAdapter(this);
        bookdocNotiRecyclerView.setLayoutManager(linearLayoutManager);
        bookdocNotiRecyclerView.setAdapter(noticeRecyclerViewAdapter);
        bookdocLoadMoreScrollListener = new BookdocLoadMoreScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

            }

            @Override
            public void onLoadMore(int page) {
                int skip = (page - 1) * 9;
                new BookdocGetNotiAsyncTask().execute(skip, 9);
            }

            @Override
            public void setVisibleTopButton() {

            }

            @Override
            public void setInvisibleTopButton() {

            }
        };
        bookdocNotiRecyclerView.addOnScrollListener(bookdocLoadMoreScrollListener);
        new BookdocGetNotiAsyncTask().execute(0, 9);
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
    public void onRefresh() {
        bookdocLoadMoreScrollListener.initData();
        noticeRecyclerViewAdapter.removeItems();
        new BookdocGetNotiAsyncTask().execute(0, 9);
        bookdocNotiSpl.setRefreshing(false);
    }


    public static class NoticeRecyclerViewAdapter extends RecyclerView.Adapter<NoticeRecyclerViewAdapter.ViewHolder> {
        ArrayList<Data> dataArrayList = new ArrayList<>();
        Context context;
        private String setPrescription = null;
        private String setWonder = null;

        public NoticeRecyclerViewAdapter(Context context) {
            this.context = context;
            setPrescription = context.getResources().getString(R.string.noti_prescription);
            setWonder = context.getResources().getString(R.string.noti_wonder);
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public final CircleImageView notiUserImage;
            public final TextView notiContent;
            public final TextView notiCreated;

            public ViewHolder(View itemView) {
                super(itemView);
                notiUserImage = (CircleImageView) itemView.findViewById(R.id.imageview_notice_item);
                notiContent = (TextView) itemView.findViewById(R.id.textview_notice_item);
                notiCreated = (TextView) itemView.findViewById(R.id.textview_notice_delay_time);
            }
        }


        @Override
        public NoticeRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_bookdoc_notice_item, parent, false);
            return new NoticeRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Data mData = dataArrayList.get(position);
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(mData.getPrescriberImagePath())
                    .into(holder.notiUserImage);
            int questioner = mData.getIsQuestioner();
            String content = null;
            String name = mData.getPrescriberName();
            if (questioner == 1) {
                content = String.format(setPrescription, name);
            } else if (questioner == 0) {
                content = String.format(setWonder, name);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.notiContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.notiContent.setText(Html.fromHtml(content));
            }
            holder.notiCreated.setText(new BookdocDateFormat().formatTimeString("yyyy-MM-dd'T'HH:mm:ss", mData.getCreated()));
        }


        @Override
        public int getItemCount() {
            return dataArrayList.size();
        }

        public void addItems(ArrayList<Data> datas) {
            dataArrayList.addAll(datas);
            notifyDataSetChanged();
        }

        public void removeItems() {
            dataArrayList.clear();
            notifyDataSetChanged();
        }


    }

    private class BookdocGetNotiAsyncTask extends AsyncTask<Integer, String, Notifications> {

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
            Toast.makeText(BookdocApplication.getBookdocContext(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Notifications notifications) {
            super.onPostExecute(notifications);
            try {
                if (notifications != null) {
                    noticeRecyclerViewAdapter.addItems(notifications.getData());
                }
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }

        @Override
        protected Notifications doInBackground(Integer... integers) {
            int skip = integers[0];
            int count = integers[1];
            Response response = null;
            OkHttpClient toServer;
            Notifications notifications = new Notifications();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                Log.d("token", BookdocPropertyManager.getInstance().getAccessToken());
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_USER_NOTIFICATION, skip, count))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("response", "response: " + responseMessage);
                if (response.isSuccessful()) {
                    notifications.setData(new JSONObject(responseMessage));
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
            return notifications;
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




