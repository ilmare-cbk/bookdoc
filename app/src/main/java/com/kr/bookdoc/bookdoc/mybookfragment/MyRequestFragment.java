package com.kr.bookdoc.bookdoc.mybookfragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;

import org.json.JSONObject;

import java.util.ArrayList;

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
import com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question.Question;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MyRequestFragment extends Fragment implements OnRefreshListener {
    private MyRequestAdapter myRequestAdapter;
    private RecyclerView myRequestRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private BookdocProgressDialog bookdocProgressDialog;
    private SwipeRefreshLayout myRequestSwipeRefreshLayout;
    private int anotherUserId;

    public MyRequestFragment() {
    }

    public static MyRequestFragment newInstance(int anotherUserId) {
        MyRequestFragment myRequestFragment = new MyRequestFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("anotherUserId", anotherUserId);
        myRequestFragment.setArguments(bundle);
        return myRequestFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_my_request, container, false);

        anotherUserId = getArguments().getInt("anotherUserId");

        bookdocProgressDialog = new BookdocProgressDialog(getContext());

        myRequestSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.my_request_sp);
        myRequestSwipeRefreshLayout.setOnRefreshListener(this);

        myRequestRecyclerView = (RecyclerView) view.findViewById(R.id.my_request_rv);
        linearLayoutManager = new LinearLayoutManager(BookdocApplication.getBookdocContext(), LinearLayoutManager.VERTICAL, false);
        myRequestAdapter = new MyRequestAdapter();
        myRequestRecyclerView.setLayoutManager(linearLayoutManager);
        myRequestRecyclerView.setAdapter(myRequestAdapter);
        myRequestRecyclerView.addOnScrollListener(new BookdocLoadMoreScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

            }

            @Override
            public void onLoadMore(int page) {
                int skip = (page - 1) * 9;
                new MyRequestAsyncTask().execute(skip, 9, anotherUserId);
            }

            @Override
            public void setVisibleTopButton() {

            }

            @Override
            public void setInvisibleTopButton() {

            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new MyRequestAsyncTask().execute(0, 9, anotherUserId);
    }

    @Override
    public void onRefresh() {
        myRequestAdapter.removeItems();
        new MyRequestAsyncTask().execute(0, 9, anotherUserId);
        myRequestSwipeRefreshLayout.setRefreshing(false);
    }

    private class MyRequestAdapter extends RecyclerView.Adapter<MyRequestAdapter.MyRequestViewHolder> {
        ArrayList<Data> dataArrayList = new ArrayList<>();

        public MyRequestAdapter() {
        }

        @Override
        public MyRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_request_item, parent, false);
            return new MyRequestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyRequestViewHolder holder, final int position) {
            final Data data = dataArrayList.get(position);
            String userImagePath = data.getUserImagePath();
            Glide.with(getContext())
                    .load(userImagePath)
                    .placeholder(R.drawable.profile_default)
                    .error(R.drawable.profile_default)
                    .into(holder.myRequestUserimage);
            holder.myRequestUsername.setText(data.getUserName());
            holder.myRequestCreatedTime.setText(BookdocDateFormat.setDateFormat("yyyy-MM-dd'T'HH:mm:ss", "yyyy.MM.dd", data.getCreated()));
            holder.myRequestContent.setText(data.getDescription());
            holder.myRequestWonderCount.setText("저도 궁금해요 " + data.getWatched() + "개");
            holder.myRequestPrescriptionCount.setText("처방 수 " + data.getPrescribed() + "개");
            if (data.getUserId() != BookdocPropertyManager.getInstance().getUserId()) {
                holder.myReqeustMoreBtn.setVisibility(View.GONE);
            } else {
                holder.myReqeustMoreBtn.setVisibility(View.VISIBLE);
            }
            holder.myReqeustMoreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BookdocPrescriptionRequestDialog bookdocPrescriptionRequestDialog = BookdocPrescriptionRequestDialog.newInstances(data.getId(), data.getDescription(), position, false);
                    bookdocPrescriptionRequestDialog.setOnDeleteRequestListener(new BookdocPrescriptionRequestDialog.OnDeleteRequestListener() {
                        @Override
                        public void onDeleteRequest(int requestId, int position) {
                            new BookdocDeleteRequestAsyncTask().execute(data.getId(), position);
                        }
                    });
                    bookdocPrescriptionRequestDialog.show(getActivity().getSupportFragmentManager(), "request prescription");
                }
            });
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

        public void removeItem(int position) {
            Log.d("position", "position: " + position);
            dataArrayList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());
        }

        public void updateItem(BookdocRequestPrescriptionActivity.QuestionPutValueObject questionPutValueObject) {
            int position = questionPutValueObject.getPosition();
            Data data = dataArrayList.get(position);
            data.setDescription(questionPutValueObject.getDescription());
            dataArrayList.set(position, data);
            notifyItemChanged(position);
        }

        public class MyRequestViewHolder extends RecyclerView.ViewHolder {
            CircleImageView myRequestUserimage;
            TextView myRequestUsername, myRequestCreatedTime, myRequestContent,
                    myRequestWonderCount, myRequestPrescriptionCount;
            ImageView myReqeustMoreBtn;

            public MyRequestViewHolder(View itemView) {
                super(itemView);
                myRequestUserimage = (CircleImageView) itemView.findViewById(R.id.my_request_item_userimage);
                myRequestUsername = (TextView) itemView.findViewById(R.id.my_request_item_username);
                myRequestCreatedTime = (TextView) itemView.findViewById(R.id.my_request_item_created_time);
                myRequestContent = (TextView) itemView.findViewById(R.id.my_request_item_content);
                myRequestWonderCount = (TextView) itemView.findViewById(R.id.my_request_item_wonder_count);
                myRequestPrescriptionCount = (TextView) itemView.findViewById(R.id.my_request_item_prescription_count);
                myReqeustMoreBtn = (ImageView) itemView.findViewById(R.id.my_request_item_more_btn);
            }
        }
    }

    public class MyRequestAsyncTask extends AsyncTask<Integer, String, Question> {

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
        protected Question doInBackground(Integer... integers) {
            int skip = integers[0];
            int count = integers[1];
            int anotherUserId = integers[2];
            Response response = null;
            OkHttpClient toServer;
            Question question = new Question();
            Request request;
            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                if (anotherUserId != 0 && anotherUserId != BookdocPropertyManager.getInstance().getUserId()) {
                    request = new Request.Builder()
                            .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_MY_REQUEST, skip, count))
                            .header("Accept", "application/json")
                            .addHeader("anotherUserId", String.valueOf(anotherUserId))
                            .build();
                } else {
                    request = new Request.Builder()
                            .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_MY_REQUEST, skip, count))
                            .header("Accept", "application/json")
                            .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                            .build();
                }

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                if (response.isSuccessful()) {
                    Log.d("responseMessage", "responseMessage; " + responseMessage);
                    question.setData(new JSONObject(responseMessage));
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
            return question;
        }

        @Override
        public void onPostExecute(Question question) {
            super.onPostExecute(question);
            try {
                myRequestAdapter.addItems(question.getData());
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }
    }

    public class BookdocDeleteRequestAsyncTask extends AsyncTask<Integer, String, Integer> {

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(), message, Toast.LENGTH_SHORT).show();
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
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
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
                myRequestAdapter.removeItem(position);
            } catch (Exception e) {

            }
        }
    }

    @Subscribe
    public void updateRequestToAdapter(BookdocRequestPrescriptionActivity.QuestionPutValueObject questionPutValueObject) {
        myRequestAdapter.updateItem(questionPutValueObject);
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
