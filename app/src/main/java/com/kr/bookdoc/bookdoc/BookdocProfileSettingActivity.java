package com.kr.bookdoc.bookdoc;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.util.Log.e;

public class BookdocProfileSettingActivity extends AppCompatActivity implements OnClickListener {
    Toolbar bookdocProfileSettingToolbar;
    TextView bookdocProfileSettingTextView;
    boolean signUp;
    CircleImageView bookdocProfileSettingImg;
    EditText bookdocProfileSettingUsername;
    EditText bookdocProfileSettingDescription;
    BookdocProgressDialog bookdocProgressDialog;
    private final int MY_PERMISSION_REQUEST_STORAGE = 100;
    private final int PICK_FROM_ALBUM = 1;
    private Uri imageCaptureUri;
    private String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_profile_setting);

        signUp = getIntent().getBooleanExtra("signUp", false);

        bookdocProgressDialog = new BookdocProgressDialog(this);

        bookdocProfileSettingTextView = (TextView) findViewById(R.id.bookdoc_profile_setting_tv);
        bookdocProfileSettingToolbar = (Toolbar) findViewById(R.id.bookdoc_profile_setting_toolbar);
        setSupportActionBar(bookdocProfileSettingToolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        ab.setDisplayShowTitleEnabled(false);
        bookdocProfileSettingImg = (CircleImageView) findViewById(R.id.bookdoc_profile_setting_img);
        bookdocProfileSettingImg.setOnClickListener(this);
        bookdocProfileSettingUsername = (EditText) findViewById(R.id.bookdoc_profile_setting_username);
        bookdocProfileSettingDescription = (EditText) findViewById(R.id.bookdoc_profile_setting_introduce);
        if (signUp) {
            bookdocProfileSettingTextView.setText("프로필 작성");
            ab.setDisplayHomeAsUpEnabled(false);
        } else {
            ab.setDisplayHomeAsUpEnabled(true);
            Glide.with(BookdocApplication.getBookdocContext())
                    .load(BookdocPropertyManager.getInstance().getFieldUserImage())
                    .into(bookdocProfileSettingImg);
            bookdocProfileSettingUsername.setText(BookdocPropertyManager.getInstance().getUserName());
            bookdocProfileSettingDescription.setText(BookdocPropertyManager.getInstance().getFieldUserDescription());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bookdoc_profile_setting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.profile_setting_btn:
                String userName = bookdocProfileSettingUsername.getText().toString();
                String description = bookdocProfileSettingDescription.getText().toString();
                if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(description)) {
                    new BookdocProfileSettingAsyncTask().execute(userName, description);
                } else {
                    Toast.makeText(BookdocApplication.getBookdocContext(), "이름과 이력을 적어주세요", Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bookdoc_profile_setting_img:
                checkPermission();
                break;
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_REQUEST_STORAGE);
            } else {
                doTaskAlbumAction();
            }
        } else {
            doTaskAlbumAction();
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    doTaskAlbumAction();
                }
                break;
        }
    }


    public void doTaskAlbumAction() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, PICK_FROM_ALBUM);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_FROM_ALBUM:
                    imageCaptureUri = data.getData();
                    s = getRealPathFromURI(imageCaptureUri);
                    new BookdocUserImageAsyncTask().execute(s);
                    break;
            }
        }
    }

    public String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        String imageUri;
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        imageUri = cursor.getString(column_index);
        return imageUri;
    }

    private class BookdocProfileSettingAsyncTask extends AsyncTask<String, String, String> {

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
        protected String doInBackground(String... strings) {
            String name = strings[0];
            String description = strings[1];
            String message = null;
            Response response = null;
            OkHttpClient toServer;

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("name", name)
                        .add("description", description)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_PUT_USER_INFO))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .put(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(responseMessage);
                    message = jsonObject.optString("message");
                    JSONObject dataJsonObject = jsonObject.optJSONObject("data");
                    BookdocPropertyManager bookdocPropertyManager = BookdocPropertyManager.getInstance();
                    bookdocPropertyManager.setUserId(dataJsonObject.optInt("id"));
                    bookdocPropertyManager.setUserName(dataJsonObject.optString("name"));
                    bookdocPropertyManager.setFieldUserDescription(dataJsonObject.optString("description"));
                    bookdocPropertyManager.setFieldUserImage(dataJsonObject.optString("imagePath"));
                    bookdocPropertyManager.setUserEmail(dataJsonObject.optString("email"));
                    bookdocPropertyManager.setFieldPushCheck(dataJsonObject.optInt("push"));
                    bookdocPropertyManager.setFieldPrescriptionCount(dataJsonObject.optInt("prescribed"));
                    bookdocPropertyManager.setFieldQuestionCount(dataJsonObject.optInt("questioned"));
                    bookdocPropertyManager.setAccessToken(dataJsonObject.optString("token"));
                } else {
                    Log.e("요청에러", response.message().toString());
                    message = new JSONObject(responseMessage).optString("message");
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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (!TextUtils.isEmpty(s) && s != null && s.equals("success")) {
                    if (signUp) {
                        BookdocPropertyManager.getInstance().setCheckCompleteUserInfo(true);
                        startActivity(new Intent(BookdocApplication.getBookdocContext(), BookdocMainActivity.class));
                    }
                    finish();
                }
            } catch (Exception e) {

            }
            if (bookdocProgressDialog.isShowing()) {
                bookdocProgressDialog.dismiss();
            }
        }
    }

    private class BookdocUserImageAsyncTask extends AsyncTask<String, String, String> {

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
        protected String doInBackground(String... strings) {
            String imagePath = strings[0];
            String message = null;
            final MediaType pngType = MediaType.parse("image/*");
            File file = new File(imagePath);

            Response response = null;
            Request request;
            OkHttpClient toServer = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            try {
                RequestBody postBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("image", file.getName(), RequestBody.create(pngType, file))
                        .build();
                request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_PUT_USER_IMAGE))
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .put(postBody)
                        .build();
                response = toServer.newCall(request).execute();
                String responsedMessage = response.body().string();
                Log.d("json", responsedMessage);
                if (response.isSuccessful()) {
                    JSONObject jsonObject = new JSONObject(responsedMessage);
                    message = jsonObject.optString("message");
                    JSONObject dataJsonObject = jsonObject.optJSONObject("data");
                    BookdocPropertyManager.getInstance().setFieldUserImage(dataJsonObject.optString("imagePath"));
                } else {
                    Log.e("요청에러", response.message().toString());
                    message = new JSONObject(responsedMessage).optString("message");
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
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (!TextUtils.isEmpty(s) && s != null && s.equals("success")) {
                    Glide.with(BookdocApplication.getBookdocContext())
                            .load(BookdocPropertyManager.getInstance().getFieldUserImage())
                            .into(bookdocProfileSettingImg);
                }
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
