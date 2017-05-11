package com.kr.bookdoc.bookdoc.bookdocmainfragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.BookdocBookPrescriptionListActivity;
import com.kr.bookdoc.bookdoc.BookdocCreatePrescriptionActivity;
import com.kr.bookdoc.bookdoc.BookdocPropertyManager;
import com.kr.bookdoc.bookdoc.BookdocRequestPrescriptionActivity;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocPrescriptionRequestDialog;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocDateFormat;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocLoadMoreScrollListener;
import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.RequestAdd;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.RequestEdit;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question.Question;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RequestPrescriptionFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView requestPrescriptionContainerRecyclerView;
    LinearLayoutManager linearLayoutManager;
    RequestPrescriptionAdapter requestPrescriptionAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    BookdocProgressDialog bookdocProgressDialog;
    Typeface typefaceNotoM,typefaceNotoR,typefaceNotoD;

    private BookdocLoadMoreScrollListener bookdocLoadMoreScrollListener;

    public RequestPrescriptionFragment() {
    }

    public static RequestPrescriptionFragment newInstance(){
        RequestPrescriptionFragment requestPrescriptionFragment = new RequestPrescriptionFragment();
        return requestPrescriptionFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.fragment_request_prescription, container, false);
        bookdocProgressDialog = new BookdocProgressDialog(getContext());
        requestPrescriptionContainerRecyclerView = (RecyclerView) view.findViewById(R.id.request_prescription_container_rv);
        linearLayoutManager = new LinearLayoutManager(BookdocApplication.getBookdocContext(), LinearLayoutManager.VERTICAL, false);
        requestPrescriptionContainerRecyclerView.setLayoutManager(linearLayoutManager);
        requestPrescriptionAdapter = new RequestPrescriptionAdapter();
        requestPrescriptionContainerRecyclerView.setAdapter(requestPrescriptionAdapter);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.request_prescription_swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        typefaceNotoM =Typeface.createFromAsset(getContext().getAssets(), "notokr-medium.ttf"); ;
        typefaceNotoR =Typeface.createFromAsset(getContext().getAssets(), "notokr-regular.ttf"); ;
        typefaceNotoD =Typeface.createFromAsset(getContext().getAssets(), "notokr-demilight.ttf"); ;


        bookdocLoadMoreScrollListener = new BookdocLoadMoreScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
            }

            @Override
            public void onLoadMore(int page) {
                int skip = (page - 1) * 9;
                new BookQuestionAsyncTask().execute(skip, 9);
            }

            @Override
            public void setVisibleTopButton() {

            }

            @Override
            public void setInvisibleTopButton() {

            }
        };
        requestPrescriptionContainerRecyclerView.addOnScrollListener(bookdocLoadMoreScrollListener);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new BookQuestionAsyncTask().execute(0, 9);
    }

    @Override
    public void onRefresh() {
        bookdocLoadMoreScrollListener.initData();
        requestPrescriptionAdapter.removeItems();
        new BookQuestionAsyncTask().execute(0,9);
        swipeRefreshLayout.setRefreshing(false);
    }


    private class RequestPrescriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int CREATE_PRESCRIPTON_ITEM = 0;
        private final int REQUEST_PRESCRIPTON_ITEM = 1;

        ArrayList<com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question.Data> questionData = new ArrayList<>();


        public RequestPrescriptionAdapter() {
        }

        @Override
        public int getItemViewType(int position) {
            if (CREATE_PRESCRIPTON_ITEM == position) {
                return CREATE_PRESCRIPTON_ITEM;
            } else {
                return REQUEST_PRESCRIPTON_ITEM;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            switch (viewType) {
                case CREATE_PRESCRIPTON_ITEM:
                    View createPrescriptionItem = inflater.inflate(R.layout.create_prescription_item, parent, false);
                    viewHolder = new CreatePrescriptionViewHolder(createPrescriptionItem);
                    break;
                case REQUEST_PRESCRIPTON_ITEM:
                    View requestPrescriptionItem = inflater.inflate(R.layout.request_prescription_item, parent, false);
                    viewHolder = new RequestPrescriptionViewHolder(requestPrescriptionItem);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            switch (holder.getItemViewType()) {
                case CREATE_PRESCRIPTON_ITEM:
                    CreatePrescriptionViewHolder createPrescriptionViewHolder = (CreatePrescriptionViewHolder) holder;
                    createPrescriptionViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(BookdocApplication.getBookdocContext(), BookdocRequestPrescriptionActivity.class);
                            getActivity().startActivity(intent);
                        }
                    });
                    break;
                case REQUEST_PRESCRIPTON_ITEM:
                    final RequestPrescriptionViewHolder requestPrescriptionViewHolder = (RequestPrescriptionViewHolder) holder;
                    final com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question.Data mData = questionData.get(position - 1);
                    int userWatches = mData.getUserWatches();
                    if(userWatches == 0) {
                        requestPrescriptionViewHolder.wonderPrescriptionFill.setVisibility(View.GONE);
                        requestPrescriptionViewHolder.wonderPrescription.setVisibility(View.VISIBLE);
                    }else if(userWatches == 1){
                        requestPrescriptionViewHolder.wonderPrescription.setVisibility(View.GONE);
                        requestPrescriptionViewHolder.wonderPrescriptionFill.setVisibility(View.VISIBLE);
                    }
                    Glide.with(BookdocApplication.getBookdocContext())
                            .load(mData.getUserImagePath())
                            .error(R.drawable.profile_default)
                            .into(requestPrescriptionViewHolder.bookQuestionUserImage);
                    requestPrescriptionViewHolder.bookQuestionUserName.setText(mData.getUserName());
                    requestPrescriptionViewHolder.bookQuestionCreatedDate.setText(BookdocDateFormat.setDateFormat("yyyy-MM-dd'T'HH:mm:ss", "yyyy.MM.dd", mData.getCreated()));
                    requestPrescriptionViewHolder.bookQuestionDescription.setText(mData.getDescription());
                    requestPrescriptionViewHolder.bookQuestionPrescribedCount.setText("처방 수 "+String.valueOf(mData.getPrescribed())+"개");
                    requestPrescriptionViewHolder.bookQuestionPrescribedCount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent prescriptionForRequestIntent = new Intent(getContext(), BookdocBookPrescriptionListActivity.class);
                            prescriptionForRequestIntent.putExtra("questionId", mData.getId());
                            getContext().startActivity(prescriptionForRequestIntent);
                        }
                    });
                    requestPrescriptionViewHolder.bookQuestionWatchedCount.setText(String.valueOf(mData.getWatched()));
                    requestPrescriptionViewHolder.bookQuestionWatchedCountFill.setText(String.valueOf(mData.getWatched()));
                    requestPrescriptionViewHolder.postPrescription.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent postPrscriptionIntent = new Intent(BookdocApplication.getBookdocContext(), BookdocCreatePrescriptionActivity.class);
                            postPrscriptionIntent.putExtra("prescriptionForRequest", true);
                            postPrscriptionIntent.putExtra("questionId", mData.getId());
                            getActivity().startActivity(postPrscriptionIntent);
                        }
                    });
                    requestPrescriptionViewHolder.wonderPrescription.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final int watched = setWatcher(mData.getId(), mData.getWatched());
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            requestPrescriptionViewHolder.wonderPrescription.setVisibility(View.GONE);
                                            requestPrescriptionViewHolder.wonderPrescriptionFill.setVisibility(View.VISIBLE);
                                            requestPrescriptionViewHolder.bookQuestionWatchedCountFill.setText(String.valueOf(watched));
                                            mData.setUserWatches(1);
                                            mData.setWatched(watched);
                                        }
                                    });
                                }
                            }).start();
                        }
                    });
                    requestPrescriptionViewHolder.wonderPrescriptionFill.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final int watched = cancleWatcher(mData.getId(), mData.getWatched());
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            requestPrescriptionViewHolder.wonderPrescriptionFill.setVisibility(View.GONE);
                                            requestPrescriptionViewHolder.wonderPrescription.setVisibility(View.VISIBLE);
                                            requestPrescriptionViewHolder.bookQuestionWatchedCount.setText(String.valueOf(watched));
                                            mData.setUserWatches(0);
                                            mData.setWatched(watched);
                                        }
                                    });
                                }
                            }).start();
                        }
                    });
                    requestPrescriptionViewHolder.bookQuestionMoreinfoBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BookdocPrescriptionRequestDialog bookdocPrescriptionRequestDialog = BookdocPrescriptionRequestDialog.newInstances(mData.getId(), mData.getDescription(), position, false);
                            bookdocPrescriptionRequestDialog.setOnDeleteRequestListener(new BookdocPrescriptionRequestDialog.OnDeleteRequestListener() {
                                @Override
                                public void onDeleteRequest(int requestId, int position) {
                                    new BookdocDeleteRequestAsyncTask().execute(mData.getId(), position);
                                }
                            });
                            bookdocPrescriptionRequestDialog.show(getActivity().getSupportFragmentManager(), "request prescription");
                        }
                    });
                    if(mData.getUserId() != BookdocPropertyManager.getInstance().getUserId()){
                        requestPrescriptionViewHolder.bookQuestionMoreinfoBtn.setVisibility(View.GONE);
                    }else{
                        requestPrescriptionViewHolder.bookQuestionMoreinfoBtn.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return questionData.size();
        }

        public void addItems(ArrayList<com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question.Data> datas) {
            questionData.addAll(datas);
            notifyDataSetChanged();
        }

        public void addItem(Data data) {
            questionData.add(0 ,data);
            notifyItemInserted(1);
        }

        public void updateItem(Data data, int position){
            questionData.set(position-1, data);
            notifyItemChanged(position);
        }

        public void removeItem(int position){
            questionData.remove(position-1);
            notifyItemRemoved(position);
        }

        public void removeItems(){
            questionData.clear();
            notifyDataSetChanged();
        }

        private class RequestPrescriptionViewHolder extends RecyclerView.ViewHolder {
            LinearLayout postPrescription, wonderPrescription, wonderPrescriptionFill;
            ImageView bookQuestionMoreinfoBtn;
            CircleImageView bookQuestionUserImage;
            TextView bookQuestionUserName, bookQuestionCreatedDate,
                    bookQuestionDescription, bookQuestionPrescribedCount,
                    bookQuestionWatchedCount, bookQuestionWatchedCountFill;

            public RequestPrescriptionViewHolder(View itemView) {
                super(itemView);
                postPrescription = (LinearLayout) itemView.findViewById(R.id.request_prescription_post_prescription);
                wonderPrescription = (LinearLayout) itemView.findViewById(R.id.request_prescription_wonder);
                wonderPrescriptionFill = (LinearLayout) itemView.findViewById(R.id.request_prescription_wonder_fill);
                bookQuestionUserImage = (CircleImageView) itemView.findViewById(R.id.imageview_question_user);
                bookQuestionUserName = (TextView) itemView.findViewById(R.id.textview_question_name);
                bookQuestionUserName.setTypeface(typefaceNotoM);
                bookQuestionCreatedDate = (TextView) itemView.findViewById(R.id.textview_question_date);
                bookQuestionCreatedDate.setTypeface(typefaceNotoR);

                bookQuestionDescription = (TextView) itemView.findViewById(R.id.textview_question_description);
                bookQuestionDescription.setTypeface(typefaceNotoD);

                bookQuestionPrescribedCount = (TextView) itemView.findViewById(R.id.textview_question_prescribed);
                bookQuestionPrescribedCount.setTypeface(typefaceNotoD);

                bookQuestionWatchedCount = (TextView) itemView.findViewById(R.id.textview_question_watched);
                bookQuestionWatchedCount.setTypeface(typefaceNotoR);

                bookQuestionWatchedCountFill = (TextView) itemView.findViewById(R.id.textview_question_watched_fill);
                bookQuestionWatchedCountFill.setTypeface(typefaceNotoR);

                bookQuestionMoreinfoBtn = (ImageView) itemView.findViewById(R.id.imageview_question_editbtn);

            }
        }

        private class CreatePrescriptionViewHolder extends RecyclerView.ViewHolder {
            LinearLayout linearLayout;

            public CreatePrescriptionViewHolder(View itemView) {
                super(itemView);
                linearLayout = (LinearLayout) itemView.findViewById(R.id.create_prescription);
            }
        }
    }

    public class BookQuestionAsyncTask extends AsyncTask<Integer, String, Question> {

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
        protected Question doInBackground(Integer... integers) {
            int skip = integers[0];
            int count = integers[1];
            Response response = null;
            OkHttpClient toServer;
            Question question = new Question();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_QUESTION, skip, count))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                if (response.isSuccessful()) {
                    Log.d("RequestPrescription","RequestPrescription: "+ responseMessage);
                    question.setData(new JSONObject(responseMessage));
                } else {
                    Log.e("요청에러", response.message().toString());
                }

            } catch (Exception e) {
                Log.e("파싱에러", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요");
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return question;
        }

        @Override
        public void onPostExecute(Question question) {
            super.onPostExecute(question);
            try {
                requestPrescriptionAdapter.addItems(question.getData());
            }catch (Exception e){

            }
            if(bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.dismiss();
            }
        }
    }


    public class BookdocDeleteRequestAsyncTask extends AsyncTask<Integer, String, Integer>{

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(),message  ,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            int requestId = integers[0];
            int position = integers[1];
            Response response = null;
            OkHttpClient toServer;

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("userId", String.valueOf(BookdocPropertyManager.getInstance().getUserId()))
                        .build();


                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_DELETE_QUESTION, requestId))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .delete(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.e("success", jsonObject.optString("message"));
                } else {
                    Log.e("요청에러", response.message().toString());
                }

            } catch (Exception e) {
                Log.e("파싱에러", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요");
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return position;
        }

        @Override
        protected void onPostExecute(Integer position) {
            super.onPostExecute(position);
            try {
                requestPrescriptionAdapter.removeItem(position);
            }catch (Exception e){

            }
        }
    }

    public int setWatcher(int requestId, int watchedCount){
        Response response = null;
        OkHttpClient toServer;
        int watched = watchedCount;
        try {
            toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", String.valueOf(1))
                    .build();

            Request request = new Request.Builder()
                    .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_WATCHER, requestId))
                    .header("Accept", "application/json")
                    .addHeader("token" , BookdocPropertyManager.getInstance().getAccessToken())
                    .post(requestBody)
                    .build();

            response = toServer.newCall(request).execute();
            String responseMessage = response.body().string();
            if (response.isSuccessful()) {
                Log.d("responseMessage","responseMessage: "+responseMessage);
                JSONObject jsonObject = new JSONObject(responseMessage);
                JSONObject dataJsonObject = jsonObject.optJSONObject("data");
                watched = dataJsonObject.optInt("watched");
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
        return watched;
    }

    private int cancleWatcher(int requestId, int watchedCount){
        Response response = null;
        OkHttpClient toServer;
        int watched = watchedCount;
        try {
            toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

            RequestBody requestBody = new FormBody.Builder()
                    .add("userId", String.valueOf(1))
                    .build();


            Request request = new Request.Builder()
                    .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_REQUEST_WATCHER, requestId))
                    .header("Accept", "application/json")
                    .addHeader("token" , BookdocPropertyManager.getInstance().getAccessToken())
                    .delete(requestBody)
                    .build();

            response = toServer.newCall(request).execute();
            String responseMessage = response.body().string();
            if (response.isSuccessful()) {
                Log.d("responseMessage","responseMessage: "+responseMessage);
                JSONObject jsonObject = new JSONObject(responseMessage);
                watched = jsonObject.optJSONObject("data").optInt("watched");
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
        return watched;
    }

    @Subscribe
    public void addRequestToAdapter(RequestAdd requestAdd){
        requestPrescriptionAdapter.addItem(requestAdd.getData());
    }

    @Subscribe
    public void updateRequestToAdapter(RequestEdit requestEdit){
        requestPrescriptionAdapter.updateItem(requestEdit.getData(), requestEdit.getPosition());
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
