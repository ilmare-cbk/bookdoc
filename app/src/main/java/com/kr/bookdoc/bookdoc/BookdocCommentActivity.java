package com.kr.bookdoc.bookdoc;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import org.json.JSONObject;

import java.util.ArrayList;

import com.kr.bookdoc.bookdoc.BookdocCommentActivity.CommentRecyclerViewAdapter.OnCommentClickListener;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocCommentDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocDateFormat;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocLoadMoreScrollListener;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.comments.CommentPost;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.comments.Comments;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.comments.Data;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class BookdocCommentActivity extends AppCompatActivity implements View.OnClickListener, OnEditorActionListener {
    RecyclerView recyclerComment;
    LinearLayoutManager linearLayoutManager;
    CommentRecyclerViewAdapter commentRecyclerViewAdapter;
    Toolbar commentToolbar;
    ActionBar actionBar;
    EditText commentEditText;
    LinearLayout commentImageView;
    int prescriptionId;
    private InputMethodManager inputMethodManager;
    boolean checkEdit = false;
    int commentId;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_comment);

        prescriptionId = getIntent().getIntExtra("prescriptionId", 0);

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        commentToolbar = (Toolbar) findViewById(R.id.toolbar_comment);
        setSupportActionBar(commentToolbar);
        actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);


        commentEditText = (EditText) findViewById(R.id.edittext_comment);
        commentEditText.setOnEditorActionListener(this);
        commentImageView = (LinearLayout) findViewById(R.id.imageview_comment_check);
        commentImageView.setOnClickListener(this);
        recyclerComment = (RecyclerView) findViewById(R.id.recyclerview_comment);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerComment.setLayoutManager(linearLayoutManager);
        recyclerComment.addOnScrollListener(new BookdocLoadMoreScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {

            }

            @Override
            public void onLoadMore(int page) {
                int skip = (page - 1) * 9;
                new BookdocCommentsListAsyncTask().execute(skip, 9);
            }

            @Override
            public void setVisibleTopButton() {

            }

            @Override
            public void setInvisibleTopButton() {

            }
        });
        commentRecyclerViewAdapter = new CommentRecyclerViewAdapter(this);
        commentRecyclerViewAdapter.setOnCommentClickListener(new OnCommentClickListener() {
            @Override
            public void onCommentClick(final Data data, int position) {
                final BookdocCommentDialog bookdocCommentDialog = BookdocCommentDialog.newInstances(data.getId(), position);
                bookdocCommentDialog.show(getSupportFragmentManager(), "comment");
                bookdocCommentDialog.setOnEditComment(new BookdocCommentDialog.OnEditCommentListener() {
                    @Override
                    public void onEditComment(int position) {
                        bookdocCommentDialog.dismiss();
                        commentEditText.requestFocus();
                        commentEditText.setText(data.getDescription());
                        inputMethodManager.toggleSoftInput(0, 0);
                        checkEdit = true;
                        commentId = position;
                    }
                });
                bookdocCommentDialog.setOnDeleteComment(new BookdocCommentDialog.OnDeleteCommentListener() {
                    @Override
                    public void onDeleteComment(int commentId, int position) {
                        bookdocCommentDialog.dismiss();
                        new BookdocDeleteCommentAsyncTask().execute(commentId, position);
                    }
                });
            }
        });
        recyclerComment.setAdapter(commentRecyclerViewAdapter);
        new BookdocCommentsListAsyncTask().execute(0, 9);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageview_comment_check:
                String comment = commentEditText.getText().toString();
                if (!TextUtils.isEmpty(comment)) {
                    postComment(comment);
                } else {
                    Toast.makeText(BookdocApplication.getBookdocContext(), "댓글을 입력하세요", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void postComment(String comment) {
        CommentPost commentPost = new CommentPost();
        commentPost.setUserId(BookdocPropertyManager.getInstance().getUserId());
        commentPost.setDescription(comment);
        commentEditText.setText("");
        inputMethodManager.hideSoftInputFromWindow(commentEditText.getWindowToken(), 0);
        if (!checkEdit) {
            new BookdocPostCommentAsyncTask().execute(commentPost);
        } else {
            checkEdit = false;
            new BookdocPutCommentAsyncTask().execute(commentPost);
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        if (i == EditorInfo.IME_ACTION_DONE) {
            String comment = commentEditText.getText().toString();
            if (!TextUtils.isEmpty(comment)) {
                postComment(comment);
            } else {
                Toast.makeText(BookdocApplication.getBookdocContext(), "댓글을 입력하세요", Toast.LENGTH_SHORT).show();
            }
            handled = true;
        }
        return handled;
    }


    public static class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.ViewHolder> {
        private ArrayList<Data> recyclerViewItemDataset = new ArrayList<>();
        private Context context;
        OnCommentClickListener onCommentClickListener;


        public CommentRecyclerViewAdapter(Context context) {
            this.context = context;
        }

        public void addItems(ArrayList<Data> recyclerList) {
            recyclerViewItemDataset.addAll(recyclerList);
            notifyDataSetChanged();
        }

        public void removeItems() {
            recyclerViewItemDataset.clear();
            notifyDataSetChanged();
        }

        public void removeItem(int position) {
            try {
                recyclerViewItemDataset.remove(position);
                notifyDataSetChanged();
            } catch (IndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final CircleImageView holderImage;
            public final TextView holderName, holderComment;
            RelativeTimeTextView holderDate;

            Typeface typeNotoR = Typeface
                    .createFromAsset(context.getAssets(), "notokr-regular.ttf");
            Typeface typeNotoL = Typeface
                    .createFromAsset(context.getAssets(), "notokr-light.ttf");


            public ViewHolder(View itemView) {
                super(itemView);
                mView = itemView;
                holderImage = (CircleImageView) itemView.findViewById(R.id.imageview_comment_item);
                holderName = (TextView) itemView.findViewById(R.id.textview_comment_name);
                holderName.setTypeface(this.typeNotoR);
                holderDate = (RelativeTimeTextView) itemView.findViewById(R.id.textview_comment_date);
                holderDate.setTypeface(this.typeNotoR);
                holderComment = (TextView) itemView.findViewById(R.id.textview_comment_comment);
                holderComment.setTypeface(this.typeNotoL);

            }
        }

        @Override
        public CommentRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_bookdoc_comment_item, parent, false);
            return new CommentRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentRecyclerViewAdapter.ViewHolder holder, final int position) {
            final Data data = recyclerViewItemDataset.get(position);
            Glide.with(context)
                    .load(data.getUserImagePath())
                    .placeholder(R.drawable.profile_default)
                    .error(R.drawable.profile_default)
                    .into(holder.holderImage);
            holder.holderName.setText(data.getUserName());
            if (data.getModified().equals("null")) {
                holder.holderDate.setText(new BookdocDateFormat().formatTimeString("yyyy-MM-dd'T'HH:mm:ss", data.getCreated()));
            } else {
                holder.holderDate.setText(new BookdocDateFormat().formatTimeString("yyyy-MM-dd'T'HH:mm:ss", data.getModified()));
            }
            holder.holderComment.setText(data.getDescription());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getUserId() == BookdocPropertyManager.getInstance().getUserId()) {
                        if (onCommentClickListener != null) {
                            onCommentClickListener.onCommentClick(data, position);
                        }
                    }
                }
            });

        }


        @Override
        public int getItemCount() {
            return recyclerViewItemDataset.size();
        }

        public interface OnCommentClickListener {
            void onCommentClick(Data data, int position);
        }

        public void setOnCommentClickListener(OnCommentClickListener onCommentClickListener) {
            this.onCommentClickListener = onCommentClickListener;
        }

    }

    public class BookdocCommentsListAsyncTask extends AsyncTask<Integer, String, Comments> {

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
        protected Comments doInBackground(Integer... integers) {
            int skip = integers[0];
            int count = integers[1];
            Response response = null;
            OkHttpClient toServer;
            Comments comments = new Comments();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_GET_PRESCRIPTION_COMMNETS, prescriptionId, skip, count))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .build();

                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(responsedMessage);
                    comments.setData(jsonObject);
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
            return comments;
        }

        @Override
        protected void onPostExecute(Comments comments) {
            super.onPostExecute(comments);
            try {
                commentRecyclerViewAdapter.addItems(comments.getData());
            } catch (Exception e) {

            }
        }
    }

    public class BookdocPostCommentAsyncTask extends AsyncTask<CommentPost, String, Comments> {

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
        protected Comments doInBackground(CommentPost... commentPosts) {
            CommentPost commentPost = commentPosts[0];
            Response response = null;
            OkHttpClient toServer;
            Comments comments = new Comments();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                RequestBody postBody = new FormBody.Builder()
                        .add("userId", String.valueOf(commentPost.getUserId()))
                        .add("description", commentPost.getDescription())
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_POST_PRESCRIPTION_COMMENTS, prescriptionId))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .post(postBody)
                        .build();

                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                Log.d("jsonData", responsedMessage);
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(responsedMessage);
                    comments.setData(jsonObject);
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
            return comments;
        }

        @Override
        protected void onPostExecute(Comments comments) {
            super.onPostExecute(comments);
            try {
                commentRecyclerViewAdapter.removeItems();
                commentRecyclerViewAdapter.addItems(comments.getData());
            } catch (Exception e) {

            }
        }
    }

    public class BookdocPutCommentAsyncTask extends AsyncTask<CommentPost, String, Comments> {

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
        protected Comments doInBackground(CommentPost... commentPosts) {
            CommentPost commentPost = commentPosts[0];
            Response response = null;
            OkHttpClient toServer;
            Comments comments = new Comments();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                RequestBody postBody = new FormBody.Builder()
                        .add("userId", String.valueOf(commentPost.getUserId()))
                        .add("description", commentPost.getDescription())
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_PUT_COMMENTS, commentId))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .put(postBody)
                        .build();

                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(responsedMessage);
                    comments.setData(jsonObject);
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
            return comments;
        }

        @Override
        protected void onPostExecute(Comments comments) {
            super.onPostExecute(comments);
            try {
                commentRecyclerViewAdapter.removeItems();
                commentRecyclerViewAdapter.addItems(comments.getData());
            } catch (Exception e) {

            }
        }
    }

    public class BookdocDeleteCommentAsyncTask extends AsyncTask<Integer, String, Integer> {

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
        protected Integer doInBackground(Integer... integers) {
            int commentId = integers[0];
            int position = integers[1];
            Log.d("commentId", String.valueOf(commentId));
            Response response = null;
            OkHttpClient toServer;
            Comments comments = new Comments();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("userId", String.valueOf(BookdocPropertyManager.getInstance().getUserId()))
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_DELETE_COMMENTS, commentId))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .delete(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(responsedMessage);
                    comments.setData(jsonObject);
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
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            try {
                commentRecyclerViewAdapter.removeItem(integer);
            } catch (Exception e) {

            }
        }
    }


}


