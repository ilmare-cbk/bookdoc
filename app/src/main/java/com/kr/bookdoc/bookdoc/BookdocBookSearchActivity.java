package com.kr.bookdoc.bookdoc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.CustomImageView;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.daumbookdata.DaumBookData;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.daumbookdata.DaumBookDatas;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BookdocBookSearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener{
    EditText bookdocBookSearchEdit;
    RecyclerView bookdocBookSearchRecyclerView;
    InputMethodManager inputMethodManager;
    BookdocBookSearchAdapter bookdocBookSearchAdapter;
    LinearLayoutManager linearLayoutManager;
    BookdocProgressDialog bookdocProgressDialog;
    boolean singleline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_book_search);

        singleline = getIntent().getBooleanExtra("singleline", false);

        bookdocProgressDialog = new BookdocProgressDialog(this);

        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        bookdocBookSearchEdit = (EditText) findViewById(R.id.bookdoc_book_search_edit);
        bookdocBookSearchEdit.setOnEditorActionListener(this);

        bookdocBookSearchRecyclerView = (RecyclerView) findViewById(R.id.bookdoc_book_search_rv);
        linearLayoutManager = new LinearLayoutManager(BookdocApplication.getBookdocContext(), LinearLayoutManager.VERTICAL, false);
        bookdocBookSearchRecyclerView.setLayoutManager(linearLayoutManager);
        bookdocBookSearchAdapter = new BookdocBookSearchAdapter();
        bookdocBookSearchRecyclerView.setAdapter(bookdocBookSearchAdapter);
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        if(i == EditorInfo.IME_ACTION_DONE){
            bookdocBookSearchAdapter.removeItems();
            inputMethodManager.hideSoftInputFromWindow(bookdocBookSearchEdit.getWindowToken(), 0);
            new BookdocBookSearchAsyncTask().execute(bookdocBookSearchEdit.getText().toString());
            handled = true;
        }
        return handled;
    }

    public class BookdocBookSearchAdapter extends RecyclerView.Adapter<BookdocBookSearchAdapter.BookdocBookSearchViewHolder>{
        ArrayList<DaumBookData> daumBookDatas = new ArrayList<>();
        private final int SEARCH_BOOK_REQUEST_CODE = 0;

        public BookdocBookSearchAdapter() {
        }

        @Override
        public BookdocBookSearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookdoc_book_search_item, parent, false);
            return new BookdocBookSearchViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookdocBookSearchViewHolder holder, int position) {
            final DaumBookData daumBookData = daumBookDatas.get(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.bookdocBookSearchBookTitle.setText(Html.fromHtml(Html.fromHtml(daumBookData.getTitle(),Html.FROM_HTML_MODE_LEGACY).toString(),Html.FROM_HTML_MODE_LEGACY));
                holder.bookdocBookSearchBookAuthor.setText(Html.fromHtml(Html.fromHtml(daumBookData.getAuthor(),Html.FROM_HTML_MODE_LEGACY).toString(),Html.FROM_HTML_MODE_LEGACY));
            }else{
                holder.bookdocBookSearchBookTitle.setText(Html.fromHtml(Html.fromHtml(daumBookData.getTitle()).toString()));
                holder.bookdocBookSearchBookAuthor.setText(Html.fromHtml(Html.fromHtml(daumBookData.getAuthor()).toString()));
            }
            holder.bookdocBookSearchBookPublisher.setText(daumBookData.getPub_nm());
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(daumBookData.getCover_l_url())
                    .into(holder.bookdocBookSearchBookImg);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(singleline){
                        Intent bookInfoSingleLine = new Intent(BookdocApplication.getBookdocContext(), BookdocCreateSimplePrescriptionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bookInfoBundle", daumBookData);
                        bookInfoSingleLine.putExtra("bookinfo",bundle);
                        startActivity(bookInfoSingleLine);
                    }else{
                        Intent bookInfoIntent = new Intent(BookdocApplication.getBookdocContext(), BookdocCreatePrescriptionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("bookInfoBundle", daumBookData);
                        bookInfoIntent.putExtra("bookinfo",bundle);
                        setResult(SEARCH_BOOK_REQUEST_CODE, bookInfoIntent);
                    }
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return daumBookDatas.size();
        }

        public void addItems(ArrayList<DaumBookData> daumBookDatas){
            this.daumBookDatas.addAll(daumBookDatas);

            notifyDataSetChanged();
        }

        public void removeItems(){
            daumBookDatas.clear();
            notifyDataSetChanged();
        }

        public class BookdocBookSearchViewHolder extends RecyclerView.ViewHolder{
            CustomImageView bookdocBookSearchBookImg;
            TextView bookdocBookSearchBookTitle;
            TextView bookdocBookSearchBookAuthor;
            TextView bookdocBookSearchBookPublisher;
            View view;

            Typeface typeNotoSansL = Typeface.createFromAsset(getAssets(), "NotoSansKR_Light_Hestia.otf");

            public BookdocBookSearchViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                bookdocBookSearchBookImg = (CustomImageView) itemView.findViewById(R.id.bookdoc_book_search_item_book_img);
                bookdocBookSearchBookTitle = (TextView) itemView.findViewById(R.id.bookdoc_book_search_book_title);
                bookdocBookSearchBookTitle.setTypeface(typeNotoSansL);
                bookdocBookSearchBookAuthor = (TextView) itemView.findViewById(R.id.bookdoc_book_search_book_author);
                bookdocBookSearchBookAuthor.setTypeface(typeNotoSansL);
                bookdocBookSearchBookPublisher = (TextView) itemView.findViewById(R.id.bookdoc_book_search_book_publisher);
                bookdocBookSearchBookPublisher.setTypeface(typeNotoSansL);

            }
        }
    }

    public class BookdocBookSearchAsyncTask extends AsyncTask<String, String, DaumBookDatas>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.show();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(),message  ,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected DaumBookDatas doInBackground(String... strings) {
            String keyword = strings[0];
            Response response = null;
            OkHttpClient toServer;
            DaumBookDatas daumBookDatas = new DaumBookDatas();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_DAUM_API, keyword))
                        .header("Accept", "application/json")
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("daumdata", "daumdata: "+responseMessage);
                if(response.isSuccessful()){
                    daumBookDatas.setData(new JSONObject(responseMessage));
                }else{
                    Log.e("요청에러", response.message().toString());
                }

            }catch(Exception e){
                Log.e("파싱에러", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요");
            }finally{
                if(response != null){
                    response.close();
                }
            }
            return daumBookDatas;
        }

        @Override
        protected void onPostExecute(DaumBookDatas daumBookDatas) {
            super.onPostExecute(daumBookDatas);
            try{
                bookdocBookSearchAdapter.addItems(daumBookDatas.getDaumBookDatas());
            }catch (Exception e){

            }
            if(bookdocProgressDialog.isShowing()){
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
