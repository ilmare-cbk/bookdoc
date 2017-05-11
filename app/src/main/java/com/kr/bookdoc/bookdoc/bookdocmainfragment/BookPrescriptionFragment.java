package com.kr.bookdoc.bookdoc.bookdocmainfragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.kr.bookdoc.bookdoc.BookdocBookPrescriptionDetailActivity;
import com.kr.bookdoc.bookdoc.BookdocCreatePrescriptionActivity;
import com.kr.bookdoc.bookdoc.BookdocPropertyManager;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocDateFormat;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocImageList;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocLoadMoreScrollListener;
import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.bookdocutils.CustomImageView;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.BookPrescriptionAdd;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.BookPrescriptionDel;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Prescription;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.PrescriptionPost;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;


public class BookPrescriptionFragment extends Fragment implements View.OnClickListener, OnRefreshListener{
    RecyclerView bookPrescriptionItemContainerRecyclerView;
    BookPrescriptionAdapter bookPrescriptionAdapter;
    LinearLayoutManager linearLayoutManager;
    Button categoryBtnAll, categoryBtnSelfImprovement, categotyBtnLiberal,
            categoryBtnHistory, categoryBtnIt, categoryBtnPolitics,
            categoryBtnEconomic, categoryBtnArt;
    FloatingActionButton bookPrescriptionCreateFab;
    FloatingActionButton bookPrescriptionTopFab;
    String category;
    SwipeRefreshLayout swipeRefreshLayout;
    BookdocProgressDialog bookdocProgressDialog;
    Typeface typefaceNanumR,typefaceNotosansM;
    int questionId;
    private BookdocLoadMoreScrollListener bookdocLoadMoreScrollListener;

    public BookPrescriptionFragment() {
    }

    public static BookPrescriptionFragment newInstance() {
        BookPrescriptionFragment bookPrescriptionFragment = new BookPrescriptionFragment();
        return bookPrescriptionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.fragment_book_prescription, container, false);

        bookdocProgressDialog = new BookdocProgressDialog(getContext());

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.book_prescription_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        bookPrescriptionCreateFab = (FloatingActionButton) view.findViewById(R.id.book_prescription_create_fab);
        bookPrescriptionCreateFab.setOnClickListener(this);
        bookPrescriptionTopFab = (FloatingActionButton) view.findViewById(R.id.book_prescription_top_fb);
        bookPrescriptionTopFab.setOnClickListener(this);

        typefaceNanumR =Typeface.createFromAsset(getContext().getAssets(), "NanumBarunGothic.ttf"); ;
        typefaceNotosansM =Typeface.createFromAsset(getContext().getAssets(), "NotoSansKR_Medium_Hestia.otf"); ;

        bookPrescriptionItemContainerRecyclerView = (RecyclerView) view.findViewById(R.id.book_prescription_item_container_rv);
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        bookPrescriptionAdapter = new BookPrescriptionAdapter();
        bookPrescriptionItemContainerRecyclerView.setLayoutManager(linearLayoutManager);
        bookPrescriptionItemContainerRecyclerView.setAdapter(bookPrescriptionAdapter);
        setCategoryBtn(view);
        bookdocLoadMoreScrollListener = new BookdocLoadMoreScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
            }

            @Override
            public void onLoadMore(int page) {
                int skip = (page-1)*9;
                if(category == null || category.length() == 0 || category.equals("")){
                    new BookPrescriptionAsyncTask().execute(skip,9);
                }else{
                    new BookPrescriptionByCategoryAsyncTask().execute(skip,9);
                }
            }

            @Override
            public void setVisibleTopButton() {
                bookPrescriptionCreateFab.setVisibility(View.GONE);
                bookPrescriptionTopFab.setVisibility(View.VISIBLE);
            }

            @Override
            public void setInvisibleTopButton() {
                bookPrescriptionTopFab.setVisibility(View.GONE);
                bookPrescriptionCreateFab.setVisibility(View.VISIBLE);
            }
        };
        bookPrescriptionItemContainerRecyclerView.addOnScrollListener(bookdocLoadMoreScrollListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new BookPrescriptionAsyncTask().execute(0,9);
    }


    private void setCategoryBtn(View view){
        categoryBtnAll = (Button) view.findViewById(R.id.category_btn_1);
        categoryBtnAll.setBackgroundResource(R.drawable.all_1);
        categoryBtnSelfImprovement = (Button) view.findViewById(R.id.category_btn_2);
        categotyBtnLiberal = (Button) view.findViewById(R.id.category_btn_3);
        categoryBtnHistory = (Button) view.findViewById(R.id.category_btn_4);
        categoryBtnPolitics = (Button) view.findViewById(R.id.category_btn_5);
        categoryBtnEconomic = (Button) view.findViewById(R.id.category_btn_6);
        categoryBtnIt = (Button) view.findViewById(R.id.category_btn_7);
        categoryBtnArt = (Button) view.findViewById(R.id.category_btn_8);
        categoryBtnAll.setOnClickListener(this);
        categoryBtnSelfImprovement.setOnClickListener(this);
        categotyBtnLiberal.setOnClickListener(this);
        categoryBtnHistory.setOnClickListener(this);
        categoryBtnIt.setOnClickListener(this);
        categoryBtnPolitics.setOnClickListener(this);
        categoryBtnEconomic.setOnClickListener(this);
        categoryBtnArt.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.category_btn_1:
            case R.id.category_btn_2:
            case R.id.category_btn_3:
            case R.id.category_btn_4:
            case R.id.category_btn_5:
            case R.id.category_btn_6:
            case R.id.category_btn_7:
            case R.id.category_btn_8:
                toggleCategoryButton(id);
                bookPrescriptionAdapter.removeItems();
                if(category == null || category.length() == 0 || category.equals("")){
                    new BookPrescriptionAsyncTask().execute(0,9);
                }else{
                    new BookPrescriptionByCategoryAsyncTask().execute(0,9);
                }
                break;
            case R.id.book_prescription_create_fab:
                Intent intent = new Intent(getContext(), BookdocCreatePrescriptionActivity.class);
                startActivity(intent);
                break;
            case R.id.book_prescription_top_fb:
                bookPrescriptionItemContainerRecyclerView.smoothScrollToPosition(0);
                break;
        }

    }

    private void toggleCategoryButton(int id){
        bookdocLoadMoreScrollListener.initData();
        switch (id){
            case R.id.category_btn_1:
                categoryBtnAll.setBackgroundResource(R.drawable.all_1);
                categoryBtnSelfImprovement.setBackgroundResource(R.drawable.selfimprovement_2_1);
                categotyBtnLiberal.setBackgroundResource(R.drawable.liberal_3_1);
                categoryBtnHistory.setBackgroundResource(R.drawable.history_4_1);
                categoryBtnPolitics.setBackgroundResource(R.drawable.politics_5_1);
                categoryBtnEconomic.setBackgroundResource(R.drawable.economic_6_1);
                categoryBtnIt.setBackgroundResource(R.drawable.it_7_1);
                categoryBtnArt.setBackgroundResource(R.drawable.art_8_1);
                category = "";
                break;
            case R.id.category_btn_2:
                categoryBtnAll.setBackgroundResource(R.drawable.all_1_1);
                categoryBtnSelfImprovement.setBackgroundResource(R.drawable.selfimprovement_2);
                categotyBtnLiberal.setBackgroundResource(R.drawable.liberal_3_1);
                categoryBtnHistory.setBackgroundResource(R.drawable.history_4_1);
                categoryBtnPolitics.setBackgroundResource(R.drawable.politics_5_1);
                categoryBtnEconomic.setBackgroundResource(R.drawable.economic_6_1);
                categoryBtnIt.setBackgroundResource(R.drawable.it_7_1);
                categoryBtnArt.setBackgroundResource(R.drawable.art_8_1);
                category = getResources().getString(R.string.second_category);
                break;
            case R.id.category_btn_3:
                categoryBtnAll.setBackgroundResource(R.drawable.all_1_1);
                categoryBtnSelfImprovement.setBackgroundResource(R.drawable.selfimprovement_2_1);
                categotyBtnLiberal.setBackgroundResource(R.drawable.liberal_3);
                categoryBtnHistory.setBackgroundResource(R.drawable.history_4_1);
                categoryBtnPolitics.setBackgroundResource(R.drawable.politics_5_1);
                categoryBtnEconomic.setBackgroundResource(R.drawable.economic_6_1);
                categoryBtnIt.setBackgroundResource(R.drawable.it_7_1);
                categoryBtnArt.setBackgroundResource(R.drawable.art_8_1);
                category = getResources().getString(R.string.third_category);
                break;
            case R.id.category_btn_4:
                categoryBtnAll.setBackgroundResource(R.drawable.all_1_1);
                categoryBtnSelfImprovement.setBackgroundResource(R.drawable.selfimprovement_2_1);
                categotyBtnLiberal.setBackgroundResource(R.drawable.liberal_3_1);
                categoryBtnHistory.setBackgroundResource(R.drawable.history_4);
                categoryBtnPolitics.setBackgroundResource(R.drawable.politics_5_1);
                categoryBtnEconomic.setBackgroundResource(R.drawable.economic_6_1);
                categoryBtnIt.setBackgroundResource(R.drawable.it_7_1);
                categoryBtnArt.setBackgroundResource(R.drawable.art_8_1);
                category = getResources().getString(R.string.fourth_category);
                break;
            case R.id.category_btn_5:
                categoryBtnAll.setBackgroundResource(R.drawable.all_1_1);
                categoryBtnSelfImprovement.setBackgroundResource(R.drawable.selfimprovement_2_1);
                categotyBtnLiberal.setBackgroundResource(R.drawable.liberal_3_1);
                categoryBtnHistory.setBackgroundResource(R.drawable.history_4_1);
                categoryBtnPolitics.setBackgroundResource(R.drawable.politics_5);
                categoryBtnEconomic.setBackgroundResource(R.drawable.economic_6_1);
                categoryBtnIt.setBackgroundResource(R.drawable.it_7_1);
                categoryBtnArt.setBackgroundResource(R.drawable.art_8_1);
                category = getResources().getString(R.string.fifth_category);
                break;
            case R.id.category_btn_6:
                categoryBtnAll.setBackgroundResource(R.drawable.all_1_1);
                categoryBtnSelfImprovement.setBackgroundResource(R.drawable.selfimprovement_2_1);
                categotyBtnLiberal.setBackgroundResource(R.drawable.liberal_3_1);
                categoryBtnHistory.setBackgroundResource(R.drawable.history_4_1);
                categoryBtnPolitics.setBackgroundResource(R.drawable.politics_5_1);
                categoryBtnEconomic.setBackgroundResource(R.drawable.economic_6);
                categoryBtnIt.setBackgroundResource(R.drawable.it_7_1);
                categoryBtnArt.setBackgroundResource(R.drawable.art_8_1);
                category = getResources().getString(R.string.sixth_category);
                break;
            case R.id.category_btn_7:
                categoryBtnAll.setBackgroundResource(R.drawable.all_1_1);
                categoryBtnSelfImprovement.setBackgroundResource(R.drawable.selfimprovement_2_1);
                categotyBtnLiberal.setBackgroundResource(R.drawable.liberal_3_1);
                categoryBtnHistory.setBackgroundResource(R.drawable.history_4_1);
                categoryBtnPolitics.setBackgroundResource(R.drawable.politics_5_1);
                categoryBtnEconomic.setBackgroundResource(R.drawable.economic_6_1);
                categoryBtnIt.setBackgroundResource(R.drawable.it_7);
                categoryBtnArt.setBackgroundResource(R.drawable.art_8_1);
                category = getResources().getString(R.string.seventh_category);
                break;
            case R.id.category_btn_8:
                categoryBtnAll.setBackgroundResource(R.drawable.all_1_1);
                categoryBtnSelfImprovement.setBackgroundResource(R.drawable.selfimprovement_2_1);
                categotyBtnLiberal.setBackgroundResource(R.drawable.liberal_3_1);
                categoryBtnHistory.setBackgroundResource(R.drawable.history_4_1);
                categoryBtnPolitics.setBackgroundResource(R.drawable.politics_5_1);
                categoryBtnEconomic.setBackgroundResource(R.drawable.economic_6_1);
                categoryBtnIt.setBackgroundResource(R.drawable.it_7_1);
                categoryBtnArt.setBackgroundResource(R.drawable.art_8);
                category = getResources().getString(R.string.eighth_category);
                break;
        }
    }

    @Override
    public void onRefresh() {
        bookdocLoadMoreScrollListener.initData();
        bookPrescriptionAdapter.removeItems();
        if(category == null || category.length() == 0 || category.equals("")){
            new BookPrescriptionAsyncTask().execute(0,9);
        }else{
            new BookPrescriptionByCategoryAsyncTask().execute(0,9);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    public class BookPrescriptionAdapter extends RecyclerView.Adapter<BookPrescriptionAdapter.BookPrescriptionViewHolder>{
        ArrayList<Data> prescriptionData = new ArrayList<>();

        public BookPrescriptionAdapter() {
        }

        @Override
        public BookPrescriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.book_prescription_item, parent, false);
            return new BookPrescriptionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final BookPrescriptionViewHolder holder, final int position) {
            try{
                if(prescriptionData.size() > 0){
                    final Data mData = prescriptionData.get(position);
                    if(mData != null){
                        if(mData.getImageType() != 0){
                            Glide.with(BookdocApplication.getBookdocContext())
                                    .load(BookdocImageList.getBookdocImage(mData.getImageType()).intValue())
                                    .placeholder(R.drawable.prescription_placeholder)
                                    .error(R.drawable.prescription_error)
                                    .into(holder.bookPrescriptionItemBackground);
                        }else{
                            Glide.with(BookdocApplication.getBookdocContext())
                                    .load(mData.getImagePath())
                                    .placeholder(R.drawable.prescription_placeholder)
                                    .error(R.drawable.prescription_error)
                                    .into(holder.bookPrescriptionItemBackground);
                        }
                        Glide.with(BookdocApplication.getBookdocContext())
                                .load(mData.getBookImagePath())
                                .error(R.drawable.mybook_error)
                                .into(holder.bookPrescriptionBookImage);
                        holder.bookPrescriptionItemTitle.setText(mData.getTitle());
                        holder.bookPrescriptionItemUserName.setText("by."+mData.getUserName());
                        holder.bookPrescriptionItemLikeCount.setText(String.valueOf(mData.getLiked()));
                        holder.bookPrescriptionItemCommentCount.setText(String.valueOf(mData.getCommented()));
                        holder.bookPrescriptionItemTime.setText(BookdocDateFormat.setDateFormat("yyyy-MM-dd'T'HH:mm:ss", "yyyy.MM.dd", mData.getCreated()));
                        holder.bookPrescriptionItemCardView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(BookdocApplication.getBookdocContext(), BookdocBookPrescriptionDetailActivity.class);
                                intent.putExtra("prescriptionId",mData.getId());
                                intent.putExtra("position",position);
                                intent.putExtra("bookId",mData.getBookId());
                                getActivity().startActivity(intent);
                            }
                        });
                    }
                }
            }catch (Exception e){

            }
        }

        @Override
        public int getItemCount() {
            return prescriptionData.size();
        }

        public void addItems(ArrayList<Data> datas){
            prescriptionData.addAll(datas);
            notifyDataSetChanged();
        }

        public void addItem(Data data){
            prescriptionData.add(0,data);
            notifyItemInserted(0);
        }

        public void removeItems(){
            prescriptionData.clear();
            notifyDataSetChanged();
        }

        public void removeItem(int position){
            prescriptionData.remove(position);
            notifyItemRangeChanged(position, prescriptionData.size());
        }

        public class BookPrescriptionViewHolder extends RecyclerView.ViewHolder {
            CardView bookPrescriptionItemCardView;
            ImageView bookPrescriptionItemBackground;
            CustomImageView bookPrescriptionBookImage;
            TextView bookPrescriptionItemTitle;
            TextView bookPrescriptionItemUserName;
            TextView bookPrescriptionItemLikeCount;
            TextView bookPrescriptionItemCommentCount;
            TextView bookPrescriptionItemTime;

            public BookPrescriptionViewHolder(View itemView) {
                super(itemView);
                bookPrescriptionItemBackground = (ImageView) itemView.findViewById(R.id.book_prescription_item_container);
                bookPrescriptionBookImage = (CustomImageView) itemView.findViewById(R.id.bookdoc_prescription_book_image);
                bookPrescriptionItemCardView = (CardView) itemView.findViewById(R.id.book_prescription_item_cv);
                bookPrescriptionItemTitle = (TextView) itemView.findViewById(R.id.book_prescription_item_title);
                bookPrescriptionItemTitle.setTypeface(typefaceNanumR);
                bookPrescriptionItemUserName = (TextView) itemView.findViewById(R.id.book_prescription_item_username);
                bookPrescriptionItemUserName.setTypeface(typefaceNanumR);
                bookPrescriptionItemLikeCount = (TextView) itemView.findViewById(R.id.book_prescription_item_like_count);
                bookPrescriptionItemLikeCount.setTypeface(typefaceNotosansM);
                bookPrescriptionItemCommentCount = (TextView) itemView.findViewById(R.id.book_prescription_item_comment_count);
                bookPrescriptionItemCommentCount.setTypeface(typefaceNotosansM);
                bookPrescriptionItemTime = (TextView) itemView.findViewById(R.id.book_prescription_item_time);
                bookPrescriptionItemTime.setTypeface(typefaceNotosansM);

            }
        }
    }

    public class BookPrescriptionAsyncTask extends AsyncTask<Integer, String, Prescription>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bookdocProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(),message  ,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Prescription doInBackground(Integer... integers) {
            int skip = integers[0];
            int count = integers[1];
            Log.d("skip & count", "skip: "+skip +" "+"count: "+count);
            Response response = null;
            OkHttpClient toServer;
            Prescription prescription = new Prescription();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_PRESCRIPTION,skip, count))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                if(response.isSuccessful()){
                    Log.d("Prescription","Prescription: "+responseMessage);
                    prescription.setData(new JSONObject(responseMessage));
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
            return prescription;
        }
        @Override
        public void onPostExecute(Prescription prescription) {
            super.onPostExecute(prescription);
            try{
                bookPrescriptionAdapter.addItems(prescription.getData());
            }catch (Exception e){

            }
            if(bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.dismiss();
            }
        }
    }


    public class BookPrescriptionByCategoryAsyncTask extends AsyncTask<Integer, String, Prescription>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bookdocProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(),message  ,Toast.LENGTH_SHORT).show();
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
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_PRESCRIPTION_BY_CATEGORY,skip, count, category))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .build();

                Log.d("Aa",String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_PRESCRIPTION_BY_CATEGORY,skip, count, category));
                response = toServer.newCall(request).execute();
                if(response.isSuccessful()){
                    prescription.setData(new JSONObject(response.body().string()));
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
            return prescription;
        }
        @Override
        public void onPostExecute(Prescription prescription) {
            super.onPostExecute(prescription);
            try {
                bookPrescriptionAdapter.addItems(prescription.getData());
            }catch (Exception e){

            }
            if(bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.dismiss();
            }
        }
    }


    public class BookdocCreatePrescriptionAsyncTask extends AsyncTask<PrescriptionPost, String, Data>{
        int questionId;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            questionId = getQuestionId();
            bookdocProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(),message  ,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Data doInBackground(PrescriptionPost... prescriptionPosts) {
            final MediaType pngType = MediaType.parse("image/*");
            PrescriptionPost prescriptionPost = prescriptionPosts[0];
            Data data = new Data();
            Gson gson = new Gson();
            Response response = null;
            Request request;
            OkHttpClient toServer = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            try{
                RequestBody postBody;
                int imageType = prescriptionPost.getImageType();
                if(imageType != 0){
                    postBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("userId",String.valueOf(BookdocPropertyManager.getInstance().getUserId()))
                            .addFormDataPart("bookIsbn", prescriptionPost.getBookIsbn())
                            .addFormDataPart("title", prescriptionPost.getTitle())
                            .addFormDataPart("description","This description is not used")
                            .addFormDataPart("cards",gson.toJson(prescriptionPost.getCards()))
                            .addFormDataPart("tags", gson.toJson(prescriptionPost.getTags()))
                            .addFormDataPart("imageType", String.valueOf(imageType))
                            .build();
                }else{
                    File file = new File(prescriptionPost.getImage());
                    postBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("userId",String.valueOf(BookdocPropertyManager.getInstance().getUserId()))
                            .addFormDataPart("bookIsbn", prescriptionPost.getBookIsbn())
                            .addFormDataPart("title", prescriptionPost.getTitle())
                            .addFormDataPart("description","This description is not used")
                            .addFormDataPart("cards",gson.toJson(prescriptionPost.getCards()))
                            .addFormDataPart("tags", gson.toJson(prescriptionPost.getTags()))
                            .addFormDataPart("image", file.getName(), RequestBody.create(pngType, file))
                            .build();
                }

                if(questionId == 0){
                    request = new Request.Builder()
                            .url(BookdocNetworkDefineConstant.SERVER_URL_POST_PRESCRIPTION)
                            .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                            .post(postBody)
                            .build();
                }else{
                    request = new Request.Builder()
                            .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_POST_PRESCRIPTION_FOR_QUESTION, questionId))
                            .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                            .post(postBody)
                            .build();
                }

                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                Log.d("json",responsedMessage);
                if( response.isSuccessful()){
                    data = gson.fromJson(responsedMessage, Data.class);
                }else{
                    Log.e("요청에러", response.message().toString());
                }

            }catch (UnknownHostException une) {
                e("aaa", une.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요");
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요");
            } catch (Exception e) {
                e("ccc", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요");
            } finally{
                if(response != null) {
                    response.close();
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(Data data) {
            super.onPostExecute(data);
            try{
                PrescriptionPost.getInstance().initData();
                bookPrescriptionAdapter.addItem(data);
            }catch (Exception e){

            }
            if(bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.dismiss();
            }
        }
    }


    public class BookdocPrescriptionDeleteAsyncTask extends AsyncTask<Integer, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bookdocProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(),message  ,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            int prescriptionId = integers[0];
            int position = integers[1];
            Response response = null;
            OkHttpClient toServer;

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("userId",String.valueOf(BookdocPropertyManager.getInstance().getUserId()))
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_DELETE_PRESCRIPTION, prescriptionId))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .delete(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                if(response.isSuccessful()){
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if(jsonObject.getString("message").equals("success")){
                       Log.d("success","success");
                    }
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
            return position;
        }

        @Override
        protected void onPostExecute(Integer position) {
            super.onPostExecute(position);
            try {
                bookPrescriptionAdapter.removeItem(position);
            }catch (Exception e){

            }
            if(bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.dismiss();
            }
        }
    }

    @Subscribe
    public void deletePrescription(BookPrescriptionDel bookPrescriptionDel){
        int prescriptionId = bookPrescriptionDel.getBookPrescriptionDelId();
        int position = bookPrescriptionDel.getBookPrescriptionDelPosition();
        new BookdocPrescriptionDeleteAsyncTask().execute(prescriptionId, position);
    }

    @Subscribe
    public void addPrescription(BookPrescriptionAdd bookPrescriptionAdd){
        this.questionId = bookPrescriptionAdd.getBookPrescriptionQuestionId();
        new BookdocCreatePrescriptionAsyncTask().execute(PrescriptionPost.getInstance());
    }

    public int getQuestionId(){
        return questionId;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bookdocProgressDialog != null) {
            bookdocProgressDialog.dismiss();
            bookdocProgressDialog = null;
        }
        BusProvider.getInstance().unregister(this);
    }

}
