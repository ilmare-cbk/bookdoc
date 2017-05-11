package com.kr.bookdoc.bookdoc;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
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
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Prescription;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.search.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.search.PrescriptionSearch;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookdocMainSearchActivity extends AppCompatActivity implements TextWatcher, AdapterView.OnItemClickListener, OnEditorActionListener {
    BookdocProgressDialog bookdocProgressDialog;
    ArrayList<String> tagsList = new ArrayList<>();
    ArrayAdapter<String> bookdocMainSearchLetterAdatper;
    AutoCompleteTextView bookdocMainSearchActv;
    String keyword;
    RecyclerView bookdocMainSearchRecyclerView;
    LinearLayoutManager bookdocMainSearchLinearLayoutManager;
    BookdocMainSearchAdapter bookdocMainSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_main_search);
        bookdocProgressDialog = new BookdocProgressDialog(this);

        bookdocMainSearchRecyclerView = (RecyclerView) findViewById(R.id.bookdoc_main_search_rv);
        bookdocMainSearchRecyclerView.addItemDecoration(new BookdocBookPrescriptionListDecoration(33));
        bookdocMainSearchLinearLayoutManager = new LinearLayoutManager(BookdocApplication.getBookdocContext(), LinearLayoutManager.VERTICAL, false);
        bookdocMainSearchRecyclerView.setLayoutManager(bookdocMainSearchLinearLayoutManager);
        bookdocMainSearchAdapter = new BookdocMainSearchAdapter();
        bookdocMainSearchRecyclerView.setAdapter(bookdocMainSearchAdapter);
        bookdocMainSearchActv = (AutoCompleteTextView) findViewById(R.id.bookdoc_main_search_actv);
        bookdocMainSearchActv.addTextChangedListener(this);
        bookdocMainSearchActv.setOnItemClickListener(this);
        bookdocMainSearchActv.setOnEditorActionListener(this);
        bookdocMainSearchRecyclerView.addOnScrollListener(new BookdocLoadMoreScrollListener(bookdocMainSearchLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

            }

            @Override
            public void onLoadMore(int page) {
                int skip = (page - 1) * 9;
                new BookdocPrescriptionSearchAsyncTask().execute(skip, 9);
            }

            @Override
            public void setVisibleTopButton() {

            }

            @Override
            public void setInvisibleTopButton() {

            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        new BookdocInitialSearchAsyncTask().execute(bookdocMainSearchActv.getText().toString().trim());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        keyword = adapterView.getItemAtPosition(i).toString().trim();
        bookdocMainSearchAdapter.removeItems();
        new BookdocPrescriptionSearchAsyncTask().execute(0, 9);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        if (i == EditorInfo.IME_ACTION_DONE) {
            keyword = bookdocMainSearchActv.getText().toString().trim();
            bookdocMainSearchAdapter.removeItems();
            new BookdocPrescriptionSearchAsyncTask().execute(0, 9);
            handled = true;
        }
        return handled;
    }

    public class BookdocInitialSearchAsyncTask extends AsyncTask<String, String, PrescriptionSearch> {

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
        protected PrescriptionSearch doInBackground(String... strings) {
            String letter = strings[0];
            Response response = null;
            OkHttpClient toServer;
            PrescriptionSearch prescriptionSearch = new PrescriptionSearch();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_TAGS, letter))
                        .header("Accept", "application/json")
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("jsondata", responseMessage);
                if (response.isSuccessful()) {
                    prescriptionSearch.setData(new JSONObject(responseMessage));
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
            return prescriptionSearch;
        }

        @Override
        protected void onPostExecute(PrescriptionSearch prescriptionSearch) {
            super.onPostExecute(prescriptionSearch);
            try {
                ArrayList<Data> data = prescriptionSearch.getData();
                tagsList.clear();
                for (int i = 0; i < data.size(); i++) {
                    tagsList.add(data.get(i).getDescription());
                }
                bookdocMainSearchLetterAdatper = new ArrayAdapter<>(getApplicationContext(), R.layout.bookdoc_autocomplete_item, tagsList);
                bookdocMainSearchActv.setAdapter(bookdocMainSearchLetterAdatper);
                bookdocMainSearchLetterAdatper.notifyDataSetChanged();
            } catch (Exception e) {

            }
        }
    }

    public class BookdocPrescriptionSearchAsyncTask extends AsyncTask<Integer, String, Prescription> {
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
            int skip = integers[0];
            int count = integers[1];
            Response response = null;
            OkHttpClient toServer;
            Prescription prescription = new Prescription();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_PRESCRIPTION_SEARCH, skip, count, keyword))
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
        protected void onPostExecute(Prescription prescription) {
            super.onPostExecute(prescription);
            try {
                bookdocMainSearchAdapter.addItems(prescription.getData());
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }
    }

    public class BookdocMainSearchAdapter extends RecyclerView.Adapter<BookdocMainSearchAdapter.BookdocMainSearchViewHolder> {
        ArrayList<com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Data> prescriptionData = new ArrayList<>();


        @Override
        public BookdocMainSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.book_prescription_item, parent, false);
            return new BookdocMainSearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookdocMainSearchViewHolder holder, final int position) {
            final com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Data mData = prescriptionData.get(position);
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

        public void addItems(ArrayList<com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Data> datas) {
            prescriptionData.addAll(datas);
            notifyDataSetChanged();
        }

        public void removeItems() {
            prescriptionData.clear();
            notifyDataSetChanged();
        }

        public class BookdocMainSearchViewHolder extends RecyclerView.ViewHolder {
            CardView bookPrescriptionItemCardView;
            ImageView bookPrescriptionItemBackground;
            CustomImageView bookPrescriptionBookImage;
            TextView bookPrescriptionItemTitle;
            TextView bookPrescriptionItemUserName;
            TextView bookPrescriptionItemLikeCount;
            TextView bookPrescriptionItemCommentCount;
            TextView bookPrescriptionItemTime;

            public BookdocMainSearchViewHolder(View itemView) {
                super(itemView);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bookdocProgressDialog != null) {
            bookdocProgressDialog.dismiss();
            bookdocProgressDialog = null;
        }
    }
}
