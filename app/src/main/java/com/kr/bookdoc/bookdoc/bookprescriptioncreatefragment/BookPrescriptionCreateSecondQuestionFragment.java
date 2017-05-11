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

import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.Card;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.prescriptionpost.PrescriptionPost;


public class BookPrescriptionCreateSecondQuestionFragment extends Fragment implements TextWatcher {
    EditText bookdocCreatePrescriptionSecondEdit;
    TextView bookdocCreatePrescriptionSecondTv;
    com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card card;
    int guideIndex;

    public static BookPrescriptionCreateSecondQuestionFragment newInstance() {
        BookPrescriptionCreateSecondQuestionFragment bookPrescriptionCreateSecondQuestionFragment = new BookPrescriptionCreateSecondQuestionFragment();
        return bookPrescriptionCreateSecondQuestionFragment;
    }

    public static BookPrescriptionCreateSecondQuestionFragment newInstance(com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card card, int index) {
        BookPrescriptionCreateSecondQuestionFragment bookPrescriptionCreateSecondQuestionFragment = new BookPrescriptionCreateSecondQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("card", card);
        bundle.putInt("guideIndex", index);
        bookPrescriptionCreateSecondQuestionFragment.setArguments(bundle);
        return bookPrescriptionCreateSecondQuestionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_book_prescription_create_second_question, container, false);
        bookdocCreatePrescriptionSecondEdit = (EditText) view.findViewById(R.id.bookdoc_create_prescription_second_edit);
        bookdocCreatePrescriptionSecondEdit.addTextChangedListener(this);
        bookdocCreatePrescriptionSecondTv = (TextView) view.findViewById(R.id.bookdoc_create_prescription_second_tv);

        if (getArguments() != null) {
            card = new com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card();
            card = (com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card) getArguments().getSerializable("card");
            guideIndex = getArguments().getInt("getIndex");
            bookdocCreatePrescriptionSecondEdit.setText(card.getDescription());
        }
        return view;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        int length = bookdocCreatePrescriptionSecondEdit.getText().toString().length();
        bookdocCreatePrescriptionSecondTv.setText(String.valueOf(length));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public void setPrescriptionMainData() {
        PrescriptionPost prescriptionPost = PrescriptionPost.getInstance();
        Card card = new Card();
        card.setTitle("");
        card.setDescription(bookdocCreatePrescriptionSecondEdit.getText().toString());
        prescriptionPost.getCards().add(card);
    }
}
