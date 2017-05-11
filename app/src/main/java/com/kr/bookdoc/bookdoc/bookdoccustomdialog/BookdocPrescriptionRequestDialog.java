package com.kr.bookdoc.bookdoc.bookdoccustomdialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.kr.bookdoc.bookdoc.BookdocRequestPrescriptionActivity;
import com.kr.bookdoc.bookdoc.R;

public class BookdocPrescriptionRequestDialog extends DialogFragment implements View.OnClickListener {
    private TextView bookdocPrescriptionRequestEdit;
    private TextView bookdocPrescriptionRequestDelete;
    private int requestId;
    private String requestDescription;
    private OnDeleteRequestListener onDeleteRequestListener;
    private int position;
    private boolean checkFrom;

    public static BookdocPrescriptionRequestDialog newInstances(int requestId , String requestDescription, int position, boolean checkFrom) {
        BookdocPrescriptionRequestDialog bookdocPrescriptionDetaiGuidelDialog = new BookdocPrescriptionRequestDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("requestId",requestId);
        bundle.putString("requestDescription", requestDescription);
        bundle.putInt("position", position);
        bundle.putBoolean("checkFrom", checkFrom);
        bookdocPrescriptionDetaiGuidelDialog.setArguments(bundle);
        return bookdocPrescriptionDetaiGuidelDialog;
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
        View view = inflater.from(getContext()).inflate(R.layout.bookdoc_prescription_request_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        requestId = getArguments().getInt("requestId");
        requestDescription = getArguments().getString("requestDescription");
        position = getArguments().getInt("position");
        checkFrom = getArguments().getBoolean("checkFrom");

        bookdocPrescriptionRequestEdit = (TextView) view.findViewById(R.id.bookdoc_prescription_request_edit);
        bookdocPrescriptionRequestDelete = (TextView) view.findViewById(R.id.bookdoc_prescription_request_delete);
        bookdocPrescriptionRequestEdit.setOnClickListener(this);
        bookdocPrescriptionRequestDelete.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bookdoc_prescription_request_edit:
                Intent requestEditIntent = new Intent(getContext(), BookdocRequestPrescriptionActivity.class);
                requestEditIntent.putExtra("requestId",requestId);
                requestEditIntent.putExtra("requestDescription", requestDescription);
                requestEditIntent.putExtra("position", position);
                requestEditIntent.putExtra("checkEdit",true);
                requestEditIntent.putExtra("checkFrom", checkFrom);
                getActivity().startActivity(requestEditIntent);
                dismiss();
                break;
            case R.id.bookdoc_prescription_request_delete:
                if(onDeleteRequestListener != null ){
                    onDeleteRequestListener.onDeleteRequest(requestId, position);
                }
                dismiss();
                break;
        }
    }

    public interface OnDeleteRequestListener{
        void onDeleteRequest(int requestId, int position);
    }

    public void setOnDeleteRequestListener(OnDeleteRequestListener onDeleteRequestListener){
        this.onDeleteRequestListener = onDeleteRequestListener;
    }
}
