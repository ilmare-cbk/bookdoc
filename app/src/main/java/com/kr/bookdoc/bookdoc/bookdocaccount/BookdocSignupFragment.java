package com.kr.bookdoc.bookdoc.bookdocaccount;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.regex.Pattern;

import com.kr.bookdoc.bookdoc.BookdocProfileSettingActivity;
import com.kr.bookdoc.bookdoc.BookdocPropertyManager;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocNetworkDefineConstant;
import com.kr.bookdoc.bookdoc.bookdocnetwork.BookdocOkHttpInitSingtonManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class BookdocSignupFragment extends Fragment implements OnClickListener, OnEditorActionListener {

    private Toolbar signupToolbar;
    private ActionBar actionBar;
    private BookdocLoginActivity activity;
    private EditText emailView, passwordView, passwordMatchView;
    private Button signUpBtn;
    private Typeface typeNotoSansR;

    public BookdocSignupFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        typeNotoSansR = Typeface.createFromAsset(getContext().getAssets(), "NotoSansKR_Regular_Hestia.otf");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof BookdocLoginActivity){
            activity = (BookdocLoginActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookdoc_signup, container, false);

        emailView = (EditText) view.findViewById(R.id.edit_sign_user_email);
        passwordView = (EditText) view.findViewById(R.id.edit_sign_password);
        passwordMatchView = (EditText) view.findViewById(R.id.edit_sign_password_match);
        signupToolbar = (Toolbar) view.findViewById(R.id.toolbar_signup);
        TextView toolbarTitle = (TextView) view.findViewById(R.id.textview_fragment_signup_title);
        emailView.setOnEditorActionListener(this);
        passwordView.setOnEditorActionListener(this);
        passwordMatchView.setOnEditorActionListener(this);

        activity.setSupportActionBar(signupToolbar);
        actionBar = activity.getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);


        signUpBtn = (Button) view.findViewById(R.id.btn_signup);
        signUpBtn.setOnClickListener(this);

        toolbarTitle.setTypeface(typeNotoSansR);
        emailView.setTypeface(typeNotoSansR);
        passwordView.setTypeface(typeNotoSansR);
        passwordMatchView.setTypeface(typeNotoSansR);

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                activity.getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_signup:
                String email = emailView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();
                String passwordMatch = passwordMatchView.getText().toString().trim();
                boolean checkEmail = isValidEmail(email);
                if(checkEmail){
                    if(password.length() > 7 && !TextUtils.isEmpty(password)
                            && passwordMatch.length() > 7 && !TextUtils.isEmpty(passwordMatch)
                            && password.equals(passwordMatch)){
                        signUpProcess();
                    }else{
                        Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getContext(),"정확한 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        boolean handled = false;
        switch (i){
            case EditorInfo.IME_ACTION_NEXT:
                switch (textView.getId()){
                    case R.id.edit_sign_user_email:
                        boolean checkEmail = isValidEmail(emailView.getText().toString().trim());
                        if(checkEmail){
                            passwordView.setFocusableInTouchMode(true);
                            handled = false;
                        }else{
                            Toast.makeText(getContext(),"정확한 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
                            handled = true;
                        }
                        return  handled;
                    case R.id.edit_sign_password:
                        String password = passwordView.getText().toString().trim();
                        if(password.length() > 7 && !TextUtils.isEmpty(password)){
                            passwordMatchView.setFocusableInTouchMode(true);
                            handled = false;
                        }else{
                            Toast.makeText(getContext(),"비밀번호는 8자리 이상입니다.", Toast.LENGTH_SHORT).show();
                            handled = true;
                        }
                        return  handled;
                }
            case EditorInfo.IME_ACTION_DONE:
                String password = passwordView.getText().toString().trim();
                String passwordMatch = passwordMatchView.getText().toString().trim();
                if(password.length() > 7 && !TextUtils.isEmpty(password)
                        && passwordMatch.length() > 7 && !TextUtils.isEmpty(passwordMatch)
                        && password.equals(passwordMatch)){
                    signUpProcess();
                    handled = false;
                }else{
                    Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    handled = true;
                }
                return  handled;
        }
        return handled;
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    private void signUpProcess(){
        final String userEmail = emailView.getText().toString().trim();
        final String matchPassword = passwordMatchView.getText().toString().trim();
        BookdocPropertyManager.getInstance().setUserEmail(userEmail);
        BookdocPropertyManager.getInstance().setPassword(matchPassword);
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        BookdocPropertyManager.getInstance().setFcmTokenKey(fcmToken);
        new BookdocSignUpAsyncTask().execute(userEmail, matchPassword, fcmToken);
    }

    private class BookdocSignUpAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String email = strings[0];
            String password = strings[1];
            String pushToken = strings[2];
            String message = null;
            Response response = null;
            OkHttpClient toServer;

            try {
                toServer = BookdocOkHttpInitSingtonManager.getOkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("email", email)
                        .add("password", password)
                        .add("accessToken", "")
                        .add("pushToken", pushToken)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(BookdocNetworkDefineConstant.SERVER_URL_SIGN_UP))
                        .header("Accept", "application/json")
                        .post(requestBody)
                        .build();

                response = toServer.newCall(request).execute();
                String responseMessage = response.body().string();

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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(!TextUtils.isEmpty(s) && s != null && s.equals("success")){
                Intent profileSettingIntent = new Intent(getContext(), BookdocProfileSettingActivity.class);
                profileSettingIntent.putExtra("signUp",true);
                startActivity(profileSettingIntent);
                getActivity().finish();
            }else{
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
