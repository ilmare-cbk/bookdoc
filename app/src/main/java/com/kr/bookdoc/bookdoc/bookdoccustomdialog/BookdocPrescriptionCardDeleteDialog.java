package com.kr.bookdoc.bookdoc.bookdoccustomdialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import java.io.Serializable;

import com.kr.bookdoc.bookdoc.R;


public class BookdocPrescriptionCardDeleteDialog extends DialogFragment{

    private DialogResultInterface mResultInterface;
    public interface DialogResultInterface extends Serializable { void onResult(boolean bOK);  }

    public BookdocPrescriptionCardDeleteDialog() {
    }

    public static BookdocPrescriptionCardDeleteDialog newInstace (DialogResultInterface result) {
        BookdocPrescriptionCardDeleteDialog bookdocPrescriptionCardDeleteDialog = new BookdocPrescriptionCardDeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("DialogResultInterface", result);
        bookdocPrescriptionCardDeleteDialog.setArguments(bundle);
        return bookdocPrescriptionCardDeleteDialog;
    }

    @Override
    public void onStart() {
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        setStyle(STYLE_NORMAL, R.style.BookdocShareDialogTheme);
        View view = inflater.from(getContext()).inflate(R.layout.bookdoc_create_prescription_card_delete_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        mResultInterface = (DialogResultInterface)getArguments().getSerializable("DialogResultInterface");
        view.findViewById(R.id.bookdoc_create_prescription_card_delete_dialog).setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick (View view)
            {
                mResultInterface.onResult (true);
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onCancel (DialogInterface dialog)
    {
        super.onCancel (dialog);
        mResultInterface.onResult (false);

    }

    public void show(Context context, String strTag)
    {
        this.show(((FragmentActivity)context).getSupportFragmentManager(), strTag);
    }

}
