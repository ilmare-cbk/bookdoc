package com.kr.bookdoc.bookdoc.bookdoccustomdialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.kr.bookdoc.bookdoc.R;


public class BookdocProgressDialog extends Dialog {
    public BookdocProgressDialog(Context context) {
        super(context);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.bookdoc_progress_dialog);
        this.setCanceledOnTouchOutside(false);
    }
}
