package com.kr.bookdoc.bookdoc.bookprescriptiondetailfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kr.bookdoc.bookdoc.R;
import com.kr.bookdoc.bookdoc.bookdocutils.BookdocApplication;
import com.kr.bookdoc.bookdoc.bookdocvalueobject.prescriptions.singleprescription.Card;


public class BookPrescriptionGuideQuestionFragment extends Fragment {
    TextView bookPrescriptionDetailGuideText;
    TextView bookPrescriptionDetailGuideContent;
    int guideIndex;
    String cardTitle;
    Card card;
    private final int FIRST_GUIDE_FRAGMENT_INDEX = 0;
    private final int SECOND_GUIDE_FRAGMENT_INDEX = 1;
    private final int LAST_GUIDE_FRAGMENT_INDEX = 2;

    public BookPrescriptionGuideQuestionFragment() {
    }

    public static BookPrescriptionGuideQuestionFragment newInstance(Card card, int index) {
        BookPrescriptionGuideQuestionFragment bookPrescriptionGuideQuestionFragment = new BookPrescriptionGuideQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("card", card);
        bundle.putInt("guideIndex", index);
        bookPrescriptionGuideQuestionFragment.setArguments(bundle);
        return bookPrescriptionGuideQuestionFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(BookdocApplication.getBookdocContext()).inflate(R.layout.fragment_book_prescription_guide_question, container, false);
        card = new Card();
        card = (Card) getArguments().getSerializable("card");
        guideIndex = getArguments().getInt("guideIndex");
        bookPrescriptionDetailGuideText = (TextView) view.findViewById(R.id.book_prescription_detail_guide_question);
        bookPrescriptionDetailGuideContent = (TextView) view.findViewById(R.id.book_prescription_guide_question_content);
        switch (guideIndex) {
            case FIRST_GUIDE_FRAGMENT_INDEX:
                cardTitle = getResources().getString(R.string.first_guide_text);
                break;
            case SECOND_GUIDE_FRAGMENT_INDEX:
                cardTitle = getResources().getString(R.string.second_guide_text);
                break;
            case LAST_GUIDE_FRAGMENT_INDEX:
                cardTitle = getResources().getString(R.string.third_guide_text);
                break;
            default:
                cardTitle = card.getTitle();
                break;
        }
        bookPrescriptionDetailGuideText.setText(cardTitle);
        bookPrescriptionDetailGuideContent.setText(card.getDescription());

        return view;
    }
}
