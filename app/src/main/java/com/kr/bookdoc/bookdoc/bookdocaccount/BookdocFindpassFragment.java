package com.kr.bookdoc.bookdoc.bookdocaccount;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.kr.bookdoc.bookdoc.R;

public class BookdocFindpassFragment extends Fragment {
    EditText emailView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_bookdoc_findpassword, container, false);
        emailView = (EditText)view.findViewById(R.id.edit_user_email);
        Button btnSendEmail = (Button)view.findViewById(R.id.btn_send_email);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "email발송", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}