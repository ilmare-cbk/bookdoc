package com.kr.bookdoc.bookdoc.mybookfragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.BookdocBookPrescriptionListActivity;
import com.kr.bookdoc.bookdoc.BookdocPropertyManager;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocLoadMoreScrollListener;
import com.kr.bookdoc.bookdoc.bookdocutils.CustomImageView;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookmark.Bookmark;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookmark.Data;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class BookMarkFragment extends Fragment implements OnRefreshListener {
    private RecyclerView bookMarkRecyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private BookMarkAdapter bookMarkAdapter;
    private BookdocProgressDialog bookdocProgressDialog;
    private SwipeRefreshLayout bookMarkSwipeRefreshLayout;
    private int anotherUserId;

    public BookMarkFragment() {
    }

    public static BookMarkFragment newInstance(int anotherUserId) {
        BookMarkFragment bookMarkFragment = new BookMarkFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("anotherUserId", anotherUserId);
        bookMarkFragment.setArguments(bundle);
        return bookMarkFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_book_mark, container, false);

        anotherUserId = getArguments().getInt("anotherUserId");

        bookdocProgressDialog = new BookdocProgressDialog(getContext());
        bookMarkSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.book_mark_sp);
        bookMarkSwipeRefreshLayout.setOnRefreshListener(this);
        bookMarkRecyclerView = (RecyclerView) view.findViewById(R.id.book_mark_rv);
        bookMarkRecyclerView.addItemDecoration(new BookmarkItemDecoration());
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        bookMarkAdapter = new BookMarkAdapter();
        bookMarkRecyclerView.setLayoutManager(staggeredGridLayoutManager);
        bookMarkRecyclerView.setAdapter(bookMarkAdapter);
        bookMarkRecyclerView.addOnScrollListener(new BookdocLoadMoreScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                int skip = (page - 1) * 9;
                new BookMarkListAsyncTask().execute(skip, 9, anotherUserId);
            }

            @Override
            public void onLoadMore(int page) {
                int skip = (page - 1) * 9;
                new BookMarkListAsyncTask().execute(skip, 9, anotherUserId);
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
        new BookMarkListAsyncTask().execute(0, 9, anotherUserId);
    }

    @Override
    public void onRefresh() {
        bookMarkAdapter.removeItems();
        new BookMarkListAsyncTask().execute(0, 9, anotherUserId);
        bookMarkSwipeRefreshLayout.setRefreshing(false);
    }

    private class BookMarkAdapter extends RecyclerView.Adapter<BookMarkAdapter.BookMarkViewHolder> {
        ArrayList<Data> dataArrayList = new ArrayList<>();
        DisplayMetrics displayMetrics;
        private int contentWidth;

        public BookMarkAdapter() {
            displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            contentWidth = (displayMetrics.widthPixels) / 3;
        }

        @Override
        public BookMarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.book_mark_item, parent, false);
            return new BookMarkViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BookMarkViewHolder holder, final int position) {
            final Data data = dataArrayList.get(position);
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(data.getImagePath())
                    .placeholder(R.drawable.mybook_placeholder)
                    .error(R.drawable.mybook_error)
                    .into(holder.bookMarkItem);
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent bookPrescriptionList = new Intent(getContext(), BookdocBookPrescriptionListActivity.class);
                    bookPrescriptionList.putExtra("id", data.getId());
                    bookPrescriptionList.putExtra("checkBookmark", true);
                    startActivity(bookPrescriptionList);
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


        public class BookMarkViewHolder extends RecyclerView.ViewHolder {
            CustomImageView bookMarkItem;
            View view;
            ViewGroup.LayoutParams layoutParams;

            public BookMarkViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                bookMarkItem = (CustomImageView) itemView.findViewById(R.id.book_mark_iv);
                layoutParams = bookMarkItem.getLayoutParams();
                layoutParams.width = contentWidth;
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                bookMarkItem.setLayoutParams(layoutParams);
            }
        }
    }

    public class BookMarkListAsyncTask extends AsyncTask<Integer, String, Bookmark> {
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
        protected Bookmark doInBackground(Integer... integers) {
            int skip = integers[0];
            int count = integers[1];
            int anotherUserId = integers[2];
            Response response = null;
            OkHttpClient toServer;
            Bookmark bookmark = new Bookmark();
            Request request;
            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                if (anotherUserId != 0 && anotherUserId != BookdocPropertyManager.getInstance().getUserId()) {
                    request = new Request.Builder()
                            .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_MY_BOOKMARK, skip, count))
                            .header("Accept", "application/json")
                            .addHeader("anotherUserId", String.valueOf(anotherUserId))
                            .build();
                } else {
                    request = new Request.Builder()
                            .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_MY_BOOKMARK, skip, count))
                            .header("Accept", "application/json")
                            .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                            .build();
                }

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("jsondata", "jsondata: " + responseMessage);
                if (response.isSuccessful()) {
                    bookmark.setData(new JSONObject(responseMessage));
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
            return bookmark;
        }

        @Override
        protected void onPostExecute(Bookmark bookmark) {
            super.onPostExecute(bookmark);
            try {
                bookMarkAdapter.addItems(bookmark.getDatas());
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }
    }

    public class BookmarkItemDecoration extends RecyclerView.ItemDecoration {
        private final int FIRST_POSITION = 0;
        private final int SECOND_POSITION = 1;
        private final int THIRD_POSITION = 2;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            final int top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                    getResources().getDisplayMetrics());
            switch (position) {
                case FIRST_POSITION:
                case SECOND_POSITION:
                case THIRD_POSITION:
                    outRect.top = top;
                    break;
                default:
                    outRect.top = 0;
                    break;
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
