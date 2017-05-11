package com.kr.bookdoc.bookdoc.bookprescriptioncreatefragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.Card;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.PrescriptionPost;


public class BookPrescriptionCreateFirstQuestionFragment extends Fragment implements TextWatcher {
    EditText bookdocCreatePrescriptionFirstEdit;
    TextView bookdocCreatePrescriptionFirstTv;
    com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card card;
    int guideIndex;

    public static BookPrescriptionCreateFirstQuestionFragment newInstance() {
        BookPrescriptionCreateFirstQuestionFragment bookPrescriptionCreateFirstQuestionFragment = new BookPrescriptionCreateFirstQuestionFragment();
        return bookPrescriptionCreateFirstQuestionFragment;
    }

    public static BookPrescriptionCreateFirstQuestionFragment newInstance(com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card card, int index) {
        BookPrescriptionCreateFirstQuestionFragment bookPrescriptionCreateFirstQuestionFragment = new BookPrescriptionCreateFirstQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("card", card);
        bundle.putInt("guideIndex", index);
        bookPrescriptionCreateFirstQuestionFragment.setArguments(bundle);
        return bookPrescriptionCreateFirstQuestionFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_book_prescription_create_first_question, container, false);

        bookdocCreatePrescriptionFirstEdit = (EditText) view.findViewById(R.id.bookdoc_create_prescription_first_edit);
        bookdocCreatePrescriptionFirstEdit.addTextChangedListener(this);
        bookdocCreatePrescriptionFirstTv = (TextView) view.findViewById(R.id.bookdoc_create_prescription_first_tv);

        if (getArguments() != null) {
            card = new com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card();
            card = (com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card) getArguments().getSerializable("card");
            guideIndex = getArguments().getInt("getIndex");
            bookdocCreatePrescriptionFirstEdit.setText(card.getDescription());
        }
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        int length = bookdocCreatePrescriptionFirstEdit.getText().toString().length();
        bookdocCreatePrescriptionFirstTv.setText(String.valueOf(length));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void setPrescriptionMainData() {
        PrescriptionPost prescriptionPost = PrescriptionPost.getInstance();
        Card card = new Card();
        card.setTitle("");
        card.setDescription(bookdocCreatePrescriptionFirstEdit.getText().toString());
        prescriptionPost.getCards().add(card);
    }
}
