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
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.PrescriptionPost;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.Card;


public class BookPrescriptionCreateThirdQuestionFragment extends Fragment implements TextWatcher {
    EditText bookdocCreatePrescriptionThirdEdit;
    TextView bookdocCreatePrescriptionThirdTv;
    com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card card;
    int guideIndex;

    public static BookPrescriptionCreateThirdQuestionFragment newInstance() {
        BookPrescriptionCreateThirdQuestionFragment bookPrescriptionCreateThirdQuestionFragment = new BookPrescriptionCreateThirdQuestionFragment();
        return bookPrescriptionCreateThirdQuestionFragment;
    }

    public static BookPrescriptionCreateThirdQuestionFragment newInstance(com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card card, int index) {
        BookPrescriptionCreateThirdQuestionFragment bookPrescriptionCreateThirdQuestionFragment = new BookPrescriptionCreateThirdQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("card", card);
        bundle.putInt("guideIndex", index);
        bookPrescriptionCreateThirdQuestionFragment.setArguments(bundle);
        return bookPrescriptionCreateThirdQuestionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_book_prescription_create_third_question, container, false);
        bookdocCreatePrescriptionThirdEdit = (EditText) view.findViewById(R.id.bookdoc_create_prescription_third_edit);
        bookdocCreatePrescriptionThirdEdit.addTextChangedListener(this);
        bookdocCreatePrescriptionThirdTv = (TextView) view.findViewById(R.id.bookdoc_create_prescription_third_tv);

        if (getArguments() != null) {
            card = new com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card();
            card = (com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card) getArguments().getSerializable("card");
            guideIndex = getArguments().getInt("getIndex");
            bookdocCreatePrescriptionThirdEdit.setText(card.getDescription());
        }
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        int length = bookdocCreatePrescriptionThirdEdit.getText().toString().length();
        bookdocCreatePrescriptionThirdTv.setText(String.valueOf(length));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void setPrescriptionMainData() {
        PrescriptionPost prescriptionPost = PrescriptionPost.getInstance();
        Card card = new Card();
        card.setTitle("");
        card.setDescription(bookdocCreatePrescriptionThirdEdit.getText().toString());
        prescriptionPost.getCards().add(card);
    }
}
