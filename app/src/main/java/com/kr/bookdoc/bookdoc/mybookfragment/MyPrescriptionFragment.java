package com.kr.bookdoc.bookdoc.mybookfragment;

import android.content.Intent;
import android.graphics.Rect;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.BookdocBookPrescriptionDetailActivity;
import com.kr.bookdoc.bookdoc.BookdocPropertyManager;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocDateFormat;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocLoadMoreScrollListener;
import com.kr.bookdoc.bookdoc.bookdocutils.CustomImageView;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Data;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescription.Prescription;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MyPrescriptionFragment extends Fragment implements OnRefreshListener {
    private RecyclerView myPrescriptionRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MyPrescriptionAdapter myPrescriptionAdapter;
    private BookdocProgressDialog bookdocProgressDialog;
    private SwipeRefreshLayout myPrescriptionSwipeRefreshLayout;
    private int anotherUserId;

    public MyPrescriptionFragment() {
    }

    public static MyPrescriptionFragment newInstance(int anotherUserId) {
        MyPrescriptionFragment myPrescriptionFragment = new MyPrescriptionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("anotherUserId", anotherUserId);
        myPrescriptionFragment.setArguments(bundle);
        return myPrescriptionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_my_prescription, container, false);

        anotherUserId = getArguments().getInt("anotherUserId");

        bookdocProgressDialog = new BookdocProgressDialog(getContext());

        myPrescriptionSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.my_prescription_sp);
        myPrescriptionSwipeRefreshLayout.setOnRefreshListener(this);

        myPrescriptionRecyclerView = (RecyclerView) view.findViewById(R.id.my_prescription_rv);
        linearLayoutManager = new LinearLayoutManager(BookdocApplication.getBookdocContext(), LinearLayoutManager.VERTICAL, false);
        myPrescriptionAdapter = new MyPrescriptionAdapter();
        myPrescriptionRecyclerView.setLayoutManager(linearLayoutManager);
        myPrescriptionRecyclerView.addItemDecoration(new MyPrescriptionItemDecoration(2));
        myPrescriptionRecyclerView.setAdapter(myPrescriptionAdapter);
        myPrescriptionRecyclerView.addOnScrollListener(new BookdocLoadMoreScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

            }

            @Override
            public void onLoadMore(int page) {
                int skip = (page - 1) * 9;
                new MyPrescriptionAsyncTask().execute(skip, 9, anotherUserId);
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
        new MyPrescriptionAsyncTask().execute(0, 9, anotherUserId);
    }

    @Override
    public void onRefresh() {
        myPrescriptionAdapter.removeItems();
        new MyPrescriptionAsyncTask().execute(0, 9, anotherUserId);
        myPrescriptionSwipeRefreshLayout.setRefreshing(false);
    }

    private class MyPrescriptionAdapter extends RecyclerView.Adapter<MyPrescriptionAdapter.MyPrescriptionViewHolder> {
        ArrayList<Data> dataArrayList = new ArrayList<>();

        public MyPrescriptionAdapter() {
        }

        @Override
        public MyPrescriptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_prescription_item, parent, false);
            return new MyPrescriptionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyPrescriptionViewHolder holder, final int position) {
            final Data data = dataArrayList.get(position);
            String bookImagePath = data.getBookImagePath();
            Glide.with(getContext())
                    .load(bookImagePath)
                    .error(R.drawable.profile_default)
                    .into(holder.myPrescriptionBookimage);

            holder.myPrescriptionTitle.setText(data.getTitle());
            holder.myPrescriptionContent.setText(data.getBookTitle());
            holder.myPrescriptionCreatedTime.setText(BookdocDateFormat.setDateFormat("yyyy-MM-dd'T'HH:mm:ss", "yyyy.MM.dd", data.getCreated()));
            holder.myPrescriptionContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(BookdocApplication.getBookdocContext(), BookdocBookPrescriptionDetailActivity.class);
                    intent.putExtra("prescriptionId", data.getId());
                    intent.putExtra("position", position);
                    intent.putExtra("bookId", data.getBookId());
                    getActivity().startActivity(intent);
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

        public class MyPrescriptionViewHolder extends RecyclerView.ViewHolder {
            TextView myPrescriptionTitle, myPrescriptionContent, myPrescriptionCreatedTime;
            CustomImageView myPrescriptionBookimage;
            LinearLayout myPrescriptionContainer;

            public MyPrescriptionViewHolder(View itemView) {
                super(itemView);
                myPrescriptionBookimage = (CustomImageView) itemView.findViewById(R.id.my_prescription_question_book_img);
                myPrescriptionTitle = (TextView) itemView.findViewById(R.id.my_prescription_title);
                myPrescriptionContent = (TextView) itemView.findViewById(R.id.my_prescription_content);
                myPrescriptionCreatedTime = (TextView) itemView.findViewById(R.id.my_prescription_created_time);
                myPrescriptionContainer = (LinearLayout) itemView.findViewById(R.id.my_prescription_container);
            }
        }
    }

    private class MyPrescriptionItemDecoration extends RecyclerView.ItemDecoration {
        private int spacing;

        public MyPrescriptionItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = 8;
            }
            outRect.bottom = spacing;
        }
    }

    public class MyPrescriptionAsyncTask extends AsyncTask<Integer, String, Prescription> {
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
            int skip = integers[0];
            int count = integers[1];
            int anotherUserId = integers[2];
            Response response = null;
            OkHttpClient toServer;
            Prescription prescription = new Prescription();
            Request request;

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                if (anotherUserId != 0 && anotherUserId != BookdocPropertyManager.getInstance().getUserId()) {
                    request = new Request.Builder()
                            .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_MY_PRESCRIPTION, skip, count))
                            .header("Accept", "application/json")
                            .addHeader("anotherUserId", String.valueOf(anotherUserId))
                            .build();
                } else {
                    request = new Request.Builder()
                            .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_MY_PRESCRIPTION, skip, count))
                            .header("Accept", "application/json")
                            .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                            .build();
                }

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
                myPrescriptionAdapter.addItems(prescription.getData());
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
