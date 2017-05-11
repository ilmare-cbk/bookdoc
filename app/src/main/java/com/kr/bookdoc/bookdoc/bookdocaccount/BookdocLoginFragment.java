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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

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


public class BookdocLoginFragment extends Fragment implements View.OnClickListener{
    private Toolbar loginToolbar;
    private BookdocLoginActivity bookdocLoginActivity;
    private EditText idView, pwView;
    private Typeface typeNotoSansR;
    private BookdocProgressDialog bookdocProgressDialog;


    public BookdocLoginFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof BookdocLoginActivity){
            bookdocLoginActivity = (BookdocLoginActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        bookdocProgressDialog = new BookdocProgressDialog(getContext());
        typeNotoSansR = Typeface.createFromAsset(getContext().getAssets(), "NotoSansKR_Regular_Hestia.otf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookdoc_login, container, false);
        loginToolbar = (Toolbar) view.findViewById(R.id.toolbar_login);
        bookdocLoginActivity.setSupportActionBar(loginToolbar);
        ActionBar actionBar = bookdocLoginActivity.getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.back_black_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        TextView toolbarTitle = (TextView) view.findViewById(R.id.textview_fragment_login_title);

        idView = (EditText)view.findViewById(R.id.edit_userid);
        pwView = (EditText)view.findViewById(R.id.edit_password);
        Button btnLogin = (Button)view.findViewById(R.id.btn_login);
        Button btnPass = (Button)view.findViewById(R.id.btn_find_password);
        btnLogin.setOnClickListener(this);
        btnPass.setOnClickListener(this);

        toolbarTitle.setTypeface(typeNotoSansR);
        idView.setTypeface(typeNotoSansR);
        pwView.setTypeface(typeNotoSansR);
        btnLogin.setTypeface(typeNotoSansR);
        btnPass.setTypeface(typeNotoSansR);

        return view;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                bookdocLoginActivity.getSupportFragmentManager().popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_login:
                loginProcess();
                break;
            case R.id.btn_find_password:
                ((BookdocLoginActivity)getActivity()).changeSendPasswordEmail();
                break;
        }
    }

    private class BookdocLocalLoginAsyncTask extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!bookdocProgressDialog.isShowing()){
                bookdocProgressDialog.show();
            }
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
            if(s != null){
                if(s.equals("success")){
                    startActivity(new Intent(getContext(), BookdocMainActivity.class));
                    getActivity().finish();
                }else{
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private void loginProcess(){
        final String userEmail = idView.getText().toString().trim();
        final String password = pwView.getText().toString().trim();
        if (!TextUtils.isEmpty(userEmail) && userEmail.length() > 1
                && !TextUtils.isEmpty(password) && password.length() > 1) {

            String fcmToken = FirebaseInstanceId.getInstance().getToken();
            Log.d("fcmToken", "fcmToken: "+fcmToken);
            if (!TextUtils.isEmpty(fcmToken) && !fcmToken.equals("")) {
                BookdocPropertyManager.getInstance().setFcmTokenKey(fcmToken);
                BookdocPropertyManager.getInstance().setUserEmail(userEmail);
                BookdocPropertyManager.getInstance().setPassword(password);
                new BookdocLocalLoginAsyncTask().execute(userEmail, password);
            } else {
                Toast.makeText(getContext(), "서버와의 연결이 원할하지 않습니다."+"\n"+"잠시후에 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "email password 입력 바람", Toast.LENGTH_SHORT).show();
        }
    }
}
