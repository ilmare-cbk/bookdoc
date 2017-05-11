package com.kr.bookdoc.bookdoc.bookdoccustomdialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.kr.bookdoc.bookdoc.BookdocBookPrescriptionEditAcitivity;
import com.kr.bookdoc.bookdoc.bookdocutils.BusProvider;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.bookdocotto.BookPrescriptionDel;


public class BookdocPrescriptionDetailMainDialog extends DialogFragment implements View.OnClickListener {
    TextView bookdocPrescriptionDetailMainEdit;
    TextView getBookdocPrescriptionDetailMainDelete;
    int prescriptionId;
    int position;

    public static BookdocPrescriptionDetailMainDialog newInstances(int prescriptionId, int position) {
        BookdocPrescriptionDetailMainDialog bookdocPrescriptionDetailMainDialog = new BookdocPrescriptionDetailMainDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("prescriptionId",prescriptionId);
        bundle.putInt("position",position);
        bookdocPrescriptionDetailMainDialog.setArguments(bundle);
        return bookdocPrescriptionDetailMainDialog;
    }

    @Override
    public void onStart() {
        Window window = getDialog().getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.TOP|Gravity.RIGHT);
        WindowManager.LayoutParams params = window.getAttributes();
        final int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                getResources().getDisplayMetrics());
        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 107,
                getResources().getDisplayMetrics());
        params.x = width;
        params.y = height;
        window.setAttributes(params);
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
        View view = inflater.from(getContext()).inflate(R.layout.bookdoc_prescriptoin_detail_main_dialog, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        prescriptionId = getArguments().getInt("prescriptionId");
        position = getArguments().getInt("position");

        bookdocPrescriptionDetailMainEdit = (TextView) view.findViewById(R.id.bookdoc_prescription_detail_main_edit);
        getBookdocPrescriptionDetailMainDelete = (TextView) view.findViewById(R.id.bookdoc_prescription_detail_main_delete);
        bookdocPrescriptionDetailMainEdit.setOnClickListener(this);
        getBookdocPrescriptionDetailMainDelete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bookdoc_prescription_detail_main_edit:
                Intent bookPrescriptionEditIntent = new Intent(getActivity(), BookdocBookPrescriptionEditAcitivity.class);
                bookPrescriptionEditIntent.putExtra("prescriptionId",prescriptionId);
                bookPrescriptionEditIntent.putExtra("position",position);
                getActivity().startActivity(bookPrescriptionEditIntent);
                dismiss();
                break;
            case R.id.bookdoc_prescription_detail_main_delete:
                BusProvider.getInstance().post(new BookPrescriptionDel(prescriptionId, position));
                getActivity().finish();
                break;
        }
    }

}
