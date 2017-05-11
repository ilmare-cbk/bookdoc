package com.kr.bookdoc.bookdoc;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;

import com.kr.bookdoc.bookdoc.bookdocaccount.BookdocLoginActivity;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocfcmpush.BookdocFirebaseInstanceIDService;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class BookdocSplashActivity extends AppCompatActivity{
    private final int SPLASH_DELAY_TIME = 1500;
    private BookdocProgressDialog bookdocProgressDialog;
    private BookdocPropertyManager bookdocPropertyManager;
    private CallbackManager callbackManager;
    private boolean checkNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_splash);
        bookdocProgressDialog = new BookdocProgressDialog(this);
        bookdocPropertyManager = BookdocPropertyManager.getInstance();
        boolean checkFacebookUser = bookdocPropertyManager.isCheckFacebookUser();
        checkNetwork = isConnectedToInternet(this);
        if(checkNetwork){
            if(checkFacebookUser){
                new RefreshFCMTokenAsyncTask().execute();
                callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        onSuccessFacebookLogin(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(BookdocApplication.getBookdocContext(), "페이스북 로그인이 취소되었습니다.", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(BookdocApplication.getBookdocContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("facebook login", exception.getLocalizedMessage());
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                });
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email","user_friends"));
            }else{
                handler.postDelayed(runnable, SPLASH_DELAY_TIME);
            }
        }else{
            Toast.makeText(BookdocApplication.getBookdocContext(), "네트워크 상태를 확인하세요", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            },2000);
        }
    }

    Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String userEmail = bookdocPropertyManager.getUserEmail();
            String password = bookdocPropertyManager.getPassword();
            if(!TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(password)){
                new RefreshFCMTokenAsyncTask().execute();
                new BookdocLocalLoginAsyncTask().execute(userEmail, password);
            }else{
                startActivity(new Intent(BookdocSplashActivity.this, BookdocLoginActivity.class));
                finish();
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void onSuccessFacebookLogin(final LoginResult loginResult){
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject json, GraphResponse response) {
                if (response.getError() != null) {
                    System.out.println("ERROR");
                } else {
                    Log.d("GraphResponse","GraphResponse: "+response.getRawResponse());
                    final String facebookUserId = json.optString("id");
                    final String accessToken = loginResult.getAccessToken().getToken();
                    final String userImage = json.optJSONObject("picture").optJSONObject("data").optString("url");
                    BookdocPropertyManager.getInstance().setFaceBookUserId(facebookUserId);
                    BookdocPropertyManager.getInstance().setFieldFacebookAccesstoken(accessToken);
                    BookdocPropertyManager.getInstance().setFacebookUserImage(userImage);
                    new BookdocLoginAsyncTask().execute();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public static boolean  isConnectedToInternet(Context context){
        boolean isConnected= false;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connectivityManager .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        isConnected= (wifi.isAvailable() && wifi.isConnectedOrConnecting() || (mobile.isAvailable() && mobile.isConnectedOrConnecting()));
        return isConnected;
    }

    private class BookdocLoginAsyncTask extends AsyncTask<Void, String, String>{

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
        protected String doInBackground(Void... voids) {
            String message = null;
            Response response = null;
            OkHttpClient toServer;

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                Log.d("facebooktoken","facebooktoken: "+BookdocPropertyManager.getInstance().getFieldFacebookAccesstoken());
                RequestBody requestBody = new FormBody.Builder()
                        .add("accessToken",BookdocPropertyManager.getInstance().getFieldFacebookAccesstoken())
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_LOGIN))
                        .header("Accept", "application/json")
                        .post(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("response","response: "+responseMessage);
                if(response.isSuccessful()){
                    JSONObject jsonObject = new JSONObject(responseMessage);
                    message = jsonObject.optString("message");
                    JSONObject dataJsonObject = jsonObject.optJSONObject("data");
                    BookdocPropertyManager bookdocPropertyManager = BookdocPropertyManager.getInstance();
                    bookdocPropertyManager.setUserId(dataJsonObject.optInt("id"));
                    bookdocPropertyManager.setUserName(dataJsonObject.optString("name"));
                    bookdocPropertyManager.setFieldFacebookUserName(dataJsonObject.optString("nameFacebook"));
                    bookdocPropertyManager.setFieldUserDescription(dataJsonObject.optString("description"));
                    bookdocPropertyManager.setFieldUserImage(dataJsonObject.optString("imagePath"));
                    bookdocPropertyManager.setUserEmail(dataJsonObject.optString("email"));
                    bookdocPropertyManager.setFacebookUserImage(dataJsonObject.optString("imagePathFacebook"));
                    bookdocPropertyManager.setFieldPushCheck(dataJsonObject.optInt("push"));
                    bookdocPropertyManager.setFieldPrescriptionCount(dataJsonObject.optInt("prescribed"));
                    bookdocPropertyManager.setFieldQuestionCount(dataJsonObject.optInt("questioned"));
                    bookdocPropertyManager.setAccessToken(dataJsonObject.optString("token"));
                }else{
                    Log.e("요청에러", response.message().toString());
                    message = new JSONObject(responseMessage).optString("message");
                }
            } catch(Exception e){
                Log.e("파싱에러", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요");
            } finally{
                if(response != null){
                    response.close();
                }
            }
            return message;
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            if(bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.dismiss();
            }
            try{
                if(message != null){
                    if(message.equals("success")){
                        startActivity(new Intent(BookdocSplashActivity.this, BookdocMainActivity.class));
                        finish();
                    }
                }
            }catch (Exception e){

            }
        }
    }

    private class BookdocLocalLoginAsyncTask extends AsyncTask<String, String, String> {

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
        protected String doInBackground(String ... strings) {
            String message = null;
            String userEmail = strings[0];
            String password = strings[1];
            Response response = null;
            OkHttpClient toServer;

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("email",userEmail)
                        .add("password",password)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_LOGIN))
                        .header("Accept", "application/json")
                        .post(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("response","response: "+responseMessage);
                if(response.isSuccessful()){
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
                }else{
                    Log.e("요청에러", response.message().toString());
                    message = new JSONObject(responseMessage).optString("message");
                }

            }catch(Exception e){
                Log.e("파싱에러", e.toString());
                publishProgress("서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요");
            }finally{
                if(response != null){
                    response.close();
                }
            }
            return message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.dismiss();
            }
            try{
                if(s != null){
                    if(s.equals("success")){
                        startActivity(new Intent(BookdocSplashActivity.this, BookdocMainActivity.class));
                        finish();
                    }
                }
            }catch (Exception e){

            }
        }
    }

    private class RefreshFCMTokenAsyncTask extends AsyncTask<Void, String, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            new UpdateUserInfoAsyncTask().execute();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Toast.makeText(BookdocApplication.getBookdocContext(), values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                new BookdocFirebaseInstanceIDService().onTokenRefresh();
            }catch (Exception e){
                Log.e("error", e.getLocalizedMessage());
                publishProgress("서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요");
            }
            return null;
        }
    }

    private class UpdateUserInfoAsyncTask extends AsyncTask<Void, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            Toast.makeText(BookdocApplication.getBookdocContext(),message  ,Toast.LENGTH_SHORT).show();
        }


        @Override
        protected String doInBackground(Void ... voids) {
            String message = null;
            Response response = null;
            OkHttpClient toServer;

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();

                RequestBody requestBody = new FormBody.Builder()
                        .add("push",String.valueOf(BookdocPropertyManager.getInstance().getFieldPushCheck()))
                        .add("pushToken",BookdocPropertyManager.getInstance().getFcmTokenKey())
                        .add("password", BookdocPropertyManager.getInstance().getPassword())
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_PUT_USER_SETTING))
                        .header("Accept", "application/json")
                        .addHeader("token", BookdocPropertyManager.getInstance().getAccessToken())
                        .put(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();
                Log.d("response","response user info: "+responseMessage);
                if(response.isSuccessful()){

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
            return message;
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
