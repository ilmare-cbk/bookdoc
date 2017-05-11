package com.kr.bookdoc.bookdoc.bookdocaccount;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import com.kr.bookdoc.bookdoc.R;


public class BookdocLoginActivity extends AppCompatActivity {
    boolean bool_isBackbuttonTap;
    private long backPressedTime = 0;
    private final long FINSH_INTERVAL_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookdoc_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.login_main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new BookdocLoginMainFragment())
                    .commit();
        }


    }


    public void changeSendPasswordEmail(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new BookdocFindpassFragment())
                .addToBackStack(null)
                .commit();
    }
    public void changeLogin(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new BookdocLoginFragment())
                .addToBackStack(null)
                .commit();
    }

    public void changeSingup() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new BookdocSignupFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        long intervalTime = currentTime - backPressedTime;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (fm.getBackStackEntryCount() > 0)
        {
            fm.popBackStack();
            ft.commit();
        }
        else
        {
            if (bool_isBackbuttonTap == true)
            {
                super.onBackPressed();

                finish();
            }
            else
            {

                bool_isBackbuttonTap = true;
                if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
                    super.onBackPressed();
                }else {
                    backPressedTime = currentTime;
                    Toast.makeText(getApplicationContext(),
                            "'뒤로' 버튼 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}