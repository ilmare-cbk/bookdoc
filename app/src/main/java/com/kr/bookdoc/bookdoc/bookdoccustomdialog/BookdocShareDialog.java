package com.kr.bookdoc.bookdoc.bookdoccustomdialog;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.kr.bookdoc.bookdoc.R;

public class BookdocShareDialog extends DialogFragment implements View.OnClickListener {
    private LinearLayout bookdocShareDialogClose;
    private LinearLayout bookdocShareDialogFacebook;
    private LinearLayout bookdocShareDialogKakaotalk;
    private LinearLayout bookdocShareDialogKakaostory;

    public static BookdocShareDialog newInstances() {
        BookdocShareDialog bookdocShareDialog = new BookdocShareDialog();
        return bookdocShareDialog;
    }
    public static BookdocShareDialog newInstances(Bitmap bitmap) {
        BookdocShareDialog bookdocShareDialog = new BookdocShareDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable("bitmap", bitmap);
        bookdocShareDialog.setArguments(bundle);
        return bookdocShareDialog;
    }

    @Override
    public void onStart() {
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setStyle(STYLE_NORMAL, R.style.BookdocShareDialogTheme);
        View view = inflater.from(getContext()).inflate(R.layout.bookdoc_share_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        bookdocShareDialogClose = (LinearLayout) view.findViewById(R.id.bookdoc_share_dialog_close);
        bookdocShareDialogFacebook = (LinearLayout) view.findViewById(R.id.bookdoc_share_dialog_facebook);
        bookdocShareDialogKakaotalk = (LinearLayout) view.findViewById(R.id.bookdoc_share_dialog_kakaotalk);
        bookdocShareDialogKakaostory = (LinearLayout) view.findViewById(R.id.bookdoc_share_dialog_kakaostory);
        bookdocShareDialogClose.setOnClickListener(this);
        bookdocShareDialogFacebook.setOnClickListener(this);
        bookdocShareDialogKakaotalk.setOnClickListener(this);
        bookdocShareDialogKakaostory.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bookdoc_share_dialog_close:
                dismiss();
                break;
            case R.id.bookdoc_share_dialog_facebook:
                dismiss();
                break;
            case R.id.bookdoc_share_dialog_kakaotalk:
                dismiss();
                break;
            case R.id.bookdoc_share_dialog_kakaostory:
                dismiss();
                break;
        }
    }
}