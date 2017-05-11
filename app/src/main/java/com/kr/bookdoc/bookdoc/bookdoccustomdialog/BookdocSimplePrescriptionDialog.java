package com.kr.bookdoc.bookdoc.bookdoccustomdialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.kr.bookdoc.bookdoc.BookdocCreateSimplePrescriptionActivity;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.singlelines.singlelinedetail.Data;

public class BookdocSimplePrescriptionDialog extends DialogFragment implements OnClickListener {
    private TextView bookdocSimplePrescriptionDialogEdit;
    private TextView bookdocSimplePrescriptionDialogDelete;
    private int id;
    private int position;
    private int currentPosition;
    private Data data;
    private OnDeleteSimplePrescriptionListener onDeleteSimplePrescriptionListener;

    public static BookdocSimplePrescriptionDialog newInstances(Data data, int position, int currentPosition) {
        BookdocSimplePrescriptionDialog bookdocSimplePrescriptionDialog = new BookdocSimplePrescriptionDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putSerializable("currentPosition", currentPosition);
        bundle.putSerializable("data", data);
        bookdocSimplePrescriptionDialog.setArguments(bundle);
        return bookdocSimplePrescriptionDialog;
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
        View view = inflater.from(getContext()).inflate(R.layout.bookdoc_simple_prescription_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        currentPosition = getArguments().getInt("currentPosition");
        position = getArguments().getInt("position");
        data = (Data) getArguments().getSerializable("data");

        bookdocSimplePrescriptionDialogEdit = (TextView) view.findViewById(R.id.bookdoc_simple_prescription_edit);
        bookdocSimplePrescriptionDialogDelete = (TextView) view.findViewById(R.id.bookdoc_simple_prescription_delete);
        bookdocSimplePrescriptionDialogEdit.setOnClickListener(this);
        bookdocSimplePrescriptionDialogDelete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.bookdoc_simple_prescription_edit:
                Intent editSimplePrescriptionIntent = new Intent(getContext(), BookdocCreateSimplePrescriptionActivity.class);
                editSimplePrescriptionIntent.putExtra("position", position+currentPosition);
                editSimplePrescriptionIntent.putExtra("checkEdit", true);
                Bundle bundle = new Bundle();
                bundle.putSerializable("dataBundle", data);
                editSimplePrescriptionIntent.putExtra("data", bundle);
                startActivity(editSimplePrescriptionIntent);
                dismiss();
                getActivity().finish();
                break;
            case R.id.bookdoc_simple_prescription_delete:
                if(onDeleteSimplePrescriptionListener != null){
                    onDeleteSimplePrescriptionListener.onDeleteSimplePrescription(data.getId(), position);
                }
                dismiss();
                break;
        }
    }


    public interface OnDeleteSimplePrescriptionListener {
        void onDeleteSimplePrescription(int id, int position);
    }

    public void setOnDeleteSimplePrescription(OnDeleteSimplePrescriptionListener onDeleteSimplePrescriptionListener){
        this.onDeleteSimplePrescriptionListener = onDeleteSimplePrescriptionListener;
    }

}
