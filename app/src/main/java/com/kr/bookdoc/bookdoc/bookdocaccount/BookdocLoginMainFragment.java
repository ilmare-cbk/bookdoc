package com.kr.bookdoc.bookdoc.bookdocaccount;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.Arrays;

import com.kr.bookdoc.bookdoc.BookdocMainActivity;
import com.kr.bookdoc.bookdoc.BookdocPropertyManager;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdoccustomdialog.BookdocProgressDialog;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class BookdocLoginMainFragment extends Fragment implements View.OnClickListener, OnPageChangeListener{
    private ImageView bookdocLoginSlideFirst, bookdocLoginSlideSecond, bookdocLoginSlideThird;
    private CallbackManager callbackManager;
    private BookdocProgressDialog bookdocProgressDialog;

    public BookdocLoginMainFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookdocProgressDialog = new BookdocProgressDialog(getContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        onSuccessFacebookLogin(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(getContext(), "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("facebook login", exception.getLocalizedMessage());
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookdoc_login_main, container, false);

        bookdocLoginSlideFirst = (ImageView) view.findViewById(R.id.bookdoc_login_slide1);
        bookdocLoginSlideSecond = (ImageView) view.findViewById(R.id.bookdoc_login_slide2);
        bookdocLoginSlideThird = (ImageView) view.findViewById(R.id.bookdoc_login_slide3);

        ViewPager viewPager = (ViewPager)view.findViewById(R.id.viewpager_login);
        LoginPagerAdapter adapter = new LoginPagerAdapter(getActivity());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        Button btnLogin = (Button)view.findViewById(R.id.btn_login_main);
        Button btnFacebook = (Button)view.findViewById(R.id.btn_signup_facebook);
        Button btnEmail = (Button)view.findViewById(R.id.btn_signup_email);
        btnLogin.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        btnEmail.setOnClickListener(this);
        return view;
    }


    private void toggleSlideImage(int id){
        switch (id){
            case 0:
                bookdocLoginSlideFirst.setImageResource(R.drawable.slide1);
                bookdocLoginSlideSecond.setImageResource(R.drawable.slide2);
                bookdocLoginSlideThird.setImageResource(R.drawable.slide2);
                break;
            case 1:
                bookdocLoginSlideFirst.setImageResource(R.drawable.slide2);
                bookdocLoginSlideSecond.setImageResource(R.drawable.slide1);
                bookdocLoginSlideThird.setImageResource(R.drawable.slide2);
                break;
            case 2:
                bookdocLoginSlideFirst.setImageResource(R.drawable.slide2);
                bookdocLoginSlideSecond.setImageResource(R.drawable.slide2);
                bookdocLoginSlideThird.setImageResource(R.drawable.slide1);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login_main:
                ((BookdocLoginActivity)getActivity()).changeLogin();
                break;
            case R.id.btn_signup_facebook:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile","email","user_friends"));
                break;
            case R.id.btn_signup_email:
                ((BookdocLoginActivity)getActivity()).changeSingup();
                break;

        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        toggleSlideImage(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private class LoginPagerAdapter extends PagerAdapter {

        Context context;
        Bitmap galImage;
        BitmapFactory.Options options;
        private final int[] galImages = new int[] {
                R.drawable.login_guide_1s,
                R.drawable.login_guide_2s,
                R.drawable.login_guide_3s,

        };

        LoginPagerAdapter(Context context) {
            this.context = context;
            options = new BitmapFactory.Options();
        }

        @Override
        public int getCount() {
            return galImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            int padding = 0;
            imageView.setPadding(padding, padding, padding, padding);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            options.inSampleSize = 1;
            galImage = BitmapFactory.decodeResource(context.getResources(), galImages[position], options);

            imageView.setImageBitmap(galImage);
            container.addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((ImageView) object);
        }
    }

    private void onSuccessFacebookLogin(final LoginResult loginResult){
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject json, GraphResponse response) {
                if (response.getError() != null) {
                    System.out.println("ERROR");
                } else {
                    Log.d("GraphResponse","GraphResponse: "+response.getRawResponse());
                    String fcmToken = FirebaseInstanceId.getInstance().getToken();
                    Log.d("fcmToken","fcmToken: "+fcmToken);
                    if (!TextUtils.isEmpty(fcmToken) && !fcmToken.equals("")) {
                        final String facebookUserId = json.optString("id");
                        final String accessToken = loginResult.getAccessToken().getToken();
                        final String userImage = json.optJSONObject("picture").optJSONObject("data").optString("url");
                        BookdocPropertyManager.getInstance().setFaceBookUserId(facebookUserId);
                        BookdocPropertyManager.getInstance().setFieldFacebookAccesstoken(accessToken);
                        BookdocPropertyManager.getInstance().setFacebookUserImage(userImage);
                        BookdocPropertyManager.getInstance().setFcmTokenKey(fcmToken);
                        new BookdocFaceBookLoginAsyncTask().execute();
                    }else{
                        Toast.makeText(getContext(), "서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private class BookdocFaceBookSignUpAsyncTask extends AsyncTask<Void, Void, String>{

        protected void onPreExecute() {
            super.onPreExecute();
            if(!bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.show();
            }
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
                        .add("pushToken", BookdocPropertyManager.getInstance().getFcmTokenKey())
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_SIGN_UP))
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
                    bookdocPropertyManager.setUserEmail(dataJsonObject.optString("email"));
                    bookdocPropertyManager.setFacebookUserImage(dataJsonObject.optString("imagePathFacebook"));
                    bookdocPropertyManager.setAccessToken(dataJsonObject.optString("token"));
                }else{
                    Log.e("요청에러", response.message().toString());
                    message = new JSONObject(responseMessage).optString("message");
                }
            }catch(Exception e){
                Log.e("파싱에러", e.toString());
            }finally{
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
            if(message != null){
                if(message.equals("success")){
                    new BookdocFaceBookLoginAsyncTask().execute();
                }
                else{
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class BookdocFaceBookLoginAsyncTask extends AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.show();
            }
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
            }finally{
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
            if(message != null){
                if(message.equals("success")){
                    BookdocPropertyManager.getInstance().setCheckFacebookUser(true);
                    startActivity(new Intent(getContext(), BookdocMainActivity.class));
                    getActivity().finish();
                }else if(message.equals("no user updated")){
                    new BookdocFaceBookSignUpAsyncTask().execute();
                }
                else{
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
