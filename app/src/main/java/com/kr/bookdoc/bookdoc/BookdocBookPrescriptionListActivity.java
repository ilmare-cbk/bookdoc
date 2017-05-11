package com.kr.bookdoc.bookdoc;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocDateFormat;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocImageList;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocLoadMoreScrollListener;
import com.kr.bookdoc.bookdoc.bookdocutils.CustomImageView;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Prescription;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookdocBookPrescriptionListActivity extends AppCompatActivity implements OnRefreshListener {
    Toolbar bookdocBookPrescriptionListToolbar;
    SwipeRefreshLayout bookdocBookPrescriptionListSRL;
    RecyclerView bookdocBookPrescriptionListRecyclerView;
    BookdocBookPrescriptionListAdapter bookdocBookPrescriptionListAdapter;
    LinearLayoutManager bookdocBookPrescriptionListLinearLayoutManager;
    BookdocProgressDialog bookdocProgressDialog;
    int questionId;
    int bookId;
    boolean checkBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_book_prescription_list);

        questionId = getIntent().getIntExtra("questionId", 0);
        bookId = getIntent().getIntExtra("id", 0);
        checkBookmark = getIntent().getBooleanExtra("checkBookmark", false);

        bookdocProgressDialog = new BookdocProgressDialog(this);

        bookdocBookPrescriptionListSRL = (SwipeRefreshLayout) findViewById(R.id.bookdoc_book_prescription_list_swipe_refresh_layout);
        bookdocBookPrescriptionListSRL.setOnRefreshListener(this);

        bookdocBookPrescriptionListToolbar = (Toolbar) findViewById(R.id.bookdoc_book_prescription_list_toolbar);
        setSupportActionBar(bookdocBookPrescriptionListToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);

        bookdocBookPrescriptionListRecyclerView = (RecyclerView) findViewById(R.id.bookdoc_book_prescription_list_rv);
        bookdocBookPrescriptionListRecyclerView.addItemDecoration(new BookdocBookPrescriptionListDecoration(33));
        bookdocBookPrescriptionListLinearLayoutManager = new LinearLayoutManager(BookdocApplication.getBookdocContext(), LinearLayoutManager.VERTICAL, false);
        bookdocBookPrescriptionListRecyclerView.setLayoutManager(bookdocBookPrescriptionListLinearLayoutManager);
        bookdocBookPrescriptionListAdapter = new BookdocBookPrescriptionListAdapter();
        bookdocBookPrescriptionListRecyclerView.setAdapter(bookdocBookPrescriptionListAdapter);
        bookdocBookPrescriptionListRecyclerView.addOnScrollListener(new BookdocLoadMoreScrollListener(bookdocBookPrescriptionListLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

            }

            @Override
            public void onLoadMore(int page) {
                int skip = (page - 1) * 9;
                new BookPrescriptionAysncTask().execute(questionId, skip, 9);
            }

            @Override
            public void setVisibleTopButton() {

            }

            @Override
            public void setInvisibleTopButton() {

            }
        });
        if (checkBookmark) {
            new BookdocBookMarkPrescriptionAsyncTask().execute(bookId, 0, 9);
        } else {
            new BookPrescriptionAysncTask().execute(questionId, 0, 9);
        }

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
        bookdocBookPrescriptionListAdapter.removeItems();
        if (checkBookmark) {
            new BookdocBookMarkPrescriptionAsyncTask().execute(bookId, 0, 9);
        } else {
            new BookPrescriptionAysncTask().execute(questionId, 0, 9);
        }
        bookdocBookPrescriptionListSRL.setRefreshing(false);
    }

    public class BookdocBookPrescriptionListAdapter extends RecyclerView.Adapter<BookdocBookPrescriptionListAdapter.BookdocBookPrescriptionListViewHolder> {
        ArrayList<Data> prescriptionData = new ArrayList<>();

        @Override
        public BookdocBookPrescriptionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.book_prescription_item, parent, false);
            return new BookdocBookPrescriptionListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookdocBookPrescriptionListViewHolder holder, final int position) {
            final Data mData = prescriptionData.get(position);
            int imageType = mData.getImageType();
            if (imageType != 0) {
                Glide.with(BookdocApplication.getBookdocContext())
                        .load(BookdocImageList.getBookdocImage(imageType))
                        .into(holder.bookPrescriptionItemBackground);
            } else {
                Glide.with(BookdocApplication.getBookdocContext())
                        .load(mData.getImagePath())
                        .into(holder.bookPrescriptionItemBackground);
            }
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(mData.getBookImagePath())
                    .error(R.drawable.mybook_error)
                    .into(holder.bookPrescriptionBookImage);
            holder.bookPrescriptionItemTitle.setText(mData.getTitle());
            holder.bookPrescriptionItemUserName.setText("by." + mData.getUserName());
            holder.bookPrescriptionItemLikeCount.setText(String.valueOf(mData.getLiked()));
            holder.bookPrescriptionItemCommentCount.setText(String.valueOf(mData.getCommented()));
            holder.bookPrescriptionItemTime.setText(BookdocDateFormat.setDateFormat("yyyy-MM-dd'T'HH:mm:ss", "yyyy.MM.dd", mData.getCreated()));
            holder.bookPrescriptionItemCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BookdocApplication.getBookdocContext(), BookdocBookPrescriptionDetailActivity.class);
                    intent.putExtra("prescriptionId", mData.getId());
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return prescriptionData.size();
        }

        public void addItems(ArrayList<Data> datas) {
            prescriptionData.addAll(datas);
            notifyDataSetChanged();
        }

        public void removeItems() {
            prescriptionData.clear();
            notifyDataSetChanged();
        }

        public class BookdocBookPrescriptionListViewHolder extends RecyclerView.ViewHolder {
            CardView bookPrescriptionItemCardView;
            ImageView bookPrescriptionItemBackground;
            CustomImageView bookPrescriptionBookImage;
            TextView bookPrescriptionItemTitle;
            TextView bookPrescriptionItemUserName;
            TextView bookPrescriptionItemLikeCount;
            TextView bookPrescriptionItemCommentCount;
            TextView bookPrescriptionItemTime;
            View view;

            public BookdocBookPrescriptionListViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                bookPrescriptionItemBackground = (ImageView) itemView.findViewById(R.id.book_prescription_item_container);
                bookPrescriptionBookImage = (CustomImageView) itemView.findViewById(R.id.bookdoc_prescription_book_image);
                bookPrescriptionItemCardView = (CardView) itemView.findViewById(R.id.book_prescription_item_cv);
                bookPrescriptionItemTitle = (TextView) itemView.findViewById(R.id.book_prescription_item_title);
                bookPrescriptionItemUserName = (TextView) itemView.findViewById(R.id.book_prescription_item_username);
                bookPrescriptionItemLikeCount = (TextView) itemView.findViewById(R.id.book_prescription_item_like_count);
                bookPrescriptionItemCommentCount = (TextView) itemView.findViewById(R.id.book_prescription_item_comment_count);
                bookPrescriptionItemTime = (TextView) itemView.findViewById(R.id.book_prescription_item_time);
            }
        }
    }

    public class BookdocBookPrescriptionListDecoration extends RecyclerView.ItemDecoration {
        int space;

        public BookdocBookPrescriptionListDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }

    public class BookPrescriptionAysncTask extends AsyncTask<Integer, String, Prescription> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bookdocProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(), message, Toast.LENGTH_SHORT).show();
        }


        @Override
        protected Prescription doInBackground(Integer... integers) {
            int questionId = integers[0];
            int skip = integers[1];
            int count = integers[2];
            Response response = null;
            OkHttpClient toServer;
            Prescription prescription = new Prescription();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_PRESCRIPTION_FOR_QUESTION, questionId, skip, count))
                        .header("Accept", "application/json")
                        .build();

                response = toServer.newCall(request).execute();
                if (response.isSuccessful()) {
                    prescription.setData(new JSONObject(response.body().string()));
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
            return prescription;
        }

        @Override
        public void onPostExecute(Prescription prescription) {
            super.onPostExecute(prescription);
            try {
                bookdocBookPrescriptionListAdapter.addItems(prescription.getData());
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }
    }

    public class BookdocBookMarkPrescriptionAsyncTask extends AsyncTask<Integer, String, Prescription> {
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
        protected Prescription doInBackground(Integer... integers) {
            int bookId = integers[0];
            int skip = integers[1];
            int count = integers[2];
            Response response = null;
            OkHttpClient toServer;
            Prescription prescription = new Prescription();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_MY_BOOKMARK_PRESCRIPTION, bookId, skip, count))
                        .header("Accept", "application/json")
                        .build();

                response = toServer.newCall(request).execute();
                if (response.isSuccessful()) {
                    prescription.setData(new JSONObject(response.body().string()));
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
            return prescription;
        }

        @Override
        public void onPostExecute(Prescription prescription) {
            super.onPostExecute(prescription);
            try {
                bookdocBookPrescriptionListAdapter.addItems(prescription.getData());
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
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
