package com.kr.bookdoc.bookdoc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.RequestAdd;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.RequestEdit;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.question.Question;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.questions.questionpost.QuestionPost;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

public class BookdocRequestPrescriptionActivity extends AppCompatActivity {
    private Toolbar requestPrescriptionToolbar;
    private Integer questionUserId;
    private EditText questionDescription;
    private int requestId;
    private int position;
    private boolean checkEdit;
    private String requestDescription;
    private BookdocProgressDialog bookdocProgressDialog;
    private CircleImageView bookdocRequestPrescriptionUserImage;
    private TextView bookdocRequestPrescriptionUserName;
    private boolean checkFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_request_prescription);

        bookdocProgressDialog = new BookdocProgressDialog(this);

        requestId = getIntent().getIntExtra("requestId", 0);
        position = getIntent().getIntExtra("position", 0);
        checkEdit = getIntent().getBooleanExtra("checkEdit", false);
        checkFrom = getIntent().getBooleanExtra("checkFrom", false);
        requestDescription = getIntent().getStringExtra("requestDescription");
        requestPrescriptionToolbar = (Toolbar) findViewById(R.id.activity_bookdoc_request_prescription_toolbar);
        setSupportActionBar(requestPrescriptionToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.close);
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);

        bookdocRequestPrescriptionUserImage = (CircleImageView) findViewById(R.id.bookdoc_request_prescription_user_image);
        Glide.with(this)
                .load(BookdocPropertyManager.getInstance().getFieldUserImage())
                .placeholder(R.drawable.profile_default)
                .error(R.drawable.profile_default)
                .into(bookdocRequestPrescriptionUserImage);
        bookdocRequestPrescriptionUserName = (TextView) findViewById(R.id.bookdoc_request_prescription_user_name);
        bookdocRequestPrescriptionUserName.setText(BookdocPropertyManager.getInstance().getUserName());

        questionDescription = (EditText) findViewById(R.id.textview_create_question_description);
        if (requestId != 0 && checkEdit == true && !TextUtils.isEmpty(requestDescription)) {
            questionDescription.setText(requestDescription);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookdoc_request_prescription_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.post_request_prescription:
                if (!checkEdit) {
                    new BookdocCreateQuestionAsyncTask().execute(setQuestionData());
                } else {
                    new BookdcoEditQuestionAsyncTask().execute(new QuestionPutValueObject(requestId, questionDescription.getText().toString()));
                }
                return true;
            default:
                return false;
        }
    }

    public class BookdocCreateQuestionAsyncTask extends AsyncTask<QuestionPost, String, Question> {

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
        protected Question doInBackground(QuestionPost... questionPosts) {
            Question question = new Question();
            QuestionPost questionPost = questionPosts[0];

            Gson gson = new Gson();
            Response response = null;
            OkHttpClient toServer = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            try {
                Log.d("QuestionPost", "userId: " + questionPost.getUserId());
                Log.d("prescriptionPost", gson.toJson(questionPost.getDescription()));

                RequestBody postBody = new FormBody.Builder()
                        .add("userId", String.valueOf(questionPost.getUserId()))
                        .add("description", questionPost.getDescription())
                        .build();
                Request request = new Request.Builder()
                        .url(BookdocNetworkDefineConstant.SERVER_URL_POST_QUESTION)
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .post(postBody)
                        .build();
                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                Log.d("json", responsedMessage);
                if (response.isSuccessful()) {
                    question = gson.fromJson(responsedMessage, Question.class);
                } else {
                    Log.e("요청에러", response.message().toString());
                }

            } catch (UnknownHostException une) {
                e("aaa", une.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
            } catch (UnsupportedEncodingException uee) {
                e("bbb", uee.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
            } catch (Exception e) {
                e("ccc", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다." + "\n" + "잠시후에 다시 시도해주세요");
            } finally {
                if (response != null) {
                    response.close();
                }
            }
            return question;
        }

        @Override
        protected void onPostExecute(Question question) {
            super.onPostExecute(question);
            try {
                BusProvider.getInstance().post(new RequestAdd(question.getData().get(0)));
            } catch (Exception e) {
            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
            checkEdit = false;
            finish();
        }
    }

    public class BookdcoEditQuestionAsyncTask extends AsyncTask<QuestionPutValueObject, String, Question> {
        QuestionPutValueObject questionPutValueObject;

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
        protected Question doInBackground(QuestionPutValueObject... questionPutValueObjects) {
            questionPutValueObject = questionPutValueObjects[0];
            Response response = null;
            OkHttpClient toServer;
            Question question = new Question();

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                Log.d("userId", "userId: " + BookdocPropertyManager.getInstance().getUserId());
                RequestBody requestBody = new FormBody.Builder()
                        .add("userId", String.valueOf(BookdocPropertyManager.getInstance().getUserId()))
                        .add("description", questionPutValueObject.description)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_PUT_QUESTION, questionPutValueObject.requestId))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .put(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                if (response.isSuccessful()) {
                    question.setData(new JSONObject(response.body().string()));
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
        protected void onPostExecute(Question question) {
            super.onPostExecute(question);
            try {
                if (checkFrom) {
                    BusProvider.getInstance().post(new RequestEdit(question.getData().get(position - 1), position));
                } else {
                    questionPutValueObject.setPosition(position);
                    BusProvider.getInstance().post(questionPutValueObject);
                }
            } catch (Exception e) {
            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
            finish();
        }
    }

    public QuestionPost setQuestionData() {
        QuestionPost questionPost = new QuestionPost();
        questionUserId = BookdocPropertyManager.getInstance().getUserId();
        questionPost.setUserId(questionUserId);
        questionPost.setDescription(questionDescription.getText().toString());
        return questionPost;
    }

    public class QuestionPutValueObject {
        public int requestId;
        public String description;
        public int position;

        public QuestionPutValueObject(int requestId, String description) {
            this.requestId = requestId;
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
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
