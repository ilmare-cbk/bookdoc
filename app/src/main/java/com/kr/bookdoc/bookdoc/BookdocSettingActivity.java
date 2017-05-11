package com.kr.bookdoc.bookdoc;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.json.JSONObject;

import com.kr.bookdoc.bookdoc.bookdocaccount.BookdocLoginActivity;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BookdocSettingActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    Toolbar bookdocSettingToolbar;
    TextView textLogout;
    ViewGroup btnProfile, btnNotice, btnVersionView;
    Switch bookdocPushSwitch;
    BookdocProgressDialog bookdocProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_setting);

        bookdocProgressDialog = new BookdocProgressDialog(this);

        bookdocSettingToolbar = (Toolbar) findViewById(R.id.bookdoc_setting_toolbar);
        setSupportActionBar(bookdocSettingToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);


        textLogout = (TextView) findViewById(R.id.text_setting_logout);
        textLogout.setOnClickListener(this);

        btnProfile = (ViewGroup) findViewById(R.id.layout_btn_setting_profile);
        btnProfile.setOnClickListener(this);

        btnNotice = (ViewGroup) findViewById(R.id.layout_btn_setting_notice);
        btnNotice.setOnClickListener(this);

        btnVersionView = (ViewGroup) findViewById(R.id.layout_btn_setting_versionview);
        btnVersionView.setOnClickListener(this);

        bookdocPushSwitch = (Switch) findViewById(R.id.switch_toggle_setting);
        bookdocPushSwitch.setOnCheckedChangeListener(this);

        int push = BookdocPropertyManager.getInstance().getFieldPushCheck();
        if (push == 0) {
            bookdocPushSwitch.setChecked(false);
        } else {
            bookdocPushSwitch.setChecked(true);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_setting_logout:
                BookdocPropertyManager.getInstance().setUserId(0);
                BookdocPropertyManager.getInstance().setUserEmail("");
                BookdocPropertyManager.getInstance().setPassword("");
                BookdocPropertyManager.getInstance().setAccessToken("");
                BookdocPropertyManager.getInstance().setCheckFacebookUser(false);
                BookdocPropertyManager.getInstance().setCheckCompleteUserInfo(false);
                Intent intentSubActivity =
                        new Intent(getApplicationContext(), BookdocLoginActivity.class);
                LoginManager.getInstance().logOut();
                intentSubActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentSubActivity);
                Toast.makeText(getApplicationContext(), "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.layout_btn_setting_profile:
                Intent profileSettingIntent = new Intent(BookdocApplication.getBookdocContext(), BookdocProfileSettingActivity.class);
                startActivity(profileSettingIntent);
                break;
            case R.id.layout_btn_setting_notice:
                Toast.makeText(getApplicationContext(), "공지사항 없음", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout_btn_setting_versionview:
                Toast.makeText(getApplicationContext(), "버전 1.0", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        int push;
        if (isChecked == true) {
            bookdocPushSwitch.setChecked(true);
            push = 1;
        } else {
            bookdocPushSwitch.setChecked(false);
            push = 0;
        }
        new BookdocPushAsyncTask().execute(String.valueOf(push), BookdocPropertyManager.getInstance().getFcmTokenKey());
    }

    public class BookdocPushAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
            try {
                if (!TextUtils.isEmpty(s) || s != null || !s.equals("")) {
                    int push = BookdocPropertyManager.getInstance().getFieldPushCheck();
                    if (push == 0) {
                        BookdocPropertyManager.getInstance().setFieldPushCheck(1);
                    } else {
                        BookdocPropertyManager.getInstance().setFieldPushCheck(0);
                    }
                }
            } catch (Exception e) {

            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(BookdocApplication.getBookdocContext(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String push = strings[0];
            String pushToken = strings[1];;
            String message = null;
            Response response = null;
            OkHttpClient toServer;

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("push", push)
                        .add("pushToken", pushToken)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_PUT_USER_SETTING))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .put(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("response", "response user info: " + responseMessage);
                if (response.isSuccessful()) {
                    message = new JSONObject(responseMessage).optString("message");
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
            return message;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bookdocProgressDialog != null) {
            bookdocProgressDialog.dismiss();
            bookdocProgressDialog = null;
        }
    }
}
